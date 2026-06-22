package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.Incident;
import java.util.List;

public interface IncidentProvider {
    List<Incident> listIncidents(IncidentQuery query);

    Incident getIncident(String incidentId);
}
