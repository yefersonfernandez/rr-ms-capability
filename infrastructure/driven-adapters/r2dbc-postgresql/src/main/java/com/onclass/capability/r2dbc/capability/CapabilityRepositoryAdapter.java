package com.onclass.capability.r2dbc.capability;

import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.gateways.CapabilityRepositoryPort;
import com.onclass.capability.r2dbc.entity.CapabilityEntity;
import com.onclass.capability.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CapabilityRepositoryAdapter extends ReactiveAdapterOperations<
        Capability,
        CapabilityEntity,
        Long,
        CapabilityRepository
        > implements CapabilityRepositoryPort {

    public CapabilityRepositoryAdapter(CapabilityRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Capability.class));
    }

    @Override
    public Mono<Capability> saveCapability(Capability capability) {
        return super.save(capability);
    }

    @Override
    public Mono<Void> deleteCapability(Long capabilityId) {
        return repository.deleteById(capabilityId);
    }

    @Override
    public Mono<Capability> findCapabilityByName(String name) {
        return repository.findByName(name)
                .map(super::toEntity);
    }
}
