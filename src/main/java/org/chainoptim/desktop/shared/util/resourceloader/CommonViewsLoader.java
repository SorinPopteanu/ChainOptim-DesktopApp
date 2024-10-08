package org.chainoptim.desktop.shared.util.resourceloader;

import org.chainoptim.desktop.core.overview.map.controller.MapController;
import org.chainoptim.desktop.core.overview.upcomingevent.controller.TimelineController;
import org.chainoptim.desktop.shared.search.controller.ListHeaderController;
import org.chainoptim.desktop.shared.common.ui.select.*;
import org.chainoptim.desktop.shared.common.ui.confirmdialog.controller.GenericConfirmDialogController;
import org.chainoptim.desktop.shared.search.controller.PageSelectorController;
import org.chainoptim.desktop.shared.table.TableToolbarController;

import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;

public interface CommonViewsLoader {

    void loadFallbackManager(StackPane fallbackContainer);
    ListHeaderController loadListHeader(StackPane headerContainer);
    PageSelectorController loadPageSelector(StackPane pageSelectorContainer);
    TableToolbarController initializeTableToolbar(StackPane tableToolbarContainer);

    <T> void loadTabContent(Tab tab, String fxmlFilepath, T data);

    <T> GenericConfirmDialogController<T> loadConfirmDialog(StackPane confirmDialogContainer);

    SelectOrCreateLocationController loadSelectOrCreateLocation(StackPane selectOrCreateLocationContainer);
    SelectDurationController loadSelectDurationView(StackPane durationInputContainer);
    SelectStageController loadSelectStageView(StackPane selectStageContainer);
    SelectFactoryController loadSelectFactoryView(StackPane selectFactoryContainer);

    MapController loadSupplyChainMap(StackPane mapContainer);
    TimelineController loadTimeline(StackPane timelineContainer);
}
