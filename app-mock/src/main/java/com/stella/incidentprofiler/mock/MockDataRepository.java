package com.stella.incidentprofiler.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stella.incidentprofiler.core.json.JsonSupport;
import com.stella.incidentprofiler.core.model.AlbAnalysis;
import com.stella.incidentprofiler.core.model.AuroraMetrics;
import com.stella.incidentprofiler.core.model.AwsTopologySnapshot;
import com.stella.incidentprofiler.core.model.ExternalCallAnalysis;
import com.stella.incidentprofiler.core.model.Incident;
import com.stella.incidentprofiler.core.model.JfrHotspotSummary;
import com.stella.incidentprofiler.core.model.LogEvent;
import com.stella.incidentprofiler.core.model.TimelineWindow;
import com.stella.incidentprofiler.core.model.TraceDetail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class MockDataRepository {
    private final Path dataDirectory;
    private final ObjectMapper objectMapper;

    public MockDataRepository(Path dataDirectory) {
        this(dataDirectory, JsonSupport.createObjectMapper());
    }

    MockDataRepository(Path dataDirectory, ObjectMapper objectMapper) {
        this.dataDirectory = dataDirectory;
        this.objectMapper = objectMapper;
    }

    public List<Incident> incidents() {
        return read("incidents.json", new TypeReference<>() {
        });
    }

    public TimelineWindow timeline() {
        return read("timeline.json", TimelineWindow.class);
    }

    public List<LogEvent> logs() {
        try {
            return Files.readAllLines(dataDirectory.resolve("logs.ndjson")).stream()
                .filter(line -> !line.isBlank())
                .map(this::readLogEvent)
                .toList();
        } catch (IOException exception) {
            throw new MockDataException("Failed to read mock log events", exception);
        }
    }

    public List<TraceDetail> traces() {
        return read("traces.json", TraceFixture.class).traces();
    }

    public JfrHotspotSummary jfrHotspots() {
        return read("jfr-hotspots.json", JfrHotspotSummary.class);
    }

    public AwsTopologySnapshot topology() {
        return read("aws-topology.json", AwsTopologySnapshot.class);
    }

    public AlbAnalysis albAnalysis() {
        return read("alb-analysis.json", AlbAnalysis.class);
    }

    public AuroraMetrics auroraMetrics() {
        return read("aurora-metrics.json", AuroraMetrics.class);
    }

    public ExternalCallAnalysis externalCallAnalysis() {
        return read("external-calls.json", ExternalCallAnalysis.class);
    }

    private LogEvent readLogEvent(String line) {
        try {
            return objectMapper.readValue(line, LogEvent.class);
        } catch (IOException exception) {
            throw new MockDataException("Failed to parse mock log event", exception);
        }
    }

    private <T> T read(String fileName, Class<T> valueType) {
        try {
            return objectMapper.readValue(dataDirectory.resolve(fileName).toFile(), valueType);
        } catch (IOException exception) {
            throw new MockDataException("Failed to read mock data file: " + fileName, exception);
        }
    }

    private <T> T read(String fileName, TypeReference<T> valueType) {
        try {
            return objectMapper.readValue(dataDirectory.resolve(fileName).toFile(), valueType);
        } catch (IOException exception) {
            throw new MockDataException("Failed to read mock data file: " + fileName, exception);
        }
    }

    private record TraceFixture(List<TraceDetail> traces) {
    }
}
