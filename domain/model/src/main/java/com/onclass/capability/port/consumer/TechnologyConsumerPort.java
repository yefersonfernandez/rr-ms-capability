package com.onclass.capability.port.consumer;

import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyAssociationConsumerPort {
    Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds);
}
