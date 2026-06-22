# Contributing to stella-incident-profiler

Thank you for your interest in contributing.

This project is an open-source JavaFX desktop and MCP-enabled incident profiler for AWS-hosted Java web applications.

## Public project identity

- Publisher: Stella International Co Ltd
- Primary maintainer: github.com/xxvw
- Repository name: `stella-incident-profiler`

## Language policy

- Write documentation, issues, pull requests, commit messages, and code comments in English.
- Write visible end-user UI text in Japanese.
- Use English for internal identifiers, APIs, schemas, configuration keys, classes, and methods.

## Development mode for contributors

Use debug/mock mode first.

Debug/mock mode must not require:

- AWS credentials
- private networks
- production logs
- real customer data
- real AWS account IDs

The GUI must render from synthetic mock data through dependency-injected providers.

## Branches and commits

- Use feature branches.
- Do not push directly to `main` by default.
- Commit frequently.
- Keep commits small.
- Write English commit messages.
- Prefer imperative style, for example: `Add mock incident timeline`.

## Pull requests

Each PR should include:

- summary
- scope
- what changed
- what was intentionally not changed
- linked issue or motivation
- screenshots for UI changes when practical
- tests added or updated
- manual verification steps
- security/privacy notes when relevant
- follow-up PRs when work is intentionally split

Implementation PRs that contain application logic, GUI behavior, MCP tool behavior, profiler logic, AWS integration, or data-processing logic should remain open for maintainer review. Core infrastructure PRs may be merged after review and passing CI.

## Tests

Add or update tests for providers, parsers, view models, MCP tools, and security filters.

If a change cannot be easily tested, explain why in the PR and provide manual verification steps.

## UI strings

All user-visible strings must be stored in Japanese resource bundles or equivalent localization files.

Do not hard-code English UI labels in JavaFX views.

## Mock data

Mock data must be synthetic and deterministic.

Do not commit:

- real logs
- real trace data
- real incident reports
- real AWS account IDs
- customer data
- secrets
- request or response payload bodies from production

## Dependency policy

Before adding a dependency, consider license compatibility, security impact, packaging impact, and whether the dependency is required in the desktop app, the control plane, the agent, or tests only.

Check `docs/20_open_source_governance.md` and document the dependency license in the PR description before adding it.

## DCO / CLA status

Recommended contribution model: DCO.

Final policy is pending maintainer confirmation. If DCO is adopted, contributors must sign commits with:

```text
git commit -s
```

Do not require a CLA unless Stella International Co Ltd explicitly adopts one.
