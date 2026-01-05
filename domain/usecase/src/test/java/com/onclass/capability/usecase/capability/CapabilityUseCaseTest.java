package com.onclass.capability.usecase.capability;

import com.onclass.capability.enums.ExceptionMessages;
import com.onclass.capability.exceptions.CapabilityAlreadyExistsException;
import com.onclass.capability.exceptions.CapabilityTechnologiesCountException;
import com.onclass.capability.exceptions.SagaCompensationException;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.gateways.CapabilityRepositoryPort;
import com.onclass.capability.port.consumer.TechnologyAssociationConsumerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapabilityUseCaseTest {

    @Mock
    private CapabilityRepositoryPort capabilityRepositoryPort;

    @Mock
    private TechnologyAssociationConsumerPort technologyAssociationConsumerPort;

    @InjectMocks
    private CapabilityUseCase capabilityUseCase;

    private static final String CAPABILITY_NAME = "TestCapability";
    private static final List<Long> VALID_TECH_IDS = List.of(1L, 2L, 3L);
    private static final List<Long> REPEATED_TECH_IDS = List.of(1L, 1L, 2L);
    private static final List<Long> EMPTY_TECH_IDS = List.of();
    private static final String ASSOCIATION_ERROR_MESSAGE = "Association failed";

    @Test
    void saveCapability_shouldThrowExceptionWhenTechnologiesCountInvalid() {
        Capability capability = new Capability();
        capability.setTechnologyIds(EMPTY_TECH_IDS);

        StepVerifier.create(capabilityUseCase.saveCapability(capability))
                .expectErrorMatches(throwable -> throwable instanceof CapabilityTechnologiesCountException
                        && throwable.getMessage().equals(ExceptionMessages.CAPABILITY_TECHNOLOGIES_COUNT_INVALID.format()))
                .verify();
    }

    @Test
    void saveCapability_shouldThrowExceptionWhenTechnologiesAreRepeated() {
        Capability capability = new Capability();
        capability.setTechnologyIds(REPEATED_TECH_IDS);

        StepVerifier.create(capabilityUseCase.saveCapability(capability))
                .expectErrorMatches(throwable -> throwable instanceof CapabilityTechnologiesCountException
                        && throwable.getMessage().equals(ExceptionMessages.CAPABILITY_TECHNOLOGIES_REPEATED.format()))
                .verify();
    }

    @Test
    void saveCapability_shouldThrowExceptionWhenCapabilityAlreadyExists() {
        Capability capability = new Capability();
        capability.setName(CAPABILITY_NAME);
        capability.setTechnologyIds(VALID_TECH_IDS);

        when(capabilityRepositoryPort.findCapabilityByName(any())).thenReturn(Mono.just(new Capability()));

        StepVerifier.create(capabilityUseCase.saveCapability(capability))
                .expectErrorMatches(throwable -> throwable instanceof CapabilityAlreadyExistsException
                        && throwable.getMessage().equals(ExceptionMessages.CAPABILITY_ALREADY_EXISTS.format(CAPABILITY_NAME)))
                .verify();
    }

    @Test
    void saveCapability_shouldSaveAndAssociateTechnologiesSuccessfully() {
        Capability capability = new Capability();
        capability.setName(CAPABILITY_NAME);
        capability.setTechnologyIds(VALID_TECH_IDS);

        when(capabilityRepositoryPort.findCapabilityByName(any())).thenReturn(Mono.empty());
        when(capabilityRepositoryPort.saveCapability(any())).thenReturn(Mono.just(capability));
        when(technologyAssociationConsumerPort.associateTechnologies(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(capabilityUseCase.saveCapability(capability))
                .expectNext(capability)
                .verifyComplete();

        verify(capabilityRepositoryPort).saveCapability(capability);
        verify(technologyAssociationConsumerPort).associateTechnologies(capability.getId(), capability.getTechnologyIds());
    }

    @Test
    void saveCapability_shouldHandleSagaCompensationOnAssociationFailure() {
        Capability capability = new Capability();
        capability.setName(CAPABILITY_NAME);
        capability.setTechnologyIds(VALID_TECH_IDS);

        when(capabilityRepositoryPort.findCapabilityByName(any())).thenReturn(Mono.empty());
        when(capabilityRepositoryPort.saveCapability(any())).thenReturn(Mono.just(capability));
        when(technologyAssociationConsumerPort.associateTechnologies(any(), any())).thenReturn(Mono.error(new RuntimeException(ASSOCIATION_ERROR_MESSAGE)));
        when(capabilityRepositoryPort.deleteCapability(any())).thenReturn(Mono.empty());

        StepVerifier.create(capabilityUseCase.saveCapability(capability))
                .expectErrorMatches(throwable -> throwable instanceof SagaCompensationException
                        && throwable.getMessage().equals(ExceptionMessages.SAGA_COMPENSATION_ASSOCIATION_FAILURE.getMessage()))
                .verify();

        verify(capabilityRepositoryPort).deleteCapability(capability.getId());
    }
}
