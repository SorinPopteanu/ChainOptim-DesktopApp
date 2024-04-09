package org.chainoptim.desktop.features.scanalysis.supply.service;

import org.chainoptim.desktop.core.user.util.TokenManager;
import org.chainoptim.desktop.features.scanalysis.supply.model.SupplierPerformance;
import org.chainoptim.desktop.features.scanalysis.supply.service.SupplierPerformanceService;
import org.chainoptim.desktop.shared.util.JsonUtil;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SupplierPerformanceServiceImpl implements SupplierPerformanceService {

    private final HttpClient client = HttpClient.newHttpClient();

    private static final String HEADER_KEY = "Authorization";
    private static final String HEADER_VALUE_PREFIX = "Bearer ";

    public CompletableFuture<Optional<SupplierPerformance>> getSupplierPerformanceBySupplierId(Integer supplierId, boolean refresh) {
        String routeAddress = "http://localhost:8080/api/v1/supplier-performance/supplier/" + supplierId.toString() + (refresh ? "/refresh" : "");

        String jwtToken = TokenManager.getToken();
        if (jwtToken == null) return new CompletableFuture<>();
        String headerValue = HEADER_VALUE_PREFIX + jwtToken;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(routeAddress))
                .GET()
                .headers(HEADER_KEY, headerValue)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != HttpURLConnection.HTTP_OK) return Optional.<SupplierPerformance>empty();
                    try {
                        SupplierPerformance supplierPerformance = JsonUtil.getObjectMapper().readValue(response.body(), SupplierPerformance.class);
                        return Optional.of(supplierPerformance);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Optional.<SupplierPerformance>empty();
                    }
                });
    }
}