package org.chainoptim.desktop.shared.util.resourceloader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.util.Pair;

public interface FXMLLoaderService {
    Node loadView(String viewPath, Callback<Class<?>, Object> controllerFactory);
    FXMLLoader setUpLoader(String viewPath, Callback<Class<?>, Object> controllerFactory);
}
