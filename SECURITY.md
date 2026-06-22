# Security Policy

## Supported versions

Before the first public stable release, only the `main` branch and the latest pre-release are supported for security review.

## Reporting a vulnerability

Do not open public GitHub issues for security vulnerabilities.

Before public release, maintainers must add a private security contact for Stella International Co Ltd or enable GitHub private vulnerability reporting.

Until then, security reports should be coordinated with the primary maintainer:

```text
github.com/xxvw
```

## Sensitive areas

Please report issues involving:

- credential storage
- AWS IAM role handling
- AWS SSO or AssumeRole behavior
- local file access
- process execution
- MCP tool authorization
- remote MCP server authentication
- telemetry capture
- log redaction
- header redaction
- payload capture
- JFR attach behavior
- release artifact integrity

## Data handling policy

Never include real credentials, customer data, production logs, request payloads, response payloads, or private AWS account details in public reports.

Use synthetic examples where possible.

## Default security posture

- MCP tools are read-only by default.
- Payload capture is disabled by default.
- Sensitive headers must be redacted by default.
- Debug/mock mode must not require AWS credentials.
- Live AWS access must require explicit user configuration.
