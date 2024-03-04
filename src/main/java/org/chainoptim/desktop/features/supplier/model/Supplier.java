package org.chainoptim.desktop.features.supplier.model;

import lombok.Data;
import org.chainoptim.desktop.shared.features.location.model.Location;

import java.time.LocalDateTime;

@Data
public class Supplier {

    private Integer id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer organizationId;
    private Location location;
}
