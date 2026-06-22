package com.stella.incidentprofiler.core.model;

import java.util.List;
import java.util.Map;

public record JfrHotspotSummary(
    String incidentId,
    Map<JfrHotspotType, List<JfrHotspot>> hotspots
) {
    public List<JfrHotspot> byType(JfrHotspotType type) {
        return hotspots.getOrDefault(type, List.of());
    }
}
