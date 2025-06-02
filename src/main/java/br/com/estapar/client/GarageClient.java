package br.com.estapar.client;

import br.com.estapar.domain.dto.GarageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "garageClient", url = "http://localhost:3003")
public interface GarageClient {

    @GetMapping("/garage")
    GarageResponse getGarageData();
}
