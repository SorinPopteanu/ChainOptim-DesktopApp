package org.chainoptim.desktop.features.production.analysis.resourceallocation.dto;

import org.chainoptim.desktop.features.production.analysis.resourceallocation.model.AllocationPlan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAllocationPlanDTO {

    private Integer id;
    private Integer factoryId;
    private AllocationPlan allocationPlan;
    private LocalDateTime activationDate;
    private boolean isActive;
}
