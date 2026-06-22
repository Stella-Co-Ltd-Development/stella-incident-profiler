package com.stella.incidentprofiler.core.model;

import java.time.Instant;
import java.util.Map;

public record TraceSpan(
    String traceId,
    String spanId,
    String parentSpanId,
    String service,
    String operation,
    TraceSpanKind kind,
    Instant startTime,
    double durationMs,
    String status,
    Map<String, String> attributes
) {
}
