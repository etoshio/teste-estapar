package br.com.estapar.domain.dto;

import lombok.*;

import java.time.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueRequest {
    private LocalDate date;
    private String sector;
}