package org.chainoptim.desktop.features.scanalysis.resourceallocation.model;

import lombok.Data;

@Data
public class ResourceAllocation {
    private Integer stageInputId;
    private Integer componentId;
    private Integer allocatorInventoryItemId;
    private Float allocatedAmount;
    private Float requestedAmount;
}