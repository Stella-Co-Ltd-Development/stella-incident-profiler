# Stella Incident Profiler

Open-source JavaFX desktop and MCP-enabled incident profiler for AWS-hosted Java web applications using JFR, OpenTelemetry, CloudWatch, ALB logs, and Aurora metrics.

Stella Incident Profiler is published by Stella International Co Ltd and maintained by [github.com/xxvw](https://github.com/xxvw). The project is designed as a public open-source tool from the start, with mock data, local debug mode, and privacy-preserving defaults so contributors can work without access to any private AWS account or production system.

## Project Goals

Stella Incident Profiler helps engineers investigate Java backend incidents by correlating:

- incident summaries and evidence windows
- CloudWatch-style application logs
- OpenTelemetry or X-Ray trace summaries
- Java Flight Recorder hotspot evidence
- ALB and CloudFront behavior
- Aurora and RDS Proxy metrics
- external API latency and error changes
- local MCP tools for read-only AI-assisted investigation

The initial implementation target is a Java 21+ JavaFX desktop application that starts in debug/mock mode without AWS credentials.

## Current Status

The `main` branch currently contains the public project specification package, schemas, sanitized mock data, diagrams, examples, and contribution policy documents. Implementation work is being added through small pull requests.

The repository is not an internal-only requirements archive. It is the public project home for `stella-incident-profiler`; the specification files under `docs/`, `schemas/`, `mock-data/`, `examples/`, and `ui/` are the source of truth for the implementation.

## Planned Architecture

The first implementation pass uses a Gradle multi-module Java project:

- `app-desktop`: JavaFX desktop application
- `app-core`: domain model, use cases, and provider interfaces
- `app-mock`: mock data providers for debug mode
- `app-aws`: AWS integration adapters behind interfaces
- `app-jfr`: JFR parsing and profiler interfaces
- `app-mcp`: embedded local MCP server and read-only tools
- `app-storage`: local settings and incident index storage abstractions
- `app-test-support`: shared test fixtures

The GUI must consume data only through provider interfaces. In debug mode, providers load synthetic fixtures from `mock-data/` and must not call AWS APIs or require credentials.

## Debug and Mock Data

External contributors must be able to run and test the project without AWS credentials. Mock mode is active when any of these are true:

- `APP_PROFILE=debug`
- `APP_DATA_MODE=mock`
- the application is launched with `--mock`
- the selected provider is a mock provider

The visible UI language is Japanese. Developer-facing text, documentation, commit messages, issues, pull requests, source comments, logs, identifiers, package names, and API fields are English.

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

## Implementation Source of Truth

Start with:

- [AGENTS.md](AGENTS.md)
- [docs/16_ai_implementation_guide.md](docs/16_ai_implementation_guide.md)
- [docs/05_javafx_gui_requirements.md](docs/05_javafx_gui_requirements.md)
- [docs/06_debug_mock_data_requirements.md](docs/06_debug_mock_data_requirements.md)
- [docs/07_mcp_server_requirements.md](docs/07_mcp_server_requirements.md)
- [docs/15_test_plan_acceptance_criteria.md](docs/15_test_plan_acceptance_criteria.md)
- [docs/20_open_source_governance.md](docs/20_open_source_governance.md)

## Security and Privacy

Do not commit credentials, private AWS account details, production logs, captured payload bodies, customer data, private incident reports, or internal network names. Public examples, tests, screenshots, and documentation must use sanitized synthetic data.

MCP tools are read-only by default. Debug mode must not initialize AWS clients or open production network connections.

## Contributing

Contributions should be made through small pull requests with English titles and descriptions. Application UI text must remain Japanese. See [CONTRIBUTING.md](CONTRIBUTING.md) for the full contribution workflow.

## License Status

The recommended license is Apache License 2.0, but the final project license is still documented as `TBD: Apache-2.0 recommended` until Stella International Co Ltd confirms the license decision. See [LICENSE_DECISION.md](LICENSE_DECISION.md) and [docs/20_open_source_governance.md](docs/20_open_source_governance.md).
