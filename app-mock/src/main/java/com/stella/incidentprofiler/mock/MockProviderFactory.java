package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.port.ProviderRegistry;
import com.stella.incidentprofiler.core.runtime.RuntimeMode;
import java.nio.file.Path;

public final class MockProviderFactory {
    private MockProviderFactory() {
    }

    public static ProviderRegistry create(Path dataDirectory) {
        MockDataRepository repository = new MockDataRepository(dataDirectory);
        return new ProviderRegistry(
            RuntimeMode.DEBUG,
            new MockIncidentProvider(repository),
            new MockTimelineProvider(repository),
            new MockLogProvider(repository),
            new MockTraceProvider(repository),
            new MockJfrProvider(repository),
            new MockAwsTopologyProvider(repository),
            new MockAnalysisProvider(repository)
        );
    }
}
