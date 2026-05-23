package com.grupo10.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.cloud.gateway.enabled=true")
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @Test
    void protectedRoute_should_Retornar401_when_SinJwt() {
        webTestClient.get()
                .uri("/api/v1/pedidos")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedRoute_should_Retornar401_when_SinJwt_inventory() {
        webTestClient.get()
                .uri("/api/v1/stock/lotes/cualquier-id")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedRoute_should_Retornar401_when_SinJwt_devoluciones() {
        webTestClient.get()
                .uri("/api/v1/devoluciones")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void actuator_should_Retornar200_when_SinJwt() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                // Puede ser 200 o 503 (downstream no disponible), nunca 401
                .expectStatus().value(status -> {
                    assert status != 401 : "Actuator no debe requerir JWT";
                });
    }
}
