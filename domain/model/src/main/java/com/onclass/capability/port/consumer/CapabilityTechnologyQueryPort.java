package com.onclass.capability.port.consumer;

import com.onclass.capability.model.technology.TechnologySummary;
import reactor.core.publisher.Flux;

public interface CapabilityTechnologyQueryPort {
    Flux<TechnologySummary> getTechnologiesByCapabilityId(Long capabilityId);
}
