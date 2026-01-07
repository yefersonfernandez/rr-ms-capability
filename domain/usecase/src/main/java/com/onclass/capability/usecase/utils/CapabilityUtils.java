package com.onclass.capability.usecase.utils;

import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.CapabilityWithTechnologies;
import com.onclass.capability.model.technology.TechnologySummary;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CapabilityUtils {

    public static boolean isValidTechnologiesCount(List<Long> techIds, int min, int max) {
        return techIds != null && techIds.size() >= min && techIds.size() <= max;
    }

    public static boolean hasNoRepeatedTechnologies(List<Long> techIds) {
        return techIds != null && techIds.stream().distinct().count() == techIds.size();
    }

    public static boolean isValidCapabilitiesCount(List<Long> capsIds, int min, int max) {
        return capsIds != null && capsIds.size() >= min && capsIds.size() <= max;
    }

    public static boolean hasNoRepeatedCapabilities(List<Long> capsIds) {
        return capsIds != null && capsIds.stream().distinct().count() == capsIds.size();
    }

    public static CapabilityWithTechnologies buildCapabilityWithTechnologies(Capability capability, List<TechnologySummary> technologies) {
        return CapabilityWithTechnologies.builder()
                .id(capability.getId())
                .name(capability.getName())
                .description(capability.getDescription())
                .technologies(technologies)
                .build();
    }
}
