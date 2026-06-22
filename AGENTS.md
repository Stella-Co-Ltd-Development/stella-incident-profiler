# Project Rules for AI Coding Agents

Repository: `stella-incident-profiler`
Publisher: Stella International Co Ltd
Primary maintainer: `github.com/xxvw`

These rules are mandatory for Codex or any AI coding agent working on this project.

## Language rules

1. Write all explanations in English.
2. Write all code comments in English.
3. Write all commit messages in English.
4. Write all PR titles and PR descriptions in English.
5. Write all issue titles and issue descriptions in English.
6. Write all visible end-user UI text in Japanese.
7. Keep internal identifiers, package names, class names, method names, schema fields, API paths, configuration keys, database table names, and logs in English.


## Open-source project rules

1. Treat this repository as a public open-source project from the first commit.
2. Do not write documentation as if the project is internal-only.
3. Use the canonical repository name `stella-incident-profiler` unless the maintainer explicitly changes it.
4. Use this repository description in generated README files and GitHub metadata: "Open-source JavaFX desktop and MCP-enabled incident profiler for AWS-hosted Java web applications using JFR, OpenTelemetry, CloudWatch, ALB logs, and Aurora metrics."
5. Public project identity is Stella International Co Ltd.
6. Primary maintainer account is `github.com/xxvw`.
7. Do not include proprietary customer data, private AWS account details, internal network names, production log excerpts, or real credentials in examples, tests, screenshots, issues, or docs.
8. Prefer mock data and sanitized synthetic examples for all public-facing documentation and tests.
9. External contributors must be able to run debug/mock mode without AWS credentials.
10. Contributions must include tests or a clear explanation when tests are not applicable.
11. UI text must remain Japanese even though all contribution discussion, code comments, docs, commits, issues, and PRs are English.
12. Before adding a dependency, verify that its license is compatible with the project license decision documented in `docs/20_open_source_governance.md`.

## Git rules

1. Commit frequently and keep commits small.
2. Prefer one commit per coherent implementation step.
3. Use English, imperative-style commit messages, for example: `Add mock incident provider`.
4. Do not push directly to `main` by default.
5. Use feature branches for implementation work.
6. Open a pull request before merging to `main`.
7. If a push or merge to `main` happens, CI must build artifacts and publish them to a GitHub Release.
8. Never include secrets, AWS credentials, production logs, or captured payload bodies in commits.

## Implementation rules

1. Use Java 21 or later.
2. Use JavaFX as the primary desktop GUI technology.
3. Use dependency injection for all data providers.
4. Implement debug mode before live AWS integration.
5. In debug mode, render the GUI from mock data only.
6. Keep AWS clients behind ports/interfaces.
7. Keep MCP tools read-only by default.
8. Use Japanese resource bundles for UI strings.
9. Add tests for every new provider, parser, and view model.
10. Add screenshots or UI snapshot checks when feasible.

## Branch naming

Use branch names like:

```text
feature/mock-incident-dashboard
feature/javafx-timeline-view
feature/local-mcp-server
fix/jfr-parser-duration
chore/update-release-workflow
```

## Default AI task workflow

1. Read the relevant requirement document.
2. Plan a small implementation step.
3. Implement the smallest useful slice.
4. Add or update tests.
5. Run formatting and tests.
6. Commit with an English message.
7. Stop before pushing to `main` unless explicitly instructed.
