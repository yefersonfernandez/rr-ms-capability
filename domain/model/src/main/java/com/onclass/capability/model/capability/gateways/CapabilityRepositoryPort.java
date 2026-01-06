package com.onclass.capability.model.capability.gateways;

import com.onclass.capability.model.capability.Capability;
import reactor.core.publisher.Mono;

public interface CapabilityRepositoryPort {
    Mono<Capability> saveCapability(Capability capability);
    Mono<Void> deleteCapability(Long capabilityId);
    Mono<Capability> findCapabilityByName(String name);
}
