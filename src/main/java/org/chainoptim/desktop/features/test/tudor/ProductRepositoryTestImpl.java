package org.chainoptim.desktop.features.test.tudor;

import com.fasterxml.jackson.core.type.TypeReference;
import org.chainoptim.desktop.features.product.model.Product;
import org.chainoptim.desktop.shared.util.JsonUtil;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProductRepositoryTestImpl implements ProductRepositoryTest {

    private final HttpClient client = HttpClient.newHttpClient();

    public CompletableFuture<Optional<List<Product>>> getProductsByOrganizationId(Integer organizationId) {
        String routeAddress = "http://localhost:8080/api/products/organizations/" + organizationId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(routeAddress))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                        try {
                            List<Product> products = JsonUtil.getObjectMapper().readValue(response.body(), new TypeReference<List<Product>>() {});
                            return Optional.of(products);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return Optional.<List<Product>>empty();
                });
    }

    public Optional<Product> getProductWithStages(Integer productId) {
        String routeAddress = "http://localhost:8080/api/products/" + productId.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(routeAddress))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                String responseBody = response.body();
                Product product = JsonUtil.getObjectMapper().readValue(responseBody, Product.class);
                return Optional.of(product);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
