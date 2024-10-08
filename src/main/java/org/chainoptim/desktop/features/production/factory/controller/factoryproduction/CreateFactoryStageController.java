package org.chainoptim.desktop.features.production.factory.controller.factoryproduction;

import org.chainoptim.desktop.core.main.context.TenantContext;
import org.chainoptim.desktop.core.tenant.user.model.User;
import org.chainoptim.desktop.features.production.stage.dto.CreateFactoryStageDTO;
import org.chainoptim.desktop.features.production.stage.model.FactoryStage;
import org.chainoptim.desktop.features.production.stage.service.FactoryStageWriteService;
import org.chainoptim.desktop.features.production.analysis.factorygraph.service.FactoryProductionGraphService;
import org.chainoptim.desktop.shared.common.ui.select.SelectDurationController;
import org.chainoptim.desktop.shared.common.ui.select.SelectFactoryController;
import org.chainoptim.desktop.shared.common.ui.select.SelectStageController;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.httphandling.Result;
import org.chainoptim.desktop.shared.util.resourceloader.CommonViewsLoader;

import lombok.Setter;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class CreateFactoryStageController implements Initializable {

    // Services
    private final FactoryStageWriteService factoryStageWriteService;
    private final FactoryProductionGraphService graphService;
    private final CommonViewsLoader commonViewsLoader;

    // Listeners
    @Setter
    private TabsActionListener actionListener;

    // State
    private final FallbackManager fallbackManager;

    // FXML
    @FXML
    private StackPane fallbackContainer;

    @FXML
    private StackPane selectStageContainer;
    private SelectStageController selectStageController;
    @FXML
    private StackPane selectFactoryContainer;
    private SelectFactoryController selectFactoryController;
    @FXML
    private TextField capacityField;
    @FXML
    private StackPane durationInputContainer;
    private SelectDurationController selectDurationController;
    @FXML
    private TextField priorityField;
    @FXML
    private TextField minimumRequiredCapacityField;

    @Inject
    public CreateFactoryStageController(
            FactoryStageWriteService factoryStageWriteService,
            FactoryProductionGraphService graphService,
            FallbackManager fallbackManager,
            CommonViewsLoader commonViewsLoader
    ) {
        this.factoryStageWriteService = factoryStageWriteService;
        this.graphService = graphService;
        this.commonViewsLoader = commonViewsLoader;
        this.fallbackManager = fallbackManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commonViewsLoader.loadFallbackManager(fallbackContainer);
        selectStageController = commonViewsLoader.loadSelectStageView(selectStageContainer);
        selectStageController.initialize();
        selectFactoryController = commonViewsLoader.loadSelectFactoryView(selectFactoryContainer);
        selectFactoryController.initialize();
        selectDurationController = commonViewsLoader.loadSelectDurationView(durationInputContainer);
    }

    @FXML
    private void handleSubmit() {
        fallbackManager.reset();
        fallbackManager.setLoading(true);

        User currentUser = TenantContext.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        CreateFactoryStageDTO stageDTO = getStageDTO();

        factoryStageWriteService.createFactoryStage(stageDTO, true)
                .thenApply(this::handleCreateStageResponse)
                .exceptionally(this::handleCreateStageException);
    }

    private Result<FactoryStage> handleCreateStageResponse(Result<FactoryStage> result) {
        Platform.runLater(() -> {
            if (result.getError() != null) {
                fallbackManager.setErrorMessage("Failed to create stage.");
                return;
            }
            FactoryStage stage = result.getData();
            fallbackManager.setLoading(false);

            graphService.refreshFactoryGraph(selectFactoryController.getSelectedFactory().getId())
                    .thenApply(graphResult -> {
                        Platform.runLater(() -> {
                            if (graphResult.getError() != null) {
                                fallbackManager.setErrorMessage("Failed to refresh factory graph");
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

    private Result<FactoryStage> handleCreateStageException(Throwable ex) {
        Platform.runLater(() -> {
            fallbackManager.setErrorMessage("Failed to create stage.");
        });
        return new Result<>();
    }

    private CreateFactoryStageDTO getStageDTO() {
        CreateFactoryStageDTO stageDTO = new CreateFactoryStageDTO();

        Integer stageId = selectStageController.getSelectedStage().getId();
        stageDTO.setStageId(stageId);
        Integer factoryId = selectFactoryController.getSelectedFactory().getId();
        stageDTO.setFactoryId(factoryId);

        stageDTO.setCapacity(parseFloat(capacityField.getText()));
        stageDTO.setDuration(selectDurationController.getTimeSeconds());
        stageDTO.setPriority(parseInt(priorityField.getText()));
        stageDTO.setMinimumRequiredCapacity(parseFloat(minimumRequiredCapacityField.getText()));

        return stageDTO;
    }

}
