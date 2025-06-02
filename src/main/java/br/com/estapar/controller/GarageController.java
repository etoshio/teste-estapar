package br.com.estapar.controller;

import br.com.estapar.domain.dto.GarageDto;
import br.com.estapar.domain.dto.GarageResponse;
import br.com.estapar.domain.dto.SpotDto;
import br.com.estapar.service.GarageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/garage")
@RequiredArgsConstructor
@Tag(name = "Garage", description = "APIs para gerenciamento garage e parking spot")
public class GarageController {

    private final GarageService service;

    @PostMapping
    @Operation(summary = "Importar garage a partir de um json", description = "Operação para importar garage a partir de um json",
            responses = {@ApiResponse(responseCode = "200", description = "Operação bem-sucedida")})
    public void importarGarage(@RequestBody GarageResponse dto) {
        service.importGarage(dto);
    }

    @GetMapping(value = "/carga")
    @Operation(summary = "Importar garage a partir de um client", description = "Operação para importar garage a partir de um client",
            responses = {@ApiResponse(responseCode = "200", description = "Operação bem-sucedida")})
    public void getGarageData() {
        service.getGarage();
    }

    @GetMapping
    @Operation(summary = "Dado utilizado que será chamado pelo cliente", description = "Dado utilizado que será chamado pelo cliente",
            responses = {@ApiResponse(responseCode = "200", description = "Operação bem-sucedida")})
    public GarageResponse getGarage() {
        List<GarageDto> garage = List.of(
                new GarageDto("A", new BigDecimal(10.0), 100, "08:00", "22:00", 240),
                new GarageDto("B", new BigDecimal(4.0), 72, "05:00", "18:00", 120)
        );

        List<SpotDto> spots = List.of(
                new SpotDto(1, "A", new BigDecimal(-23.561684), new BigDecimal(-46.655981)),
                new SpotDto(2, "B",  new BigDecimal(-23.561674),  new BigDecimal(-46.655971))
        );

        return new GarageResponse(garage, spots);
    }
}

