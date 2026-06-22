package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.LogLevel;
import java.time.Instant;

public record LogQuery(
    String service,
    LogLevel level,
    String query,
    String traceId,
    Instant from,
    Instant to,
    int limit
) {
}
