package br.com.estapar.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "revenue_record", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String sector;
    private BigDecimal amount;
}
