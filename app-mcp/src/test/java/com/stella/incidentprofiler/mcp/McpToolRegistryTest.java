package com.stella.incidentprofiler.mcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stella.incidentprofiler.mock.MockProviderFactory;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class McpToolRegistryTest {
    private final Path mockDataDir = Path.of(System.getProperty("user.dir")).getParent().resolve("mock-data");

    @Test
    void definesRequiredReadOnlyTools() {
        Set<String> toolNames = McpToolRegistry.readOnlyTools().stream()
            .peek(tool -> assertTrue(tool.readOnly(), tool.name()))
            .map(McpToolDefinition::name)
            .collect(Collectors.toSet());

        assertEquals(
            Set.of(
                "list_services",
                "list_incidents",
                "get_incident_summary",
                "get_incident_timeline",
                "search_logs",
                "get_trace",
                "get_jfr_hotspots",
                "compare_alb_before_after",
                "compare_external_calls_before_after",
                "generate_incident_report"
            ),
            toolNames
        );
    }

    @Test
    void executesMockBackedReadOnlyTools() {
        McpIncidentToolExecutor executor = new McpIncidentToolExecutor(MockProviderFactory.create(mockDataDir));

        McpToolResponse incidents = executor.execute(new McpToolRequest("list_incidents", Map.of()));
        McpToolResponse summary = executor.execute(new McpToolRequest("get_incident_summary", Map.of("incidentId", "inc-20260623-001")));
        McpToolResponse report = executor.execute(new McpToolRequest("generate_incident_report", Map.of("incidentId", "inc-20260623-001")));

        assertTrue(incidents.readOnly());
        assertTrue(incidents.redacted());
        assertEquals("get_incident_summary", summary.toolName());
        assertTrue(report.result().toString().contains("REDACTED"));
    }

    @Test
    void rejectsUnknownTools() {
        McpIncidentToolExecutor executor = new McpIncidentToolExecutor(MockProviderFactory.create(mockDataDir));

        assertThrows(IllegalArgumentException.class, () -> executor.execute(new McpToolRequest("restart_service", Map.of())));
    }
}
