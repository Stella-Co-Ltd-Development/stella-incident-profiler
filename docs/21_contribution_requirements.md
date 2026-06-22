# Contribution Requirements

## Contribution goal

The project must be easy for external contributors to run, test, and extend without access to private AWS environments.

Debug/mock mode is the default contribution path.

## Required contributor workflow

1. Fork the repository or create a feature branch.
2. Create a small branch with a descriptive English name.
3. Implement a focused change.
4. Add or update tests.
5. Run the required local checks.
6. Commit with a small English commit message.
7. Open a pull request in English.
8. Wait for maintainer review.

## Commit rules

- Commit messages must be English.
- Commits must be small and focused.
- Prefer imperative mood, for example `Add mock trace provider`.
- Do not mix formatting-only changes with behavior changes.
- Do not commit generated artifacts unless the repository explicitly requires them.

## Branch rules

Recommended branch names:

```text
feature/mock-incident-dashboard
feature/japanese-ui-bundle
feature/local-mcp-tools
fix/timeline-range-selection
chore/update-ci-release
```

Do not push directly to `main` by default.

## Pull request requirements

A PR must include:

- summary of the change
- motivation or linked issue
- screenshots for UI changes when possible
- tests added or updated
- manual test notes
- security/privacy notes when the change touches telemetry, logs, traces, payloads, AWS permissions, or MCP tools

## UI contribution rules

All visible UI text must be Japanese.

Examples:

```text
OK: インシデント一覧
OK: タイムライン
OK: ログ検索
NG: Incident List
NG: Timeline
NG: Search Logs
```

English is required for code identifiers, comments, documentation, commits, PRs, and issues.

## Mock data contribution rules

Mock data must be synthetic.

Do not copy real customer logs, real incident reports, real AWS topology, real trace IDs, real IP addresses, real request payloads, or real account IDs.

Use stable deterministic mock data so screenshots and tests are reproducible.

## Testing expectations

Contributions should add tests for:

- providers
- parsers
- view models
- MCP tools
- security filters
- mock/live provider switching

UI changes should include screenshot tests or a documented manual verification path when automated GUI tests are not practical.

## Dependency contribution rules

Before adding a dependency, document:

- why it is needed
- why existing dependencies are insufficient
- license name
- whether it runs in the desktop app, control plane, agent, or tests only

Do not add dependencies with restrictive or unclear licenses without maintainer approval.

## Security-sensitive changes

Maintainer approval is required for changes involving:

- payload capture
- header capture
- AWS IAM permissions
- credential storage
- MCP tools that can mutate state
- remote MCP server authentication
- JFR attach behavior
- local file access
- process execution

## Documentation requirements

Documentation must be written in English.

Public documentation must assume the reader is an external contributor and has no Stella International Co Ltd private context.
