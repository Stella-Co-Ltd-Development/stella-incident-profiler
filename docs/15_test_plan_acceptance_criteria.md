# Test Plan and Acceptance Criteria

## Test strategy

The implementation must be testable without AWS through mock data first.

## Test levels

| Level | Purpose |
|---|---|
| Unit tests | domain logic, provider parsing, redaction, DTO mapping |
| Integration tests | mock providers, local database, MCP tools |
| UI tests | JavaFX view rendering and navigation |
| Contract tests | API DTOs and schema compatibility |
| Security tests | redaction, no AWS calls in debug mode, MCP limits |
| Release tests | packaged artifact creation |

## P0 acceptance criteria

### AC-001 Debug startup

Given `APP_PROFILE=debug` and `APP_DATA_MODE=mock`, when the app starts, then it must render the dashboard from mock data without AWS credentials.

### AC-002 Japanese UI

Given the app is running, all visible UI labels must be Japanese.

### AC-003 Incident list

Given mock incidents exist, the incident list must display them with Japanese column labels.

### AC-004 Incident detail

Given a mock incident is selected, the detail screen must display summary, timeline, logs, traces, and JFR hotspot panels.

### AC-005 Provider injection

Given debug mode is active, provider registry must use mock provider implementations.

### AC-006 AWS call prevention

Given debug mode is active, tests must verify no AWS client is initialized or called.

### AC-007 MCP mock mode

Given local MCP is enabled in debug mode, MCP tools must return data from mock providers.

### AC-008 Release workflow

Given a push to `main`, CI must build artifacts and attach them to a GitHub Release.

## P1 acceptance criteria

- Live CloudWatch Logs provider works behind the same `LogProvider` interface.
- Live topology provider can discover configured AWS resources.
- JFR hotspot parser can read a `.jfr` file or normalized JFR summary artifact.
- Local MCP server exposes required read-only tools.
- Incident report export works in Markdown and JSON.

## Mock data validation

All files under `mock-data/` must validate against schemas under `schemas/` where schemas exist.

## UI snapshot recommendations

For major screens, capture stable screenshots in debug mode:

- dashboard
- incident list
- incident detail
- timeline
- log viewer
- trace waterfall
- JFR hotspots
- MCP console

## Security test cases

- Authorization header is redacted.
- Cookie header is redacted.
- Payload body is not returned by default.
- MCP `search_logs` enforces a result limit.
- Debug mode blocks AWS providers.
- Release workflow does not package `.env` files containing secrets.
