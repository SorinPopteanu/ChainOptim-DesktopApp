package org.chainoptim.desktop.features.scanalysis.factorygraph.service;

import org.chainoptim.desktop.features.scanalysis.factorygraph.model.FactoryProductionGraph;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface FactoryProductionGraphService {

    CompletableFuture<List<FactoryProductionGraph>> getFactoryGraphById(Integer factoryId);
    CompletableFuture<Optional<FactoryProductionGraph>> refreshFactoryGraph(Integer factoryId);
}
