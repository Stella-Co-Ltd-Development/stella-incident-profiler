package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.model.TimelineWindow;
import com.stella.incidentprofiler.core.port.TimelineProvider;
import java.time.Instant;

public final class MockTimelineProvider implements TimelineProvider {
    private final MockDataRepository repository;

    MockTimelineProvider(MockDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public TimelineWindow getTimeline(String incidentId, Instant from, Instant to) {
        TimelineWindow timeline = repository.timeline();
        if (!timeline.incidentId().equals(incidentId)) {
            throw new MockDataException("Unknown mock timeline incident: " + incidentId);
        }
        return new TimelineWindow(
            timeline.incidentId(),
            timeline.resolution(),
            timeline.points().stream()
                .filter(point -> from == null || !point.timestamp().isBefore(from))
                .filter(point -> to == null || !point.timestamp().isAfter(to))
                .toList()
        );
    }
}
