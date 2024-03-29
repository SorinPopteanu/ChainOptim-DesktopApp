package org.chainoptim.desktop.features.factory.service;

import org.chainoptim.desktop.features.factory.dto.FactoriesSearchDTO;
import org.chainoptim.desktop.features.factory.model.Factory;
import org.chainoptim.desktop.shared.search.model.PaginatedResults;
import org.chainoptim.desktop.shared.search.model.SearchParams;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface FactoryService {

    CompletableFuture<Optional<List<FactoriesSearchDTO>>> getFactoriesByOrganizationIdSmall(Integer organizationId);
    CompletableFuture<Optional<List<Factory>>> getFactoriesByOrganizationId(Integer organizationId);
    CompletableFuture<Optional<PaginatedResults<Factory>>> getFactoriesByOrganizationIdAdvanced(
            Integer organizationId,
            SearchParams searchParams
    );
    CompletableFuture<Optional<Factory>> getFactoryById(Integer factoryId);
}
