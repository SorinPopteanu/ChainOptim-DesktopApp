package org.chainoptim.desktop.features.goods.component.service;

import org.chainoptim.desktop.features.goods.component.dto.ComponentsSearchDTO;
import org.chainoptim.desktop.features.goods.component.dto.CreateComponentDTO;
import org.chainoptim.desktop.features.goods.component.dto.UpdateComponentDTO;
import org.chainoptim.desktop.features.goods.component.model.Component;
import org.chainoptim.desktop.shared.httphandling.Result;
import org.chainoptim.desktop.shared.search.model.PaginatedResults;
import org.chainoptim.desktop.shared.search.model.SearchParams;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ComponentService {

    CompletableFuture<Result<List<Component>>> getComponentsByOrganizationId(Integer organizationId);
    CompletableFuture<Result<PaginatedResults<Component>>> getComponentsByOrganizationIdAdvanced(
            Integer organizationId,
            SearchParams searchParams
    );
    CompletableFuture<Result<Component>> getComponentById(Integer id);

    CompletableFuture<Result<List<ComponentsSearchDTO>>> getComponentsByOrganizationIdSmall(Integer organizationId);

    CompletableFuture<Result<Component>> createComponent(CreateComponentDTO componentDTO);
    CompletableFuture<Result<Component>> updateComponent(UpdateComponentDTO componentDTO);
    CompletableFuture<Result<Integer>> deleteComponent(Integer id);
}
