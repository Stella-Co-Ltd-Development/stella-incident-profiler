# Collector and Control Plane Requirements

## Goal

The control plane coordinates incident detection, evidence indexing, artifact storage, and live data access for the GUI and MCP tools.

## Deployment options

### Local-only mode

The JavaFX app talks directly to local mock data or live AWS read APIs.

### Remote control plane mode

A backend service runs in AWS and provides normalized APIs to the JavaFX GUI and remote MCP server.

Recommended implementation language:

- Java 21+
- Spring Boot or Quarkus

## Control plane responsibilities

- service inventory
- incident indexing
- evidence window creation
- metric aggregation
- log query normalization
- trace summary normalization
- JFR artifact indexing
- ALB/CloudFront log aggregation
- Aurora/RDS Proxy metric retrieval
- external dependency comparison
- report generation
- audit logging

## Data stores

### Required

- S3 for artifacts and reports
- metadata store for incidents and indexes

### Candidate metadata stores

- Aurora PostgreSQL
- DynamoDB
- OpenSearch for logs/indexes
- ClickHouse for high-volume analytics

The first implementation may use local SQLite and mock data. The completed architecture should allow cloud metadata storage.

## API style

The GUI should use a typed API client. The control plane should expose REST or gRPC APIs. REST with OpenAPI is recommended for broad tool compatibility.

## Incident lifecycle

```text
DETECTED
  -> COLLECTING_EVIDENCE
  -> READY_FOR_ANALYSIS
  -> ACKNOWLEDGED
  -> MITIGATED
  -> RESOLVED
  -> ARCHIVED
```

## Evidence indexing

Each evidence item must have:

- evidence ID
- incident ID
- type
- source
- time range
- service
- environment
- storage URI or query reference
- redaction status
- retention policy

## Release separation

The control plane and desktop app may be versioned together but packaged separately.

Artifacts:

- desktop installer for each OS
- control plane container image
- profiler agent jar
- schema bundle
- documentation bundle
