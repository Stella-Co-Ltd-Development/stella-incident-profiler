# API Contracts

## Design principles

- API contracts must use English names.
- UI labels must be translated in the UI layer, not in API payloads.
- APIs must be stable across mock and live providers.
- Provider interfaces must return the same DTOs in debug and live mode.

## Desktop provider ports

### IncidentProvider

```java
public interface IncidentProvider {
    List<IncidentSummary> listIncidents(IncidentQuery query);
    IncidentDetail getIncident(String incidentId);
}
```

### TimelineProvider

```java
public interface TimelineProvider {
    TimelineWindow getTimeline(String incidentId, Instant from, Instant to, TimelineResolution resolution);
}
```

### LogProvider

```java
public interface LogProvider {
    Page<LogEvent> searchLogs(LogQuery query);
}
```

### TraceProvider

```java
public interface TraceProvider {
    List<TraceSummary> findSlowTraces(TraceQuery query);
    TraceDetail getTrace(String traceId);
}
```

### JfrProvider

```java
public interface JfrProvider {
    JfrHotspotSummary getHotspots(String incidentId, JfrHotspotType type);
    Optional<Path> downloadJfrArtifact(String incidentId);
}
```

### AwsTopologyProvider

```java
public interface AwsTopologyProvider {
    AwsTopologySnapshot getTopology(TopologyQuery query);
}
```

## Control plane REST API draft

### List incidents

```http
GET /v1/incidents?service=checkout-api&from=2026-06-23T00:00:00Z&to=2026-06-23T23:59:59Z
```

### Get incident

```http
GET /v1/incidents/{incidentId}
```

### Get timeline

```http
GET /v1/incidents/{incidentId}/timeline?from=...&to=...&resolution=10s
```

### Search logs

```http
POST /v1/logs/search
Content-Type: application/json

{
  "service": "checkout-api",
  "from": "2026-06-23T12:00:00Z",
  "to": "2026-06-23T12:15:00Z",
  "level": "ERROR",
  "query": "timeout",
  "limit": 100
}
```

### Get trace

```http
GET /v1/traces/{traceId}
```

### Get JFR hotspots

```http
GET /v1/incidents/{incidentId}/jfr/hotspots?type=cpu
```

### Generate report

```http
POST /v1/incidents/{incidentId}/report
```

## Error response format

```json
{
  "errorCode": "PROVIDER_UNAVAILABLE",
  "message": "The requested provider is unavailable.",
  "details": {
    "provider": "CloudWatchLogsProvider"
  },
  "traceId": "internal-trace-id"
}
```

## Pagination

All APIs returning potentially large data must support:

- `limit`
- `nextToken`
- deterministic ordering

## Redaction metadata

Responses that may contain sensitive data must include redaction metadata:

```json
{
  "redactionStatus": "REDACTED",
  "redactedFields": ["headers.authorization", "headers.cookie"]
}
```
