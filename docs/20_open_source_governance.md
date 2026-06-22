# Open Source Governance

## Project status

This project is designed as a future public OSS repository from the beginning.

The repository must not assume access to Stella International Co Ltd private systems, private AWS accounts, customer data, or internal-only infrastructure.

## Publisher

Stella International Co Ltd is the public project publisher.

## Maintainer

Primary maintainer:

```text
github.com/xxvw
```

The maintainer is responsible for:

- reviewing pull requests
- managing issues and discussions
- approving releases
- maintaining project direction
- enforcing security and privacy rules
- ensuring public examples use synthetic data only

## Governance model

Initial governance model: benevolent maintainer with public contribution workflow.

Decision-making rules:

1. Small implementation changes may be merged after maintainer review and passing CI.
2. Public API, MCP tool, data model, security, licensing, and architecture changes require explicit maintainer approval.
3. Changes that affect captured data, payload handling, authentication, or AWS permissions require a security review.
4. Breaking changes must be documented in the changelog and release notes.

## License decision

Recommended license: Apache License 2.0.

Reasoning:

- permissive OSS model
- common in Java/AWS/cloud tooling
- includes express patent license language
- friendly to both community and commercial adoption

Required action before public release:

- Confirm that Stella International Co Ltd approves Apache License 2.0 or replace this recommendation with the selected license.
- Add a root `LICENSE` file using the approved license text.
- Add SPDX license headers only if the maintainers decide to enforce source-level headers.

Until the license is confirmed, generated implementation tasks must mark the license as `TBD: Apache-2.0 recommended` and must not publish a public release claiming a final license.

## Contributor agreement policy

Default recommendation: Developer Certificate of Origin (DCO), not a custom CLA, unless Stella International Co Ltd requires a CLA.

If DCO is adopted:

- Contributors must sign commits with `Signed-off-by`.
- CI should include a DCO check.
- `CONTRIBUTING.md` must explain how to sign commits.

If a CLA is required later:

- Add a CLA bot or manual process.
- Update `CONTRIBUTING.md` before requiring external contributors to sign it.

## Code of conduct

The repository includes `CODE_OF_CONDUCT.md` based on a contributor-friendly public OSS behavior policy. The maintainer should replace contact placeholders before public release.

## Security policy

The repository includes `SECURITY.md` with responsible disclosure guidance.

Security-sensitive reports must not be opened as public GitHub issues.

## Public roadmap policy

Public roadmap items should be documented in GitHub Issues, GitHub Projects, or `docs/roadmap.md`.

Do not publish roadmap items that reveal private customer systems, private AWS account structures, or unreleased commercial obligations.

## Release ownership

Only maintainers may publish official releases.

The main branch is a release branch. A push or merge to `main` must trigger CI to package artifacts and publish them to a GitHub Release, as defined in `.github/workflows/release-on-main.yml`.

## Public data policy

Allowed in repository:

- synthetic mock incidents
- synthetic AWS topology examples
- synthetic logs
- synthetic traces
- sanitized screenshots generated from mock data

Not allowed in repository:

- production logs
- customer data
- AWS account IDs unless synthetic
- IP addresses unless reserved documentation ranges
- real payload captures
- credentials, tokens, cookies, or secrets
- private incident reports
