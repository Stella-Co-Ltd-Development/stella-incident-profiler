package com.stella.incidentprofiler.core.model;

import java.time.Instant;

public record Incident(
    String id,
    String service,
    String environment,
    Severity severity,
    IncidentStatus status,
    TriggerType triggerType,
    Instant startTime,
    Instant peakTime,
    Instant endTime,
    String summary,
    TimeRange evidenceWindow
) {
}
