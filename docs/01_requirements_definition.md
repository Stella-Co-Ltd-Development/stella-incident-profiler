# Requirements Definition

## Scope

This document defines requirements for a completed product, not only an MVP.

The product includes:

- JavaFX desktop GUI
- local mock data rendering mode
- AWS live data integration mode
- local MCP server embedded in the desktop application
- optional remote MCP server and control plane
- profiler agent and collector design
- incident detection and evidence correlation
- exportable reports and release artifacts

## Target users

| User | Description | Primary goals |
|---|---|---|
| Backend engineer | Owns Java/Spring Boot services | Find root causes in JVM, traces, logs, and DB behavior |
| SRE | Owns reliability and operations | Identify incident impact, timeline, and mitigation options |
| Platform engineer | Owns AWS platform and observability | Validate service topology and instrumentation coverage |
| Engineering manager | Needs incident reports | Read summarized incident evidence and actions |
| AI coding/investigation agent | Uses MCP tools | Query structured evidence safely |

## Functional requirements

### FR-001 Service inventory

The system must display the target application topology:

- CloudFront distribution
- S3 frontend bucket
- AWS WAF association when available
- ALB
- target groups
- ECS cluster, service, tasks, and containers
- Java backend services
- RDS Proxy
- Aurora cluster and instances
- CloudWatch log groups
- trace backend references
- S3 locations for ALB logs, CloudFront logs, and JFR files

### FR-002 Incident detection

The system must create incident records when configured thresholds are breached:

- p95 request latency baseline multiplier
- 5xx rate threshold
- Java CPU threshold
- GC pause threshold
- heap pressure threshold
- blocked thread threshold
- DB connection wait threshold
- Aurora query latency threshold
- external API latency or error rate threshold

### FR-003 Incident window capture

When an incident is detected, the system must preserve a configurable evidence window.

Default window:

```text
T-5 minutes to T+5 minutes
```

Evidence categories:

- metrics
- logs
- traces
- JFR snapshots
- ALB access log summaries
- CloudFront log summaries
- Aurora metrics
- RDS Proxy metrics
- external call summaries
- service topology snapshot

### FR-004 Java profiling

The system must support:

- JFR start, stop, and dump operations
- reading `.jfr` files
- CPU hotspot summaries
- allocation hotspot summaries
- GC pause summaries
- thread state summaries
- lock contention summaries
- exception event summaries

### FR-005 Unified timeline

The GUI must show a unified timeline containing:

- request count
- error count
- p50/p95/p99 latency
- Java CPU
- heap usage
- GC pauses
- blocked threads
- slow traces
- log events
- ALB target response time
- CloudFront edge response information
- Aurora latency
- external API latency
- JFR event overlays

### FR-006 Trace/log/JFR correlation

The system must correlate evidence using:

- timestamp
- service name
- environment
- AWS account ID
- AWS region
- ECS cluster/service/task identifiers
- trace ID
- span ID
- thread name
- request path template
- dependency name

### FR-007 Japanese UI

All end-user visible GUI text must be Japanese.

Examples:

- Dashboard: `ダッシュボード`
- Incident List: `インシデント一覧`
- Timeline: `タイムライン`
- Search Logs: `ログ検索`
- Start JFR: `JFR開始`
- Export Report: `レポート出力`

### FR-008 English engineering artifacts

All source code comments, documents, commit messages, PR descriptions, and internal identifiers must be English.

### FR-009 Mock rendering in debug mode

When the application profile is `debug` or the data mode is `mock`, the GUI must render using injected mock providers and local mock JSON/NDJSON files. It must not require AWS credentials.

### FR-010 MCP integration

The desktop application must embed a local MCP server with read-only tools for incident investigation. Optional remote MCP support may be added for team environments.

### FR-011 Report export

The system must export an incident report in Markdown and JSON formats. PDF export may be added later.

## Non-goals

- Replacing every feature of JDK Mission Control
- Capturing all packet payloads by default
- Storing secrets or sensitive request bodies
- Automatically restarting services by default
- Providing write-capable MCP tools without explicit approval workflows

## Priority scale

| Priority | Meaning |
|---|---|
| P0 | Required for first usable implementation |
| P1 | Required for completed product baseline |
| P2 | Advanced capability |
| P3 | Optional future extension |

## Requirement priority summary

| Requirement | Priority |
|---|---:|
| JavaFX desktop shell | P0 |
| Japanese UI resource bundle | P0 |
| Mock data provider and debug rendering | P0 |
| Incident list/detail screens | P0 |
| Unified timeline with mock data | P0 |
| Log viewer with mock data | P0 |
| Trace waterfall with mock data | P0 |
| JFR hotspot mock view | P0 |
| AWS provider interfaces | P1 |
| Live CloudWatch Logs integration | P1 |
| Live ALB/Aurora metrics integration | P1 |
| JFR file parser | P1 |
| Local MCP server | P1 |
| Release workflow on main push | P1 |
| Remote MCP server | P2 |
| Payload capture opt-in | P2 |
| Multi-account/multi-region | P2 |
| Automated root-cause ranking | P2 |
