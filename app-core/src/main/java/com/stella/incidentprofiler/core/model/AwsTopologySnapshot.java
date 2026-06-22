package com.stella.incidentprofiler.core.model;

import java.util.List;
import java.util.Map;

public record AwsTopologySnapshot(
    String environment,
    String accountId,
    String region,
    Map<String, String> resources,
    List<String> warnings
) {
}
