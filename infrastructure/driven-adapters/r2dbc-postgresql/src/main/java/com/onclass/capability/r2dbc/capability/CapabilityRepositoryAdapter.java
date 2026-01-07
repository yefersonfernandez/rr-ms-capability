package com.onclass.capability.r2dbc.capability;

import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.gateways.CapabilityRepositoryPort;
import com.onclass.capability.r2dbc.entity.CapabilityEntity;
import com.onclass.capability.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
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

    @Override
    public Flux<Capability> findCapabilitiesPagedAndSorted(int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return repository.findAllBy(pageRequest)
                .map(super::toEntity)
                .doOnNext(cap -> log.info("[DB RESULT] capability_id={}, name={}, technology_count={}", cap.getId(), cap.getName(), cap.getTechnologyCount()));
    }

    @Override
    public Mono<Long> countByIds(List<Long> capabilityIds) {
        return repository.countByIdIn(capabilityIds);
    }
}
