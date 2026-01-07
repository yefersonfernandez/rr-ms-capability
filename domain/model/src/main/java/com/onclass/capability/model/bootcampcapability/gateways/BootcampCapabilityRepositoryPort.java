package com.onclass.capability.model.bootcampcapability.gateways;

import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampCapabilityRepositoryPort {
    Mono<Void> saveAll(Long bootcampId, List<Long> capabilityIds);
}
