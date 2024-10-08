package org.chainoptim.desktop.features.goods.product.controller.productproduction;

import org.chainoptim.desktop.core.main.abstraction.ControllerFactory;
import org.chainoptim.desktop.core.main.context.TenantContext;
import org.chainoptim.desktop.core.tenant.user.model.User;
import org.chainoptim.desktop.features.goods.product.model.TabsActionListener;
import org.chainoptim.desktop.features.goods.stage.dto.CreateStageDTO;
import org.chainoptim.desktop.features.goods.stage.model.Stage;
import org.chainoptim.desktop.features.goods.stage.service.StageWriteService;
import org.chainoptim.desktop.features.goods.productgraph.service.ProductProductionGraphService;
import org.chainoptim.desktop.shared.common.ui.select.SelectProductController;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.httphandling.Result;
import org.chainoptim.desktop.shared.util.resourceloader.FXMLLoaderService;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateProductStageController implements Initializable {

    private final StageWriteService stageWriteService;
    private final ProductProductionGraphService graphService;
    private final FXMLLoaderService fxmlLoaderService;
    private final ControllerFactory controllerFactory;
    private final FallbackManager fallbackManager;

    @Setter
    private TabsActionListener actionListener;

    @FXML
    private StackPane fallbackContainer;

    @FXML
    private StackPane selectProductContainer;
    private SelectProductController selectProductController;
    @FXML
    private TextField nameField;

    @Inject
    public CreateProductStageController(
            StageWriteService stageWriteService,
            ProductProductionGraphService graphService,
            FallbackManager fallbackManager,
            FXMLLoaderService fxmlLoaderService,
            ControllerFactory controllerFactory
    ) {
        this.stageWriteService = stageWriteService;
        this.graphService = graphService;
        this.fxmlLoaderService = fxmlLoaderService;
        this.controllerFactory = controllerFactory;
        this.fallbackManager = fallbackManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFallbackManager();
        loadSelectProductView();
    }

    private void loadFallbackManager() {
        Node fallbackView = fxmlLoaderService.loadView(
                "/org/chainoptim/desktop/shared/fallback/FallbackManagerView.fxml",
                controllerFactory::createController
        );
        fallbackContainer.getChildren().add(fallbackView);
    }

    private void loadSelectProductView() {
        // Initialize time selection input view
        FXMLLoader selectProductLoader = fxmlLoaderService.setUpLoader(
                "/org/chainoptim/desktop/shared/common/ui/select/SelectProductView.fxml",
                controllerFactory::createController
        );
        try {
            Node selectProductView = selectProductLoader.load();
            selectProductController = selectProductLoader.getController();
            selectProductController.initialize();
            selectProductContainer.getChildren().add(selectProductView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmit() {
        fallbackManager.reset();
        fallbackManager.setLoading(true);

        User currentUser = TenantContext.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        CreateStageDTO stageDTO = getStageDTO(currentUser.getOrganization().getId());
        System.out.println(stageDTO);

        stageWriteService.createStage(stageDTO)
                .thenApply(this::handleCreateStageResponse)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return new Result<>();
                });
    }

    private Result<Stage> handleCreateStageResponse(Result<Stage> result) {
        Platform.runLater(() -> {
            if (result.getError() != null) {
                fallbackManager.setErrorMessage("Failed to create stage.");
                return;
            }
            Stage stage = result.getData();
            fallbackManager.setLoading(false);

            // Refresh product graph
            graphService.refreshProductGraph(stage.getProductId())
                    .thenApply(graphResult -> {
                        Platform.runLater(() -> {
                            if (graphResult.getError() != null) {
                                fallbackManager.setErrorMessage("Failed to refresh product graph");
                            }

                            if (actionListener != null && graphResult.getData() != null) {
                                actionListener.onAddStage(graphResult.getData());
                            }
                        });
                        return graphResult;
                    });
        });

        return result;
    }

    private CreateStageDTO getStageDTO(Integer organizationId) {
        CreateStageDTO stageDTO = new CreateStageDTO();

        Integer productId = selectProductController.getSelectedProduct().getId();
        stageDTO.setProductId(productId);
        stageDTO.setOrganizationId(organizationId);

        stageDTO.setName(nameField.getText());
        return stageDTO;
    }

}
