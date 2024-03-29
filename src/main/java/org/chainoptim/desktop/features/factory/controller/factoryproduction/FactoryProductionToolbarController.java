package org.chainoptim.desktop.features.factory.controller.factoryproduction;

import org.chainoptim.desktop.MainApplication;
import org.chainoptim.desktop.features.factory.model.Factory;
import org.chainoptim.desktop.features.factory.model.ProductionToolbarActionListener;
import org.chainoptim.desktop.features.scanalysis.resourceallocation.model.AllocationPlan;
import org.chainoptim.desktop.features.scanalysis.resourceallocation.service.ResourceAllocationService;
import org.chainoptim.desktop.shared.common.uielements.SelectDurationController;
import org.chainoptim.desktop.shared.util.resourceloader.FXMLLoaderService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.Setter;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.chainoptim.desktop.shared.util.JsonUtil.prepareJsonString;

public class FactoryProductionToolbarController {

    private final ResourceAllocationService resourceAllocationService;
    private final FXMLLoaderService fxmlLoaderService;

    @Setter
    private ProductionToolbarActionListener actionListener;

    private Factory factory;
    private AllocationPlan allocationPlan;

    private WebView webView;

    // - Edit Configuration
    @FXML
    private Button toggleEditConfigurationButton;
    @FXML
    private VBox editConfigurationContentVBox;
    @FXML
    private Button addStageButton;
    @FXML
    private Button updateStageButton;
    @FXML
    private Button deleteStageButton;
    @FXML
    private Button addConnectionButton;
    @FXML
    private Button deleteConnectionButton;

    // - Display Info
    @FXML
    private Button toggleDisplayInfoButton;
    @FXML
    private VBox displayInfoContentVBox;
    @FXML
    private CheckBox quantitiesCheckBox;
    @FXML
    private CheckBox capacityCheckBox;
    @FXML
    private CheckBox priorityCheckBox;

    // - Resource Allocation
    @FXML
    private Button toggleResourceAllocationButton;
    @FXML
    private VBox resourceAllocationContentBox;
    @FXML
    private StackPane durationInputContainer;
    private SelectDurationController selectDurationController;
    @FXML
    private Button viewAllocationPlanButton;

    // - Seek Resources
    @FXML
    private Button toggleSeekResourcesButton;
    @FXML
    private VBox seekResourcesContentBox;

    // - Icons
    private Image addImage;
    private Image updateImage;
    private Image deleteImage;
    private Image angleUpImage;
    private Image angleDownImage;

    @Inject
    public FactoryProductionToolbarController(ResourceAllocationService resourceAllocationService,
                                              FXMLLoaderService fxmlLoaderService) {
        this.resourceAllocationService = resourceAllocationService;
        this.fxmlLoaderService = fxmlLoaderService;
    }

    public void initialize(WebView webView, Factory factory) {
        this.webView = webView;
        this.factory = factory;

        initializeToolbarUI();
        setupCheckboxListeners();
    }

    private void setupCheckboxListeners() {
        quantitiesCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> webView.getEngine().executeScript("window.renderInfo('quantities', " + newValue + ");"));

        capacityCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> webView.getEngine().executeScript("window.renderInfo('capacities', " + newValue + ");"));

        priorityCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> webView.getEngine().executeScript("window.renderInfo('priorities', " + newValue + ");"));
    }

    @FXML
    private void handleAllocateResources() {
        Float durationSeconds = selectDurationController.getTimeSeconds();

        resourceAllocationService
                .allocateFactoryResources(factory.getId(), durationSeconds)
                .thenApply(this::handleAllocationPlanResponse);
    }

    private AllocationPlan handleAllocationPlanResponse(Optional<AllocationPlan> allocationPlanOptional) {
        if (allocationPlanOptional.isEmpty()) {
            return new AllocationPlan();
        }
        allocationPlan = allocationPlanOptional.get();
        updateViewAllocationPlanButtonVisibility();

        String escapedJsonString = prepareJsonString(allocationPlan);
        String script = "window.renderResourceAllocations('" + escapedJsonString + "');";

        // Ensure script execution happens on the JavaFX Application Thread
        Platform.runLater(() -> {
            try {
                webView.getEngine().executeScript(script);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return allocationPlan;
    }

    // Toolbar
    private void initializeToolbarUI() {
        // Initialize expand/collapse buttons
        angleUpImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/angle-up-solid.png")));
        angleDownImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/angle-down-solid.png")));
        addImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/plus.png")));
        updateImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/pen-to-square-solid.png")));
        deleteImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/trash-solid.png")));

        toggleEditConfigurationButton.setGraphic(createImageView(angleUpImage));
        toggleDisplayInfoButton.setGraphic(createImageView(angleUpImage));
        toggleResourceAllocationButton.setGraphic(createImageView(angleUpImage));
        toggleSeekResourcesButton.setGraphic(createImageView(angleUpImage));
        addStageButton.setGraphic(createImageView(addImage));
        updateStageButton.setGraphic(createImageView(updateImage));
        deleteStageButton.setGraphic(createImageView(deleteImage));
        addConnectionButton.setGraphic(createImageView(addImage));
        deleteConnectionButton.setGraphic(createImageView(deleteImage));

        viewAllocationPlanButton.setVisible(false);
        viewAllocationPlanButton.setManaged(false);

        // Initialize time selection input view
        FXMLLoader timeInputLoader = fxmlLoaderService.setUpLoader(
                "/org/chainoptim/desktop/shared/common/uielements/SelectDurationView.fxml",
                MainApplication.injector::getInstance
        );
        try {
            Node timeInputView = timeInputLoader.load();
            selectDurationController = timeInputLoader.getController();
            durationInputContainer.getChildren().add(timeInputView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Toggle Toolbar sections
    @FXML
    private void toggleEditConfigurationSection(ActionEvent event) {
        toggleSection(editConfigurationContentVBox, toggleEditConfigurationButton);
    }

    @FXML
    private void toggleDisplayInfoSection(ActionEvent event) {
        toggleSection(displayInfoContentVBox, toggleDisplayInfoButton);
    }

    @FXML
    private void toggleResourceAllocationSection(ActionEvent event) {
        toggleSection(resourceAllocationContentBox, toggleResourceAllocationButton);
    }

    @FXML
    private void toggleSeekResourcesSection(ActionEvent event) {
        toggleSection(seekResourcesContentBox, toggleSeekResourcesButton);
    }

    private void toggleSection(VBox sectionVBox, Button sectionToggleButton) {
        boolean isVisible = sectionVBox.isVisible();
        sectionVBox.setVisible(!isVisible);
        sectionVBox.setManaged(!isVisible);
        if (isVisible) {
            sectionToggleButton.setGraphic(createImageView(angleDownImage));
        } else {
            sectionToggleButton.setGraphic(createImageView(angleUpImage));
        }
    }

    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(12);
        imageView.setFitHeight(12);
        return imageView;
    }

    private void updateViewAllocationPlanButtonVisibility() {
        // Update button visibility based on whether an allocation plan is available
        System.out.println("Allocation plan: " + allocationPlan);
        boolean isAllocationPlanAvailable = allocationPlan != null;
        viewAllocationPlanButton.setVisible(isAllocationPlanAvailable);
        viewAllocationPlanButton.setManaged(isAllocationPlanAvailable);
    }

    @FXML
    private void openAddStageAction() {
        actionListener.onOpenAddStageRequested();
    }

    @FXML
    private void openUpdateStageAction() {
        actionListener.onOpenUpdateStageRequested();
    }

    @FXML
    private void deleteStageAction() {
//        actionListener.onDeleteStageRequested();
    }

    @FXML
    private void addConnectionAction() {
    }

    @FXML
    private void deleteConnectionAction() {
    }

    @FXML
    private void openAllocationPlan() {
        actionListener.onOpenAllocationPlanRequested(allocationPlan);
    }
}
