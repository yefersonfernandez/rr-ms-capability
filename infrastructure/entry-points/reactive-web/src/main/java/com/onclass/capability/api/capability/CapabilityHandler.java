package com.onclass.capability.api.capability;

import com.onclass.capability.api.dto.request.CapabilityRequestDto;
import com.onclass.capability.api.mapper.CapabilityMapper;
import com.onclass.capability.api.utils.HandlersResponseUtil;
import com.onclass.capability.api.utils.ValidatorUtil;
import com.onclass.capability.enums.ExceptionStatusCode;
import com.onclass.capability.usecase.capability.CapabilityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapabilityHandler {

    private final CapabilityUseCase capabilityUseCase;
    private final CapabilityMapper capabilityMapper;
    private final ValidatorUtil validatorUtil;

    public Mono<ServerResponse> listenSaveCapability(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CapabilityRequestDto.class)
                .doOnNext(capabilityRequestDto -> log.info("Received capability request: {}", capabilityRequestDto))
                .flatMap(validatorUtil::validate)
                .map(capabilityMapper::toModel)
                .flatMap(capabilityUseCase::saveCapability)
                .map(capabilityMapper::toCapabilityResponseDto)
                .flatMap(savedCapability -> ServerResponse.created(URI.create(""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(HandlersResponseUtil.buildBodySuccessResponse(ExceptionStatusCode.CREATED.status(), savedCapability))
                );
    }
}
