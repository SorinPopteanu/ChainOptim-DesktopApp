package org.chainoptim.desktop.features.production.analysis.productionperformance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FactoryPerformanceReport {

    private Map<Integer, FactoryStagePerformanceReport> stageReports = new HashMap<>(); // Key: factoryStageId

    private Float overallScore;
    private Float resourceDistributionScore; // Measures distribution of resources among stages
    private Float resourceReadinessScore;
    private Float resourceUtilizationScore;
}
