package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.TraceDetail;
import com.stella.incidentprofiler.core.model.TraceSummary;
import java.util.List;

public interface TraceProvider {
    TraceDetail getTrace(String traceId);

    List<TraceSummary> findSlowTraces(TraceQuery query);
}
