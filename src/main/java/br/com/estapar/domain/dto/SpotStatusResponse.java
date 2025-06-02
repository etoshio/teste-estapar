package br.com.estapar.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Data
public class SpotStatusResponse {
    private boolean ocupied;
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime timeParked;
    private BigDecimal priceUntilNow;
}
