package br.com.estapar.domain.dto;

import java.math.BigDecimal;

public record SpotDto(
        long id,
        String sector,
        BigDecimal lat,
        BigDecimal lng
) {}
