package com.onclass.capability.api.bootcampcapability;

import com.onclass.capability.api.config.BootcampCapabilityPath;
import com.onclass.capability.api.openapi.BootcampCapabilityOpenApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
@RequiredArgsConstructor
public class BootcampCapabilityRouterRest {
    private final BootcampCapabilityPath bootcampCapabilityPath;

    @Bean
    public RouterFunction<ServerResponse> routerFunctionBootcampCapability(BootcampCapabilityHandler handler) {
        return route()
                .POST(bootcampCapabilityPath.getAssociateCapabilities(), handler::listenAssociateCapabilities, BootcampCapabilityOpenApi::associateCapabilities)
                .GET(bootcampCapabilityPath.getCapabilitiesByBootcampId(), handler::listenGetCapabilitiesByBootcampId, BootcampCapabilityOpenApi::getCapabilitiesByBootcampId)
                .DELETE(bootcampCapabilityPath.getDeleteBootcampCascade(), handler::listenDeleteAssociatedDataByBootcampId, BootcampCapabilityOpenApi::deleteAssociatedDataByBootcampId)
                .build();
    }
}
