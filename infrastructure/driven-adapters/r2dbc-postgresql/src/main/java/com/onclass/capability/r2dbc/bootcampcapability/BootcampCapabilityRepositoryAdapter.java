package com.onclass.capability.r2dbc.bootcampcapability;

import com.onclass.capability.model.bootcampcapability.BootcampCapability;
import com.onclass.capability.model.bootcampcapability.gateways.BootcampCapabilityRepositoryPort;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.gateways.CapabilityRepositoryPort;
import com.onclass.capability.r2dbc.entity.BootcampCapabilityEntity;
import com.onclass.capability.r2dbc.entity.CapabilityEntity;
import com.onclass.capability.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
public class BootcampCapabilityRepositoryAdapter extends ReactiveAdapterOperations<
        BootcampCapability,
        BootcampCapabilityEntity,
        Long,
        BootcampCapabilityRepository
        > implements BootcampCapabilityRepositoryPort {

    private final TransactionalOperator transactionalOperator;

    public BootcampCapabilityRepositoryAdapter(BootcampCapabilityRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, BootcampCapability.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Void> saveAll(Long bootcampId, List<Long> capabilityIds) {
        return Flux.fromIterable(capabilityIds)
                .map(capId -> BootcampCapabilityEntity.builder()
                        .bootcampId(bootcampId)
                        .capabilityId(capId)
                        .build())
                .collectList()
                .flatMapMany(repository::saveAll)
                .then()
                .as(transactionalOperator::transactional);
    }
}
