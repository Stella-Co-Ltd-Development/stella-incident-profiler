# Observability Architecture

## Goals

The observability layer must collect enough data to reconstruct incidents across the edge, application, JVM, database, and dependencies.

## Signal categories

| Signal | Source | Purpose |
|---|---|---|
| Metrics | CloudWatch, OpenTelemetry, Aurora | Detect spikes and trends |
| Logs | CloudWatch Logs | Understand errors and application state |
| Traces | OpenTelemetry/ADOT, X-Ray, OTLP backend | Follow request execution |
| JFR | JVM Flight Recorder | Inspect CPU, allocation, GC, locks, threads |
| Edge/API logs | ALB and CloudFront logs | Analyze entry traffic and response behavior |
| Dependency summary | OTel spans or interceptors | Analyze external APIs and DB calls |

## Collection model

```text
Java backend container
  |-- OpenTelemetry or ADOT Java agent
  |-- JFR recording policy
  |-- JSON logs with trace_id/span_id
  |-- optional HTTP/gRPC/JDBC interceptors
        |
        v
AWS observability services and artifact stores
  |-- CloudWatch Logs
  |-- CloudWatch Metrics
  |-- X-Ray or OTLP trace backend
  |-- S3 for JFR and access logs
        |
        v
Control Plane API / Desktop providers
        |
        v
JavaFX GUI and MCP server
```

## Correlation keys

Every signal should preserve as many of the following fields as possible:

- timestamp
- service name
- environment
- AWS account ID
- AWS region
- ECS cluster name
- ECS service name
- ECS task ID
- container name
- host or task IP
- trace ID
- span ID
- parent span ID
- thread name
- log level
- request route
- dependency name

## Log format requirement

Application logs should be structured JSON. At minimum:

```json
{
  "timestamp": "2026-06-23T12:00:00Z",
  "level": "ERROR",
  "service": "checkout-api",
  "environment": "prod",
  "traceId": "...",
  "spanId": "...",
  "thread": "http-nio-8080-exec-12",
  "logger": "com.example.CheckoutController",
  "message": "Payment provider timeout",
  "exceptionClass": "java.net.SocketTimeoutException"
}
```

## Trace requirements

Trace spans should include:

- service name
- operation name
- span kind
- start time
- duration
- status
- error flag
- HTTP method and route
- status code
- peer service
- DB system and operation
- AWS resource attributes where available

## JFR requirements

JFR collection must support:

- continuous low-overhead profile template
- incident snapshot dump
- manual start/stop/dump from GUI
- CPU samples
- allocation samples
- GC events
- Java monitor blocked events
- thread events
- exception events

## Incident evidence window

Default:

```text
T-5 minutes to T+5 minutes
```

Configurable:

```text
minimum: T-1 minute to T+1 minute
recommended maximum: T-30 minutes to T+30 minutes
```

## Data volume controls

The system must limit data volume through:

- top-N summaries
- sampling controls
- time range constraints
- pagination
- retention policies
- payload redaction and capture disablement

## Payload capture policy

Payload capture is not part of the default observability layer.

If implemented:

- It must be opt-in.
- It must be limited by endpoint allowlist.
- It must redact sensitive data.
- It must enforce size limits.
- It must be excluded from MCP responses unless explicitly permitted.
