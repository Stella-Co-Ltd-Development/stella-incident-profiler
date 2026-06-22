package com.stella.incidentprofiler.core.port;

import java.time.Instant;

public record TraceQuery(String service, Instant from, Instant to, int limit) {
}
