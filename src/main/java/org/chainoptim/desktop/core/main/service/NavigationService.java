package org.chainoptim.desktop.core.main.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import org.chainoptim.desktop.MainApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
 * Service responsible for handling app navigation in SidebarController
 * Loads views on demand and caches them
 *
 */
public class NavigationService {

    @Setter
    private StackPane mainContentArea;

    private String currentViewKey;

    private static final Map<String, Node> viewCache = new HashMap<>();

    @Getter
    private final Map<String, String> viewMap = Map.of(
            "Overview", "/org/chainoptim/desktop/core/main/OverviewView.fxml",
            "Organization", "/org/chainoptim/desktop/core/organization/OrganizationView.fxml",
            "Products", "/org/chainoptim/desktop/features/test/tudor/ProductsView.fxml",
            "Factories", "/org/chainoptim/desktop/features/factory/FactoriesView.fxml",
            "Warehouses", "/org/chainoptim/desktop/features/warehouse/WarehousesView.fxml",
            "Suppliers", "/org/chainoptim/desktop/features/supplier/SuppliersView.fxml"
    );

    public void switchView(String viewKey) {
        // Skip if already there
        if (Objects.equals(currentViewKey, viewKey)) {
            return;
        }

        // Get view from cache or load it
        Node view = viewCache.computeIfAbsent(viewKey, this::loadView);

        // Display view
        if (view != null) {
            Platform.runLater(() -> mainContentArea.getChildren().setAll(view));
            currentViewKey = viewKey;
        }
    }

    private Node loadView(String viewKey) {
        String viewPath = viewMap.get(viewKey);
        if (viewPath == null) {
            System.out.println("View path for " + viewKey + " not found.");
            return null;
        }

        try {
            System.out.println("Loading view: " + viewPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            loader.setControllerFactory(MainApplication.injector::getInstance);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void invalidateViewCache() {
        viewCache.clear();
    }
}