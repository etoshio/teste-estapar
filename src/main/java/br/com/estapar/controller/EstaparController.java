package br.com.estapar.controller;

import br.com.estapar.domain.dto.*;
import br.com.estapar.service.EstaparService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Estapar", description = "APIs para consultar a plate-status, spot-status e revenue")
public class EstaparController {

    private final EstaparService service;

    @PostMapping("/plate-status")
    @Operation(summary = "Consulta pela placa", description = "Operação para consultar a placa",
            responses = {@ApiResponse(responseCode = "200", description = "Operação bem-sucedida")})
    public ResponseEntity<PlateStatusResponse> plateStatus(@RequestBody PlateStatusRequest plateStatusRequest) {
        return service.plateStatus(plateStatusRequest);
    }

    @PostMapping("/spot-status")
    @Operation(summary = "Consultar pela vaga", description = "Operação para consultar a vaga",
            responses = {@ApiResponse(responseCode = "200", description = "Operação bem-sucedida")})
    public ResponseEntity<SpotStatusResponse> spotStatus(@RequestBody ParkingSpotStatusRequest parkingSpotStatusRequest) {
        return service.spotStatus(parkingSpotStatusRequest);
    }

    @PostMapping("/revenue")
    @Operation(summary = "Consultar o faturamento pela vaga/data", description = "Operação para consultar o faturamento pela vaga/data",
            responses = {@ApiResponse(responseCode = "200", description = "Operação bem-sucedida")})
    public ResponseEntity<RevenueResponse> revenue(@RequestBody RevenueRequest revenueRequest) {
        return service.revenue(revenueRequest);
    }
}
