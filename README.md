# Teste Estapar #

# Sobre o projeto
Este é o teste para Desevolvedor Java/Kotlin da Estapar.

O objetivo é criar um sistema de gestão de estacionamentos, que controla o número de vagas em aberto,
entrada, saida e faturameto do setor.

O teste não precisa estar 100% completo, iremos avaliar até o ponto onde você conseguiu chegar.

# Tecnologias utilizadas
## Back end
- Java 17
- Spring Boot 3.4.5
- Spring JPA / Hibernate
- Lombok
- Maven
- OpenApi/Swagger
- Banco de Dados Postgre
- Docker

# Como executar o projeto
## Back end
Pré-requisitos: Java 17 e Docker

```bash
Ao executar o sistema rodar o docker embarcado.

# executar o comando
mvn clean install

# rodar aplicação
mvn spring-boot:run

# abrindo o openapi da aplicação no navegador (Chrome/FireFox)
http://localhost:3003/swagger-ui/index.html

```

