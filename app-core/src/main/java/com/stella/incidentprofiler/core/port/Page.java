package com.stella.incidentprofiler.core.port;

import java.util.List;

public record Page<T>(List<T> items, String nextPageToken) {
}
