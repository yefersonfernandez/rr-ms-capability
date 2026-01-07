package com.onclass.capability.model.bootcampcapability;
import lombok.*;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BootcampCapability {
    private Long id;
    private Long bootcampId;
    private Long capabilityId;
}
