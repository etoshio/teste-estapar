package br.com.estapar.service;

import br.com.estapar.domain.entity.GarageSector;
import br.com.estapar.domain.entity.ParkingSpot;
import br.com.estapar.domain.entity.RevenueRecord;
import br.com.estapar.domain.entity.VehicleEntry;
import br.com.estapar.domain.exceptions.BadRequestException;
import br.com.estapar.domain.exceptions.NotFoundException;
import br.com.estapar.repository.GarageSectorRepository;
import br.com.estapar.repository.ParkingSpotRepository;
import br.com.estapar.repository.RevenueRecordRepository;
import br.com.estapar.repository.VehicleEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleEntryRepository vehicleEntryRepository;
    private final GarageSectorRepository garageSectorRepository;
    private final RevenueRecordRepository revenueRecordRepository;

    @Transactional
    public void entry(String licensePlate, LocalDateTime entryTime) {
        vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(licensePlate).ifPresent(entry -> {
            throw new BadRequestException("Já existe uma entrada ativa para o veículo: " + licensePlate);
        });

        for (GarageSector sector : garageSectorRepository.findAll()) {
            BigDecimal total = parkingSpotRepository.countBySector(sector.getSector());
            BigDecimal used = parkingSpotRepository.countBySectorAndOccupiedTrue(sector.getSector());
            BigDecimal maxCapacity = BigDecimal.valueOf(sector.getMaxCapacity());

            if (used.compareTo(maxCapacity) < 0) {
                BigDecimal price = calculateDynamic(used, total);
                BigDecimal chargedAmount = sector.getBasePrice()
                        .multiply(price)
                        .setScale(2, RoundingMode.HALF_UP);

                VehicleEntry entry = VehicleEntry.builder()
                        .licensePlate(licensePlate)
                        .entryTime(entryTime)
                        .sector(sector.getSector())
                        .chargedAmount(chargedAmount)
                        .build();

                vehicleEntryRepository.save(entry);
                return;
            }
        }
        throw new BadRequestException("Todos os setores estão ocupados.");
    }

    @Transactional
    public void parked(String licensePlate, BigDecimal lat, BigDecimal lng) {
        ParkingSpot parkingSpot = parkingSpotRepository.findByLatAndLng(lat, lng)
                .orElseThrow(() -> new NotFoundException("Vaga não encontrada para coordenadas informadas."));

        if (parkingSpot.getOccupied()) {
            throw new BadRequestException("Vaga já está ocupada.");
        }

        parkingSpot.setOccupied(true);
        parkingSpot.setLicensePlate(licensePlate);
        parkingSpot.setEntryTime(LocalDateTime.now());

        parkingSpotRepository.save(parkingSpot);
    }

    @Transactional
    public void exit(String licensePlate, LocalDateTime outTime) {
        VehicleEntry entry = vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(licensePlate)
                .orElseThrow(() -> new NotFoundException("Entrada ativa não encontrada para o veículo: " + licensePlate));

        entry.setExitTime(outTime);

        long minutes = Duration.between(entry.getEntryTime(), outTime).toMinutes();

        GarageSector sector = garageSectorRepository.findById(entry.getSector())
                .orElseThrow(() -> new NotFoundException("Setor '" + entry.getSector() + "' não encontrado."));

        BigDecimal finalAmount = calculate(minutes, entry.getChargedAmount(), sector.getDurationLimitMinutes());
        entry.setChargedAmount(finalAmount);
        vehicleEntryRepository.save(entry);

        parkingSpotRepository.findByLicensePlate(licensePlate).ifPresent(parkingSpot -> {
            parkingSpot.setOccupied(false);
            parkingSpot.setLicensePlate(null);
            parkingSpot.setEntryTime(null);
            parkingSpotRepository.save(parkingSpot);
        });

        RevenueRecord revenue = revenueRecordRepository.findBySectorAndDate(entry.getSector(), outTime.toLocalDate())
                .orElseGet(() -> RevenueRecord.builder()
                        .date(outTime.toLocalDate())
                        .sector(entry.getSector())
                        .amount(BigDecimal.ZERO)
                        .build());

        revenue.setAmount(revenue.getAmount().add(finalAmount));
        revenueRecordRepository.save(revenue);
    }

    private BigDecimal calculate(Long minutes, BigDecimal basePrice, Integer maxDuration) {
        if (minutes <= maxDuration) {
            return basePrice.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal minutesExceeded = BigDecimal.valueOf(minutes - maxDuration);
        BigDecimal hoursExceeded = minutesExceeded.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
        BigDecimal surcharge = basePrice.multiply(BigDecimal.valueOf(0.5)).multiply(hoursExceeded);

        return basePrice.add(surcharge).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDynamic(BigDecimal used, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }

        BigDecimal ratio = used.divide(total, 4, RoundingMode.HALF_UP);

        if (ratio.compareTo(BigDecimal.valueOf(0.25)) < 0) {
            return BigDecimal.valueOf(0.9);
        } else if (ratio.compareTo(BigDecimal.valueOf(0.5)) <= 0) {
            return BigDecimal.ONE;
        } else if (ratio.compareTo(BigDecimal.valueOf(0.75)) <= 0) {
            return BigDecimal.valueOf(1.1);
        } else {
            return BigDecimal.valueOf(1.25);
        }
    }
}
