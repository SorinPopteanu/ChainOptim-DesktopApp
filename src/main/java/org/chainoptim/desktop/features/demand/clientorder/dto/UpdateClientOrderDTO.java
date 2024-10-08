package org.chainoptim.desktop.features.demand.clientorder.dto;

import org.chainoptim.desktop.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientOrderDTO {

    private Integer id;
    private Integer organizationId;
    private Integer productId;
    private String companyId;
    private Float quantity;
    private Float deliveredQuantity;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveryDate;
    private OrderStatus status;
}
