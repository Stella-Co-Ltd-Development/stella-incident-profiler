package com.stella.incidentprofiler.mcp;

import java.util.List;
import java.util.Map;

public final class McpToolRegistry {
    private McpToolRegistry() {
    }

    public static List<McpToolDefinition> readOnlyTools() {
        return List.of(
            tool("list_services", "List known services.", Map.of("environment", "Environment name.")),
            tool("list_incidents", "List incidents within a time range.", Map.of("service", "Service name.", "from", "Start time.", "to", "End time.")),
            tool("get_incident_summary", "Return incident summary and evidence bounds.", Map.of("incidentId", "Incident identifier.")),
            tool("get_incident_timeline", "Return downsampled timeline points.", Map.of("incidentId", "Incident identifier.")),
            tool("search_logs", "Search redacted log events.", Map.of("service", "Service name.", "query", "Text query.", "limit", "Result limit.")),
            tool("get_trace", "Return trace summary and spans.", Map.of("traceId", "Trace identifier.")),
            tool("get_jfr_hotspots", "Return JFR hotspot summaries.", Map.of("incidentId", "Incident identifier.", "type", "Hotspot type.")),
            tool("compare_alb_before_after", "Compare ALB behavior before and during an incident.", Map.of("incidentId", "Incident identifier.")),
            tool("compare_external_calls_before_after", "Compare external dependency behavior before and during an incident.", Map.of("incidentId", "Incident identifier.")),
            tool("generate_incident_report", "Generate a Markdown incident report from available evidence.", Map.of("incidentId", "Incident identifier."))
        );
    }

    private static McpToolDefinition tool(String name, String description, Map<String, String> inputFields) {
        return new McpToolDefinition(name, description, true, inputFields);
    }
}
