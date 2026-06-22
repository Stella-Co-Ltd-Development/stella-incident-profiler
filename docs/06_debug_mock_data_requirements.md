# Debug and Mock Data Requirements

## Goal

The application must be fully usable in debug mode without AWS credentials, remote services, or live Java applications. This enables fast JavaFX implementation and AI-assisted development.

## Debug mode definition

Debug mode is active when any of the following is true:

- `APP_PROFILE=debug`
- `APP_DATA_MODE=mock`
- the application is launched with `--mock`
- the selected data provider is `MockDataProvider`

## Hard rule

When debug mode is active, the application must not call AWS APIs, open production network connections, or require AWS credentials.

## Provider design

All data used by the GUI must come through interfaces.

```java
public interface IncidentProvider {
    List<IncidentSummary> listIncidents(IncidentQuery query);
    IncidentDetail getIncident(String incidentId);
}

public interface TimelineProvider {
    TimelineWindow getTimeline(String incidentId, Instant from, Instant to);
}

public interface LogProvider {
    Page<LogEvent> searchLogs(LogQuery query);
}

public interface TraceProvider {
    TraceDetail getTrace(String traceId);
    List<TraceSummary> findSlowTraces(TraceQuery query);
}

public interface JfrProvider {
    JfrHotspotSummary getHotspots(String incidentId, JfrHotspotType type);
}

public interface AwsTopologyProvider {
    AwsTopologySnapshot getTopology(String environmentId);
}
```

Debug implementation classes:

```text
MockIncidentProvider
MockTimelineProvider
MockLogProvider
MockTraceProvider
MockJfrProvider
MockAwsTopologyProvider
```

Live implementation classes:

```text
AwsIncidentProvider
CloudWatchTimelineProvider
CloudWatchLogsProvider
XRayTraceProvider or OtlpTraceProvider
JfrS3Provider
AwsTopologyProviderImpl
```

## Required mock files

```text
mock-data/
  incidents.json
  timeline.json
  logs.ndjson
  traces.json
  jfr-hotspots.json
  aws-topology.json
  external-calls.json
  aurora-metrics.json
  alb-analysis.json
```

## Mock data requirements

Mock data must include at least:

- one high-severity latency incident
- one 5xx spike incident
- one GC/heap pressure incident
- one external API timeout incident
- one Aurora latency incident
- logs with trace IDs
- traces with nested spans
- JFR CPU/allocation/GC/lock summaries
- ALB and CloudFront summaries
- Aurora and RDS Proxy metrics

## Mock rendering requirements

The following screens must render entirely from mock data:

- Dashboard
- Service map
- Incident list
- Incident detail
- Unified timeline
- Trace waterfall
- Log viewer
- JFR hotspot view
- ALB/CloudFront analysis
- Aurora analysis
- External API analysis
- MCP console test responses

## Mock mode visual indicator

The GUI must show a visible Japanese indicator in debug mode:

```text
モックデータ表示中
```

## Test requirements

- Unit tests must verify that debug mode selects mock providers.
- Unit tests must verify that AWS providers are not constructed in debug mode.
- UI tests should verify that the incident list and incident detail can render from bundled mock data.
- MCP tool tests should run against mock providers.

## Example debug boot flow

```text
Application starts
  -> read environment and CLI flags
  -> determine RuntimeMode.DEBUG
  -> create provider registry with mock adapters
  -> load mock-data/*.json and *.ndjson
  -> initialize JavaFX views
  -> render dashboard and mock incident list
```

## Example live boot flow

```text
Application starts
  -> read environment and CLI flags
  -> determine RuntimeMode.LIVE
  -> require AWS account/region/role selection
  -> create provider registry with live adapters
  -> validate credentials
  -> render service inventory
```
