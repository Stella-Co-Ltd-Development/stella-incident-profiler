# AI Implementation Guide

## Start here

An AI coding agent should start implementation from this file, `AGENTS.md`, and `.codex/environment.env`.

## Mandatory project rules

- Use Java 21 or later.
- Use JavaFX as the primary GUI framework.
- Use Gradle unless the user explicitly chooses Maven.
- Write all explanations, comments, commits, PRs, and issues in English.
- Write all visible UI labels in Japanese.
- Commit frequently and keep commits small.
- Do not push directly to `main` by default.
- If code reaches `main`, release artifacts must be built and attached to a GitHub Release.
- Implement debug/mock mode before live AWS integration.

## Recommended implementation order

### Step 1: Project skeleton

Create a Gradle multi-module project:

```text
settings.gradle.kts
build.gradle.kts
app-desktop/
core-domain/
core-application/
infrastructure-mock/
infrastructure-aws/
infrastructure-jfr/
infrastructure-mcp/
```

Commit:

```text
Initialize JavaFX project structure
```

### Step 2: Domain model

Implement core domain records/classes:

- Incident
- IncidentSummary
- TimelineWindow
- MetricPoint
- LogEvent
- TraceSummary
- TraceDetail
- TraceSpan
- JfrHotspotSummary
- AwsTopologySnapshot

Commit:

```text
Add core incident domain model
```

### Step 3: Provider ports

Implement provider interfaces:

- IncidentProvider
- TimelineProvider
- LogProvider
- TraceProvider
- JfrProvider
- AwsTopologyProvider

Commit:

```text
Add provider ports for incident data
```

### Step 4: Mock providers

Implement mock providers that load JSON/NDJSON from `mock-data/`.

Commit:

```text
Load incident views from mock providers
```

### Step 5: JavaFX shell

Create the main JavaFX app with navigation:

- Dashboard
- Service map
- Incident list
- Incident detail
- Settings

Commit:

```text
Add JavaFX navigation shell
```

### Step 6: Japanese resource bundle

Load visible UI strings from Japanese resources.

Commit:

```text
Add Japanese UI labels
```

### Step 7: Incident list and detail

Render incident data from mock providers.

Commit:

```text
Render incident detail from mock data
```

### Step 8: Timeline, logs, traces, and JFR panels

Add primary investigation panels using mock data.

Commit examples:

```text
Render unified timeline from mock data
Render log viewer from mock data
Render trace waterfall from mock data
Render JFR hotspot view from mock data
```

### Step 9: Local MCP server

Add local MCP tools backed by provider interfaces.

Commit:

```text
Add local MCP incident tools
```

### Step 10: Release workflow

Add CI workflow for test, package, and release on main.

Commit:

```text
Add release workflow for main branch
```

## Avoid these mistakes

- Do not hardcode UI strings in English.
- Do not call AWS in debug mode.
- Do not put provider logic in JavaFX controllers.
- Do not return raw sensitive payloads from MCP tools.
- Do not implement write-capable MCP tools early.
- Do not make one huge commit.
- Do not push directly to `main` unless explicitly instructed.

## Definition of done for first implementation pass

The first implementation pass is done when:

- The app starts in debug mode.
- The dashboard renders mock data.
- Incident list and detail screens render mock data.
- UI text is Japanese.
- Provider interfaces are separated from implementations.
- Unit tests verify mock provider selection.
- The project has release-on-main workflow configuration.
