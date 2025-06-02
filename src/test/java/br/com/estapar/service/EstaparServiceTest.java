package br.com.estapar.service;

import br.com.estapar.domain.dto.*;
import br.com.estapar.domain.entity.ParkingSpot;
import br.com.estapar.domain.entity.RevenueRecord;
import br.com.estapar.domain.entity.VehicleEntry;
import br.com.estapar.repository.ParkingSpotRepository;
import br.com.estapar.repository.RevenueRecordRepository;
import br.com.estapar.repository.VehicleEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstaparServiceTest {

    private ParkingSpotRepository spotRepo;
    private VehicleEntryRepository entryRepo;
    private RevenueRecordRepository revenueRepo;
    private EstaparService service;

    @BeforeEach
    void setUp() {
        spotRepo = mock(ParkingSpotRepository.class);
        entryRepo = mock(VehicleEntryRepository.class);
        revenueRepo = mock(RevenueRecordRepository.class);
        service = new EstaparService(spotRepo, entryRepo, revenueRepo);
    }

    @Test
    void testPlateStatus_found() {
        String licensePlate = "ABC-1234";

        VehicleEntry entry = new VehicleEntry();
        entry.setLicensePlate(licensePlate);
        entry.setEntryTime(LocalDateTime.now().minusHours(1));
        entry.setChargedAmount(BigDecimal.TEN);

        ParkingSpot spot = new ParkingSpot();
        spot.setLat(BigDecimal.valueOf(-23.5));
        spot.setLng(BigDecimal.valueOf(-46.6));
        spot.setEntryTime(LocalDateTime.now().minusMinutes(50));

        when(entryRepo.findByLicensePlateAndExitTimeIsNull(licensePlate)).thenReturn(Optional.of(entry));
        when(spotRepo.findByLicensePlate(licensePlate)).thenReturn(Optional.of(spot));

        PlateStatusRequest req = new PlateStatusRequest(licensePlate);

        ResponseEntity<PlateStatusResponse> response = service.plateStatus(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(licensePlate, response.getBody().getLicensePlate());
        assertEquals(spot.getLat(), response.getBody().getLat());
        assertEquals(spot.getLng(), response.getBody().getLng());

        verify(entryRepo).findByLicensePlateAndExitTimeIsNull(licensePlate);
        verify(spotRepo).findByLicensePlate(licensePlate);
    }

    @Test
    void testPlateStatus_notFound() {
        String licensePlate = "XYZ-9999";

        when(entryRepo.findByLicensePlateAndExitTimeIsNull(licensePlate)).thenReturn(Optional.empty());

        PlateStatusRequest req = new PlateStatusRequest(licensePlate);

        ResponseEntity<PlateStatusResponse> response = service.plateStatus(req);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(entryRepo).findByLicensePlateAndExitTimeIsNull(licensePlate);
        verifyNoMoreInteractions(spotRepo);
    }

    @Test
    void testSpotStatus_found() {
        BigDecimal lat = BigDecimal.valueOf(-23.5);
        BigDecimal lng = BigDecimal.valueOf(-46.6);

        ParkingSpot spot = new ParkingSpot();
        spot.setOccupied(true);
        spot.setLicensePlate("ABC-1234");
        spot.setEntryTime(LocalDateTime.now().minusMinutes(30));

        when(spotRepo.findByLatAndLng(lat, lng)).thenReturn(Optional.of(spot));

        ParkingSpotStatusRequest req = new ParkingSpotStatusRequest(lat, lng);

        ResponseEntity<SpotStatusResponse> response = service.spotStatus(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isOcupied());
        assertEquals(spot.getLicensePlate(), response.getBody().getLicensePlate());

        verify(spotRepo).findByLatAndLng(lat, lng);
    }

    @Test
    void testSpotStatus_notFound() {
        BigDecimal lat = BigDecimal.valueOf(-10);
        BigDecimal lng = BigDecimal.valueOf(-20);

        when(spotRepo.findByLatAndLng(lat, lng)).thenReturn(Optional.empty());

        ParkingSpotStatusRequest req = new ParkingSpotStatusRequest(lat, lng);

        ResponseEntity<SpotStatusResponse> response = service.spotStatus(req);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(spotRepo).findByLatAndLng(lat, lng);
    }

    @Test
    void testRevenue_found() {
        LocalDate date = LocalDate.now();
        String sector = "A";

        RevenueRecord revenueRecord = new RevenueRecord();
        revenueRecord.setAmount(BigDecimal.valueOf(150.0));
        revenueRecord.setDate(date);
        revenueRecord.setSector(sector);

        when(revenueRepo.findBySectorAndDate(sector, date)).thenReturn(Optional.of(revenueRecord));

        RevenueRequest req = new RevenueRequest(date, sector);

        ResponseEntity<RevenueResponse> response = service.revenue(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(BigDecimal.valueOf(150.0), response.getBody().getAmount());
        assertEquals("BRL", response.getBody().getCurrency());

        verify(revenueRepo).findBySectorAndDate(sector, date);
    }

    @Test
    void testRevenue_notFound() {
        LocalDate date = LocalDate.now();
        String sector = "B";

        when(revenueRepo.findBySectorAndDate(sector, date)).thenReturn(Optional.empty());

        RevenueRequest req = new RevenueRequest(date, sector);

        ResponseEntity<RevenueResponse> response = service.revenue(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(BigDecimal.ZERO, response.getBody().getAmount());
        assertEquals("BRL", response.getBody().getCurrency());

        verify(revenueRepo).findBySectorAndDate(sector, date);
    }
}
