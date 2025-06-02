package br.com.estapar.domain.dto;

import br.com.estapar.domain.enums.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WebhookEventDTO {
    @JsonProperty("event_type")
    private EventType eventType;
    @JsonProperty("license_plate")
    private String licensePlate;
    private BigDecimal lat;
    private BigDecimal lng;
    @JsonProperty("entry_time")
    private LocalDateTime entryTime;
    @JsonProperty("exit_time")
    private LocalDateTime exitTime;
}
