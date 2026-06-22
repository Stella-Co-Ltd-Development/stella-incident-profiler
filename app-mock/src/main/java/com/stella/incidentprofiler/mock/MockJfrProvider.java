package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.model.JfrHotspotSummary;
import com.stella.incidentprofiler.core.model.JfrHotspotType;
import com.stella.incidentprofiler.core.port.JfrProvider;
import java.util.Map;

public final class MockJfrProvider implements JfrProvider {
    private final MockDataRepository repository;

    MockJfrProvider(MockDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public JfrHotspotSummary getHotspots(String incidentId, JfrHotspotType type) {
        JfrHotspotSummary summary = repository.jfrHotspots();
        if (!summary.incidentId().equals(incidentId)) {
            throw new MockDataException("Unknown mock JFR incident: " + incidentId);
        }
        if (type == null) {
            return summary;
        }
        return new JfrHotspotSummary(summary.incidentId(), Map.of(type, summary.byType(type)));
    }
}
