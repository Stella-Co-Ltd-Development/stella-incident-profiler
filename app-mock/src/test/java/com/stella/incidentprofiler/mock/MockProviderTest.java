package com.stella.incidentprofiler.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.stella.incidentprofiler.core.model.JfrHotspotType;
import com.stella.incidentprofiler.core.model.LogLevel;
import com.stella.incidentprofiler.core.port.IncidentQuery;
import com.stella.incidentprofiler.core.port.LogQuery;
import com.stella.incidentprofiler.core.port.ProviderRegistry;
import com.stella.incidentprofiler.core.port.TraceQuery;
import com.stella.incidentprofiler.core.runtime.RuntimeMode;
import com.stella.incidentprofiler.core.runtime.RuntimeSettings;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MockProviderTest {
    private final Path mockDataDir = Path.of(System.getProperty("user.dir")).getParent().resolve("mock-data");

    @Test
    void loadsAllMockDataFiles() {
        MockDataRepository repository = new MockDataRepository(mockDataDir);

        assertEquals(3, repository.incidents().size());
        assertFalse(repository.timeline().points().isEmpty());
        assertEquals(5, repository.logs().size());
        assertEquals(1, repository.traces().size());
        assertFalse(repository.jfrHotspots().byType(JfrHotspotType.CPU).isEmpty());
        assertEquals("prod", repository.topology().environment());
        assertEquals(1730.0, repository.albAnalysis().during().targetResponseP95Ms());
        assertEquals(3, repository.auroraMetrics().metrics().size());
        assertEquals("REGRESSED", repository.externalCallAnalysis().dependencies().getFirst().change());
    }

    @Test
    void selectsMockProvidersInDebugMode() {
        RuntimeSettings settings = RuntimeSettings.fromEnvironment(
            Map.of("APP_PROFILE", "debug", "APP_DATA_MODE", "mock", "APP_AWS_ENABLED", "true"),
            new String[0],
            mockDataDir
        );
        ProviderRegistry registry = MockProviderFactory.create(settings.mockDataDirectory());

        assertEquals(RuntimeMode.DEBUG, settings.mode());
        assertFalse(settings.awsEnabled());
        assertEquals(RuntimeMode.DEBUG, registry.mode());
        assertInstanceOf(MockIncidentProvider.class, registry.incidents());
        assertInstanceOf(MockLogProvider.class, registry.logs());
        assertInstanceOf(MockTraceProvider.class, registry.traces());
    }

    @Test
    void filtersIncidentLogsTimelineAndTraces() {
        ProviderRegistry registry = MockProviderFactory.create(mockDataDir);

        assertEquals(1, registry.incidents().listIncidents(new IncidentQuery("catalog-api", "prod", null, null)).size());
        assertEquals(
            2,
            registry.timelines()
                .getTimeline("inc-20260623-001", Instant.parse("2026-06-23T12:06:00Z"), Instant.parse("2026-06-23T12:07:00Z"))
                .points()
                .size()
        );
        assertEquals(
            2,
            registry.logs()
                .searchLogs(new LogQuery("checkout-api", LogLevel.ERROR, null, null, null, null, 10))
                .items()
                .size()
        );
        assertEquals(1, registry.traces().findSlowTraces(new TraceQuery("checkout-api", null, null, 5)).size());
        assertEquals("trace-004", registry.traces().getTrace("trace-004").traceId());
    }

    @Test
    void throwsForUnknownMockIncidentData() {
        ProviderRegistry registry = MockProviderFactory.create(mockDataDir);

        assertThrows(MockDataException.class, () -> registry.incidents().getIncident("missing"));
        assertThrows(MockDataException.class, () -> registry.traces().getTrace("missing"));
        assertThrows(MockDataException.class, () -> registry.analysis().compareAlbBeforeAfter("missing"));
    }
}
