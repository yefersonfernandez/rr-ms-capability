package com.onclass.capability.api.capability;

import com.onclass.capability.api.config.CapabilityPath;
import com.onclass.capability.api.openapi.CapabilityOpenApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
@RequiredArgsConstructor
public class CapabilityRouterRest {

    private final CapabilityPath capabilityPath;
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CapabilityHandler handler) {
        return route()
                .POST(capabilityPath.getCapabilities(), handler::listenSaveCapability, CapabilityOpenApi::saveCapability)
                .build();
    }
}
