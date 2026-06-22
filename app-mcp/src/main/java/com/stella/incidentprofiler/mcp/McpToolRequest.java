package com.stella.incidentprofiler.mcp;

import java.util.Map;

public record McpToolRequest(String toolName, Map<String, Object> arguments) {
}
