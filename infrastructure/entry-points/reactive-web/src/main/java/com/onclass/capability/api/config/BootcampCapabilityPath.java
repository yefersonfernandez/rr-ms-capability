package com.onclass.capability.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class BootcampCapabilityPath {
    private String associateCapabilities;
    private String getCapabilitiesByBootcampId;
    private String deleteBootcampCascade;

    public String getCapabilitiesByBootcampId() {
        return getCapabilitiesByBootcampId;
    }
}
