package org.chainoptim.desktop.features.factory.dto;

import lombok.Data;

@Data
public class CreateFactoryInventoryItemDTO {

    private Integer factoryId;
    private Integer organizationId;
    private Integer productId;
    private Integer componentId;
    private Float quantity;
    private Float minimumRequiredQuantity;
}