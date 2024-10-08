package org.chainoptim.desktop.features.storage.warehouse.service;

import org.chainoptim.desktop.core.tenant.user.service.TokenManager;
import org.chainoptim.desktop.features.storage.warehouse.dto.CreateWarehouseDTO;
import org.chainoptim.desktop.features.storage.warehouse.dto.UpdateWarehouseDTO;
import org.chainoptim.desktop.features.storage.warehouse.model.Warehouse;
import org.chainoptim.desktop.shared.httphandling.HttpMethod;
import org.chainoptim.desktop.shared.httphandling.RequestBuilder;
import org.chainoptim.desktop.shared.httphandling.RequestHandler;
import org.chainoptim.desktop.shared.httphandling.Result;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletableFuture;

public class WarehouseWriteServiceImpl implements WarehouseWriteService {

    private final RequestHandler requestHandler;
    private final RequestBuilder requestBuilder;
    private final TokenManager tokenManager;

    @Inject
    public WarehouseWriteServiceImpl(RequestHandler requestHandler,
                                     RequestBuilder requestBuilder,
                                     TokenManager tokenManager) {
        this.requestHandler = requestHandler;
        this.requestBuilder = requestBuilder;
        this.tokenManager = tokenManager;
    }

    public CompletableFuture<Result<Warehouse>> createWarehouse(CreateWarehouseDTO warehouseDTO) {
        String routeAddress = "http://localhost:8080/api/v1/warehouses/create";

        HttpRequest request = requestBuilder.buildWriteRequest(
                HttpMethod.POST, routeAddress, tokenManager.getToken(), warehouseDTO);
        if (request == null) return requestHandler.getParsingErrorResult();

        return requestHandler.sendRequest(request, new TypeReference<Warehouse>() {});
    }

    public CompletableFuture<Result<Warehouse>> updateWarehouse(UpdateWarehouseDTO warehouseDTO) {
        String routeAddress = "http://localhost:8080/api/v1/warehouses/update";

        HttpRequest request = requestBuilder.buildWriteRequest(
                HttpMethod.PUT, routeAddress, tokenManager.getToken(), warehouseDTO);
        if (request == null) return requestHandler.getParsingErrorResult();

        return requestHandler.sendRequest(request, new TypeReference<Warehouse>() {});
    }

    public CompletableFuture<Result<Integer>> deleteWarehouse(Integer warehouseId) {
        String routeAddress = "http://localhost:8080/api/v1/warehouses/delete/" + warehouseId;

        HttpRequest request = requestBuilder.buildWriteRequest(
                HttpMethod.DELETE, routeAddress, tokenManager.getToken(), null);

        return requestHandler.sendRequest(request, new TypeReference<Integer>() {});
    }
}
