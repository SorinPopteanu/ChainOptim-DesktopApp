package org.chainoptim.desktop.features.product.controller;

import org.chainoptim.desktop.MainApplication;
import org.chainoptim.desktop.core.abstraction.ControllerFactory;
import org.chainoptim.desktop.core.main.service.CurrentSelectionService;
import org.chainoptim.desktop.features.product.model.Product;
import org.chainoptim.desktop.features.product.service.ProductService;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.util.DataReceiver;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import org.chainoptim.desktop.shared.util.resourceloader.FXMLLoaderService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    private final ProductService productService;
    private final CurrentSelectionService currentSelectionService;
    private final FXMLLoaderService fxmlLoaderService;
    private final ControllerFactory controllerFactory;
    private final FallbackManager fallbackManager;

    private Product product;

    @FXML
    private StackPane fallbackContainer;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab overviewTab;
    @FXML
    private Tab productionTab;
    @FXML
    private Tab evaluationTab;

    @FXML
    private Label productName;
    @FXML
    private Label productDescription;

    @Inject
    public ProductController(ProductService productService,
                             FXMLLoaderService fxmlLoaderService,
                             ControllerFactory controllerFactory,
                             CurrentSelectionService currentSelectionService,
                             FallbackManager fallbackManager) {
        this.productService = productService;
        this.currentSelectionService = currentSelectionService;
        this.fxmlLoaderService = fxmlLoaderService;
        this.controllerFactory = controllerFactory;
        this.fallbackManager = fallbackManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFallbackManager();
        setupListeners();

        Integer productId = currentSelectionService.getSelectedId();
        if (productId != null) {
            loadProduct(productId);
        } else {
            System.out.println("Missing product id.");
            fallbackManager.setErrorMessage("Failed to load product: missing product ID.");
        }
    }

    private void loadFallbackManager() {
        // Load view into fallbackContainer
        Node fallbackView = fxmlLoaderService.loadView(
                "/org/chainoptim/desktop/shared/fallback/FallbackManagerView.fxml",
                controllerFactory::createController
        );
        fallbackContainer.getChildren().add(fallbackView);
    }

    private void setupListeners() {
        overviewTab.selectedProperty().addListener((observable, wasSelected, isNowSelected) -> {
            if (Boolean.TRUE.equals(isNowSelected) && overviewTab.getContent() == null) {
                loadTabContent(overviewTab, "/org/chainoptim/desktop/features/product/ProductOverviewView.fxml", this.product);
            }
        });
        productionTab.selectedProperty().addListener((observable, wasSelected, isNowSelected) -> {
            if (Boolean.TRUE.equals(isNowSelected) && productionTab.getContent() == null) {
                loadTabContent(productionTab, "/org/chainoptim/desktop/features/product/ProductProductionView.fxml", this.product);
            }
        });
        evaluationTab.selectedProperty().addListener((observable, wasSelected, isNowSelected) -> {
            if (Boolean.TRUE.equals(isNowSelected) && evaluationTab.getContent() == null) {
                loadTabContent(evaluationTab, "/org/chainoptim/desktop/features/product/ProductEvaluationView.fxml", this.product);
            }
        });

        fallbackManager.isEmptyProperty().addListener((observable, oldValue, newValue) -> {
            tabPane.setVisible(newValue);
            tabPane.setManaged(newValue);
            fallbackContainer.setVisible(!newValue);
            fallbackContainer.setManaged(!newValue);
        });
    }

    private void loadProduct(Integer productId) {
        fallbackManager.reset();
        fallbackManager.setLoading(true);

        productService.getProductWithStages(productId)
                .thenApply(this::handleProductResponse)
                .exceptionally(this::handleProductException)
                .thenRun(() -> Platform.runLater(() -> fallbackManager.setLoading(false)));
    }

    private Optional<Product> handleProductResponse(Optional<Product> productOptional) {
        Platform.runLater(() -> {
            if (productOptional.isEmpty()) {
                fallbackManager.setErrorMessage("Failed to load product.");
                return;
            }
            this.product = productOptional.get();
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            System.out.println("Product: " + product);

            // Load overview tab
            loadTabContent(overviewTab, "/org/chainoptim/desktop/features/product/ProductOverviewView.fxml", this.product);
        });

        return productOptional;
    }

    private Optional<Product> handleProductException(Throwable ex) {
        Platform.runLater(() -> fallbackManager.setErrorMessage("Failed to load product."));
        return Optional.empty();
    }

    private void loadTabContent(Tab tab, String fxmlFilepath, Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilepath));
            loader.setControllerFactory(MainApplication.injector::getInstance);
            Node content = loader.load();
            DataReceiver<Product> controller = loader.getController();
            controller.setData(product);
            tab.setContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditProduct() {
        System.out.println("Edit Product Working");
    }

}
