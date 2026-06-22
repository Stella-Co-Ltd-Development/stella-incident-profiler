# Project Rules

## Purpose

This document restates mandatory project rules in a human-readable form. Machine-readable environment settings are stored in `.codex/environment.env` and `.env.codex`.

## Language policy

| Area | Language |
|---|---|
| UI labels | Japanese |
| Error messages shown to users | Japanese |
| Tooltips | Japanese |
| Report titles shown in GUI | Japanese |
| Source comments | English |
| Commit messages | English |
| PR descriptions | English |
| Issue descriptions | English |
| Documentation | English |
| API fields | English |
| Database fields | English |
| Class/method names | English |

## Git policy

- Use small, frequent commits.
- Commit messages must be English.
- Do not push directly to `main` by default.
- Use feature branches.
- A push or merge to `main` must trigger artifact creation and release publication.

## Codex environment policy

The following settings must be treated as required policy:

```text
CODEX_COMMIT_LANGUAGE=English
CODEX_COMMIT_STYLE=small_frequent_commits
CODEX_MAIN_PUSH_POLICY=do_not_push_to_main_by_default
CODEX_RELEASE_ON_MAIN_PUSH=true
CODEX_EXPLANATION_LANGUAGE=English
CODEX_UI_LANGUAGE=Japanese
APP_PROFILE=debug
APP_DATA_MODE=mock
APP_DISABLE_AWS_IN_DEBUG=true
```

## Release policy

The `main` branch is a release branch. When `main` is updated, CI must package artifacts and attach them to a GitHub Release.

## Debug policy

Debug mode must be implemented first and must always remain available.

Debug mode requires:

- mock providers
- local mock data
- no AWS credentials
- no AWS calls
- Japanese UI rendering
- deterministic test data

## Security policy

- MCP tools are read-only by default.
- Payload capture is disabled by default.
- Sensitive headers are never captured by default.
- Production access requires explicit user selection.
- No secrets in commits.


## Open-source policy

- Repository name: `stella-incident-profiler`
- Repository description: Open-source JavaFX desktop and MCP-enabled incident profiler for AWS-hosted Java web applications using JFR, OpenTelemetry, CloudWatch, ALB logs, and Aurora metrics.
- Publisher: Stella International Co Ltd
- Primary maintainer: `github.com/xxvw`
- Documentation audience: public open-source contributors
- Public examples must use synthetic mock data only.
- The repository must not assume access to private company infrastructure.
- Add or update `CONTRIBUTING.md`, `SECURITY.md`, and `CODE_OF_CONDUCT.md` when contribution or governance behavior changes.
