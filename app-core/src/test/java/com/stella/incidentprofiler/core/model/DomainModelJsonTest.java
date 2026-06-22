package com.stella.incidentprofiler.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stella.incidentprofiler.core.json.JsonSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class DomainModelJsonTest {
    private final ObjectMapper objectMapper = JsonSupport.createObjectMapper();
    private final Path mockDataDir = Path.of(System.getProperty("user.dir")).getParent().resolve("mock-data");

    @Test
    void mapsIncidentMockData() throws IOException {
        List<Incident> incidents = objectMapper.readValue(
            mockDataDir.resolve("incidents.json").toFile(),
            new TypeReference<>() {
            }
        );

        assertEquals(3, incidents.size());
        Incident first = incidents.getFirst();
        assertEquals("inc-20260623-001", first.id());
        assertEquals(Severity.HIGH, first.severity());
        assertEquals(IncidentStatus.READY_FOR_ANALYSIS, first.status());
        assertEquals(TriggerType.P95_LATENCY_SPIKE, first.triggerType());
        assertEquals(Instant.parse("2026-06-23T11:58:00Z"), first.evidenceWindow().from());
        assertNull(incidents.get(2).endTime());
    }

    @Test
    void mapsTimelineMockData() throws IOException {
        TimelineWindow timeline = objectMapper.readValue(mockDataDir.resolve("timeline.json").toFile(), TimelineWindow.class);

        assertEquals("inc-20260623-001", timeline.incidentId());
        assertEquals("1m", timeline.resolution());
        assertFalse(timeline.points().isEmpty());
        assertEquals(1920.0, timeline.points().get(9).latencyP95Ms());
    }

    @Test
    void mapsNdjsonLogEvents() throws IOException {
        List<LogEvent> logs = Files.readAllLines(mockDataDir.resolve("logs.ndjson")).stream()
            .filter(line -> !line.isBlank())
            .map(this::readLogEvent)
            .toList();

        assertEquals(5, logs.size());
        assertEquals(LogLevel.WARN, logs.getFirst().level());
        assertEquals("trace-004", logs.get(3).traceId());
        assertTrue(logs.stream().allMatch(LogEvent::redacted));
    }

    @Test
    void mapsTraceDetailMockData() throws IOException {
        TraceFixture traces = objectMapper.readValue(mockDataDir.resolve("traces.json").toFile(), TraceFixture.class);

        TraceDetail trace = traces.traces().getFirst();
        assertEquals("trace-004", trace.traceId());
        assertEquals(6, trace.spans().size());
        assertEquals(TraceSpanKind.SERVER, trace.spans().getFirst().kind());
        assertEquals("payment-provider", trace.spans().get(3).attributes().get("peer.service"));
        assertEquals(trace.traceId(), trace.summary().traceId());
    }

    @Test
    void mapsJfrHotspotMockData() throws IOException {
        JfrHotspotSummary summary = objectMapper.readValue(mockDataDir.resolve("jfr-hotspots.json").toFile(), JfrHotspotSummary.class);

        List<JfrHotspot> cpuHotspots = summary.byType(JfrHotspotType.CPU);
        assertEquals("inc-20260623-001", summary.incidentId());
        assertFalse(cpuHotspots.isEmpty());
        assertEquals(JfrHotspotType.CPU, cpuHotspots.getFirst().type());
        assertEquals("inc-20260623-001", cpuHotspots.getFirst().incidentId());
    }

    @Test
    void mapsAnalysisAndTopologyMockData() throws IOException {
        AwsTopologySnapshot topology = objectMapper.readValue(mockDataDir.resolve("aws-topology.json").toFile(), AwsTopologySnapshot.class);
        AlbAnalysis alb = objectMapper.readValue(mockDataDir.resolve("alb-analysis.json").toFile(), AlbAnalysis.class);
        AuroraMetrics aurora = objectMapper.readValue(mockDataDir.resolve("aurora-metrics.json").toFile(), AuroraMetrics.class);
        ExternalCallAnalysis externalCalls = objectMapper.readValue(
            mockDataDir.resolve("external-calls.json").toFile(),
            ExternalCallAnalysis.class
        );

        assertEquals("prod", topology.environment());
        assertEquals("checkout-api", topology.resources().get("ecsService"));
        assertEquals(1730.0, alb.during().targetResponseP95Ms());
        assertEquals(3, aurora.metrics().size());
        assertEquals("payment-provider", externalCalls.dependencies().getFirst().name());
        assertEquals("REGRESSED", externalCalls.dependencies().getFirst().change());
    }

    private LogEvent readLogEvent(String line) {
        try {
            return objectMapper.readValue(line, LogEvent.class);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Failed to parse mock log event", exception);
        }
    }

    private record TraceFixture(List<TraceDetail> traces) {
    }
}
