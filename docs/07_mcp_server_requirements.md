# MCP Server Requirements

## Goal

The product must expose structured incident investigation data to AI assistants through MCP while preserving safety, evidence traceability, and read-only defaults.

## Implementation choice

The local MCP server must be embedded in the JavaFX desktop application or launched as a Java child process using the MCP Java SDK.

## Supported MCP modes

### Local MCP server

Primary mode.

```text
AI client
  -> local MCP transport
    -> JavaFX desktop app / local MCP server
      -> provider interfaces
        -> mock providers or live AWS providers
```

### Remote MCP server

Optional team mode.

```text
AI client
  -> Streamable HTTP MCP transport
    -> remote control plane MCP server
      -> incident service APIs
        -> AWS and artifact stores
```

## Default safety rule

All MCP tools must be read-only by default.

The following operations are not allowed by default:

- restart service
- scale service
- modify ALB
- modify ECS service
- delete logs
- change retention
- start packet capture
- enable payload capture
- download unredacted sensitive data

## Required local MCP tools

### `list_services`

Lists known services.

Input:

```json
{
  "environment": "prod"
}
```

Output:

```json
{
  "services": [
    {
      "serviceName": "checkout-api",
      "environment": "prod",
      "status": "DEGRADED"
    }
  ]
}
```

### `list_incidents`

Lists incidents within a time range.

Input:

```json
{
  "service": "checkout-api",
  "from": "2026-06-23T00:00:00Z",
  "to": "2026-06-23T23:59:59Z"
}
```

### `get_incident_summary`

Returns summary, timeline bounds, trigger, severity, likely causes, and evidence references.

### `get_incident_timeline`

Returns downsampled timeline points and event markers.

### `search_logs`

Searches logs with time range, service, level, and query filters.

### `get_trace`

Returns trace summary and spans for a trace ID.

### `find_slow_traces`

Returns slow traces in a time range.

### `get_jfr_hotspots`

Returns CPU, allocation, GC, lock, thread, or exception hotspot summaries.

### `compare_alb_before_after`

Compares ALB behavior before and during an incident.

### `compare_external_calls_before_after`

Compares external dependency behavior before and during an incident.

### `get_aurora_metrics`

Returns Aurora and RDS Proxy metrics for a time range.

### `generate_incident_report`

Generates a Markdown incident report from available evidence.

## MCP resources

Required resources:

```text
incident://{incident_id}/summary
incident://{incident_id}/timeline.json
incident://{incident_id}/logs.ndjson
incident://{incident_id}/traces.json
incident://{incident_id}/jfr-hotspots.json
service://{service_name}/dependency-map
service://{service_name}/aws-resources
service://{service_name}/slo
```

## MCP prompt templates

The product should provide prompt templates for AI assistants:

- Analyze likely root causes.
- Compare JVM vs database vs external API causes.
- Draft a stakeholder incident report.
- Suggest missing instrumentation.
- Suggest remediation and prevention actions.

## Response requirements

MCP tool responses must:

- include evidence identifiers
- include time ranges
- include redaction status
- avoid sensitive payload bodies
- be bounded in size
- include pagination or result limits for large outputs

## Audit requirements

The system must record local MCP executions in an audit log:

- timestamp
- tool name
- input hash or redacted input
- result count
- data mode: mock or live
- AWS account and region if live
- success or failure

## Security requirements

- No unredacted secrets in MCP responses.
- No payload bodies unless explicitly enabled and authorized.
- No write operations unless enabled through a separate policy and UI confirmation.
- MCP server must show running/stopped status in the GUI.
- Local MCP must be disabled by default until the user enables it in settings, unless launched in developer mode.
