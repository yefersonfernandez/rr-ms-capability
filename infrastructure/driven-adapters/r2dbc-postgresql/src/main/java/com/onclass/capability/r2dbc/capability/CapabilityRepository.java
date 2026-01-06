package com.onclass.capability.r2dbc.capability;

import com.onclass.capability.r2dbc.entity.CapabilityEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapabilityRepository extends ReactiveCrudRepository<CapabilityEntity, Long>, ReactiveQueryByExampleExecutor<CapabilityEntity> {
    Mono<CapabilityEntity> findByName(String name);
    Flux<CapabilityEntity> findAllBy(Pageable pageable);
}
