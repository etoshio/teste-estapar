package br.com.estapar.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GarageDto(
        String sector,
        @JsonProperty("basePrice") BigDecimal basePrice,
        @JsonProperty("max_capacity") Integer maxCapacity,
        @JsonProperty("open_hour") String openHour,
        @JsonProperty("close_hour") String closeHour,
        @JsonProperty("duration_limit_minutes") Integer durationLimitMinutes
) {}

