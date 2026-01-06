package com.onclass.capability.consumer.dto.request;

import java.util.List;

public record AssociationRequestDto(Long capabilityId, List<Long> technologyIds) {}

