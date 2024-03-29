package org.chainoptim.desktop.features.product.service;

import org.chainoptim.desktop.features.product.dto.ProductsSearchDTO;
import org.chainoptim.desktop.features.product.model.Product;
import org.chainoptim.desktop.shared.search.model.PaginatedResults;
import org.chainoptim.desktop.shared.search.model.SearchParams;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ProductService {

    public CompletableFuture<Optional<List<ProductsSearchDTO>>> getProductsByOrganizationId(Integer organizationId, boolean small);

    public CompletableFuture<Optional<PaginatedResults<Product>>> getProductsByOrganizationIdAdvanced(
            Integer organizationId,
            SearchParams searchParams
    );
    public CompletableFuture<Optional<Product>> getProductWithStages(Integer productId);
}
