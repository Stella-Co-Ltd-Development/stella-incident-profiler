package com.stella.incidentprofiler.mock;

import com.stella.incidentprofiler.core.model.AwsTopologySnapshot;
import com.stella.incidentprofiler.core.port.AwsTopologyProvider;

public final class MockAwsTopologyProvider implements AwsTopologyProvider {
    private final MockDataRepository repository;

    MockAwsTopologyProvider(MockDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public AwsTopologySnapshot getTopology(String environmentId) {
        AwsTopologySnapshot topology = repository.topology();
        if (environmentId != null && !environmentId.equals(topology.environment())) {
            throw new MockDataException("Unknown mock environment: " + environmentId);
        }
        return topology;
    }
}
