package com.stella.incidentprofiler.core.model;

import java.time.Instant;
import java.util.Map;

public record MetricPoint(
    Instant timestamp,
    String service,
    String name,
    double value,
    String unit,
    Map<String, String> tags
) {
}
