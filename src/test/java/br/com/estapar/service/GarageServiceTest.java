package br.com.estapar.service;

import br.com.estapar.client.GarageClient;
import br.com.estapar.domain.dto.GarageDto;
import br.com.estapar.domain.dto.GarageResponse;
import br.com.estapar.domain.dto.SpotDto;
import br.com.estapar.domain.entity.GarageSector;
import br.com.estapar.domain.entity.ParkingSpot;
import br.com.estapar.repository.GarageSectorRepository;
import br.com.estapar.repository.ParkingSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GarageServiceTest {

    private GarageSectorRepository garageSectorRepository;
    private ParkingSpotRepository parkingSpotRepository;
    private GarageClient garageClient;
    private GarageService garageService;

    @BeforeEach
    void setup() {
        garageSectorRepository = mock(GarageSectorRepository.class);
        parkingSpotRepository = mock(ParkingSpotRepository.class);
        garageClient = mock(GarageClient.class);

        garageService = new GarageService(garageSectorRepository, parkingSpotRepository, garageClient);
    }

    @Test
    void shouldImportGarageData() {
        // Arrange
        GarageDto garageDto = new GarageDto("A", new BigDecimal("10.00"), 50, "08:00", "18:00", 120);
        SpotDto spotDto = new SpotDto(1L,"B", new BigDecimal(11.0), new BigDecimal(22.0));

        GarageResponse response = new GarageResponse(List.of(garageDto), List.of(spotDto));

        // Act
        garageService.importGarage(response);

        // Assert
        ArgumentCaptor<GarageSector> garageCaptor = ArgumentCaptor.forClass(GarageSector.class);
        verify(garageSectorRepository, times(1)).save(garageCaptor.capture());
        GarageSector savedSector = garageCaptor.getValue();
        assertEquals("A", savedSector.getSector());
        assertEquals(LocalTime.of(8, 0), savedSector.getOpenHour());

        ArgumentCaptor<ParkingSpot> spotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotRepository, times(1)).save(spotCaptor.capture());
        ParkingSpot savedSpot = spotCaptor.getValue();
        assertEquals("B", savedSpot.getSector());
        assertEquals(new BigDecimal(11), savedSpot.getLat());
    }

    @Test
    void shouldGetGarageDataFromClient() {
        GarageDto garageDto = new GarageDto("B", new BigDecimal("15.00"), 30, "09:00", "17:00", 90);
        SpotDto spotDto = new SpotDto(1L,"B", new BigDecimal(11.0), new BigDecimal(22.0));

        GarageResponse response = new GarageResponse(List.of(garageDto), List.of(spotDto));
        when(garageClient.getGarageData()).thenReturn(response);

        garageService.getGarage();

        verify(garageSectorRepository, times(1)).save(any(GarageSector.class));
        verify(parkingSpotRepository, times(1)).save(any(ParkingSpot.class));
    }

    @Test
    void shouldDoNothingWhenGarageClientReturnsNull() {
        when(garageClient.getGarageData()).thenReturn(mock(GarageResponse.class));

        garageService.getGarage();

        verify(garageSectorRepository, never()).save(any());
        verify(parkingSpotRepository, never()).save(any());
    }
}
