package com.onclass.capability.model.capability.gateways;

import com.onclass.capability.model.capability.Capability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapabilityRepositoryPort {
    Mono<Capability> saveCapability(Capability capability);
    Mono<Void> deleteCapability(Long capabilityId);
    Mono<Capability> findCapabilityByName(String name);
    Flux<Capability> findCapabilitiesPagedAndSorted(int page, int size, String sortBy, String order);
    Mono<Long> countByIds(List<Long> capabilityIds);
}
