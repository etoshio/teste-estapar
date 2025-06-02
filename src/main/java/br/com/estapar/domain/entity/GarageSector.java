package br.com.estapar.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "garage_sector", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarageSector {
    @Id
    private String sector;

    private BigDecimal basePrice;
    private Integer maxCapacity;
    private LocalTime openHour;
    private LocalTime closeHour;
    private Integer durationLimitMinutes;
}


