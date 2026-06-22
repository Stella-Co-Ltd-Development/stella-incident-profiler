package com.stella.incidentprofiler.core.model;

import java.time.Instant;

public record TraceSummary(
    String traceId,
    String service,
    String operation,
    Instant startTime,
    double durationMs,
    String status,
    int spanCount,
    int errorCount
) {
}
