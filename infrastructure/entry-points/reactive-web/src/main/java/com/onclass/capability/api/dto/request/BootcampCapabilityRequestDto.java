package com.onclass.capability.api.dto.request;


import java.util.List;

public record BootcampCapabilityRequestDto(
        Long bootcampId,
        List<Long> capabilityIds
) {}
