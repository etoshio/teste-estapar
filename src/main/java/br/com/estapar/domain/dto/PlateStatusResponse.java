package br.com.estapar.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Data
public class PlateStatusResponse {
    private String licensePlate;
    private LocalDateTime entryTime;
    private BigDecimal priceUntilNow;
    private BigDecimal lat;
    private BigDecimal lng;
    private LocalDateTime timeParked;
}
