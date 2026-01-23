package com.onclass.capability.usecase.capability;

import com.onclass.capability.enums.ExceptionMessages;
import com.onclass.capability.exceptions.CapabilityAlreadyExistsException;
import com.onclass.capability.exceptions.CapabilityTechnologiesCountException;
import com.onclass.capability.exceptions.SagaCompensationException;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.CapabilityWithTechnologies;
import com.onclass.capability.model.technology.TechnologySummary;
import com.onclass.capability.model.capability.gateways.CapabilityRepositoryPort;
import com.onclass.capability.port.consumer.TechnologyConsumerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
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
    private TechnologyConsumerPort technologyConsumerPort;

    @InjectMocks
    private CapabilityUseCase capabilityUseCase;

    private static final String CAPABILITY_NAME = "TestCapability";
    private static final List<Long> VALID_TECH_IDS = List.of(1L, 2L, 3L);
    private static final List<Long> REPEATED_TECH_IDS = List.of(1L, 1L, 2L);
    private static final List<Long> EMPTY_TECH_IDS = List.of();
    private static final String ASSOCIATION_ERROR_MESSAGE = "Association failed";
    private static final int PAGE = 0;
    private static final int SIZE = 2;
    private static final String SORT_BY = "name";
    private static final String ORDER_ASC = "asc";
    private static final Long CAP1_ID = 1L;
    private static final Long CAP2_ID = 2L;
    private static final String CAP1_NAME = "A";
    private static final String CAP2_NAME = "B";
    private static final String CAP1_DESC = "descA";
    private static final String CAP2_DESC = "descB";
    private static final Long TECH1_ID = 10L;
    private static final Long TECH2_ID = 11L;
    private static final String TECH1_NAME = "Java";
    private static final String TECH2_NAME = "Spring Boot";

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
        when(technologyConsumerPort.associateTechnologies(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(capabilityUseCase.saveCapability(capability))
                .expectNext(capability)
                .verifyComplete();

        verify(capabilityRepositoryPort).saveCapability(capability);
        verify(technologyConsumerPort).associateTechnologies(capability.getId(), capability.getTechnologyIds());
    }

    @Test
    void saveCapability_shouldHandleSagaCompensationOnAssociationFailure() {
        Capability capability = new Capability();
        capability.setName(CAPABILITY_NAME);
        capability.setTechnologyIds(VALID_TECH_IDS);

        when(capabilityRepositoryPort.findCapabilityByName(any())).thenReturn(Mono.empty());
        when(capabilityRepositoryPort.saveCapability(any())).thenReturn(Mono.just(capability));
        when(technologyConsumerPort.associateTechnologies(any(), any())).thenReturn(Mono.error(new RuntimeException(ASSOCIATION_ERROR_MESSAGE)));
        when(capabilityRepositoryPort.deleteCapability(any())).thenReturn(Mono.empty());

        StepVerifier.create(capabilityUseCase.saveCapability(capability))
                .expectErrorMatches(throwable -> throwable instanceof SagaCompensationException
                        && throwable.getMessage().equals(ExceptionMessages.SAGA_COMPENSATION_ASSOCIATION_FAILURE.getMessage()))
                .verify();

        verify(capabilityRepositoryPort).deleteCapability(capability.getId());
    }

    @Test
    void getCapabilitiesWithTechnologies_shouldReturnCapabilitiesWithTechnologies() {
        Capability cap1 = new Capability(); cap1.setId(CAP1_ID); cap1.setName(CAP1_NAME); cap1.setDescription(CAP1_DESC);
        Capability cap2 = new Capability(); cap2.setId(CAP2_ID); cap2.setName(CAP2_NAME); cap2.setDescription(CAP2_DESC);

        TechnologySummary tech1 = new TechnologySummary(TECH1_ID, TECH1_NAME);
        TechnologySummary tech2 = new TechnologySummary(TECH2_ID, TECH2_NAME);

        CapabilityWithTechnologies expected1 = CapabilityWithTechnologies.builder()
            .id(CAP1_ID).name(CAP1_NAME).description(CAP1_DESC).technologies(List.of(tech1, tech2)).build();
        CapabilityWithTechnologies expected2 = CapabilityWithTechnologies.builder()
            .id(CAP2_ID).name(CAP2_NAME).description(CAP2_DESC).technologies(List.of(tech2)).build();

        when(capabilityRepositoryPort.findCapabilitiesPagedAndSorted(PAGE, SIZE, SORT_BY, ORDER_ASC))
            .thenReturn(Flux.just(cap1, cap2));
        when(technologyConsumerPort.getTechnologiesByCapabilityId(CAP1_ID))
            .thenReturn(Flux.just(tech1, tech2));
        when(technologyConsumerPort.getTechnologiesByCapabilityId(CAP2_ID))
            .thenReturn(Flux.just(tech2));

        StepVerifier.create(capabilityUseCase.getCapabilitiesWithTechnologies(PAGE, SIZE, SORT_BY, ORDER_ASC))
            .expectNext(expected1)
            .expectNext(expected2)
            .verifyComplete();
    }
}
