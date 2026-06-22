package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.model.LogEvent;
import com.stella.incidentprofiler.core.port.LogProvider;
import com.stella.incidentprofiler.core.port.LogQuery;
import com.stella.incidentprofiler.core.port.Page;
import java.util.List;

public final class MockLogProvider implements LogProvider {
    private static final int DEFAULT_LIMIT = 100;
    private final MockDataRepository repository;

    MockLogProvider(MockDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<LogEvent> searchLogs(LogQuery query) {
        int limit = query.limit() > 0 ? query.limit() : DEFAULT_LIMIT;
        List<LogEvent> logs = repository.logs().stream()
            .filter(event -> query.service() == null || query.service().equals(event.service()))
            .filter(event -> query.level() == null || query.level() == event.level())
            .filter(event -> query.traceId() == null || query.traceId().equals(event.traceId()))
            .filter(event -> query.from() == null || !event.timestamp().isBefore(query.from()))
            .filter(event -> query.to() == null || !event.timestamp().isAfter(query.to()))
            .filter(event -> query.query() == null || event.message().toLowerCase().contains(query.query().toLowerCase()))
            .limit(limit)
            .toList();
        return new Page<>(logs, null);
    }
}
