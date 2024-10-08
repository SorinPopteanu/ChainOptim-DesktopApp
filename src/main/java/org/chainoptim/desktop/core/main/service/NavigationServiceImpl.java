package org.chainoptim.desktop.core.main.service;

import org.chainoptim.desktop.core.main.abstraction.ControllerFactory;
import org.chainoptim.desktop.core.main.abstraction.ThreadRunner;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.util.DataReceiver;
import org.chainoptim.desktop.shared.util.resourceloader.FXMLLoaderService;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsible for handling app navigation throughout the app
 * Loads views on demand and caches them, including dynamic routes
 * Also records navigation history for back navigation
 */
public class NavigationServiceImpl implements NavigationService {

    private final FXMLLoaderService fxmlLoaderService;
    private final ControllerFactory controllerFactory;
    private final ThreadRunner threadRunner;
    private final FallbackManager fallbackManager;

    @Inject
    public NavigationServiceImpl(FXMLLoaderService fxmlLoaderService,
                                 ControllerFactory controllerFactory,
                                 ThreadRunner threadRunner,
                                 FallbackManager fallbackManager) {
        this.fxmlLoaderService = fxmlLoaderService;
        this.controllerFactory = controllerFactory;
        this.threadRunner = threadRunner;
        this.fallbackManager = fallbackManager;
    }

    @Setter
    private StackPane mainContentArea;

    private String currentViewKey;
    private List<String> previousViewKeys;

    @Getter
    private static final Map<String, Node> viewCache = new HashMap<>();

    private final Map<String, String> viewMap = Map.ofEntries(
            Map.entry("Overview", "/org/chainoptim/desktop/core/overview/OverviewView.fxml"),

            Map.entry("Organization", "/org/chainoptim/desktop/core/organization/OrganizationView.fxml"),
            Map.entry("Update-Organization", "/org/chainoptim/desktop/core/organization/UpdateOrganizationView.fxml"),
            Map.entry("Add-New-Members", "/org/chainoptim/desktop/core/organization/AddNewMembersView.fxml"),

            Map.entry("Products", "/org/chainoptim/desktop/features/goods/ProductsView.fxml"),
            Map.entry("Product", "/org/chainoptim/desktop/features/goods/ProductView.fxml"),
            Map.entry("Create-Product", "/org/chainoptim/desktop/features/goods/CreateProductView.fxml"),
            Map.entry("Update-Product", "/org/chainoptim/desktop/features/goods/UpdateProductView.fxml"),
            Map.entry("Stages", "/org/chainoptim/desktop/features/productpipeline/StagesView.fxml"),
            Map.entry("Components", "/org/chainoptim/desktop/features/productpipeline/ComponentsView.fxml"),
            Map.entry("Component", "/org/chainoptim/desktop/features/productpipeline/ComponentView.fxml"),
            Map.entry("Create-Component", "/org/chainoptim/desktop/features/productpipeline/CreateComponentView.fxml"),
            Map.entry("Update-Component", "/org/chainoptim/desktop/features/productpipeline/UpdateComponentView.fxml"),

            Map.entry("Factories", "/org/chainoptim/desktop/features/production/FactoriesView.fxml"),
            Map.entry("Factory", "/org/chainoptim/desktop/features/production/FactoryView.fxml"),
            Map.entry("Create-Factory", "/org/chainoptim/desktop/features/production/CreateFactoryView.fxml"),
            Map.entry("Update-Factory", "/org/chainoptim/desktop/features/production/UpdateFactoryView.fxml"),
            Map.entry("Factory-Inventory", "/org/chainoptim/desktop/features/production/FactoryInventoryView.fxml"),
            Map.entry("Factory-Performances", "/org/chainoptim/desktop/features/production/FactoryPerformancesView.fxml"),

            Map.entry("Warehouses", "/org/chainoptim/desktop/features/storage/WarehousesView.fxml"),
            Map.entry("Warehouse", "/org/chainoptim/desktop/features/storage/WarehouseView.fxml"),
            Map.entry("Create-Warehouse", "/org/chainoptim/desktop/features/storage/CreateWarehouseView.fxml"),
            Map.entry("Update-Warehouse", "/org/chainoptim/desktop/features/storage/UpdateWarehouseView.fxml"),
            Map.entry("Warehouse-Inventory", "/org/chainoptim/desktop/features/storage/WarehouseInventoryView.fxml"),

            Map.entry("Suppliers", "/org/chainoptim/desktop/features/supply/SuppliersView.fxml"),
            Map.entry("Supplier", "/org/chainoptim/desktop/features/supply/SupplierView.fxml"),
            Map.entry("Create-Supplier", "/org/chainoptim/desktop/features/supply/CreateSupplierView.fxml"),
            Map.entry("Update-Supplier", "/org/chainoptim/desktop/features/supply/UpdateSupplierView.fxml"),
            Map.entry("Supplier-Orders", "/org/chainoptim/desktop/features/supply/SupplierOrdersView.fxml"),
            Map.entry("Supplier-Shipments", "/org/chainoptim/desktop/features/supply/SupplierShipmentsView.fxml"),
            Map.entry("Supplier-Performances", "/org/chainoptim/desktop/features/supply/SupplierPerformancesView.fxml"),

            Map.entry("Clients", "/org/chainoptim/desktop/features/demand/ClientsView.fxml"),
            Map.entry("Client", "/org/chainoptim/desktop/features/demand/ClientView.fxml"),
            Map.entry("Create-Client", "/org/chainoptim/desktop/features/demand/CreateClientView.fxml"),
            Map.entry("Update-Client", "/org/chainoptim/desktop/features/demand/UpdateClientView.fxml"),
            Map.entry("Client-Orders", "/org/chainoptim/desktop/features/demand/ClientOrdersView.fxml"),
            Map.entry("Client-Shipments", "/org/chainoptim/desktop/features/demand/ClientShipmentsView.fxml"),

            Map.entry("Create-Stage", "/org/chainoptim/desktop/features/demand/CreateFactoryStageView.fxml"),

            Map.entry("Settings", "/org/chainoptim/desktop/core/settings/SettingsView.fxml")
    );

