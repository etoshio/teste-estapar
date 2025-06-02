package br.com.estapar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Estapar - Gestão de Estacionamentos")
                        .description("API para gerenciamento de entradas, saídas, vagas e faturamento")
                        .version("1.0.0"));
    }
}