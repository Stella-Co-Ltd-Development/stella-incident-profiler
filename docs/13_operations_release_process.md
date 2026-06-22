# Operations and Release Process

## Git policy

- Do not push directly to `main` by default.
- Use feature branches.
- Commit frequently with small commits.
- Write commit messages in English.
- Prefer imperative commit messages.
- Open pull requests for review before merging.

## Branch policy

Recommended branch names:

```text
feature/javafx-shell
feature/mock-data-provider
feature/unified-timeline
feature/local-mcp-server
feature/jfr-hotspot-view
fix/mock-trace-waterfall
chore/release-workflow
```

## Commit examples

```text
Add mock incident provider
Render incident list from mock data
Add Japanese UI resource bundle
Create local MCP tool registry
Add release workflow for main branch
```

## Main branch release rule

If a push or merge to `main` occurs, CI must:

1. run tests
2. build the Java application
3. package desktop artifacts
4. produce checksums
5. create or update a GitHub Release
6. upload artifacts to the release

## Release artifacts

Required release artifacts:

- desktop application package for the CI OS
- profiler agent jar
- schema bundle zip
- requirements documentation zip
- mock data zip
- checksums file

Future artifacts:

- Windows installer
- macOS DMG
- Linux DEB/RPM
- control plane container image

## Versioning

Use semantic versioning:

```text
MAJOR.MINOR.PATCH
```

Examples:

```text
0.1.0-debug-mock-gui
0.2.0-local-mcp
1.0.0
```

## Environments

| Environment | Purpose |
|---|---|
| debug | local mock rendering, no AWS |
| dev | development AWS account |
| staging | production-like testing |
| prod | production investigation |

## Operational modes

### Debug mode

- uses mock providers
- no AWS calls
- no credentials required
- MCP tools run against mock data

### Live mode

- requires explicit AWS account/region selection
- uses live providers
- reads AWS observability data
- write operations disabled unless explicitly enabled

## Incident retention

Default recommendations:

- local mock data: unlimited in repository
- local cache: user-configurable
- cloud evidence: 30 to 180 days depending on compliance needs
- release artifacts: retain per GitHub release policy

## Backup and recovery

- Cloud artifacts stored in S3 should use versioning for critical evidence buckets.
- Metadata DB should use backups when remote control plane mode is used.
- Local cache should be disposable and rebuildable from source evidence.


## OSS release policy

The project is intended to be released as open source by Stella International Co Ltd.

Official release artifacts must be produced from `main` by CI and attached to a GitHub Release.

Release notes must be written in English and must include:

- release summary
- notable changes
- breaking changes
- security-relevant changes
- known issues
- artifact checksums when available

Maintainers must ensure that release artifacts do not include secrets, private AWS configuration, production logs, captured payloads, or private customer data.
