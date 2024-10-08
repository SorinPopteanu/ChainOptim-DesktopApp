package org.chainoptim.desktop.features.production.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFactoryInventoryItemDTO {

    private Integer factoryId;
    private Integer organizationId;
    private Integer productId;
    private Integer componentId;
    private Float quantity;
    private Float minimumRequiredQuantity;
    private String companyId;
}
