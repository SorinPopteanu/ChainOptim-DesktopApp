package org.chainoptim.desktop.features.supplier.service;

import org.chainoptim.desktop.features.supplier.dto.CreateSupplierOrderDTO;
import org.chainoptim.desktop.features.supplier.dto.UpdateSupplierOrderDTO;
import org.chainoptim.desktop.features.supplier.model.SupplierOrder;
import org.chainoptim.desktop.shared.httphandling.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SupplierOrdersWriteService {

    CompletableFuture<Result<SupplierOrder>> createSupplierOrder(CreateSupplierOrderDTO supplierDTO);

    CompletableFuture<Result<List<Integer>>> deleteSupplierOrderInBulk(List<Integer> orderIds);

    CompletableFuture<Result<List<SupplierOrder>>> updateSupplierOrdersInBulk(List<UpdateSupplierOrderDTO> orderDTOs);

    CompletableFuture<Result<List<SupplierOrder>>> createSupplierOrdersInBulk(List<CreateSupplierOrderDTO> orderDTOs);
}
