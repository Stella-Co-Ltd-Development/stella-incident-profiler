package com.stella.incidentprofiler.core.model;

import java.util.List;

public record AlbAnalysis(
    String incidentId,
    AlbSummary before,
    AlbSummary during,
    List<AlbRouteAnalysis> topRoutes
) {
}
