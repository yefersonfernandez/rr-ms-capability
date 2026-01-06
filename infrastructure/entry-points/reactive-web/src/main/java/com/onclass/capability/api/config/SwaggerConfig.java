package com.onclass.capability.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Capability Microservice",
                description = "Manages capabilities and their association with bootcamps."
        )
)
public class SwaggerConfig {}
