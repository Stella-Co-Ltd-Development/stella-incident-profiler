package com.stella.incidentprofiler.core.model;

public record AlbSummary(
    int requestCount,
    int target5xx,
    double targetResponseP95Ms
) {
}
