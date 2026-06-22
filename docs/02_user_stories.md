# User Stories

## Backend engineer stories

### US-001 Open mock dashboard in debug mode

As a backend engineer, I want to open the desktop GUI without AWS credentials so that I can develop and test views locally.

Acceptance criteria:

- The app starts with `APP_PROFILE=debug`.
- The app loads `mock-data/incidents.json` and related files.
- The dashboard renders Japanese UI text.
- No AWS SDK call is made.

### US-002 Investigate a latency spike

As a backend engineer, I want to select a latency incident and inspect the unified timeline so that I can identify whether the spike came from the JVM, database, external API, or edge layer.

Acceptance criteria:

- The incident detail screen shows the incident peak time.
- Timeline overlays metrics, logs, traces, and JFR events.
- Selecting a time range filters logs, traces, and JFR hotspot panels.

### US-003 Inspect slow traces

As a backend engineer, I want to view a trace waterfall for slow requests so that I can see which span dominated latency.

Acceptance criteria:

- The trace view displays parent/child spans.
- The longest span is visually distinguishable.
- Span details show service name, operation name, duration, status, and attributes.

### US-004 Inspect JFR hotspots

As a backend engineer, I want to inspect CPU, allocation, GC, and lock hotspots so that I can understand JVM-level causes.

Acceptance criteria:

- JFR hotspot tabs exist for CPU, allocation, GC, and lock contention.
- Mock hotspot data renders in debug mode.
- Live `.jfr` parsing can be added behind the same interface.

## SRE stories

### US-005 Export incident report

As an SRE, I want to export an incident report so that I can share the timeline and evidence with stakeholders.

Acceptance criteria:

- The report is generated in Markdown.
- The report includes incident summary, impact, timeline, likely causes, and evidence links.
- The UI labels are Japanese while report section keys may be English unless exported for end users.

### US-006 Compare before and after windows

As an SRE, I want to compare metrics and network/dependency behavior before and after a spike so that I can identify abnormal changes.

Acceptance criteria:

- The GUI shows baseline window and incident window.
- The comparison lists changed endpoints, external dependencies, DB latency, and error rates.

## Platform engineer stories

### US-007 Validate AWS topology

As a platform engineer, I want to view the AWS resource topology so that I can confirm which services and logs are connected.

Acceptance criteria:

- The GUI shows CloudFront, S3, ALB, ECS, RDS Proxy, Aurora, CloudWatch log groups, and trace backend references.
- Missing instrumentation is shown as a warning.

### US-008 Run without direct production access

As a platform engineer, I want local development to use mock data and live production use to require explicit AWS authentication so that accidental production access is prevented.

Acceptance criteria:

- Debug mode blocks AWS calls by default.
- Live mode requires explicit profile/account/role selection.
- The selected account and region are visible in the GUI.

## AI/MCP stories

### US-009 Query incident summary through MCP

As an AI investigation agent, I want to call `get_incident_summary` so that I can summarize an incident using structured evidence.

Acceptance criteria:

- The MCP server exposes read-only incident tools.
- The MCP response includes evidence IDs or resource URIs.
- No sensitive payload body is returned.

### US-010 Search logs through MCP

As an AI investigation agent, I want to search logs within an incident window so that I can identify error patterns.

Acceptance criteria:

- The tool accepts service, time range, level, and query.
- The result is limited and redacted.
- The tool returns references to full evidence resources.
