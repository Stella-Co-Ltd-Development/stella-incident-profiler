package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.JfrHotspotSummary;
import com.stella.incidentprofiler.core.model.JfrHotspotType;

public interface JfrProvider {
    JfrHotspotSummary getHotspots(String incidentId, JfrHotspotType type);
}
