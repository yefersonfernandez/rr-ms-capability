package com.onclass.capability.api.mapper;

import com.onclass.capability.api.dto.request.CapabilityRequestDto;
import com.onclass.capability.api.dto.response.CapabilityResponseDto;
import com.onclass.capability.api.dto.response.CapabilitySummaryResponseDto;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.CapabilityWithTechnologies;
import com.onclass.capability.api.dto.response.CapabilityWithTechnologiesResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CapabilityMapper {
    CapabilityResponseDto toCapabilityResponseDto(Capability capability);
    Capability toModel(CapabilityRequestDto capabilityRequestDto);
    CapabilityWithTechnologiesResponseDto toCapabilityWithTechnologiesResponseDto(CapabilityWithTechnologies model);
    CapabilitySummaryResponseDto toCapabilitySummaryResponseDto(Capability capability);
}