    public <T> void switchView(String viewKey, boolean forward, T extraData) {
        // Skip if already there
        if (Objects.equals(currentViewKey, viewKey)) {
            return;
        }

        // Reset fallback state between pages
        fallbackManager.reset();

        // Get view from cache or load it
        Node view = viewCache.computeIfAbsent(viewKey, key -> loadView(viewKey, extraData));

        // Display view
        if (view != null) {
            threadRunner.runLater(() -> mainContentArea.getChildren().setAll(view));
            handleHistory(forward);
            currentViewKey = viewKey;
        }
    }

    private <T> Node loadView(String viewKey, T extraData) {
        // Extract key without dynamic parameter
        String baseViewKey = findBaseKey(viewKey);

        // Get View path
        String viewPath = viewMap.get(baseViewKey);
        if (viewPath == null) {
            System.out.println("View path for " + baseViewKey + " not found.");
            return null;
        }

        FXMLLoader loader = fxmlLoaderService.setUpLoader(viewPath, controllerFactory::createController);
        try {
            Node view = loader.load();
            Object controller = loader.getController();
            if (controller instanceof DataReceiver dataReceiver) {
                dataReceiver.setData(extraData);
            }

            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String findBaseKey(String viewKey) {
        Pattern pattern = Pattern.compile("([^?]+)\\?id=(\\d+)");
        Matcher matcher = pattern.matcher(viewKey);

        String baseViewKey;
        Integer id = null;

        if (matcher.find()) {
            baseViewKey = matcher.group(1);
            id = Integer.valueOf(matcher.group(2));
        } else {
            baseViewKey = viewKey;
        }
        System.out.println("Requested id: " + id);

        return baseViewKey;
    }

    private void handleHistory(boolean forward) {
        // Add to history if forward and remove last otherwise
        if (forward) {
            if (previousViewKeys == null) {
                previousViewKeys = new ArrayList<>();
            }
            if (currentViewKey != null) {
                previousViewKeys.add(currentViewKey);
            }
        } else {
            if (previousViewKeys != null && !previousViewKeys.isEmpty()) {
                previousViewKeys.removeLast();
            }
        }
    }

    public void goBack() {
        if (previousViewKeys != null && !previousViewKeys.isEmpty()) {
            switchView(previousViewKeys.getLast(), false, null);
        }
    }

    public static void invalidateViewCache() {
        viewCache.clear();
    }

    public static void invalidateViewCache(String viewKey) {
        viewCache.remove(viewKey);
    }
}
