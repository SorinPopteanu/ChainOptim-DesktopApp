package org.chainoptim.desktop.features.factory.controller;

import org.chainoptim.desktop.features.factory.model.Factory;
import org.chainoptim.desktop.features.scanalysis.productionhistory.model.DailyProductionRecord;
import org.chainoptim.desktop.features.scanalysis.productionhistory.model.FactoryProductionHistory;
import org.chainoptim.desktop.features.scanalysis.productionhistory.model.ProductionHistory;
import org.chainoptim.desktop.features.scanalysis.productionhistory.service.FactoryProductionHistoryService;
import org.chainoptim.desktop.features.scanalysis.productionperformance.model.FactoryPerformance;
import org.chainoptim.desktop.features.scanalysis.productionperformance.service.FactoryPerformanceService;
import org.chainoptim.desktop.features.scanalysis.resourceallocation.model.ResourceAllocation;
import org.chainoptim.desktop.shared.fallback.FallbackManager;
import org.chainoptim.desktop.shared.util.DataReceiver;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FactoryPerformanceController implements DataReceiver<Factory> {

    // Services
    private final FactoryPerformanceService factoryPerformanceService;

    // State
    private final FallbackManager fallbackManager;
    private FactoryPerformance factoryPerformance;

    @Inject
    public FactoryPerformanceController(FactoryPerformanceService factoryPerformanceService, FallbackManager fallbackManager) {
        this.factoryPerformanceService = factoryPerformanceService;
        this.fallbackManager = fallbackManager;
    }

    @Override
    public void setData(Factory factory) {
        loadFactoryPerformance(factory.getId(), false);
    }



    private void loadFactoryPerformance(Integer factoryId, boolean refresh) {
        fallbackManager.reset();
        fallbackManager.setLoading(true);

        factoryPerformanceService.getFactoryPerformanceByFactoryId(factoryId, refresh)
                .thenApply(this::handleFactoryPerformanceResponse)
                .exceptionally(this::handleFactoryPerformanceException);
    }

    private Optional<FactoryPerformance> handleFactoryPerformanceResponse(Optional<FactoryPerformance> performanceOptional) {
        Platform.runLater(() -> {
            if (performanceOptional.isEmpty()) {
                fallbackManager.setErrorMessage("Failed to load factory performance");
                return;
            }
            factoryPerformance = performanceOptional.get();
            fallbackManager.setLoading(false);

            System.out.println("Factory Performance: " + factoryPerformance.getReport());

        });
        return performanceOptional;
    }

    private Optional<FactoryPerformance> handleFactoryPerformanceException(Throwable ex) {
        Platform.runLater(() -> {
            fallbackManager.setErrorMessage("Failed to load factory performance");
            ex.printStackTrace();
        });
        return Optional.empty();
    }
}

// Line Chart
//private void updateComponentUI(ProductionHistory history) {
//    Map<Float, Pair<Float, Float>> dataOverTime = history.getDailyProductionRecords().entrySet().stream()
//            .collect(Collectors.toMap(
//                    Map.Entry::getKey,
//                    entry -> {
//                        var allocations = entry.getValue().getAllocations().stream()
//                                .filter(alloc -> alloc.getComponentId().equals(selectedComponentId))
//                                .findFirst()
//                                .orElse(new ResourceAllocation());
//
//                        float requestedAmount = allocations.getRequestedAmount();
//                        float allocatedAmount = allocations.getAllocatedAmount();
//                        return new Pair<>(requestedAmount, allocatedAmount);
//                    }));
//    plotData(history.getStartDate(), dataOverTime);
//}
//
//
//private void plotData(LocalDateTime firstDeliveryDate, Map<Float, Pair<Float, Float>> dataOverTime) {
//    lineChart.getData().clear();
//    lineChart.setLegendVisible(true);
//
//    // Create two series for the two types of data
//    XYChart.Series<String, Number> requestedSeries = new XYChart.Series<>();
//    requestedSeries.setName("Needed Amount");
//    XYChart.Series<String, Number> allocatedSeries = new XYChart.Series<>();
//    allocatedSeries.setName("Allocated Amount");
//
//    LocalDate startDate = firstDeliveryDate.toLocalDate();
//    float maxDays = dataOverTime.keySet().stream().max(Float::compare).orElse(0f);
//    LocalDate endDate = startDate.plusDays((long) maxDays);
//
//    // Prepare the list of all month-year labels to be used as categories
//    List<String> allLabels = new ArrayList<>();
//    LocalDate currentMonth = startDate.withDayOfMonth(1);
//    while (!currentMonth.isAfter(endDate)) {
//        String monthYear = currentMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"));
//        allLabels.add(monthYear);
//        currentMonth = currentMonth.plusMonths(1);
//    }
//
//    CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
//    xAxis.setCategories(FXCollections.observableArrayList(allLabels));
//
//    // Plot the data for both requested and allocated amounts
//    currentMonth = startDate.withDayOfMonth(1);
//    for (String label : allLabels) {
//        LocalDate nextMonth = currentMonth.plusMonths(1);
//        Pair<Float, Float> sumValuesForMonth = new Pair<>(0f, 0f);
//        int count = 0;
//        for (Map.Entry<Float, Pair<Float, Float>> entry : dataOverTime.entrySet()) {
//            LocalDate entryDate = startDate.plusDays(entry.getKey().longValue());
//            if (!entryDate.isBefore(currentMonth) && entryDate.isBefore(nextMonth)) {
//                sumValuesForMonth = new Pair<>(sumValuesForMonth.getKey() + entry.getValue().getKey(), sumValuesForMonth.getValue() + entry.getValue().getValue());
//                count++;
//            }
//        }
//        float averageRequested = count > 0 ? sumValuesForMonth.getKey() / count : 0;
//        float averageAllocated = count > 0 ? sumValuesForMonth.getValue() / count : 0;
//        requestedSeries.getData().add(new XYChart.Data<>(label, averageRequested));
//        allocatedSeries.getData().add(new XYChart.Data<>(label, averageAllocated));
//
//        currentMonth = nextMonth; // Advance to the next month
//    }
//
//    // Add both series to the chart
//    lineChart.getData().addAll(requestedSeries, allocatedSeries);
//
//    applyCustomSeriesStyles();
//}
//
//private void applyCustomSeriesStyles() {
//    Platform.runLater(() -> {
//        for (int i = 0; i < lineChart.getData().size(); i++) {
//            XYChart.Series<String, Number> series = lineChart.getData().get(i);
//            switch (series.getName()) {
//                case "Needed Amount":
//                    series.getNode().setStyle("-fx-stroke: blue;");
//                    break;
//                case "Allocated Amount":
//                    series.getNode().setStyle("-fx-stroke: orange;");
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        // Add event listener on legend items to toggle visibility of series
//        for (Node node : lineChart.lookupAll(".chart-legend-item")) {
//            node.setOnMouseClicked(mouseEvent -> {
//                for (XYChart.Series<String, Number> s : lineChart.getData()) {
//                    if (s.getName().equals(((Label) node).getText())) {
//                        s.getNode().setVisible(!s.getNode().isVisible());
//                        s.getData().forEach(data -> {
//                            Node dataNode = data.getNode();
//                            if (dataNode != null) {
//                                dataNode.setVisible(s.getNode().isVisible());
//                            }
//                        });
//                    }
//                }
//            });
//        }
//    });
//}