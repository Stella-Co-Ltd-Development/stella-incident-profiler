package com.stella.incidentprofiler.core.model;

import java.time.Instant;

public record LogEvent(
    Instant timestamp,
    String service,
    LogLevel level,
    String message,
    String traceId,
    String spanId,
    String thread,
    String logger,
    String exceptionClass,
    boolean redacted
) {
}
