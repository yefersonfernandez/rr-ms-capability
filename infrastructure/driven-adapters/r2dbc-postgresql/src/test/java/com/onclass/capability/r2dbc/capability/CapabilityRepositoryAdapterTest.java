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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
}


