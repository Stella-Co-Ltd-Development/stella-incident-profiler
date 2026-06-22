# Security and Privacy Requirements

## Security principles

1. Read-only by default.
2. No sensitive payload capture by default.
3. No production access in debug mode.
4. Least privilege AWS access.
5. Explicit user confirmation for live environments.
6. Evidence traceability and audit logs.

## Sensitive data policy

The system must not store or expose the following by default:

- Authorization headers
- Cookie headers
- Set-Cookie headers
- API keys
- passwords
- session tokens
- access tokens
- refresh tokens
- payment card data
- personal identification numbers
- raw request bodies
- raw response bodies

## Header capture policy

Default behavior:

```text
captureHeaders=false
captureSensitiveHeaders=false
```

Allowed headers should be allowlisted, not blocklisted.

## Payload capture policy

Default behavior:

```text
payloadCapture=false
```

If payload capture is implemented later:

- endpoint allowlist required
- explicit user/admin approval required
- size limit required
- retention limit required
- redaction required
- MCP exclusion by default
- audit log required

## AWS access policy

- Prefer AssumeRole.
- Avoid long-lived credentials.
- Show current AWS identity in live mode.
- Enforce account and region confirmation.
- Use separate roles for read-only investigation and artifact writing.

## MCP security

- MCP tools are read-only by default.
- Local MCP must display running status.
- Remote MCP must require authentication and authorization.
- MCP responses must be size-bounded.
- MCP responses must include redaction status.
- MCP must not execute shell commands from user input.
- MCP must not expose write operations unless explicitly enabled and approved.

## Local file security

- Store local cache under an application-specific directory.
- Do not store AWS credentials in application-managed files.
- Store only references to AWS profiles or roles.
- Allow users to clear local cache.
- Encrypt sensitive local settings if needed.

## Audit logging

Audit events must include:

- user action
- timestamp
- data mode
- provider
- AWS account and region if live
- incident ID if applicable
- MCP tool name if applicable
- result status

## Release security

- CI must not publish artifacts containing secrets.
- Release artifacts must be generated from CI, not a developer workstation.
- Main branch release workflow must run tests before publishing.
- Artifacts should include checksums.
