package br.com.estapar.repository;

import br.com.estapar.domain.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    BigDecimal countBySectorAndOccupiedTrue(String sector);
    BigDecimal countBySector(String sector);
    Optional<ParkingSpot> findByLatAndLng(BigDecimal lat, BigDecimal lng);
    Optional<ParkingSpot> findByLicensePlate(String licensePlate);
}