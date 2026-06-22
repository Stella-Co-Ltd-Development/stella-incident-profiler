package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.AlbAnalysis;
import com.stella.incidentprofiler.core.model.AuroraMetrics;
import com.stella.incidentprofiler.core.model.ExternalCallAnalysis;

public interface AnalysisProvider {
    AlbAnalysis compareAlbBeforeAfter(String incidentId);

    AuroraMetrics getAuroraMetrics(String incidentId);

    ExternalCallAnalysis compareExternalCallsBeforeAfter(String incidentId);
}
