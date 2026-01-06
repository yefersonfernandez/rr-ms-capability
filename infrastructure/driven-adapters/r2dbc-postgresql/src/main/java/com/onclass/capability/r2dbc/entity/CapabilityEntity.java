package com.onclass.capability.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("capability")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CapabilityEntity {
    @Id
    @Column("capability_id")
    private Long id;
    private String name;
    private String description;
}
