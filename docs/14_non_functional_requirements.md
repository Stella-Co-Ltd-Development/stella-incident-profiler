# Non-Functional Requirements

## Performance

- The desktop app must start in debug mode within a reasonable time for local development.
- Incident list rendering must handle at least 1,000 incident summaries.
- Log viewer must handle at least 100,000 log events through virtualization or pagination.
- Timeline rendering must support at least 10,000 aggregated points and markers.
- MCP tools must enforce default result limits.

## Reliability

- Failure of AWS providers must not crash the GUI.
- Failure of MCP server startup must show a Japanese error message and keep the GUI usable.
- Failure to parse one evidence file must not prevent other evidence from loading.
- Mock mode must always be available.

## Availability

For remote control plane mode:

- Control plane should be Multi-AZ.
- Artifact storage should use durable S3 storage.
- Collector failures must not impact the production application.

## Scalability

Completed product should support:

- multiple services
- multiple environments
- multiple AWS accounts
- multiple regions
- large incident histories through pagination and indexing

## Security

- Least privilege IAM.
- No production access in debug mode.
- MCP read-only default.
- No sensitive payload storage by default.
- Redaction before display and export.

## Maintainability

- Domain model must be independent of JavaFX.
- Providers must be replaceable through interfaces.
- Mock data must remain versioned and schema-validated.
- API DTOs must be tested.
- UI text must be externalized.

## Portability

- Desktop app should support Windows, macOS, and Linux over time.
- Initial CI may build only on one OS, but the project should be structured for multi-OS packaging.

## Observability of the tool itself

The product should log its own operations:

- provider calls
- MCP tool invocations
- parse errors
- cache events
- AWS identity selection

These logs must not include secrets.

## Usability

- Japanese UI must be clear for operations engineers.
- Every live-mode screen must show environment/account context.
- Debug mode must be visually obvious.
- Empty states must explain what data is missing and why.
