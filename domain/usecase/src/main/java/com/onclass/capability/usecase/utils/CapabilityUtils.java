package com.onclass.capability.usecase.utils;

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
}

