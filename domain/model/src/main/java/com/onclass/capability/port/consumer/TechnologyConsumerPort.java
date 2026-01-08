package com.onclass.capability.port.consumer;

import com.onclass.capability.model.technology.TechnologySummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyConsumerPort {
    Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds);
    Flux<TechnologySummary> getTechnologiesByCapabilityId(Long capabilityId);
    Mono<Void> deleteTechnologiesByCapabilityIds(List<Long> capabilityIds);
}
