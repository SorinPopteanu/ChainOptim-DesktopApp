package org.chainoptim.desktop.features.production.factory.controller.factoryproduction;

import org.chainoptim.desktop.features.production.factory.model.Factory;
import org.chainoptim.desktop.features.production.analysis.productionhistory.model.FactoryProductionHistory;
import org.chainoptim.desktop.features.production.analysis.productionhistory.service.FactoryProductionHistoryService;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.httphandling.Result;
import org.chainoptim.desktop.shared.util.DataReceiver;
import com.google.inject.Inject;
import javafx.application.Platform;

public class FactoryPerformanceControllerTBC implements DataReceiver<Factory> {

    // Services
    private final FactoryProductionHistoryService productionHistoryService;

    // State
    private final FallbackManager fallbackManager;
    private FactoryProductionHistory productionHistory;

    @Inject
    public FactoryPerformanceControllerTBC(FactoryProductionHistoryService productionHistoryService, FallbackManager fallbackManager) {
        this.productionHistoryService = productionHistoryService;
        this.fallbackManager = fallbackManager;
    }

    @Override
    public void setData(Factory factory) {
        loadProductionHistory(factory.getId());
    }

    private void loadProductionHistory(Integer factoryId) {
        fallbackManager.reset();
        fallbackManager.setLoading(true);

        productionHistoryService.getFactoryProductionHistoryByFactoryId(factoryId)
                .thenApply(this::handleProductionHistoryResponse)
                .exceptionally(this::handleProductionHistoryException);
    }

    private Result<FactoryProductionHistory> handleProductionHistoryResponse(Result<FactoryProductionHistory> result) {
        Platform.runLater(() -> {
            if (result.getError() != null) {
                fallbackManager.setErrorMessage("Failed to load production history");
                return;
            }
            productionHistory = result.getData();
            fallbackManager.setLoading(false);

            System.out.println("Production History: " + productionHistory);

        });
        return result;
    }

    private Result<FactoryProductionHistory> handleProductionHistoryException(Throwable ex) {
        Platform.runLater(() ->
            fallbackManager.setErrorMessage("Failed to load production history"));
        return new Result<>();
    }
}
