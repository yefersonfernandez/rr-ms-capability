package com.onclass.capability.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO representing a Technology summary")
public record TechnologySummaryDto(
        @Schema(description = "Unique identifier of the technology", example = "1")
        Long id,
        @Schema(description = "Name of the technology", example = "Java")
        String name
) {}

