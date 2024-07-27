package org.chainoptim.desktop.features.warehouse.dto;

import org.chainoptim.desktop.features.warehouse.model.CompartmentData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompartmentDTO {

    private Integer id;
    private String name;
    private CompartmentData data;
}
