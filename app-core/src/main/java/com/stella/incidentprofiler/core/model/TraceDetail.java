package com.stella.incidentprofiler.core.model;

import java.time.Instant;
import java.util.List;

public record TraceDetail(
    String traceId,
    String service,
    String operation,
    Instant startTime,
    double durationMs,
    String status,
    int spanCount,
    int errorCount,
    List<TraceSpan> spans
) {
    public TraceSummary summary() {
        return new TraceSummary(traceId, service, operation, startTime, durationMs, status, spanCount, errorCount);
    }
}
