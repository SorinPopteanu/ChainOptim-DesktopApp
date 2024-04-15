package org.chainoptim.desktop.features.supplier.service;

import org.chainoptim.desktop.features.supplier.dto.CreateSupplierOrderDTO;
import org.chainoptim.desktop.features.supplier.model.SupplierOrder;
import org.chainoptim.desktop.shared.search.model.PaginatedResults;
import org.chainoptim.desktop.shared.search.model.SearchParams;
import org.chainoptim.desktop.features.supplier.dto.UpdateSupplierOrderDTO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface SupplierOrdersService {

    CompletableFuture<Optional<List<SupplierOrder>>> getSupplierOrdersByOrganizationId(Integer organizationId);
    CompletableFuture<Optional<PaginatedResults<SupplierOrder>>> getSuppliersBySupplierIdAdvanced(
            Integer supplierId,
            SearchParams searchParams
    );
    CompletableFuture<SupplierOrder> createSupplierOrder(CreateSupplierOrderDTO supplierDTO);

    CompletableFuture<List<Integer>> deleteSupplierOrderInBulk(List<Integer> orderIds);

    CompletableFuture<List<SupplierOrder>> updateSupplierOrdersInBulk(List<UpdateSupplierOrderDTO> orderDTOs);

    CompletableFuture<List<SupplierOrder>> createSupplierOrdersInBulk(List<CreateSupplierOrderDTO> orderDTOs);

}
