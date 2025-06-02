package br.com.estapar.service;

import br.com.estapar.domain.dto.*;
import br.com.estapar.repository.ParkingSpotRepository;
import br.com.estapar.repository.RevenueRecordRepository;
import br.com.estapar.repository.VehicleEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EstaparService {
    private final ParkingSpotRepository spotRepo;
    private final VehicleEntryRepository entryRepo;
    private final RevenueRecordRepository revenueRepo;

    public ResponseEntity<PlateStatusResponse> plateStatus(PlateStatusRequest req) {
        return entryRepo.findByLicensePlateAndExitTimeIsNull(req.getLicensePlate())
                .map(entry -> {
                    PlateStatusResponse resp = new PlateStatusResponse();
                    resp.setLicensePlate(entry.getLicensePlate());
                    resp.setEntryTime(entry.getEntryTime());
                    resp.setPriceUntilNow(entry.getChargedAmount());

                    spotRepo.findByLicensePlate(req.getLicensePlate()).ifPresent(spot -> {
                        resp.setLat(spot.getLat());
                        resp.setLng(spot.getLng());
                        resp.setTimeParked(spot.getEntryTime());
                    });

                    return ResponseEntity.ok(resp);
                }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<RevenueResponse> revenue(RevenueRequest req) {
        return revenueRepo.findBySectorAndDate(req.getSector(), req.getDate())
                .map(r -> ResponseEntity.ok(new RevenueResponse(r.getAmount(), "BRL", LocalDateTime.now())))
                .orElse(ResponseEntity.ok(new RevenueResponse(BigDecimal.ZERO, "BRL", LocalDateTime.now())));
    }


    public ResponseEntity<SpotStatusResponse> spotStatus(ParkingSpotStatusRequest req) {
        return spotRepo.findByLatAndLng(req.getLat(), req.getLng())
                .map(spot -> {
                    SpotStatusResponse resp = new SpotStatusResponse();
                    resp.setOcupied(spot.getOccupied());
                    resp.setLicensePlate(spot.getLicensePlate());
                    resp.setEntryTime(spot.getEntryTime());
                    resp.setTimeParked(spot.getEntryTime());
                    resp.setPriceUntilNow(BigDecimal.ZERO);
                    return ResponseEntity.ok(resp);
                }).orElse(ResponseEntity.notFound().build());
    }
}
