package com.onclass.capability.r2dbc.capability;

import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.r2dbc.entity.CapabilityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapabilityRepositoryAdapterTest {

    @InjectMocks
    private CapabilityRepositoryAdapter adapter;
    @Mock
    private CapabilityRepository repository;
    @Mock
    private ObjectMapper mapper;

    private Capability capability;
    private CapabilityEntity capabilityEntity;

    private static final int PAGE = 0;
    private static final int SIZE = 2;
    private static final String SORT_BY = "name";
    private static final String ORDER_ASC = "asc";
    private static final String NAME_A = "A";
    private static final String NAME_B = "B";

    @BeforeEach
    void setUp() {
        capability = new Capability();
        capabilityEntity = new CapabilityEntity();
    }

    @Test
    @DisplayName("saveCapability should save and return capability")
    void saveCapability_shouldSaveAndReturnCapability() {
        when(mapper.map(capability, CapabilityEntity.class)).thenReturn(capabilityEntity);
        when(repository.save(capabilityEntity)).thenReturn(Mono.just(capabilityEntity));
        when(mapper.map(capabilityEntity, Capability.class)).thenReturn(capability);

        StepVerifier.create(adapter.saveCapability(capability))
                .expectNext(capability)
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteCapability should delete by id and complete")
    void deleteCapability_shouldDeleteById() {
        Long id = 1L;
        when(repository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteCapability(id))
                .verifyComplete();
    }

    @Test
    @DisplayName("findCapabilityByName should return capability when found")
    void findCapabilityByName_shouldReturnCapability() {
        String name = "Test Capability";
        when(repository.findByName(name)).thenReturn(Mono.just(capabilityEntity));
        when(mapper.map(capabilityEntity, Capability.class)).thenReturn(capability);

        StepVerifier.create(adapter.findCapabilityByName(name))
                .expectNext(capability)
                .verifyComplete();
    }

    @Test
    @DisplayName("findCapabilityByName should complete empty when not found")
    void findCapabilityByName_shouldReturnEmptyWhenNotFound() {
        String name = "NotFound";
        when(repository.findByName(name)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findCapabilityByName(name))
                .verifyComplete();
    }

    @Test
    @DisplayName("findCapabilitiesPagedAndSorted should return paginated and ordered capabilities")
    void findCapabilitiesPagedAndSorted_shouldReturnPaginatedAndOrdered() {
        var entity1 = new CapabilityEntity(); entity1.setId(1L); entity1.setName(NAME_A);
        var entity2 = new CapabilityEntity(); entity2.setId(2L); entity2.setName(NAME_B);
        var cap1 = new Capability(); cap1.setId(1L); cap1.setName(NAME_A);
        var cap2 = new Capability(); cap2.setId(2L); cap2.setName(NAME_B);

        when(repository.findAllBy(PageRequest.of(PAGE, SIZE,Sort.by(Sort.Direction.ASC, SORT_BY))))
            .thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, Capability.class)).thenReturn(cap1);
        when(mapper.map(entity2, Capability.class)).thenReturn(cap2);

        StepVerifier.create(adapter.findCapabilitiesPagedAndSorted(PAGE, SIZE, SORT_BY, ORDER_ASC))
            .expectNext(cap1)
            .expectNext(cap2)
            .verifyComplete();
    }

    @Test
    @DisplayName("findCapabilityById should return capability when found")
    void findCapabilityById_shouldReturnCapability() {
        Long id = 1L;
        capabilityEntity.setId(id);
        capability.setId(id);
        when(repository.findById(id)).thenReturn(Mono.just(capabilityEntity));
        when(mapper.map(capabilityEntity, Capability.class)).thenReturn(capability);

        StepVerifier.create(adapter.findCapabilityById(id))
                .expectNext(capability)
                .verifyComplete();
    }

    @Test
    @DisplayName("findCapabilityById should complete empty when not found")
    void findCapabilityById_shouldReturnEmptyWhenNotFound() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findCapabilityById(id))
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteCapabilitiesByIds should delete all by ids and complete")
    void deleteCapabilitiesByIds_shouldDeleteAllByIds() {
        var ids = List.of(1L, 2L, 3L);
        when(repository.deleteAllById(ids)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteCapabilitiesByIds(ids))
                .verifyComplete();
    }

    @Test
    @DisplayName("countByIds should return count of matching ids")
    void countByIds_shouldReturnCount() {
        var ids = List.of(1L, 2L, 3L);
        when(repository.countByIdIn(ids)).thenReturn(Mono.just(2L));

        StepVerifier.create(adapter.countByIds(ids))
                .expectNext(2L)
                .verifyComplete();
    }
}
