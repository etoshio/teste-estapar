package br.com.estapar.controller;

import br.com.estapar.domain.dto.WebhookEventDTO;
import br.com.estapar.domain.enums.EventType;
import br.com.estapar.domain.exceptions.BadRequestException;
import br.com.estapar.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Tag(name = "Webwook", description = "APIs para consumir a entrada, estacionado e saída")
public class WebhookController {

    private final ParkingService parkingService;

    @PostMapping
    @Operation(summary = "Evento de entrada, estacionado e saída", description = "Operação para evento de entrada, estacionado e saída",
            responses = {@ApiResponse(responseCode = "200", description = "Operação bem-sucedida")})
    public ResponseEntity<String> event(@Valid @RequestBody WebhookEventDTO dto) {
        EventType type = dto.getEventType();

        switch (type) {
            case ENTRY -> parkingService.entry(dto.getLicensePlate(), dto.getEntryTime());
            case PARKED -> parkingService.parked(dto.getLicensePlate(), dto.getLat(), dto.getLng());
            case EXIT -> parkingService.exit(dto.getLicensePlate(), dto.getExitTime());
            default -> throw new BadRequestException("Evento desconhecido: " + type);
        }

        return ResponseEntity.ok("Evento " + type + " processado com sucesso");
    }
}
