package com.onclass.capability.consumer.rest;

import com.onclass.capability.exceptions.TechnologyMicroserviceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import java.io.IOException;

import static com.onclass.capability.enums.ExceptionMessages.WEB_CLIENT_INTERNAL_SERVER_ERROR;

class CapabilityTechnologyQueryRestConsumerTest {
    private static CapabilityTechnologyQueryRestConsumer consumer;
    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        consumer = new CapabilityTechnologyQueryRestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Successful technology query returns TechnologySummary list")
    void getTechnologiesByCapabilityId_success() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"code\":200,\"data\":[{\"id\":1,\"name\":\"Java\"},{\"id\":2,\"name\":\"Spring Boot\"}]}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(consumer.getTechnologiesByCapabilityId(123L))
                .expectNextMatches(t -> t.getId().equals(1L) && t.getName().equals("Java"))
                .expectNextMatches(t -> t.getId().equals(2L) && t.getName().equals("Spring Boot"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Empty technology list returns empty Flux")
    void getTechnologiesByCapabilityId_empty() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"code\":200,\"data\":[]}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(consumer.getTechnologiesByCapabilityId(999L))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Internal server error returns TechnologyMicroserviceException")
    void getTechnologiesByCapabilityId_serverError() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Server error")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(consumer.getTechnologiesByCapabilityId(123L))
                .expectErrorSatisfies(e -> {
                    assert e instanceof TechnologyMicroserviceException;
                    assert e.getMessage().contains(WEB_CLIENT_INTERNAL_SERVER_ERROR.getMessage());
                })
                .verify();
    }
}
