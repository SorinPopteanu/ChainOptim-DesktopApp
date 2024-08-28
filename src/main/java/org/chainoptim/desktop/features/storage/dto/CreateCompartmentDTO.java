package org.chainoptim.desktop.features.storage.dto;

import org.chainoptim.desktop.features.storage.model.CompartmentData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompartmentDTO {

    private String name;
    private Integer warehouseId;
    private Integer organizationId;
    private CompartmentData data;
}