package org.chainoptim.desktop.core.map.service;

import org.chainoptim.desktop.core.map.model.SupplyChainMap;
import org.chainoptim.desktop.shared.httphandling.Result;

import java.util.concurrent.CompletableFuture;

public interface SupplyChainMapService {

    CompletableFuture<Result<SupplyChainMap>> getMapByOrganizationId(Integer organizationId, boolean refresh);
}
