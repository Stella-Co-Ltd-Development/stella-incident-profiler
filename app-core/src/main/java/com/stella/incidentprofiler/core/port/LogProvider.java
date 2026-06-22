package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.LogEvent;

public interface LogProvider {
    Page<LogEvent> searchLogs(LogQuery query);
}
