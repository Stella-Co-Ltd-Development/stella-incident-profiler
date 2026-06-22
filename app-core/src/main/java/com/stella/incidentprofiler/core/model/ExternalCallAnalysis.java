package com.stella.incidentprofiler.core.model;

import java.util.List;

public record ExternalCallAnalysis(
    String incidentId,
    List<ExternalCallSummary> dependencies
) {
}
