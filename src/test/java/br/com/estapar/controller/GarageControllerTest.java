package br.com.estapar.controller;

import br.com.estapar.domain.dto.GarageDto;
import br.com.estapar.domain.dto.GarageResponse;
import br.com.estapar.domain.dto.SpotDto;
import br.com.estapar.service.GarageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GarageControllerTest {

    @Mock
    private GarageService garageService;

    @InjectMocks
    private GarageController garageController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testImportarGarage_deveChamarServicoComGarageResponse() {
        // Arrange
        GarageDto garageDto = new GarageDto("A", BigDecimal.TEN, 100, "08:00", "18:00", 120);
        SpotDto spotDto = new SpotDto(1, "A", BigDecimal.ONE, BigDecimal.ONE);
        GarageResponse request = new GarageResponse(List.of(garageDto), List.of(spotDto));

        // Act
        garageController.importarGarage(request);

        // Assert
        verify(garageService, times(1)).importGarage(request);
    }

    @Test
    void testGetGarageData_deveChamarServicoGetGarage() {
        // Act
        garageController.getGarageData();

        // Assert
        verify(garageService, times(1)).getGarage();
    }

    @Test
    void testGetGarage_deveRetornarGarageResponseMockado() {
        // Act
        GarageResponse response = garageController.getGarage();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.garage().size());
        assertEquals(2, response.spots().size());

        assertEquals("A", response.garage().get(0).sector());
        assertEquals("B", response.garage().get(1).sector());
    }
}
