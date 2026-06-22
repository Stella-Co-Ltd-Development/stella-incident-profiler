package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.runtime.RuntimeMode;

public record ProviderRegistry(
    RuntimeMode mode,
    IncidentProvider incidents,
    TimelineProvider timelines,
    LogProvider logs,
    TraceProvider traces,
    JfrProvider jfr,
    AwsTopologyProvider topology,
    AnalysisProvider analysis
) {
}
