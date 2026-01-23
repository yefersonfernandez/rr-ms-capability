package com.onclass.capability.usecase.bootcampcapability;

import com.onclass.capability.enums.ExceptionMessages;
import com.onclass.capability.exceptions.InvalidCountException;
import com.onclass.capability.exceptions.NotFoundException;
import com.onclass.capability.exceptions.RepeatedCapabilitiesException;
import com.onclass.capability.model.bootcampcapability.gateways.BootcampCapabilityRepositoryPort;
import com.onclass.capability.model.capability.Capability;
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
class BootcampCapabilityUseCaseTest {

    @Mock
    private BootcampCapabilityRepositoryPort bootcampCapabilityRepositoryPort;
    @Mock
    private CapabilityRepositoryPort capabilityRepositoryPort;
    @Mock
    private TechnologyConsumerPort technologyConsumerPort;
    @InjectMocks
    private BootcampCapabilityUseCase useCase;

    private static final Long BOOTCAMP_ID = 1L;
    private static final List<Long> VALID_CAP_IDS = List.of(10L, 11L);
    private static final List<Long> REPEATED_CAP_IDS = List.of(10L, 10L);
    private static final List<Long> EMPTY_CAP_IDS = List.of();
    private static final int MIN_CAPS = 1;
    private static final int MAX_CAPS = 4;
    private static final Long CAP_ID = 10L;
    private static final Capability CAPABILITY = new Capability();

    @Test
    void associateCapabilities_shouldThrowExceptionWhenCountInvalid() {
        StepVerifier.create(useCase.associateCapabilities(BOOTCAMP_ID, EMPTY_CAP_IDS))
                .expectErrorMatches(t -> t instanceof InvalidCountException &&
                        t.getMessage().equals(ExceptionMessages.BOOTCAMP_CAPABILITIES_COUNT_INVALID.format(MIN_CAPS, MAX_CAPS)))
                .verify();
    }

    @Test
    void associateCapabilities_shouldThrowExceptionWhenRepeated() {
        StepVerifier.create(useCase.associateCapabilities(BOOTCAMP_ID, REPEATED_CAP_IDS))
                .expectErrorMatches(t -> t instanceof RepeatedCapabilitiesException &&
                        t.getMessage().equals(ExceptionMessages.BOOTCAMP_CAPABILITIES_REPEATED.format()))
                .verify();
    }

    @Test
    void associateCapabilities_shouldThrowExceptionWhenNotFound() {
        when(capabilityRepositoryPort.countByIds(VALID_CAP_IDS)).thenReturn(Mono.just(1L));
        StepVerifier.create(useCase.associateCapabilities(BOOTCAMP_ID, VALID_CAP_IDS))
                .expectErrorMatches(t -> t instanceof NotFoundException &&
                        t.getMessage().equals(ExceptionMessages.CAPABILITY_NOT_FOUND.format()))
                .verify();
    }

    @Test
    void associateCapabilities_shouldAssociateSuccessfully() {
        when(capabilityRepositoryPort.countByIds(VALID_CAP_IDS)).thenReturn(Mono.just((long) VALID_CAP_IDS.size()));
        when(bootcampCapabilityRepositoryPort.saveAll(BOOTCAMP_ID, VALID_CAP_IDS)).thenReturn(Mono.empty());
        StepVerifier.create(useCase.associateCapabilities(BOOTCAMP_ID, VALID_CAP_IDS))
                .verifyComplete();
        verify(bootcampCapabilityRepositoryPort).saveAll(BOOTCAMP_ID, VALID_CAP_IDS);
    }

    @Test
    void getCapabilitiesByBootcampId_shouldReturnCapabilities() {
        when(bootcampCapabilityRepositoryPort.findCapabilityIdsByBootcampId(BOOTCAMP_ID)).thenReturn(Flux.just(CAP_ID));
        when(capabilityRepositoryPort.findCapabilityById(CAP_ID)).thenReturn(Mono.just(CAPABILITY));
        StepVerifier.create(useCase.getCapabilitiesByBootcampId(BOOTCAMP_ID))
                .expectNext(CAPABILITY)
                .verifyComplete();
    }

    @Test
    void deleteAssociatedDataByBootcampId_shouldDeleteCascade() {
        List<Long> associatedIds = List.of(10L, 11L);
        when(bootcampCapabilityRepositoryPort.findCapabilityIdsByBootcampId(BOOTCAMP_ID)).thenReturn(Flux.fromIterable(associatedIds));
        when(bootcampCapabilityRepositoryPort.countBootcampsByCapability(any())).thenReturn(Mono.just(1L));
        when(technologyConsumerPort.deleteTechnologiesByCapabilityIds(any())).thenReturn(Mono.empty());
        when(capabilityRepositoryPort.deleteCapabilitiesByIds(any())).thenReturn(Mono.empty());
        when(bootcampCapabilityRepositoryPort.deleteAssociationsByBootcampId(BOOTCAMP_ID)).thenReturn(Mono.empty());
        StepVerifier.create(useCase.deleteAssociatedDataByBootcampId(BOOTCAMP_ID))
                .verifyComplete();
    }

    @Test
    void deleteAssociatedDataByBootcampId_shouldDeleteAssociationsEvenIfEmpty() {
        when(bootcampCapabilityRepositoryPort.findCapabilityIdsByBootcampId(BOOTCAMP_ID)).thenReturn(Flux.empty());
        when(bootcampCapabilityRepositoryPort.deleteAssociationsByBootcampId(BOOTCAMP_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteAssociatedDataByBootcampId(BOOTCAMP_ID))
                .verifyComplete();

        verify(bootcampCapabilityRepositoryPort, times(1)).deleteAssociationsByBootcampId(BOOTCAMP_ID);
    }
}
