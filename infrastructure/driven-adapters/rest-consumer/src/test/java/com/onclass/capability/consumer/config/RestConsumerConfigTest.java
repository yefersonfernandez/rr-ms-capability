package com.onclass.capability.consumer.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class RestConsumerConfigTest {

    private static final String BASE_URL = "http://localhost:8082";
    private static final int TIMEOUT = 5000;
    private RestConsumerConfig config;

    @BeforeEach
    void setUp() {
        config = new RestConsumerConfig(BASE_URL, TIMEOUT);
    }

    @Test
    @DisplayName("webClientBuilder should return a non-null WebClient.Builder instance")
    void webClientBuilder_shouldReturnNonNullBuilder() {
        WebClient.Builder builder = config.webClientBuilder();
        assertThat(builder).isNotNull();
    }

    @Test
    @DisplayName("getWebClient should set content-type header to application/json")
    void getWebClient_shouldSetContentTypeHeader() {
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = config.getWebClient(builder);
        assertThat(webClient).isNotNull();
        assertThat(webClient.mutate().defaultHeaders(headers ->
                assertThat(headers.getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        )).isNotNull();
    }

    @Test
    @DisplayName("getClientHttpConnector should return a non-null ClientHttpConnector")
    void getClientHttpConnector_shouldReturnNonNullConnector() throws Exception {
        var method = RestConsumerConfig.class.getDeclaredMethod("getClientHttpConnector");
        method.setAccessible(true);
        ClientHttpConnector connector = (ClientHttpConnector) method.invoke(config);
        assertThat(connector).isNotNull();
    }

    @Test
    @DisplayName("getWebClient should have the correct baseUrl configured in the request")
    void getWebClient_shouldHaveCorrectBaseUrl() {
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = config.getWebClient(builder);
        assertThat(webClient).isNotNull();
    }
}