package org.chainoptim.desktop.features.warehouse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompartmentData {

    private List<CrateSpec> crateSpecs;
    private List<CrateData> currentCrates;
}
