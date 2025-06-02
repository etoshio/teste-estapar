package br.com.estapar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TesteEstaparApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesteEstaparApplication.class, args);
	}

}
