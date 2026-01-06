package com.onclass.capability.consumer;

import com.onclass.capability.exceptions.CapabilityTechnologiesCountException;
import com.onclass.capability.exceptions.RepeatedTechnologiesException;
import com.onclass.capability.exceptions.TechnologyNotFoundException;
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
import java.util.List;

class TechnologyAssociationRestConsumerTest {

    private static TechnologyAssociationRestConsumer consumer;
    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        consumer = new TechnologyAssociationRestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Successful association returns completed Mono<Void>")
    void associateTechnologies_success() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value()));
        var result = consumer.associateTechnologies(1L, List.of(1L, 2L, 3L));
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    @DisplayName("Invalid count returns CapabilityTechnologiesCountException")
    void associateTechnologies_invalidCount() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setBody("Invalid count"));
        var result = consumer.associateTechnologies(1L, List.of(1L));
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> {
                    assert e instanceof CapabilityTechnologiesCountException;
                    assert e.getMessage().contains("Invalid count");
                })
                .verify();
    }

    @Test
    @DisplayName("Repeated technologies returns RepeatedTechnologiesException")
    void associateTechnologies_repeated() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CONFLICT.value())
                .setBody("Repeated technologies"));
        var result = consumer.associateTechnologies(1L, List.of(1L, 1L, 2L));
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> {
                    assert e instanceof RepeatedTechnologiesException;
                    assert e.getMessage().contains("Repeated technologies");
                })
                .verify();
    }

    @Test
    @DisplayName("Technologies not found returns TechnologyNotFoundException")
    void associateTechnologies_notFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("Not found"));
        var result = consumer.associateTechnologies(1L, List.of(99L));
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> {
                    assert e instanceof TechnologyNotFoundException;
                    assert e.getMessage().contains("Not found");
                })
                .verify();
    }

    @Test
    @DisplayName("Internal server error returns RuntimeException")
    void associateTechnologies_serverError() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Server error"));
        var result = consumer.associateTechnologies(1L, List.of(1L, 2L, 3L));
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> {
                    assert e instanceof RuntimeException;
                    assert e.getMessage().contains("Server error");
                })
                .verify();
    }
}
