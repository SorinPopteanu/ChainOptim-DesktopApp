package org.chainoptim.desktop.core.map;

import org.chainoptim.desktop.core.map.model.SupplyChainMap;
import org.chainoptim.desktop.features.scanalysis.factorygraph.service.JavaConnector;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.util.DataReceiver;
import com.google.inject.Inject;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.Objects;

import static org.chainoptim.desktop.shared.util.JsonUtil.prepareJsonString;

public class MapController implements DataReceiver<SupplyChainMap> {

    SupplyChainMap supplyChainMap;

    private final FallbackManager fallbackManager;

    private WebView webView;
    private JavaConnector javaConnector;

    @FXML
    private StackPane mapContainer;

    @Inject
    public MapController(FallbackManager fallbackManager) {
        this.fallbackManager = fallbackManager;
    }

    @Override
    public void setData(SupplyChainMap data) {
        this.supplyChainMap = data;

        if (supplyChainMap == null) {
            return;
        }

        initializeWebView();

//        displayMap();
    }

    private void initializeWebView() {
        webView = new WebView();
        webView.getEngine().loadContent(Objects.requireNonNull(getClass().getResource("/html/supplychainmap.html")).toExternalForm());
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject jsObject = (JSObject) webView.getEngine().executeScript("window");
                javaConnector = new JavaConnector();
                jsObject.setMember("javaConnector", javaConnector);
            }
        });
    }

    private void displayMap() {
        if (webView.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            renderMap();
        }
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                // Execute script for rendering map (using timeout for now to ensure bundle is loaded at this point)
                renderMap();
            }
        });

        mapContainer.getChildren().add(webView);
    }

    public void renderMap() {
        String escapedJsonString = prepareJsonString(supplyChainMap);

        String script = "window.renderFactoryGraph('" + escapedJsonString + "');";
        try {
            webView.getEngine().executeScript(script);
        } catch (Exception e) {
            fallbackManager.setErrorMessage("Error drawing map. Please try again.");
            e.printStackTrace();
        }
    }
}
