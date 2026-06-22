package com.stella.incidentprofiler.mcp;

public record McpToolResponse(
    String toolName,
    Object result,
    boolean readOnly,
    boolean redacted
) {
}
