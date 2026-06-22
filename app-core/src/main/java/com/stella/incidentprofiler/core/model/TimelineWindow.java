package com.stella.incidentprofiler.core.model;

import java.util.List;

public record TimelineWindow(
    String incidentId,
    String resolution,
    List<TimelinePoint> points
) {
}
