package com.stella.incidentprofiler.core.model;

import java.util.List;

public record AuroraMetrics(
    String incidentId,
    String cluster,
    List<AuroraMetricPoint> metrics
) {
}
