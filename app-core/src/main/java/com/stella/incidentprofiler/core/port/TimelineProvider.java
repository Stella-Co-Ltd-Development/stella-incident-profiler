package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.TimelineWindow;
import java.time.Instant;

public interface TimelineProvider {
    TimelineWindow getTimeline(String incidentId, Instant from, Instant to);
}
