package org.chainoptim.desktop.features.storage.dto;

import org.chainoptim.desktop.features.storage.model.CompartmentData;
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