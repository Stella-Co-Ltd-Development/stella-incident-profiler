package com.stella.incidentprofiler.core.model;

import java.time.Instant;

public record AuroraMetricPoint(
    Instant timestamp,
    int databaseConnections,
    double readLatencyMs,
    double writeLatencyMs,
    double cpuPercent
) {
}
