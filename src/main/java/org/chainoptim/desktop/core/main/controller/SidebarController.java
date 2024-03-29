package org.chainoptim.desktop.core.main.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import org.chainoptim.desktop.core.main.model.SidebarButton;
import org.chainoptim.desktop.core.main.service.NavigationService;
import org.chainoptim.desktop.core.user.service.AuthenticationService;
import org.chainoptim.desktop.core.main.service.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 * Controller for Sidebar
 * Responsible for creating its buttons with NavigationService, img/ icons and sidebar.css
 * and toggling its expanded/collapsed view
 */
public class SidebarController {

    @Setter
    private NavigationService navigationService;
    private final AuthenticationService authenticationService;

    @Inject
    public SidebarController(NavigationService navigationService, AuthenticationService authenticationService) {
        this.navigationService = navigationService;
        this.authenticationService = authenticationService;
    }

    // UI elements
    @FXML
    private AnchorPane sidebar;
    @FXML
    private HBox dashboardHBox;
    @FXML
    private VBox buttonContainer;

    private final List<SidebarButton> navigationButtons = new ArrayList<>();
    private static final String ICONS_PATH = "/img/";
    @FXML
    public Button toggleButton;
    @FXML
    public Button backButton;
    @FXML
    public Button logoutButton;

    // Configuration
    private final List<String> orderedKeys = List.of("Overview", "Organization", "Products", "Factories", "Warehouses", "Suppliers", "Clients", "Settings");
    private final Map<String, String> buttonIconMap = Map.ofEntries(
            Map.entry("Overview", "globe-solid.png"),
            Map.entry("Organization", "building-solid.png"),
            Map.entry("Products", "box-solid.png"),
            Map.entry("Factories", "industry-solid.png"),
            Map.entry("Warehouses", "warehouse-solid.png"),
            Map.entry("Suppliers", "truck-arrow-right-solid.png"),
            Map.entry("Clients", "universal-access-solid.png"),
            Map.entry("Settings", "gear-solid.png"),

            Map.entry("Account", "user-solid.png"),
            Map.entry("Back", "arrow-left-solid.png"),
            Map.entry("Toggle", "bars-solid.png"),
            Map.entry("Logout", "right-from-bracket-solid.png")
    );
    private static final double COLLAPSED_WIDTH = 52;
    private static final double EXPANDED_WIDTH = 256;
    private boolean isSidebarMinimized = false;

    // Initialization
    @FXML
    public void initialize() {
        initializeNavigationButtons();
        createSidebarButtons();

        // Navigate to Overview
        navigationService.switchView("Overview", true);

        // Back, Toggle and Logout buttons
        setButtonGraphic(backButton, ICONS_PATH + buttonIconMap.get("Back"));
        backButton.setOnAction(e -> navigationService.goBack());
        setButtonGraphic(toggleButton, ICONS_PATH + buttonIconMap.get("Toggle"));
        toggleButton.setOnAction(e -> toggleSidebar());
        setButtonGraphic(logoutButton, ICONS_PATH + buttonIconMap.get("Logout"));
    }

    private void initializeNavigationButtons() {
        orderedKeys.forEach(key -> {
            String iconPath = ICONS_PATH + buttonIconMap.get(key);
            Runnable action = () -> navigationService.switchView(key, true);
            navigationButtons.add(new SidebarButton(key, iconPath, action));
        });
    }

    private void createSidebarButtons() {
        navigationButtons.forEach(model -> {
            Button button = new Button(model.getName());
            button.setOnAction(e -> model.getAction().run());
            setButtonGraphic(button, model.getIconPath());
            button.getStyleClass().add("sidebar-button");
            button.setMaxWidth(Double.MAX_VALUE);
            buttonContainer.getChildren().add(button);
        });
    }

    public void setButtonGraphic(Button button, String imagePath){
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        button.setGraphic(imageView);
        button.setGraphicTextGap(10);
    }

    // Sidebar toggling
    private void toggleSidebar() {
        if (isSidebarMinimized) {
            expandSidebar();
        } else {
            collapseSidebar();
        }
        isSidebarMinimized = !isSidebarMinimized;
    }

    private void collapseSidebar() {
        // Reduce width and hide everything but the icons
        sidebar.setPrefWidth(COLLAPSED_WIDTH);
        dashboardHBox.getChildren().forEach(child -> child.setVisible(false));
        dashboardHBox.getChildren().forEach(child -> child.setManaged(false));
        toggleButton.setVisible(true);
        toggleButton.setManaged(true);

        buttonContainer.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        });
        logoutButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void expandSidebar() {
        sidebar.setPrefWidth(EXPANDED_WIDTH);
        dashboardHBox.getChildren().forEach(child -> child.setVisible(true));
        dashboardHBox.getChildren().forEach(child -> child.setManaged(true));

        buttonContainer.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                button.setContentDisplay(ContentDisplay.LEFT);
            }
        });
        logoutButton.setContentDisplay(ContentDisplay.LEFT);
    }

    // Handle logout
    @FXML
    private void handleLogout() {
        authenticationService.logout(); // Clear JWT token from storage

        // Switch back to login scene
        try {
            SceneManager.loadLoginScene();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
