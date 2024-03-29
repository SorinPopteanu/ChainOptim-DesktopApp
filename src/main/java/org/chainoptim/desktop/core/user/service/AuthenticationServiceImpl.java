package org.chainoptim.desktop.core.user.service;

import com.google.inject.Inject;
import org.chainoptim.desktop.core.context.TenantContext;
import org.chainoptim.desktop.core.main.service.NavigationServiceImpl;
import org.chainoptim.desktop.core.user.util.TokenManager;

import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/*
 * Manager of Authentication. Uses API endpoints to validate credentials on login
 * or validate JWT token on demand
 *
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private final HttpClient client;

    @Inject
    public AuthenticationServiceImpl(HttpClient client) {
        this.client = client;
    }

    public boolean login(String username, String password) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password)))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);
                String jwtToken = jsonResponse.getString("accessToken");
                TokenManager.saveToken(jwtToken);
                return true;
            } else {
                return false;
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Call API endpoint to validate JWT token
    public boolean validateJWTToken(String jwtToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/validate-token"))
                .POST(HttpRequest.BodyPublishers.ofString(jwtToken))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Call API endpoint to get username from token
    public Optional<String> getUsernameFromJWTToken(String jwtToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/get-username-from-token"))
                .POST(HttpRequest.BodyPublishers.ofString(jwtToken))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body().describeConstable();
            } else {
                return Optional.empty();
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public void logout() {
        // Clear JWT Token from storage, TenantContext and ViewCache from memory
        TokenManager.removeToken();
        TenantContext.setCurrentUser(null);
        NavigationServiceImpl.invalidateViewCache();
    }
}
