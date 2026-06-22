# Stella Incident Profiler - Requirements Specification Pack

This package contains the requirements, architecture, UI, MCP, data model, security, open-source governance, operations, and AI implementation guidance for `stella-incident-profiler`, a Java-based incident debugging and performance investigation platform.


## Repository metadata

- Repository name: `stella-incident-profiler`
- Repository description: Open-source JavaFX desktop and MCP-enabled incident profiler for AWS-hosted Java web applications using JFR, OpenTelemetry, CloudWatch, ALB logs, and Aurora metrics.
- Public identity: Stella International Co Ltd
- Primary maintainer: `github.com/xxvw`
- Repository visibility target: public OSS repository
- Suggested GitHub topics: `java`, `javafx`, `aws`, `jfr`, `opentelemetry`, `mcp`, `incident-response`, `profiler`, `aurora`, `cloudwatch`

This project must be written and governed as an open-source project, not as an internal-only company tool. Documentation should explain public contribution paths, safe local mock-mode development, security boundaries, and how maintainers review external contributions.

## Product summary

The product is a JavaFX desktop application and supporting AWS control plane for investigating Java backend incidents in an AWS web application stack:

- Frontend: TypeScript single-page application hosted on S3 and CloudFront
- Edge/API entry: CloudFront, AWS WAF, ALB
- Backend: Java/Spring Boot on ECS Fargate or ECS on EC2
- Database: Aurora PostgreSQL or Aurora MySQL, optionally behind RDS Proxy
- Observability: JFR, OpenTelemetry/ADOT, CloudWatch Logs, X-Ray or OTLP traces, ALB access logs, CloudFront logs, Aurora metrics
- AI integration: Local and optional remote MCP server

## Required implementation language policy

- All explanations, source comments, commit messages, PR descriptions, issue descriptions, and implementation documentation must be written in English.
- End-user visible UI labels, menus, error messages, tooltips, and report titles must be written in Japanese.
- Internal identifiers, APIs, schemas, database columns, package names, class names, and configuration keys must be English.

## Primary GUI technology decision

- Primary desktop GUI: JavaFX / OpenJFX
- Primary implementation language: Java 21+
- Visualization: JavaFX-native components by default, JavaFX WebView + ECharts allowed for dense timeline/chart views when it improves implementation quality
- Packaging: jlink + jpackage
- Local MCP: MCP Java SDK embedded in the desktop app
- AWS integration: AWS SDK for Java 2.x
- Local storage: SQLite for metadata/settings and DuckDB for analytical caches

## Debug/mock mode policy

In debug mode, the GUI must not require AWS credentials or remote services. It must render all primary views from injectable mock providers and mock data files included in this repository. This enables AI-assisted implementation, UI development, and automated screenshot testing without a live AWS environment.

## File map

```text
.
├── AGENTS.md
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── SECURITY.md
├── .codex/
│   └── environment.env
├── .github/workflows/
│   └── release-on-main.yml
├── docs/
│   ├── 00_product_vision.md
│   ├── 01_requirements_definition.md
│   ├── 02_user_stories.md
│   ├── 03_target_aws_architecture.md
│   ├── 04_observability_architecture.md
│   ├── 05_javafx_gui_requirements.md
│   ├── 06_debug_mock_data_requirements.md
│   ├── 07_mcp_server_requirements.md
│   ├── 08_profiler_agent_requirements.md
│   ├── 09_collector_control_plane_requirements.md
│   ├── 10_data_model.md
│   ├── 11_api_contracts.md
│   ├── 12_security_privacy_requirements.md
│   ├── 13_operations_release_process.md
│   ├── 14_non_functional_requirements.md
│   ├── 15_test_plan_acceptance_criteria.md
│   ├── 16_ai_implementation_guide.md
│   ├── 17_project_rules.md
│   ├── 19_repository_metadata.md
│   ├── 20_open_source_governance.md
│   └── 21_contribution_requirements.md
├── diagrams/
├── schemas/
├── mock-data/
├── examples/
├── ui/
└── blueprint/
```

## Intended AI usage

Give this zip file to the implementation AI and start with `docs/16_ai_implementation_guide.md`, `AGENTS.md`, and `.codex/environment.env`. The AI should implement in small increments, commit frequently with English commit messages, and never push directly to `main` by default.
