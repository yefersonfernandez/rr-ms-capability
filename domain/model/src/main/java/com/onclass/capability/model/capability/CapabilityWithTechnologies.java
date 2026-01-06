package com.onclass.capability.model.capability;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import java.util.List;
import com.onclass.capability.model.technology.TechnologySummary;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class CapabilityWithTechnologies {
    private Long id;
    private String name;
    private String description;
    private List<TechnologySummary> technologies;
}
