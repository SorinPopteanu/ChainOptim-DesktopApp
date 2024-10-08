package org.chainoptim.desktop.features.production.stage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import org.chainoptim.desktop.core.tenant.user.service.TokenManager;
import org.chainoptim.desktop.features.production.stage.dto.CreateFactoryStageDTO;
import org.chainoptim.desktop.features.production.stage.dto.UpdateFactoryStageDTO;
import org.chainoptim.desktop.features.production.stage.model.FactoryStage;
import org.chainoptim.desktop.shared.httphandling.HttpMethod;
import org.chainoptim.desktop.shared.httphandling.RequestBuilder;
import org.chainoptim.desktop.shared.httphandling.RequestHandler;
import org.chainoptim.desktop.shared.httphandling.Result;

import java.net.http.HttpRequest;
import java.util.concurrent.CompletableFuture;

public class FactoryStageWriteServiceImpl implements FactoryStageWriteService {

    private final RequestHandler requestHandler;
    private final RequestBuilder requestBuilder;
    private final TokenManager tokenManager;

    @Inject
    public FactoryStageWriteServiceImpl(RequestHandler requestHandler,
                                        RequestBuilder requestBuilder,
                                        TokenManager tokenManager) {
        this.requestHandler = requestHandler;
        this.requestBuilder = requestBuilder;
        this.tokenManager = tokenManager;
    }

    public CompletableFuture<Result<FactoryStage>> createFactoryStage(CreateFactoryStageDTO stageDTO, Boolean refreshGraph) {
        String routeAddress = "http://localhost:8080/api/v1/factory-stages/create";
        if (Boolean.TRUE.equals(refreshGraph)) routeAddress += "/true";
        else routeAddress += "/false";

        HttpRequest request = requestBuilder.buildWriteRequest(
                HttpMethod.POST, routeAddress, tokenManager.getToken(), stageDTO);
        if (request == null) return requestHandler.getParsingErrorResult();

        return requestHandler.sendRequest(request, new TypeReference<FactoryStage>() {});
    }

    public CompletableFuture<Result<FactoryStage>> updateFactoryStage(UpdateFactoryStageDTO stageDTO) {
        String routeAddress = "http://localhost:8080/api/v1/factory-stages/update";

        HttpRequest request = requestBuilder.buildWriteRequest(
                HttpMethod.PUT, routeAddress, tokenManager.getToken(), stageDTO);
        if (request == null) return requestHandler.getParsingErrorResult();

        return requestHandler.sendRequest(request, new TypeReference<FactoryStage>() {});
    }

    public CompletableFuture<Result<Integer>> deleteFactoryStage(Integer stageId) {
        String routeAddress = "http://localhost:8080/api/v1/factory-stages/delete/" + stageId;

        HttpRequest request = requestBuilder.buildWriteRequest(
                HttpMethod.DELETE, routeAddress, tokenManager.getToken(), null);
        if (request == null) return requestHandler.getParsingErrorResult();

        return requestHandler.sendRequest(request, new TypeReference<Integer>() {});
    }
}
