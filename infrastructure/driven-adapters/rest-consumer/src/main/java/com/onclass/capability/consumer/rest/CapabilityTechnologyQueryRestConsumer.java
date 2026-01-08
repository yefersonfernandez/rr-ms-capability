package com.onclass.capability.consumer.rest;

import com.onclass.capability.consumer.dto.response.TechnologyListResponseDto;
import com.onclass.capability.exceptions.TechnologyMicroserviceException;
import com.onclass.capability.model.technology.TechnologySummary;
import com.onclass.capability.port.consumer.CapabilityTechnologyQueryPort;
import com.onclass.capability.enums.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapabilityTechnologyQueryRestConsumer implements CapabilityTechnologyQueryPort {
    private static final String GET_TECHNOLOGIES_URL = "/technology/api/v1/capabilities/{capabilityId}/technologies";
    private final WebClient technologyWebClient;

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
}
