package com.stella.incidentprofiler.mcp;

import java.util.Map;

public record McpToolDefinition(
    String name,
    String description,
    boolean readOnly,
    Map<String, String> inputFields
) {
}
