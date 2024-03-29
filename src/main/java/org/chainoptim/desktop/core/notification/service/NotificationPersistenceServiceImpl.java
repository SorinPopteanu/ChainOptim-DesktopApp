package org.chainoptim.desktop.core.notification.service;

import org.chainoptim.desktop.core.notification.model.Notification;
import org.chainoptim.desktop.core.user.util.TokenManager;
import org.chainoptim.desktop.features.factory.dto.FactoriesSearchDTO;
import org.chainoptim.desktop.shared.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NotificationPersistenceServiceImpl implements NotificationPersistenceService {

    private final HttpClient client = HttpClient.newHttpClient();

    private static final String HEADER_KEY = "Authorization";
    private static final String HEADER_VALUE_PREFIX = "Bearer ";

    public CompletableFuture<Optional<List<Notification>>> getNotificationsByUserId(String userId) {
        String routeAddress = "http://localhost:8080/api/v1/notifications/user/" + userId;

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
                    if (response.statusCode() != HttpURLConnection.HTTP_OK) return Optional.<List<Notification>>empty();
                    try {
                        List<Notification> notifications = JsonUtil.getObjectMapper().readValue(response.body(), new TypeReference<List<Notification>>() {});
                        return Optional.of(notifications);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Optional.<List<Notification>>empty();
                    }
                });
    }
}
