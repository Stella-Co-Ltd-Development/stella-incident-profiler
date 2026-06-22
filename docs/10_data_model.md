# Data Model

## Core entities

### Incident

```json
{
  "id": "inc-20260623-001",
  "service": "checkout-api",
  "environment": "prod",
  "severity": "HIGH",
  "status": "READY_FOR_ANALYSIS",
  "triggerType": "P95_LATENCY_SPIKE",
  "startTime": "2026-06-23T12:03:00Z",
  "peakTime": "2026-06-23T12:07:30Z",
  "endTime": "2026-06-23T12:13:00Z",
  "summary": "p95 latency increased while payment provider calls timed out.",
  "evidenceWindow": {
    "from": "2026-06-23T11:58:00Z",
    "to": "2026-06-23T12:18:00Z"
  }
}
```

### MetricPoint

```json
{
  "timestamp": "2026-06-23T12:07:00Z",
  "service": "checkout-api",
  "name": "http.server.duration.p95",
  "value": 1850.0,
  "unit": "ms",
  "tags": {
    "route": "/api/orders",
    "environment": "prod"
  }
}
```

### LogEvent

```json
{
  "timestamp": "2026-06-23T12:07:10Z",
  "service": "checkout-api",
  "level": "ERROR",
  "message": "Payment provider timeout",
  "traceId": "trace-001",
  "spanId": "span-004",
  "thread": "http-nio-8080-exec-12",
  "logger": "com.example.payment.PaymentClient",
  "exceptionClass": "java.net.SocketTimeoutException",
  "redacted": true
}
```

### TraceSpan

```json
{
  "traceId": "trace-001",
  "spanId": "span-004",
  "parentSpanId": "span-002",
  "service": "checkout-api",
  "operation": "POST payment-provider /charges",
  "kind": "CLIENT",
  "startTime": "2026-06-23T12:07:08Z",
  "durationMs": 1500,
  "status": "ERROR",
  "attributes": {
    "http.method": "POST",
    "peer.service": "payment-provider",
    "http.status_code": "504"
  }
}
```

### JfrHotspot

```json
{
  "incidentId": "inc-20260623-001",
  "type": "CPU",
  "method": "com.example.OrderService.createOrder",
  "sampleCount": 1240,
  "percentage": 31.2,
  "thread": "http-nio-8080-exec-*"
}
```

### EvidenceReference

```json
{
  "id": "ev-log-001",
  "incidentId": "inc-20260623-001",
  "type": "LOG",
  "source": "CloudWatchLogs",
  "from": "2026-06-23T12:05:00Z",
  "to": "2026-06-23T12:10:00Z",
  "uri": "incident://inc-20260623-001/logs.ndjson",
  "redactionStatus": "REDACTED"
}
```

## Enumerations

### Severity

```text
LOW
MEDIUM
HIGH
CRITICAL
```

### IncidentStatus

```text
DETECTED
COLLECTING_EVIDENCE
READY_FOR_ANALYSIS
ACKNOWLEDGED
MITIGATED
RESOLVED
ARCHIVED
```

### TriggerType

```text
P95_LATENCY_SPIKE
ERROR_RATE_SPIKE
CPU_SPIKE
GC_PAUSE_SPIKE
HEAP_PRESSURE
BLOCKED_THREAD_SPIKE
AURORA_LATENCY_SPIKE
EXTERNAL_API_LATENCY_SPIKE
MANUAL
```

### EvidenceType

```text
METRIC
LOG
TRACE
JFR
ALB_ACCESS_LOG
CLOUDFRONT_ACCESS_LOG
AURORA_METRIC
EXTERNAL_CALL_SUMMARY
REPORT
```

## Storage design

Local desktop cache:

- SQLite: settings, provider config, incident index, audit log
- DuckDB: metric windows, summarized logs, trace summaries
- file storage: `.jfr`, `.json`, `.ndjson`, `.md` reports

Cloud storage:

- S3: immutable evidence artifacts
- metadata DB: incident indexes and evidence references

## Timestamp policy

- Store timestamps as UTC ISO 8601.
- Show timestamps in the user's selected timezone in the UI.
- The UI must display the timezone explicitly.
