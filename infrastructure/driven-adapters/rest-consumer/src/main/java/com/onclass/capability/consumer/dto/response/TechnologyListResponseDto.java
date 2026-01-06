package com.onclass.capability.consumer.dto.response;

import com.onclass.capability.model.technology.TechnologySummary;
import java.util.List;

public record TechnologyListResponseDto(List<TechnologySummary> data) {}
