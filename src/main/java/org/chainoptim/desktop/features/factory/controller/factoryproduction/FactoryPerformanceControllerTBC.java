package org.chainoptim.desktop.features.factory.controller.factoryproduction;

import org.chainoptim.desktop.features.factory.model.Factory;
import org.chainoptim.desktop.features.scanalysis.productionhistory.model.FactoryProductionHistory;
import org.chainoptim.desktop.features.scanalysis.productionhistory.service.FactoryProductionHistoryService;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.util.DataReceiver;
import com.google.inject.Inject;
import javafx.application.Platform;

import java.util.Optional;

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

    private Optional<FactoryProductionHistory> handleProductionHistoryResponse(Optional<FactoryProductionHistory> productionHistoryOptional) {
        Platform.runLater(() -> {
            if (productionHistoryOptional.isEmpty()) {
                fallbackManager.setErrorMessage("Failed to load production history");
                return;
            }
            productionHistory = productionHistoryOptional.get();
            fallbackManager.setLoading(false);

            System.out.println("Production History: " + productionHistory);

        });
        return productionHistoryOptional;
    }

    private Optional<FactoryProductionHistory> handleProductionHistoryException(Throwable ex) {
        Platform.runLater(() -> {
            fallbackManager.setErrorMessage("Failed to load production history");
            ex.printStackTrace();
        });
        return Optional.empty();
    }
}
