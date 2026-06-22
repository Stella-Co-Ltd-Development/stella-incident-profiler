# Stella Incident Profiler

[日本語版 README](README_ja.md)

Open-source JavaFX desktop and MCP-enabled incident profiler for AWS-hosted Java web applications using JFR, OpenTelemetry, CloudWatch, ALB logs, and Aurora metrics.

Stella Incident Profiler is published by Stella International Co Ltd and maintained by [github.com/xxvw](https://github.com/xxvw). The project is designed for small, AI-driven engineering companies that need serious incident investigation capability without building a large observability platform team first.

The product direction is simple: keep production data private, make local debug mode useful from day one, expose investigation context to AI assistants through read-only MCP tools, and keep every implementation step small enough for human review.

## Why This Exists

Small AI-driven development companies often run lean infrastructure and rely heavily on automation. When a Java service on AWS slows down, the evidence is usually scattered across traces, logs, JFR artifacts, ALB behavior, Aurora metrics, and external dependency symptoms. The missing piece is not another dashboard. It is a local, reviewable investigation workbench that organizes evidence, keeps sensitive data bounded, and lets AI assistants ask safe, structured questions.

Stella Incident Profiler is that workbench.

It is built around these principles:

- **Mock-first development**: contributors can run the product without AWS credentials.
- **Provider ports before integrations**: JavaFX never calls AWS, CloudWatch, X-Ray, S3, JFR files, or remote services directly.
- **Read-only AI access**: MCP tools expose investigation evidence, not production mutation controls.
- **Japanese user interface**: visible application text is Japanese, while developer-facing text stays English.
- **Small PRs**: architecture, CI, GUI, providers, and MCP evolve through reviewable branches.
- **OSS-safe examples**: all public fixtures are synthetic and sanitized.

## Who It Is For

This project is a good fit for:

- small AI-native software companies running Java services on AWS
- teams that want local incident investigation before adding live AWS integrations
- maintainers who want AI assistants to inspect incident evidence safely
- engineering teams that prefer explicit provider interfaces over direct SDK calls inside UI code
- contributors who need to work from public mock data without private infrastructure access

It is not intended to be a write-capable production control plane by default. Restarting services, changing ALB/ECS configuration, deleting logs, changing retention, or capturing payload bodies are intentionally outside the default MCP and GUI behavior.

## Current Repository State

The `main` branch now contains the initial runnable OSS implementation, not only the original specification package. It includes the Gradle multi-module project, JavaFX desktop shell, mock providers, domain/provider API contracts, mock-backed investigation views, read-only MCP tool skeleton, CI, and release artifact workflow.

Current implemented scope:

- Gradle multi-module Java 21 project structure
- JavaFX desktop shell that starts in mock/debug mode
- Japanese UI resource loading from `ui/ja-JP.json`
- visible mock-mode indicator: `モックデータ表示中`
- provider interfaces in `app-core`
- mock providers in `app-mock` backed by `mock-data/*`
- dashboard, incident list, incident detail, timeline, log, trace, JFR, ALB, Aurora, external API, settings, and MCP console surfaces
- read-only MCP tool definitions, DTOs, and provider-backed skeleton executor
- unit tests for domain JSON mapping, mock provider loading, UI resource key coverage, and MCP tool contracts
- pull request CI with Java 21 and Gradle
- release-on-main workflow that builds portable artifacts, bundles docs/schemas/mock data, writes checksums, and publishes a GitHub Release

## Architecture

The current Gradle modules are:

| Module | Responsibility |
|---|---|
| `app-desktop` | JavaFX desktop application and Japanese UI |
| `app-core` | domain model, provider interfaces, runtime settings, JSON support |
| `app-mock` | debug-mode providers backed by `mock-data/*` |
| `app-aws` | placeholder module for future AWS adapters behind provider interfaces |
| `app-jfr` | placeholder module for future JFR parsing and profiler control interfaces |
| `app-mcp` | read-only MCP tool definitions, DTOs, and provider-backed executor skeleton |
| `app-storage` | placeholder module for local settings, audit log, and incident index abstractions |
| `app-test-support` | shared test fixtures |

The key design boundary is the provider registry. GUI, MCP, storage, AWS, and JFR code meet through ports instead of direct dependencies.

```java
public record ProviderRegistry(
    RuntimeMode mode,
    IncidentProvider incidents,
    TimelineProvider timelines,
    LogProvider logs,
    TraceProvider traces,
    JfrProvider jfr,
    AwsTopologyProvider topology,
    AnalysisProvider analysis
) {
}
```

Mock mode wires those ports without AWS:

```java
public static ProviderRegistry create(Path dataDirectory) {
    MockDataRepository repository = new MockDataRepository(dataDirectory);
    return new ProviderRegistry(
        RuntimeMode.DEBUG,
        new MockIncidentProvider(repository),
        new MockTimelineProvider(repository),
        new MockLogProvider(repository),
        new MockTraceProvider(repository),
        new MockJfrProvider(repository),
        new MockAwsTopologyProvider(repository),
        new MockAnalysisProvider(repository)
    );
}
```

The JavaFX shell is designed to boot from runtime settings, load Japanese UI text, and show mock mode explicitly:

```java
RuntimeSettings settings = RuntimeSettings.fromEnvironment(
    System.getenv(),
    getParameters().getRaw().toArray(String[]::new),
    Path.of("mock-data")
);
providers = settings.mode() == RuntimeMode.DEBUG
    ? MockProviderFactory.create(settings.mockDataDirectory())
    : MockProviderFactory.create(settings.mockDataDirectory());
```

Live AWS providers will replace the mock factory behind the same ports. The UI should not need to know which provider implementation is active.

## Provider API

The current application API is the Java provider-port layer under `app-core/src/main/java/com/stella/incidentprofiler/core/port`. These interfaces are the stable boundary between JavaFX, MCP, mock data, and future live integrations.

### Provider Registry

```java
public record ProviderRegistry(
    RuntimeMode mode,
    IncidentProvider incidents,
    TimelineProvider timelines,
    LogProvider logs,
    TraceProvider traces,
    JfrProvider jfr,
    AwsTopologyProvider topology,
    AnalysisProvider analysis
) {
}
```

### Incident API

```java
public interface IncidentProvider {
    List<Incident> listIncidents(IncidentQuery query);

    Incident getIncident(String incidentId);
}

public record IncidentQuery(String service, String environment, Instant from, Instant to) {
    public static IncidentQuery all() {
        return new IncidentQuery(null, null, null, null);
    }
}
```

### Timeline API

```java
public interface TimelineProvider {
    TimelineWindow getTimeline(String incidentId, Instant from, Instant to);
}
```

`TimelineWindow` contains the incident ID, resolution, and timeline points for latency, errors, CPU, heap, GC pause, blocked threads, DB latency, external API latency, and ALB target response time.

### Log API

```java
public interface LogProvider {
    Page<LogEvent> searchLogs(LogQuery query);
}

public record LogQuery(
    String service,
    LogLevel level,
    String query,
    String traceId,
    Instant from,
    Instant to,
    int limit
) {
}

public record Page<T>(List<T> items, String nextPageToken) {
}
```

The current mock implementation returns redacted synthetic log events from `mock-data/logs.ndjson`.

### Trace API

```java
public interface TraceProvider {
    TraceDetail getTrace(String traceId);

    List<TraceSummary> findSlowTraces(TraceQuery query);
}

public record TraceQuery(String service, Instant from, Instant to, int limit) {
}
```

### JFR API

```java
public interface JfrProvider {
    JfrHotspotSummary getHotspots(String incidentId, JfrHotspotType type);
}
```

The current mock JFR provider loads normalized hotspot summaries. It does not parse raw `.jfr` files yet.

### AWS Topology And Analysis APIs

```java
public interface AwsTopologyProvider {
    AwsTopologySnapshot getTopology(String environmentId);
}

public interface AnalysisProvider {
    AlbAnalysis compareAlbBeforeAfter(String incidentId);

    AuroraMetrics getAuroraMetrics(String incidentId);

    ExternalCallAnalysis compareExternalCallsBeforeAfter(String incidentId);
}
```

These APIs currently return mock topology, ALB, Aurora, and external dependency evidence. Future live adapters should implement the same ports.

## Draft REST API Direction

There is no running REST control plane in the current implementation. The draft control-plane API remains a design target for future remote or team-mode integrations:

```http
GET /v1/incidents?service=checkout-api&from=2026-06-23T00:00:00Z&to=2026-06-23T23:59:59Z
GET /v1/incidents/{incidentId}
GET /v1/incidents/{incidentId}/timeline?from=...&to=...
POST /v1/logs/search
GET /v1/traces/{traceId}
GET /v1/incidents/{incidentId}/jfr/hotspots?type=cpu
POST /v1/incidents/{incidentId}/report
```

REST responses should follow the same domain DTOs and redaction rules as the provider and MCP layers. Any endpoint returning large data must support `limit`, `nextToken`, and deterministic ordering.

## MCP Specification

MCP support makes incident evidence available to AI assistants in a controlled, read-only form. The current implementation includes tool definitions, request/response DTOs, a provider-backed executor skeleton, and an MCP console listing inside the JavaFX shell. It does not yet include MCP Java SDK transport/server integration.

### MCP DTOs

```java
public record McpToolDefinition(
    String name,
    String description,
    boolean readOnly,
    Map<String, String> inputFields
) {
}

public record McpToolRequest(String toolName, Map<String, Object> arguments) {
}

public record McpToolResponse(
    String toolName,
    Object result,
    boolean readOnly,
    boolean redacted
) {
}
```

### Implemented Read-Only Tool Registry

The read-only tool registry is shaped like this:

```java
public static List<McpToolDefinition> readOnlyTools() {
    return List.of(
        tool("list_services", "List known services.", Map.of("environment", "Environment name.")),
        tool("list_incidents", "List incidents within a time range.", Map.of("service", "Service name.", "from", "Start time.", "to", "End time.")),
        tool("get_incident_summary", "Return incident summary and evidence bounds.", Map.of("incidentId", "Incident identifier.")),
        tool("get_incident_timeline", "Return downsampled timeline points.", Map.of("incidentId", "Incident identifier.")),
        tool("search_logs", "Search redacted log events.", Map.of("service", "Service name.", "query", "Text query.", "limit", "Result limit.")),
        tool("get_trace", "Return trace summary and spans.", Map.of("traceId", "Trace identifier.")),
        tool("get_jfr_hotspots", "Return JFR hotspot summaries.", Map.of("incidentId", "Incident identifier.", "type", "Hotspot type.")),
        tool("compare_alb_before_after", "Compare ALB behavior before and during an incident.", Map.of("incidentId", "Incident identifier.")),
        tool("compare_external_calls_before_after", "Compare external dependency behavior before and during an incident.", Map.of("incidentId", "Incident identifier.")),
        tool("generate_incident_report", "Generate a Markdown incident report from available evidence.", Map.of("incidentId", "Incident identifier."))
    );
}
```

The MCP philosophy is strict:

- tools are read-only by default
- responses must be redacted and bounded
- large result sets need limits or pagination
- payload bodies are excluded unless a future explicit policy enables them
- local MCP executions should be auditable
- write operations require a separate policy and explicit user confirmation

### Current MCP Executor Behavior

`McpIncidentToolExecutor` dispatches requests by `toolName`:

| Tool | Current behavior |
|---|---|
| `list_services` | Lists unique services from incidents |
| `list_incidents` | Returns all mock incidents |
| `get_incident_summary` | Returns one `Incident` by `incidentId` |
| `get_incident_timeline` | Returns the incident timeline for the incident evidence window |
| `search_logs` | Searches logs by optional `service`, `query`, and `limit` |
| `get_trace` | Returns one trace by `traceId` |
| `get_jfr_hotspots` | Returns JFR hotspots for an incident |
| `compare_alb_before_after` | Returns ALB before/during comparison |
| `compare_external_calls_before_after` | Returns external dependency comparison |
| `generate_incident_report` | Returns a Markdown report string from mock evidence |

All current responses are marked `readOnly=true` and `redacted=true`.

Current MCP limitations:

- no MCP Java SDK transport/server has been wired yet
- no MCP resources or prompt templates are registered yet
- no local MCP audit log is persisted yet
- `get_aurora_metrics` is still a planned MCP tool even though Aurora data is available through `AnalysisProvider` and the JavaFX panel
- request validation is intentionally minimal in the skeleton executor

## Debug Mode

Debug mode is active when any of these are true:

- `APP_PROFILE=debug`
- `APP_DATA_MODE=mock`
- the application is launched with `--mock`
- the selected provider is a mock provider

In debug mode, the application must not initialize AWS clients, require AWS credentials, or open production network connections. It should render from repository fixtures:

- `mock-data/incidents.json`
- `mock-data/timeline.json`
- `mock-data/logs.ndjson`
- `mock-data/traces.json`
- `mock-data/jfr-hotspots.json`
- `mock-data/aws-topology.json`
- `mock-data/external-calls.json`
- `mock-data/aurora-metrics.json`
- `mock-data/alb-analysis.json`

The UI must show:

```text
モックデータ表示中
```

## User Interface

The desktop UI is JavaFX/OpenJFX. Visible UI labels, menus, tooltips, errors, and report titles are Japanese. Developer-facing code, comments, logs, identifiers, documentation, commit messages, PRs, and issues are English.

Current mock-backed screens and surfaces:

- `ダッシュボード`
- `サービス構成`
- `インシデント一覧`
- `インシデント詳細`
- `統合タイムライン`
- `ログビューア`
- `トレースウォーターフォール`
- `JFRホットスポット`
- `MCPコンソール`
- `設定`

UI text is sourced from `ui/ja-JP.json`.

The current UI is intentionally simple and table-driven. It is suitable for validating data flow, provider wiring, and Japanese labels, but not yet a polished production investigation console. Charting, timeline zoom/pan, UI snapshot tests, and richer filters remain future work.

## Build, Test, And Release

Local build:

```sh
./gradlew build
```

Run the desktop app in mock mode on a machine with a graphical desktop:

```sh
./gradlew :app-desktop:run --args='--mock'
```

Pull request CI:

- checks out the repository
- installs Temurin Java 21
- configures Gradle caching
- runs `./gradlew build --no-daemon`

Release-on-main workflow:

- runs on pushes to `main`
- runs `./gradlew build --no-daemon`
- creates a portable desktop distribution zip
- creates a desktop application jar
- bundles schemas, documentation, mock data, examples, and UI resources
- writes SHA-256 checksums
- publishes a GitHub Release for the main branch build

Native installers are not produced yet.

## Repository Contents

- `docs/`: product, architecture, security, testing, release, MCP, and implementation requirements
- `schemas/`: JSON schemas for incident, log, metric, trace, JFR, and MCP data
- `mock-data/`: sanitized synthetic fixtures for debug mode
- `examples/`: public synthetic sample inputs and reports
- `ui/ja-JP.json`: Japanese UI labels and messages
- `diagrams/`: Mermaid architecture and timeline diagrams
- `blueprint/`: implementation reference snippets and project structure notes
- `CONTRIBUTING.md`: public contribution workflow
- `SECURITY.md`: responsible disclosure guidance
- `CODE_OF_CONDUCT.md`: community behavior policy
- `LICENSE_DECISION.md`: current license decision status

## Implementation Source Of Truth

Start with:

- [AGENTS.md](AGENTS.md)
- [docs/16_ai_implementation_guide.md](docs/16_ai_implementation_guide.md)
- [docs/05_javafx_gui_requirements.md](docs/05_javafx_gui_requirements.md)
- [docs/06_debug_mock_data_requirements.md](docs/06_debug_mock_data_requirements.md)
- [docs/07_mcp_server_requirements.md](docs/07_mcp_server_requirements.md)
- [docs/15_test_plan_acceptance_criteria.md](docs/15_test_plan_acceptance_criteria.md)
- [docs/20_open_source_governance.md](docs/20_open_source_governance.md)

## Development Workflow

The project should continue to use small, reviewable PRs. Current next likely implementation areas are:

- MCP Java SDK transport/server integration
- local MCP audit logging
- richer MCP resources and prompt templates
- live AWS adapters behind the existing provider ports
- JFR parser support for raw or normalized JFR artifacts
- local storage for settings, incident index, and MCP audit events
- UI snapshot tests and richer JavaFX interaction tests
- timeline zoom/pan and linked filtering across logs, traces, and hotspots
- native packaging through jlink/jpackage

## Security And Privacy

Do not commit credentials, private AWS account details, production logs, captured payload bodies, customer data, private incident reports, or internal network names. Public examples, tests, screenshots, and documentation must use sanitized synthetic data.

Default posture:

- MCP tools are read-only.
- Payload capture is disabled.
- Sensitive headers are redacted.
- Debug mode does not require credentials.
- Live AWS access requires explicit user configuration.

## Contributing

Contributions should be made through small pull requests with English titles and descriptions. Application UI text must remain Japanese. See [CONTRIBUTING.md](CONTRIBUTING.md).

## License Status

The recommended license is Apache License 2.0, but the final project license is documented as `TBD: Apache-2.0 recommended` until Stella International Co Ltd confirms the license decision. See [LICENSE_DECISION.md](LICENSE_DECISION.md) and [docs/20_open_source_governance.md](docs/20_open_source_governance.md).
