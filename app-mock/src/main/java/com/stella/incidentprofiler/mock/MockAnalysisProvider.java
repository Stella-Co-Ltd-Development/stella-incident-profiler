package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.model.AlbAnalysis;
import com.stella.incidentprofiler.core.model.AuroraMetrics;
import com.stella.incidentprofiler.core.model.ExternalCallAnalysis;
import com.stella.incidentprofiler.core.port.AnalysisProvider;

public final class MockAnalysisProvider implements AnalysisProvider {
    private final MockDataRepository repository;

    MockAnalysisProvider(MockDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public AlbAnalysis compareAlbBeforeAfter(String incidentId) {
        AlbAnalysis analysis = repository.albAnalysis();
        requireIncident(incidentId, analysis.incidentId());
        return analysis;
    }

    @Override
    public AuroraMetrics getAuroraMetrics(String incidentId) {
        AuroraMetrics metrics = repository.auroraMetrics();
        requireIncident(incidentId, metrics.incidentId());
        return metrics;
    }

    @Override
    public ExternalCallAnalysis compareExternalCallsBeforeAfter(String incidentId) {
        ExternalCallAnalysis analysis = repository.externalCallAnalysis();
        requireIncident(incidentId, analysis.incidentId());
        return analysis;
    }

    private void requireIncident(String requestedIncidentId, String actualIncidentId) {
        if (!actualIncidentId.equals(requestedIncidentId)) {
            throw new MockDataException("Unknown mock analysis incident: " + requestedIncidentId);
        }
    }
}
