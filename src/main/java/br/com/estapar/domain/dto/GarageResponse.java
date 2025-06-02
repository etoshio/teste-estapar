package br.com.estapar.domain.dto;

import java.util.List;

public record GarageResponse(
        List<GarageDto> garage,
        List<SpotDto> spots
) {}
