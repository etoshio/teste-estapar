package br.com.estapar.repository;

import br.com.estapar.domain.entity.RevenueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RevenueRecordRepository extends JpaRepository<RevenueRecord, Long> {
    Optional<RevenueRecord> findBySectorAndDate(String sector, LocalDate date);
}