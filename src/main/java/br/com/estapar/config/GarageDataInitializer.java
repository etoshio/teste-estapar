package br.com.estapar.config;

import br.com.estapar.client.GarageClient;
import br.com.estapar.service.GarageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GarageDataInitializer implements ApplicationRunner {

    private final GarageClient garageClient;
    private final GarageService garageService;

    @Override
    public void run(ApplicationArguments args) {
       // garageService.importGarage(garageClient.getGarageData());
    }
}
