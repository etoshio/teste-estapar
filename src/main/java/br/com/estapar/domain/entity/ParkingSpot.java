package br.com.estapar.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "parking_spot", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sector;
    @Column(precision = 9, scale = 6)
    private BigDecimal lat;
    @Column(precision = 9, scale = 6)
    private BigDecimal lng;
    private Boolean occupied;
    private String licensePlate;
    private LocalDateTime entryTime;
}
