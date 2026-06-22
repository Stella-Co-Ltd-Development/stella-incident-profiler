package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.model.TraceDetail;
import com.stella.incidentprofiler.core.model.TraceSummary;
import com.stella.incidentprofiler.core.port.TraceProvider;
import com.stella.incidentprofiler.core.port.TraceQuery;
import java.util.Comparator;
import java.util.List;

public final class MockTraceProvider implements TraceProvider {
    private final MockDataRepository repository;

    MockTraceProvider(MockDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public TraceDetail getTrace(String traceId) {
        return repository.traces().stream()
            .filter(trace -> trace.traceId().equals(traceId))
            .findFirst()
            .orElseThrow(() -> new MockDataException("Unknown mock trace: " + traceId));
    }

    @Override
    public List<TraceSummary> findSlowTraces(TraceQuery query) {
        int limit = query.limit() > 0 ? query.limit() : 25;
        return repository.traces().stream()
            .filter(trace -> query.service() == null || query.service().equals(trace.service()))
            .filter(trace -> query.from() == null || !trace.startTime().isBefore(query.from()))
            .filter(trace -> query.to() == null || !trace.startTime().isAfter(query.to()))
            .sorted(Comparator.comparingDouble(TraceDetail::durationMs).reversed())
            .limit(limit)
            .map(TraceDetail::summary)
            .toList();
    }
}
