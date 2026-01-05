package com.onclass.capability.usecase.capability;

import com.onclass.capability.exceptions.SagaCompensationException;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.gateways.CapabilityRepositoryPort;
import com.onclass.capability.enums.ExceptionMessages;
import com.onclass.capability.exceptions.CapabilityTechnologiesCountException;
import com.onclass.capability.port.consumer.TechnologyAssociationConsumerPort;
import com.onclass.capability.exceptions.CapabilityAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import static com.onclass.capability.constants.CapabilityConstants.MAX_TECHS;
import static com.onclass.capability.constants.CapabilityConstants.MIN_TECHS;
import static com.onclass.capability.usecase.utils.CapabilityUtils.hasNoRepeatedTechnologies;
import static com.onclass.capability.usecase.utils.CapabilityUtils.isValidTechnologiesCount;

@RequiredArgsConstructor
public class CapabilityUseCase {

    private final CapabilityRepositoryPort capabilityRepositoryPort;
    private final TechnologyAssociationConsumerPort technologyAssociationConsumerPort;

    public Mono<Capability> saveCapability(Capability capability) {
        var techIds = capability.getTechnologyIds();
        return Mono.just(capability)
                .filter(cap -> isValidTechnologiesCount(techIds, MIN_TECHS, MAX_TECHS))
                .switchIfEmpty(Mono.error(new CapabilityTechnologiesCountException(
                        ExceptionMessages.CAPABILITY_TECHNOLOGIES_COUNT_INVALID.format()
                )))
                .filter(cap -> hasNoRepeatedTechnologies(techIds))
                .switchIfEmpty(Mono.error(new CapabilityTechnologiesCountException(
                        ExceptionMessages.CAPABILITY_TECHNOLOGIES_REPEATED.format()
                )))
                .flatMap(this::validateUniqueName)
                .flatMap(this::saveAndAssociateTechnologies);
    }

    private Mono<Capability> validateUniqueName(Capability capability) {
        return capabilityRepositoryPort.findCapabilityByName(capability.getName())
                .flatMap(existing -> Mono.<Capability>error(new CapabilityAlreadyExistsException(
                        ExceptionMessages.CAPABILITY_ALREADY_EXISTS.format(capability.getName())
                )))
                .switchIfEmpty(Mono.just(capability));
    }

    private Mono<Capability> saveAndAssociateTechnologies(Capability capability) {
        var techIds = capability.getTechnologyIds();
        return capabilityRepositoryPort.saveCapability(capability)
                .flatMap(savedCap -> technologyAssociationConsumerPort
                        .associateTechnologies(savedCap.getId(), techIds)
                        .thenReturn(savedCap)
                        .onErrorResume(e -> capabilityRepositoryPort.deleteCapability(savedCap.getId())
                                .then(Mono.error(new SagaCompensationException(
                                        ExceptionMessages.SAGA_COMPENSATION_ASSOCIATION_FAILURE.getMessage()
                                )))
                        )
                );
    }
}
