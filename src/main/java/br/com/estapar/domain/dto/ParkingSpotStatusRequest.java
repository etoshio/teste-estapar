package br.com.estapar.domain.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotStatusRequest {
    private BigDecimal lat;
    private BigDecimal lng;
}