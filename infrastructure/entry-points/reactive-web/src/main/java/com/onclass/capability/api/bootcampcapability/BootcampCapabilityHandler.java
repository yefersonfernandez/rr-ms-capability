package com.onclass.capability.api.bootcampcapability;
import com.onclass.capability.api.dto.request.BootcampCapabilityRequestDto;
import com.onclass.capability.api.mapper.CapabilityMapper;
import com.onclass.capability.enums.ExceptionStatusCode;
import com.onclass.capability.usecase.bootcampcapability.BootcampCapabilityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class BootcampCapabilityHandler {
    private final BootcampCapabilityUseCase bootcampCapabilityUseCase;
    private final CapabilityMapper capabilityMapper;

    public Mono<ServerResponse> listenAssociateCapabilities(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BootcampCapabilityRequestDto.class)
                .doOnNext(request -> log.info("Received bootcamp-capability association request: {}", request))
                .flatMap(request ->
                        bootcampCapabilityUseCase.associateCapabilities(request.bootcampId(), request.capabilityIds())
                )
                .then(ServerResponse.created(URI.create(""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildBodySuccessResponse(ExceptionStatusCode.CREATED.status(), null))
                );
    }

    public Mono<ServerResponse> listenGetCapabilitiesByBootcampId(ServerRequest request) {
        Long bootcampId = Long.valueOf(request.pathVariable("bootcampId"));
        return bootcampCapabilityUseCase.getCapabilitiesByBootcampId(bootcampId)
                .map(capabilityMapper::toCapabilitySummaryResponseDto)
                .collectList()
                .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildBodySuccessResponse(ExceptionStatusCode.OK.status(), list)));
    }

    public Mono<ServerResponse> listenDeleteAssociatedDataByBootcampId(ServerRequest request) {
        Long bootcampId = Long.valueOf(request.pathVariable("bootcampId"));
        log.info("[HANDLER] Received request to delete bootcamp with id: {}", bootcampId);
        return bootcampCapabilityUseCase.deleteAssociatedDataByBootcampId(bootcampId)
                .then(ServerResponse.noContent().build())
                .doOnSuccess(resp -> log.info("[HANDLER] Bootcamp {} Delete associated successfully (cascade)", bootcampId))
                .doOnError(e -> log.error("[HANDLER] Error deleting bootcamp {}: {}", bootcampId, e.getMessage()));
    }

}
