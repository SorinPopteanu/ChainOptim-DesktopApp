package org.chainoptim.desktop.features.demand.client.dto;

import org.chainoptim.desktop.shared.search.dto.SmallEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientOverviewDTO {


    private List<SmallEntityDTO> suppliedProducts;
    private List<SmallEntityDTO> deliveredFromFactories;
    private List<SmallEntityDTO> deliveredFromWarehouses;
}
