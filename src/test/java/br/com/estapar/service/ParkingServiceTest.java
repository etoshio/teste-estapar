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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private VehicleEntryRepository vehicleEntryRepository;

    @Mock
    private GarageSectorRepository garageSectorRepository;

    @Mock
    private RevenueRecordRepository revenueRecordRepository;

    private GarageSector sector;

    @BeforeEach
    void setUp() {
        sector = GarageSector.builder()
                .sector("A")
                .maxCapacity(10)
                .basePrice(new BigDecimal("10.00"))
                .durationLimitMinutes(60)
                .build();
    }

    @Test
    void testEntry_success() {
        String licensePlate = "ABC1234";
        LocalDateTime entryTime = LocalDateTime.now();

        when(vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(licensePlate))
                .thenReturn(Optional.empty());

        when(garageSectorRepository.findAll())
                .thenReturn(List.of(sector));

        when(parkingSpotRepository.countBySector(sector.getSector())).thenReturn(BigDecimal.valueOf(10));
        when(parkingSpotRepository.countBySectorAndOccupiedTrue(sector.getSector())).thenReturn(BigDecimal.valueOf(5));

        parkingService.entry(licensePlate, entryTime);

        ArgumentCaptor<VehicleEntry> captor = ArgumentCaptor.forClass(VehicleEntry.class);
        verify(vehicleEntryRepository, times(1)).save(captor.capture());

        VehicleEntry savedEntry = captor.getValue();
        assertEquals(licensePlate, savedEntry.getLicensePlate());
        assertEquals(entryTime, savedEntry.getEntryTime());
        assertEquals("A", savedEntry.getSector());
        assertNotNull(savedEntry.getChargedAmount());
    }

    @Test
    void testEntry_activeEntryExists_throwsBadRequest() {
        String licensePlate = "XYZ9999";

        when(vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(licensePlate))
                .thenReturn(Optional.of(VehicleEntry.builder().build()));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> parkingService.entry(licensePlate, LocalDateTime.now()));

        assertTrue(ex.getMessage().contains("Já existe uma entrada ativa"));
        verify(vehicleEntryRepository, never()).save(any());
    }

    @Test
    void testEntry_allSectorsFull_throwsBadRequest() {
        String licensePlate = "FULL000";

        when(vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(licensePlate))
                .thenReturn(Optional.empty());

        GarageSector fullSector = GarageSector.builder()
                .sector("Full")
                .maxCapacity(2)
                .basePrice(new BigDecimal("10.00"))
                .durationLimitMinutes(60)
                .build();

        when(garageSectorRepository.findAll()).thenReturn(List.of(fullSector));
        when(parkingSpotRepository.countBySector(fullSector.getSector())).thenReturn(BigDecimal.valueOf(2));
        when(parkingSpotRepository.countBySectorAndOccupiedTrue(fullSector.getSector())).thenReturn(BigDecimal.valueOf(2)); // cheio

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> parkingService.entry(licensePlate, LocalDateTime.now()));

        assertTrue(ex.getMessage().contains("Todos os setores estão ocupados"));
        verify(vehicleEntryRepository, never()).save(any());
    }

    @Test
    void testParked_success() {
        String licensePlate = "PARK123";
        BigDecimal lat = BigDecimal.valueOf(-23.5);
        BigDecimal lng = BigDecimal.valueOf(-46.6);

        ParkingSpot spot = ParkingSpot.builder()
                .occupied(false)
                .lat(lat)
                .lng(lng)
                .build();

        when(parkingSpotRepository.findByLatAndLng(lat, lng))
                .thenReturn(Optional.of(spot));

        parkingService.parked(licensePlate, lat, lng);

        assertTrue(spot.getOccupied());
        assertEquals(licensePlate, spot.getLicensePlate());
        assertNotNull(spot.getEntryTime());

        verify(parkingSpotRepository).save(spot);
    }

    @Test
    void testParked_spotNotFound_throwsNotFound() {
        when(parkingSpotRepository.findByLatAndLng(any(), any()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> parkingService.parked("LP123", BigDecimal.ONE, BigDecimal.ONE));

        assertTrue(ex.getMessage().contains("Vaga não encontrada"));
    }

    @Test
    void testParked_spotAlreadyOccupied_throwsBadRequest() {
        ParkingSpot spot = ParkingSpot.builder()
                .occupied(true)
                .build();

        when(parkingSpotRepository.findByLatAndLng(any(), any()))
                .thenReturn(Optional.of(spot));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> parkingService.parked("LP123", BigDecimal.ONE, BigDecimal.ONE));

        assertTrue(ex.getMessage().contains("Vaga já está ocupada"));
    }

    @Test
    void testExit_success() {
        String licensePlate = "EXIT123";
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(90);
        LocalDateTime exitTime = LocalDateTime.now();

        VehicleEntry vehicleEntry = VehicleEntry.builder()
                .licensePlate(licensePlate)
                .entryTime(entryTime)
                .chargedAmount(new BigDecimal("10.00"))
                .sector("A")
                .build();

        GarageSector sector = GarageSector.builder()
                .sector("A")
                .durationLimitMinutes(60)
                .build();

        ParkingSpot spot = ParkingSpot.builder()
                .occupied(true)
                .licensePlate(licensePlate)
                .build();

        RevenueRecord revenueRecord = RevenueRecord.builder()
                .date(exitTime.toLocalDate())
                .sector("A")
                .amount(BigDecimal.TEN)
                .build();

        when(vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(licensePlate))
                .thenReturn(Optional.of(vehicleEntry));

        when(garageSectorRepository.findById("A")).thenReturn(Optional.of(sector));

        when(parkingSpotRepository.findByLicensePlate(licensePlate))
                .thenReturn(Optional.of(spot));

        when(revenueRecordRepository.findBySectorAndDate("A", exitTime.toLocalDate()))
                .thenReturn(Optional.of(revenueRecord));

        parkingService.exit(licensePlate, exitTime);

        assertEquals(exitTime, vehicleEntry.getExitTime());
        assertTrue(vehicleEntry.getChargedAmount().compareTo(new BigDecimal("10.00")) >= 0);

        assertFalse(spot.getOccupied());
        assertNull(spot.getLicensePlate());
        assertNull(spot.getEntryTime());

        verify(vehicleEntryRepository).save(vehicleEntry);
        verify(parkingSpotRepository).save(spot);
        verify(revenueRecordRepository).save(revenueRecord);
    }

    @Test
    void testExit_noActiveEntry_throwsNotFound() {
        when(vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(any()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> parkingService.exit("LP999", LocalDateTime.now()));

        assertTrue(ex.getMessage().contains("Entrada ativa não encontrada"));
    }

    @Test
    void testExit_sectorNotFound_throwsNotFound() {
        VehicleEntry entry = VehicleEntry.builder()
                .licensePlate("LP999")
                .entryTime(LocalDateTime.now().minusMinutes(10))
                .chargedAmount(new BigDecimal("10.00"))
                .sector("X")
                .build();

        when(vehicleEntryRepository.findByLicensePlateAndExitTimeIsNull(any()))
                .thenReturn(Optional.of(entry));

        when(garageSectorRepository.findById("X")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> parkingService.exit("LP999", LocalDateTime.now()));

        assertTrue(ex.getMessage().contains("Setor 'X' não encontrado"));
    }
}
