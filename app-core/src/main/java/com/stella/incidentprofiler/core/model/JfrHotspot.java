package com.stella.incidentprofiler.core.model;

public record JfrHotspot(
    String incidentId,
    JfrHotspotType type,
    String method,
    int sampleCount,
    double percentage,
    String thread
) {
}
