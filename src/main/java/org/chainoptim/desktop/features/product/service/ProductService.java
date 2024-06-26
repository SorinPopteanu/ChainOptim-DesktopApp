package org.chainoptim.desktop.features.product.service;

import org.chainoptim.desktop.features.product.dto.ProductOverviewDTO;
import org.chainoptim.desktop.features.product.dto.ProductsSearchDTO;
import org.chainoptim.desktop.features.product.model.Product;
import org.chainoptim.desktop.shared.httphandling.Result;
import org.chainoptim.desktop.shared.search.model.PaginatedResults;
import org.chainoptim.desktop.shared.search.model.SearchParams;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {

    CompletableFuture<Result<List<ProductsSearchDTO>>> getProductsByOrganizationId(Integer organizationId, boolean small);

    CompletableFuture<Result<PaginatedResults<Product>>> getProductsByOrganizationIdAdvanced(
            Integer organizationId,
            SearchParams searchParams
    );
    CompletableFuture<Result<Product>> getProductWithStages(Integer productId);
    CompletableFuture<Result<ProductOverviewDTO>> getProductOverview(Integer productId);
}
