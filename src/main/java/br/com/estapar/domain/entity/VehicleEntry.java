package br.com.estapar.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "vehicle_entry", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String sector;
    private BigDecimal chargedAmount;
}
