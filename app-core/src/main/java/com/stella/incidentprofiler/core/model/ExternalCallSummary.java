package com.stella.incidentprofiler.core.model;

public record ExternalCallSummary(
    String name,
    double baselineP95Ms,
    double incidentP95Ms,
    double baselineErrorRate,
    double incidentErrorRate,
    String change
) {
}
