package org.chainoptim.desktop.features.supplier.dto;

import lombok.Data;
import org.chainoptim.desktop.features.supplier.model.SupplierOrder;

import java.time.LocalDateTime;

@Data
public class CreateSupplierOrderDTO {

    private Integer supplierId;
    private Integer componentId;
    private Float quantity;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveryDate;
    private SupplierOrder.Status status;
}
