package br.com.estapar.controller;

import br.com.estapar.domain.dto.*;
import br.com.estapar.service.EstaparService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstaparControllerTest {

    private EstaparService service;
    private EstaparController controller;

    @BeforeEach
    void setUp() {
        service = mock(EstaparService.class);
        controller = new EstaparController(service);
    }

    @Test
    void testPlateStatus() {
        PlateStatusRequest req = new PlateStatusRequest("ABC-1234");

        PlateStatusResponse resp = new PlateStatusResponse();
        resp.setLicensePlate("ABC-1234");
        resp.setEntryTime(LocalDateTime.now());
        resp.setPriceUntilNow(BigDecimal.valueOf(10));
        resp.setLat(BigDecimal.valueOf(-23.5));
        resp.setLng(BigDecimal.valueOf(-46.6));
        resp.setTimeParked(LocalDateTime.now());

        when(service.plateStatus(req)).thenReturn(ResponseEntity.ok(resp));

        ResponseEntity<PlateStatusResponse> response = controller.plateStatus(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("ABC-1234", response.getBody().getLicensePlate());
        verify(service, times(1)).plateStatus(req);
    }

    @Test
    void testSpotStatus() {
        ParkingSpotStatusRequest req = new ParkingSpotStatusRequest(BigDecimal.valueOf(-23.5), BigDecimal.valueOf(-46.6));

        SpotStatusResponse resp = new SpotStatusResponse();
        resp.setOcupied(true);
        resp.setLicensePlate("XYZ-9876");
        resp.setEntryTime(LocalDateTime.now().minusHours(1));
        resp.setTimeParked(LocalDateTime.now());
        resp.setPriceUntilNow(BigDecimal.valueOf(15));

        when(service.spotStatus(req)).thenReturn(ResponseEntity.ok(resp));

        ResponseEntity<SpotStatusResponse> response = controller.spotStatus(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isOcupied());
        verify(service, times(1)).spotStatus(req);
    }

    @Test
    void testRevenue() {
        RevenueRequest req = new RevenueRequest(LocalDate.now(), "A");

        RevenueResponse resp = new RevenueResponse(BigDecimal.valueOf(123.45), "BRL", LocalDateTime.now());

        when(service.revenue(req)).thenReturn(ResponseEntity.ok(resp));

        ResponseEntity<RevenueResponse> response = controller.revenue(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("BRL", response.getBody().getCurrency());
        verify(service, times(1)).revenue(req);
    }
}
