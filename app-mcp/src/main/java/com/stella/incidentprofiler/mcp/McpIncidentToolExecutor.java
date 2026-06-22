package com.stella.incidentprofiler.mcp;

import com.stella.incidentprofiler.core.model.Incident;
import com.stella.incidentprofiler.core.port.IncidentQuery;
import com.stella.incidentprofiler.core.port.LogQuery;
import com.stella.incidentprofiler.core.port.ProviderRegistry;
import java.util.Map;
import java.util.stream.Collectors;

public final class McpIncidentToolExecutor {
    private final ProviderRegistry providers;

    public McpIncidentToolExecutor(ProviderRegistry providers) {
        this.providers = providers;
    }

    public McpToolResponse execute(McpToolRequest request) {
        Object result = switch (request.toolName()) {
            case "list_services" -> listServices();
            case "list_incidents" -> providers.incidents().listIncidents(IncidentQuery.all());
            case "get_incident_summary" -> providers.incidents().getIncident(requiredString(request, "incidentId"));
            case "get_incident_timeline" -> incidentTimeline(requiredString(request, "incidentId"));
            case "search_logs" -> providers.logs().searchLogs(new LogQuery(optionalString(request, "service"), null, optionalString(request, "query"), null, null, null, optionalInt(request, "limit", 50)));
            case "get_trace" -> providers.traces().getTrace(requiredString(request, "traceId"));
            case "get_jfr_hotspots" -> providers.jfr().getHotspots(requiredString(request, "incidentId"), null);
            case "compare_alb_before_after" -> providers.analysis().compareAlbBeforeAfter(requiredString(request, "incidentId"));
            case "compare_external_calls_before_after" -> providers.analysis().compareExternalCallsBeforeAfter(requiredString(request, "incidentId"));
            case "generate_incident_report" -> generateIncidentReport(requiredString(request, "incidentId"));
            default -> throw new IllegalArgumentException("Unknown MCP tool: " + request.toolName());
        };
        return new McpToolResponse(request.toolName(), result, true, true);
    }

    private Object listServices() {
        return providers.incidents().listIncidents(IncidentQuery.all()).stream()
            .collect(Collectors.groupingBy(Incident::service))
            .keySet()
            .stream()
            .sorted()
            .toList();
    }

    private Object incidentTimeline(String incidentId) {
        Incident incident = providers.incidents().getIncident(incidentId);
        return providers.timelines().getTimeline(incidentId, incident.evidenceWindow().from(), incident.evidenceWindow().to());
    }

    private String generateIncidentReport(String incidentId) {
        Incident incident = providers.incidents().getIncident(incidentId);
        return """
            # Incident Report

            ## Summary
            %s

            ## Evidence
            - Incident: %s
            - Service: %s
            - Redaction: REDACTED
            """.formatted(incident.summary(), incident.id(), incident.service());
    }

    private static String requiredString(McpToolRequest request, String key) {
        Object value = request.arguments().get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Missing required MCP argument: " + key);
        }
        return value.toString();
    }

    private static String optionalString(McpToolRequest request, String key) {
        Object value = request.arguments().get(key);
        return value == null ? null : value.toString();
    }

    private static int optionalInt(McpToolRequest request, String key, int defaultValue) {
        Object value = request.arguments().get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Integer.parseInt(text);
        }
        return defaultValue;
    }
}
