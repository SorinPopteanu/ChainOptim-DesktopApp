package org.chainoptim.desktop.features.supplier.controller;

import org.chainoptim.desktop.core.abstraction.ControllerFactory;
import org.chainoptim.desktop.core.main.service.CurrentSelectionService;
import org.chainoptim.desktop.core.main.service.NavigationService;
import org.chainoptim.desktop.core.main.service.NavigationServiceImpl;
import org.chainoptim.desktop.features.supplier.dto.UpdateSupplierDTO;
import org.chainoptim.desktop.features.supplier.model.Supplier;
import org.chainoptim.desktop.features.supplier.service.SupplierService;
import org.chainoptim.desktop.features.supplier.service.SupplierWriteService;
import org.chainoptim.desktop.shared.common.uielements.SelectOrCreateLocationController;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.util.resourceloader.FXMLLoaderService;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UpdateSupplierController implements Initializable {

    private final SupplierService supplierService;
    private final SupplierWriteService supplierWriteService;
    private final NavigationService navigationService;
    private final CurrentSelectionService currentSelectionService;
    private final FXMLLoaderService fxmlLoaderService;
    private final ControllerFactory controllerFactory;
    private final FallbackManager fallbackManager;

    private Supplier supplier;

    private SelectOrCreateLocationController selectOrCreateLocationController;

    @FXML
    private StackPane fallbackContainer;
    @FXML
    private StackPane selectOrCreateLocationContainer;
    @FXML
    private TextField nameField;

    @Inject
    public UpdateSupplierController(
            SupplierService supplierService,
            SupplierWriteService supplierWriteService,
            NavigationService navigationService,
            CurrentSelectionService currentSelectionService,
            FallbackManager fallbackManager,
            FXMLLoaderService fxmlLoaderService,
            ControllerFactory controllerFactory
    ) {
        this.supplierService = supplierService;
        this.supplierWriteService = supplierWriteService;
        this.navigationService = navigationService;
        this.currentSelectionService = currentSelectionService;
        this.fxmlLoaderService = fxmlLoaderService;
        this.controllerFactory = controllerFactory;
        this.fallbackManager = fallbackManager;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        loadFallbackManager();
        loadSelectOrCreateLocation();
        loadSupplier(currentSelectionService.getSelectedId());
    }

    private void loadFallbackManager() {
        Node fallbackView = fxmlLoaderService.loadView(
                "/org/chainoptim/desktop/shared/fallback/FallbackManagerView.fxml",
                controllerFactory::createController
        );
        fallbackContainer.getChildren().add(fallbackView);
    }

    private void loadSelectOrCreateLocation() {
        FXMLLoader loader = fxmlLoaderService.setUpLoader(
                "/org/chainoptim/desktop/shared/common/uielements/SelectOrCreateLocationView.fxml",
                controllerFactory::createController
        );
        try {
            Node selectOrCreateLocationView = loader.load();
            selectOrCreateLocationController = loader.getController();
            selectOrCreateLocationContainer.getChildren().add(selectOrCreateLocationView);
            selectOrCreateLocationController.initialize();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSupplier(Integer supplierId) {
        fallbackManager.reset();
        fallbackManager.setLoading(true);

        supplierService.getSupplierById(supplierId)
                .thenApply(this::handleSupplierResponse)
                .exceptionally(this::handleSupplierException)
                .thenRun(() -> Platform.runLater(() -> fallbackManager.setLoading(false)));
    }

    private Optional<Supplier> handleSupplierResponse(Optional<Supplier> supplierOptional) {
        Platform.runLater(() -> {
            if (supplierOptional.isEmpty()) {
                fallbackManager.setErrorMessage("Failed to load supplier.");
                return;
            }
            supplier = supplierOptional.get();

            nameField.setText(supplier.getName());
            selectOrCreateLocationController.setSelectedLocation(supplier.getLocation());
        });

        return supplierOptional;
    }

    private Optional<Supplier> handleSupplierException(Throwable ex) {
        Platform.runLater(() -> fallbackManager.setErrorMessage("Failed to load supplier."));
        return Optional.empty();
    }

    @FXML
    private void handleSubmit() {
        fallbackManager.reset();
        fallbackManager.setLoading(true);

        UpdateSupplierDTO supplierDTO = getUpdateSupplierDTO();
        System.out.println(supplierDTO);

        supplierWriteService.updateSupplier(supplierDTO)
                .thenAccept(supplierOptional ->
                    Platform.runLater(() -> {
                        if (supplierOptional.isEmpty()) {
                            fallbackManager.setErrorMessage("Failed to create supplier.");
                            return;
                        }
                        fallbackManager.setLoading(false);

                        // Manage navigation, invalidating previous supplier cache
                        Supplier updatedSupplier = supplierOptional.get();
                        String supplierPage = "Supplier?id=" + updatedSupplier.getId();
                        NavigationServiceImpl.invalidateViewCache(supplierPage);
                        currentSelectionService.setSelectedId(updatedSupplier.getId());
                        navigationService.switchView(supplierPage, true);
                    })
                )
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private UpdateSupplierDTO getUpdateSupplierDTO() {
        UpdateSupplierDTO supplierDTO = new UpdateSupplierDTO();
        supplierDTO.setId(supplier.getId());
        supplierDTO.setName(nameField.getText());

        if (selectOrCreateLocationController.isCreatingNewLocation()) {
            supplierDTO.setCreateLocation(true);
            supplierDTO.setLocation(selectOrCreateLocationController.getNewLocationDTO());
        } else {
            supplierDTO.setCreateLocation(false);
            supplierDTO.setLocationId(selectOrCreateLocationController.getSelectedLocation().getId());
        }

        return supplierDTO;
    }
}

