package br.com.estapar.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlateStatusRequest {
    @JsonProperty("license_plate")
    private String licensePlate;
}
