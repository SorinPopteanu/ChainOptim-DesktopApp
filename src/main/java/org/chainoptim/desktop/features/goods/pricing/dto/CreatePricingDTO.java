package org.chainoptim.desktop.features.goods.pricing.dto;

import org.chainoptim.desktop.features.goods.pricing.model.ProductPricing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePricingDTO {

    private Integer productId;
    private ProductPricing productPricing;

}
