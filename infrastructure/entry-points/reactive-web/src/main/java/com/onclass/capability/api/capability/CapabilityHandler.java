package com.onclass.capability.api.capability;

import com.onclass.capability.api.dto.request.CapabilityRequestDto;
import com.onclass.capability.api.mapper.CapabilityMapper;
import com.onclass.capability.api.utils.ValidatorUtil;
import com.onclass.capability.enums.ExceptionStatusCode;
import com.onclass.capability.usecase.capability.CapabilityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.net.URI;

import static com.onclass.capability.api.utils.HandlersResponseUtil.buildBodySuccessResponse;

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
                        .bodyValue(buildBodySuccessResponse(ExceptionStatusCode.CREATED.status(), savedCapability))
                );
    }

    public Mono<ServerResponse> listenListCapabilities(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String order = request.queryParam("order").orElse("asc");

        log.info("[HANDLER] listenListCapabilities called with page={}, size={}, sortBy={}, order={}", page, size, sortBy, order);

        return capabilityUseCase.getCapabilitiesWithTechnologies(page, size, sortBy, order)
                .map(capabilityMapper::toCapabilityWithTechnologiesResponseDto)
                .collectList()
                .doOnNext(dtoList -> log.info("[HANDLER] Capabilities mapped: {}", dtoList))
                .map(dtoList -> new PageImpl<>(dtoList, PageRequest.of(page, size), dtoList.size()))
                .flatMap(pageResult -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildBodySuccessResponse(ExceptionStatusCode.OK.status(), pageResult))
                );
    }
}
