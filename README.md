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

The `main` branch contains the public specification package, schemas, sanitized mock data, diagrams, examples, UI text resources, and OSS governance documents.

Implementation is being developed in small open pull requests. The active stack introduces:

- Gradle multi-module Java 21 project structure
- JavaFX desktop shell
- domain models and provider ports
- mock data providers
- mock-backed incident list/detail views
- timeline, log, trace, JFR, ALB, Aurora, and external API panels
- read-only MCP tool skeleton
- CI and release workflows

The implementation PRs are intentionally left open for review unless they are core infrastructure PRs approved for merge.

## Architecture

The planned Gradle modules are:

| Module | Responsibility |
|---|---|
| `app-desktop` | JavaFX desktop application and Japanese UI |
| `app-core` | domain model, use cases, provider interfaces, runtime settings |
| `app-mock` | debug-mode providers backed by `mock-data/*` |
| `app-aws` | future AWS adapters behind provider interfaces |
| `app-jfr` | future JFR parsing and profiler control interfaces |
| `app-mcp` | local MCP tool definitions, DTOs, and future server transport |
| `app-storage` | local settings, audit log, and incident index abstractions |
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

## MCP Direction

MCP support is intended to make incident evidence available to AI assistants in a controlled, read-only form. The local MCP server should run inside the desktop app or alongside it as a local child process. The first implementation slice defines tool contracts and provider-backed execution before adding transport/server SDK details.

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

Primary planned screens:

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

The project is intentionally built through small PRs:

1. Bootstrap the Gradle project.
2. Add CI.
3. Add release artifacts for `main`.
4. Add domain models and provider ports.
5. Add mock providers.
6. Add JavaFX shell.
7. Add mock-backed investigation views.
8. Add MCP tool contracts.
9. Add live adapters only after the mock and interface design is stable.

Core infrastructure PRs may be merged after review and passing CI. Implementation PRs that include application logic, GUI behavior, MCP behavior, profiler logic, AWS integration, or data-processing logic should remain open for maintainer review.

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
