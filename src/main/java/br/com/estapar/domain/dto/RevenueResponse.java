package br.com.estapar.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@Data
@AllArgsConstructor
public class RevenueResponse {
    private BigDecimal amount;
    private String currency;
    private LocalDateTime lastUpdated;
}
