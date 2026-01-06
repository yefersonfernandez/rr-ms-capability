package com.onclass.capability.consumer;

import com.onclass.capability.consumer.dto.request.AssociationRequestDto;
import com.onclass.capability.exceptions.CapabilityTechnologiesCountException;
import com.onclass.capability.exceptions.RepeatedTechnologiesException;
import com.onclass.capability.exceptions.TechnologyNotFoundException;
import com.onclass.capability.port.consumer.TechnologyAssociationConsumerPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechnologyAssociationRestConsumer implements TechnologyAssociationConsumerPort {
    private static final String ASSOCIATE_TECHNOLOGIES_URL = "/technology/api/v1/capabilities/associate-technologies";

    private final WebClient client;

    @CircuitBreaker(name = "associateTechnologiesCB")
    public Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds) {
        return client.post()
                .uri(ASSOCIATE_TECHNOLOGIES_URL)
                .bodyValue(new AssociationRequestDto(capabilityId, technologyIds))
                .retrieve()
                .onStatus(status -> status.value() == 400, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new CapabilityTechnologiesCountException(body)))
                )
                .onStatus(status -> status.value() == 409, response ->
                        response.bodyToMono(String.class).flatMap(body -> Mono.error(new RepeatedTechnologiesException(body)))
                )
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(String.class).flatMap(body -> Mono.error(new TechnologyNotFoundException(body)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class).flatMap(body -> Mono.error(new RuntimeException(body)))
                )
                .toBodilessEntity()
                .then();
    }
}
