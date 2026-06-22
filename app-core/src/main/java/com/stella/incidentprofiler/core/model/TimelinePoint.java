package com.stella.incidentprofiler.core.model;

import java.time.Instant;

public record TimelinePoint(
    Instant timestamp,
    double latencyP95Ms,
    int errorCount,
    double cpuPercent,
    double heapPercent,
    double gcPauseMs,
    int blockedThreads,
    double dbLatencyMs,
    double externalApiLatencyMs,
    double albTargetResponseMs
) {
}
