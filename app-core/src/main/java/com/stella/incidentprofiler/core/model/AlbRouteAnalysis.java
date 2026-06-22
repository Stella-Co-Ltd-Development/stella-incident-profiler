package com.stella.incidentprofiler.core.model;

public record AlbRouteAnalysis(
    String route,
    double beforeP95Ms,
    double duringP95Ms,
    int during5xx
) {
}
