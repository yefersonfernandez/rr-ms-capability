package com.onclass.capability.usecase.bootcampcapability;
import com.onclass.capability.enums.ExceptionMessages;
import com.onclass.capability.exceptions.InvalidCountException;
import com.onclass.capability.exceptions.NotFoundException;
import com.onclass.capability.exceptions.RepeatedCapabilitiesException;
import com.onclass.capability.model.bootcampcapability.gateways.BootcampCapabilityRepositoryPort;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.gateways.CapabilityRepositoryPort;
import com.onclass.capability.port.consumer.TechnologyConsumerPort;
import com.onclass.capability.usecase.utils.CapabilityUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

import static com.onclass.capability.constants.CapabilityConstants.*;
import static com.onclass.capability.usecase.utils.CapabilityUtils.isValidCapabilitiesCount;


@RequiredArgsConstructor
public class BootcampCapabilityUseCase {
    private final BootcampCapabilityRepositoryPort bootcampCapabilityRepositoryPort;
    private final CapabilityRepositoryPort capabilityRepositoryPort;
    private final TechnologyConsumerPort technologyConsumerPort;

    public Mono<Void> associateCapabilities(Long bootcampId, List<Long> capabilityIds) {
        return Mono.just(capabilityIds)
                .filter(ids -> isValidCapabilitiesCount(ids, MIN_CAPS, MAX_CAPS))
                .switchIfEmpty(Mono.error(new InvalidCountException(ExceptionMessages.BOOTCAMP_CAPABILITIES_COUNT_INVALID.format(MIN_CAPS, MAX_CAPS))))
                .filter(CapabilityUtils::hasNoRepeatedCapabilities)
                .switchIfEmpty(Mono.error(new RepeatedCapabilitiesException(ExceptionMessages.BOOTCAMP_CAPABILITIES_REPEATED.format())))
                .flatMap(ids -> capabilityRepositoryPort.countByIds(ids)
                        .filter(count -> count == ids.size())
                        .switchIfEmpty(Mono.error(new NotFoundException(ExceptionMessages.CAPABILITY_NOT_FOUND.format())))
                        .thenReturn(ids))
                .flatMap(ids -> bootcampCapabilityRepositoryPort.saveAll(bootcampId, ids))
                .then().log();
    }

    public Flux<Capability> getCapabilitiesByBootcampId(Long bootcampId) {
        return bootcampCapabilityRepositoryPort.findCapabilityIdsByBootcampId(bootcampId)
                .flatMap(capabilityRepositoryPort::findCapabilityById);
    }

    public Mono<Void> deleteAssociatedDataByBootcampId(Long bootcampId) {
        return bootcampCapabilityRepositoryPort.findCapabilityIdsByBootcampId(bootcampId)
                .collectList()
                .filter(associatedIds -> !associatedIds.isEmpty())
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::identifyOrphanCapability)
                .collectList()
                .flatMap(this::executeCascadingDelete)
                .then(bootcampCapabilityRepositoryPort.deleteAssociationsByBootcampId(bootcampId));
    }

    private Mono<Long> identifyOrphanCapability(Long capabilityId) {
        return bootcampCapabilityRepositoryPort.countBootcampsByCapability(capabilityId)
                .filter(usageCount -> usageCount <= SOLE_BOOTCAMP_ASSOCIATION)
                .map(unused -> capabilityId);
    }

    private Mono<Void> executeCascadingDelete(List<Long> orphanCapabilityIds) {
        return Mono.just(orphanCapabilityIds)
                .filter(ids -> !ids.isEmpty())
                .flatMap(ids -> deleteTechnologiesInCascade(ids)
                        .then(deleteOrphanCapacities(ids)));
    }

    private Mono<Void> deleteTechnologiesInCascade(List<Long> orphanCapabilityIds) {
        return technologyConsumerPort.deleteTechnologiesByCapabilityIds(orphanCapabilityIds);
    }

    private Mono<Void> deleteOrphanCapacities(List<Long> orphanCapabilityIds) {
        return capabilityRepositoryPort.deleteCapabilitiesByIds(orphanCapabilityIds);
    }
}
