package com.stella.incidentprofiler.core.port;

import com.stella.incidentprofiler.core.model.AwsTopologySnapshot;

public interface AwsTopologyProvider {
    AwsTopologySnapshot getTopology(String environmentId);
}
