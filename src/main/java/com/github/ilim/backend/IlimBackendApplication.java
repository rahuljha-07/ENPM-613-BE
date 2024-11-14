package com.github.ilim.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class IlimBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IlimBackendApplication.class, args);
    }

    // General Beans
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

}
