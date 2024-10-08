package org.chainoptim.desktop.features.production.factory.controller;

import org.chainoptim.desktop.MainApplication;
import org.chainoptim.desktop.features.production.factory.controller.factoryproduction.FactoryProductionTabsController;
import org.chainoptim.desktop.features.production.factory.controller.factoryproduction.FactoryProductionToolbarController;
import org.chainoptim.desktop.features.production.factory.model.Factory;
import org.chainoptim.desktop.features.production.factory.controller.factoryproduction.ProductionToolbarActionListener;
import org.chainoptim.desktop.features.production.analysis.factorygraph.service.JavaConnector;
import org.chainoptim.desktop.features.production.analysis.resourceallocation.model.AllocationPlan;
import org.chainoptim.desktop.shared.util.DataReceiver;
import org.chainoptim.desktop.shared.util.resourceloader.FXMLLoaderService;

import com.google.inject.Inject;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.util.Objects;

public class FactoryProductionController implements DataReceiver<Factory>, ProductionToolbarActionListener {

    private final FXMLLoaderService fxmlLoaderService;

    private Factory factory;

    private WebView webView;
    private JavaConnector javaConnector;

    private FactoryProductionTabsController productionTabsController;

    @FXML
    private StackPane tabsContainer;
    @FXML
    private StackPane toolbarContainer;

    @Inject
    public FactoryProductionController(FXMLLoaderService fxmlLoaderService) {
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @Override
    public void setData(Factory factory) {
        this.factory = factory;
        initializeWebView();
        loadTabs();
        loadToolbar();
    }

    private void initializeWebView() {
        webView = new WebView();
        webView.getEngine().load(Objects.requireNonNull(getClass().getResource("/html/factorygraph.html")).toExternalForm());
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject jsObject = (JSObject) webView.getEngine().executeScript("window");
                javaConnector = new JavaConnector();
                jsObject.setMember("javaConnector", javaConnector);
            }
        });
    }

    private void loadTabs() {
        FXMLLoader loader = fxmlLoaderService.setUpLoader("/org/chainoptim/desktop/features/production/factoryproduction/FactoryProductionTabsView.fxml", MainApplication.injector::getInstance);
        try {
            Node tabsView = loader.load();
            tabsContainer.getChildren().add(tabsView);
            productionTabsController = loader.getController();
            productionTabsController.initialize(webView, factory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadToolbar() {
        FXMLLoader loader = fxmlLoaderService.setUpLoader("/org/chainoptim/desktop/features/production/factoryproduction/FactoryProductionToolbarView.fxml", MainApplication.injector::getInstance);
        try {
            Node toolbarView = loader.load();
            toolbarContainer.getChildren().add(toolbarView);
            FactoryProductionToolbarController toolbarController = loader.getController();
            toolbarController.initialize(webView, factory);
            toolbarController.setActionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpenAddStageRequested() {
        productionTabsController.addTab("Add Stage", null);
    }

    @Override
    public void onOpenUpdateStageRequested() {
        Integer factoryStageId = javaConnector.getSelectedNodeId();
        if (factoryStageId != null) {
            productionTabsController.addTab("Update Stage", factoryStageId);
        }
    }

    @Override
    public void onOpenAllocationPlanRequested(AllocationPlan allocationPlan, Boolean isCurrentPlan) {
        productionTabsController.addTab("Allocation Plan", new Pair<AllocationPlan, Boolean>(allocationPlan, isCurrentPlan));
    }

    @Override
    public void onOpenProductionHistoryRequested() {
        productionTabsController.addTab("Production History", null);
    }

    @Override
    public void onOpenAddRecordRequested() {
        System.out.println("Adding Record");
    }

}
