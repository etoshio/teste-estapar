package br.com.estapar.repository;

import br.com.estapar.domain.entity.VehicleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleEntryRepository extends JpaRepository<VehicleEntry, Long> {
    Optional<VehicleEntry> findByLicensePlateAndExitTimeIsNull(String licensePlate);
}