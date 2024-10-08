package org.chainoptim.desktop.features.storage.crate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Crate {

    private Integer id;
    private String name;
    private Integer organizationId;
    private Integer componentId;
    private Float quantity;
    private Float volumeM3;
    private Boolean stackable;
    private Float heightM;
}
