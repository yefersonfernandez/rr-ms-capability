package com.onclass.capability.model.capability;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Capability {
    private Long id;
    private String name;
    private String description;
    private List<Long> technologyIds;
}
