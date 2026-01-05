package com.onclass.capability.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO representing a Capability")
public record CapabilityResponseDto(

        @Schema(description = "Unique identifier of the capability", example = "1")
        Long id,

        @Schema(description = "Name of the capability", example = "Reactive Spring")
        String name,

        @Schema(description = "Description of the capability", example = "A reactive framework for Spring applications")
        String description
) {}
