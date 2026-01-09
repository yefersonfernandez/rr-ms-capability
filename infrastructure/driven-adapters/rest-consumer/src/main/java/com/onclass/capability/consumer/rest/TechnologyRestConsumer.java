package com.onclass.capability.consumer.rest;

import com.onclass.capability.consumer.dto.request.AssociationRequestDto;
import com.onclass.capability.consumer.dto.response.TechnologyListResponseDto;
import com.onclass.capability.enums.ExceptionMessages;
import com.onclass.capability.exceptions.CapabilityTechnologiesCountException;
import com.onclass.capability.exceptions.RepeatedTechnologiesException;
import com.onclass.capability.exceptions.TechnologyMicroserviceException;
import com.onclass.capability.exceptions.TechnologyNotFoundException;
import com.onclass.capability.model.technology.TechnologySummary;
import com.onclass.capability.port.consumer.TechnologyConsumerPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechnologyRestConsumer implements TechnologyConsumerPort {
    private static final String ASSOCIATE_TECHNOLOGIES_URL = "/technology/api/v1/capabilities/associate-technologies";
    private static final String GET_TECHNOLOGIES_URL = "/technology/api/v1/capabilities/{capabilityId}/technologies";
    private static final String DELETE_TECH_URL = "/technology/api/v1/capabilities";

    private final WebClient technologyWebClient;

    @CircuitBreaker(name = "associateTechnologiesCB")
    public Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds) {
        return technologyWebClient.post()
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

    @Override
    public Flux<TechnologySummary> getTechnologiesByCapabilityId(Long capabilityId) {
        return technologyWebClient.get()
                .uri(GET_TECHNOLOGIES_URL, capabilityId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new TechnologyMicroserviceException(
                                        ExceptionMessages.WEB_CLIENT_INTERNAL_SERVER_ERROR.format(body)
                                ))
                        )
                )
                .bodyToMono(TechnologyListResponseDto.class)
                .flatMapMany(response -> Flux.fromIterable(response.data() != null ? response.data() : List.of()));
    }

    @Override
    public Mono<Void> deleteTechnologiesByCapabilityIds(List<Long> capabilityIds) {
        return technologyWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_TECH_URL)
                        .queryParam("ids", capabilityIds)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new TechnologyMicroserviceException(
                                        ExceptionMessages.WEB_CLIENT_INTERNAL_SERVER_ERROR.format(body)))
                        )
                )
                .toBodilessEntity()
                .then();
    }

}
