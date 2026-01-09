package com.onclass.capability.r2dbc.bootcampcapability;

import com.onclass.capability.r2dbc.entity.BootcampCapabilityEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampCapabilityRepository extends ReactiveCrudRepository<BootcampCapabilityEntity, Long>, ReactiveQueryByExampleExecutor<BootcampCapabilityEntity> {
    Flux<BootcampCapabilityEntity> findAllByBootcampId(Long capabilityId);
    Mono<Long> countByCapabilityId(Long capabilityId);
    Mono<Void> deleteAllByBootcampId(Long bootcampId);
}


