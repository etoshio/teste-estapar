package br.com.estapar.service;

import br.com.estapar.client.GarageClient;
import br.com.estapar.domain.dto.GarageDto;
import br.com.estapar.domain.dto.GarageResponse;
import br.com.estapar.domain.dto.SpotDto;
import br.com.estapar.domain.entity.GarageSector;
import br.com.estapar.domain.entity.ParkingSpot;
import br.com.estapar.repository.GarageSectorRepository;
import br.com.estapar.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GarageService {

    private final GarageSectorRepository garageSectorRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final GarageClient garageClient;

    @Transactional
    public void importGarage(GarageResponse garage) {
        sectors(garage.garage());
        parkingSpots(garage.spots());
    }

    @Transactional
    public void getGarage() {
        GarageResponse garage = garageClient.getGarageData();
        if(garage != null) {
            sectors(garage.garage());
            parkingSpots(garage.spots());
        }
    }

    private void parkingSpots(List<SpotDto> spotDTOs) {
        spotDTOs.stream()
                .map(dto -> ParkingSpot.builder()
                        .lat(dto.lat())
                        .lng(dto.lng())
                        .sector(dto.sector())
                        .occupied(false)
                        .build())
                .forEach(parkingSpotRepository::save);
    }

    private void sectors(List<GarageDto> sectorDTOs) {
        sectorDTOs.stream()
                .map(dto -> GarageSector.builder()
                        .sector(dto.sector())
                        .basePrice(dto.basePrice())
                        .maxCapacity(dto.maxCapacity())
                        .openHour(LocalTime.parse(dto.openHour()))
                        .closeHour(LocalTime.parse(dto.closeHour()))
                        .durationLimitMinutes(dto.durationLimitMinutes())
                        .build())
                .forEach(garageSectorRepository::save);
    }

}
