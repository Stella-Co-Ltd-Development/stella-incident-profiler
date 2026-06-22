package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.model.Incident;
import com.stella.incidentprofiler.core.port.IncidentProvider;
import com.stella.incidentprofiler.core.port.IncidentQuery;
import java.util.List;

public final class MockIncidentProvider implements IncidentProvider {
    private final MockDataRepository repository;

    MockIncidentProvider(MockDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Incident> listIncidents(IncidentQuery query) {
        return repository.incidents().stream()
            .filter(incident -> query.service() == null || query.service().equals(incident.service()))
            .filter(incident -> query.environment() == null || query.environment().equals(incident.environment()))
            .filter(incident -> query.from() == null || !incident.peakTime().isBefore(query.from()))
            .filter(incident -> query.to() == null || !incident.startTime().isAfter(query.to()))
            .toList();
    }

    @Override
    public Incident getIncident(String incidentId) {
        return repository.incidents().stream()
            .filter(incident -> incident.id().equals(incidentId))
            .findFirst()
            .orElseThrow(() -> new MockDataException("Unknown mock incident: " + incidentId));
    }
}
