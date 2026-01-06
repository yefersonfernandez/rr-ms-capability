package com.onclass.capability.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response DTO representing a Capability with its technologies")
public record CapabilityWithTechnologiesResponseDto(
        @Schema(description = "Unique identifier of the capability", example = "1")
        Long id,
        @Schema(description = "Name of the capability", example = "Reactive Spring")
        String name,
        @Schema(description = "Description of the capability", example = "A reactive framework for Spring applications")
        String description,
        @Schema(description = "List of associated technologies (id and name only)")
        List<TechnologySummaryDto> technologies
) {}

