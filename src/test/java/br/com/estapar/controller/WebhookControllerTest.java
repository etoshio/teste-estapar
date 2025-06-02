package br.com.estapar.controller;

import br.com.estapar.domain.dto.WebhookEventDTO;
import br.com.estapar.domain.enums.EventType;
import br.com.estapar.domain.exceptions.BadRequestException;
import br.com.estapar.service.ParkingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebhookControllerTest {

    private final ParkingService parkingService = mock(ParkingService.class);
    private final WebhookController controller = new WebhookController(parkingService);

    @Test
    void shouldProcessEntryEvent() {
        WebhookEventDTO dto = new WebhookEventDTO();
        dto.setEventType(EventType.ENTRY);
        dto.setLicensePlate("ABC1234");
        dto.setEntryTime(LocalDateTime.now());

        ResponseEntity<String> response = controller.event(dto);

        verify(parkingService, times(1)).entry(dto.getLicensePlate(), dto.getEntryTime());
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("processado com sucesso"));
    }

    @Test
    void shouldProcessParkedEvent() {
        WebhookEventDTO dto = new WebhookEventDTO();
        dto.setEventType(EventType.PARKED);
        dto.setLicensePlate("XYZ5678");
        dto.setLat(BigDecimal.valueOf(-23.5));
        dto.setLng(BigDecimal.valueOf(-46.6));

        ResponseEntity<String> response = controller.event(dto);

        verify(parkingService, times(1)).parked(dto.getLicensePlate(), dto.getLat(), dto.getLng());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldProcessExitEvent() {
        WebhookEventDTO dto = new WebhookEventDTO();
        dto.setEventType(EventType.EXIT);
        dto.setLicensePlate("LMN8901");
        dto.setExitTime(LocalDateTime.now());

        ResponseEntity<String> response = controller.event(dto);

        verify(parkingService, times(1)).exit(dto.getLicensePlate(), dto.getExitTime());
        assertEquals(200, response.getStatusCodeValue());
    }

}
