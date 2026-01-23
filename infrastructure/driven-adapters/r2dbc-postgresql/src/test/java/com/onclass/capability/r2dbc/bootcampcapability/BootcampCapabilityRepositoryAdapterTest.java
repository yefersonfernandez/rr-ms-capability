package com.onclass.capability.r2dbc.bootcampcapability;

import com.onclass.capability.r2dbc.entity.BootcampCapabilityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampCapabilityRepositoryAdapterTest {
    @InjectMocks
    private BootcampCapabilityRepositoryAdapter adapter;
    @Mock
    private BootcampCapabilityRepository repository;
    @Mock
    private TransactionalOperator transactionalOperator;

    private BootcampCapabilityEntity entity;

    @BeforeEach
    void setUp() {
        entity = BootcampCapabilityEntity.builder().bootcampId(1L).capabilityId(2L).build();
    }

    @Test
    @DisplayName("saveAll should save associations and complete")
    void saveAll_shouldSaveAssociations() {
        List<Long> capabilityIds = List.of(2L, 3L);
        var entity2 = BootcampCapabilityEntity.builder().bootcampId(1L).capabilityId(3L).build();
        when(repository.saveAll(org.mockito.ArgumentMatchers.anyList())).thenReturn(Flux.just(entity, entity2));

        StepVerifier.create(adapter.saveAll(1L, capabilityIds))
                .verifyComplete();
    }

    @Test
    @DisplayName("findCapabilityIdsByBootcampId should return capability ids")
    void findCapabilityIdsByBootcampId_shouldReturnIds() {
        var entity2 = BootcampCapabilityEntity.builder().bootcampId(1L).capabilityId(3L).build();
        when(repository.findAllByBootcampId(1L)).thenReturn(Flux.just(entity, entity2));

        StepVerifier.create(adapter.findCapabilityIdsByBootcampId(1L))
                .expectNext(2L)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    @DisplayName("countBootcampsByCapability should return count")
    void countBootcampsByCapability_shouldReturnCount() {
        when(repository.countByCapabilityId(2L)).thenReturn(Mono.just(5L));
        StepVerifier.create(adapter.countBootcampsByCapability(2L))
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteAssociationsByBootcampId should delete and complete")
    void deleteAssociationsByBootcampId_shouldDeleteAndComplete() {
        when(repository.deleteAllByBootcampId(1L)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(ArgumentMatchers.<Mono<Void>>any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.deleteAssociationsByBootcampId(1L))
                .verifyComplete();
    }
}
