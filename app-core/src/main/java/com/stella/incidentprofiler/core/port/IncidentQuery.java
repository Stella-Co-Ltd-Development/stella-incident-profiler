package com.stella.incidentprofiler.core.port;

import java.time.Instant;

public record IncidentQuery(String service, String environment, Instant from, Instant to) {
    public static IncidentQuery all() {
        return new IncidentQuery(null, null, null, null);
    }
}
