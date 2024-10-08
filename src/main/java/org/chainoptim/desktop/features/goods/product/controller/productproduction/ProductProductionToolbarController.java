package org.chainoptim.desktop.features.goods.product.controller.productproduction;

import org.chainoptim.desktop.core.main.context.TenantSettingsContext;
import org.chainoptim.desktop.features.goods.product.model.ProductionToolbarActionListener;
import org.chainoptim.desktop.features.goods.product.model.Product;
import org.chainoptim.desktop.shared.common.ui.info.InfoLabel;
import org.chainoptim.desktop.shared.enums.Feature;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.Setter;

import java.util.Objects;

public class ProductProductionToolbarController {

    // Listeners
    @Setter
    private ProductionToolbarActionListener actionListener;

    // State
    private Product product;

    private WebView webView;

    // FXML
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
    @FXML
    private InfoLabel stageInfoLabel;

    // - Display Info
    @FXML
    private Button toggleDisplayInfoButton;
    @FXML
    private VBox displayInfoContentVBox;
    @FXML
    private CheckBox quantitiesCheckBox;

    // Icons
    private Image addImage;
    private Image updateImage;
    private Image deleteImage;
    private Image angleUpImage;
    private Image angleDownImage;

    public void initialize(WebView webView, Product product) {
        this.webView = webView;
        this.product = product;

        initializeToolbarUI();
        setupCheckboxListeners();
    }

    private void setupCheckboxListeners() {
        quantitiesCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> webView.getEngine().executeScript("window.renderInfo('quantities', " + newValue + ");"));
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
        addStageButton.setGraphic(createImageView(addImage));
        updateStageButton.setGraphic(createImageView(updateImage));
        deleteStageButton.setGraphic(createImageView(deleteImage));
        addConnectionButton.setGraphic(createImageView(addImage));
        deleteConnectionButton.setGraphic(createImageView(deleteImage));

        stageInfoLabel.setFeatureAndLevel(Feature.PRODUCT_STAGE,
                TenantSettingsContext.getCurrentUserSettings().getGeneralSettings().getInfoLevel());
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

}
