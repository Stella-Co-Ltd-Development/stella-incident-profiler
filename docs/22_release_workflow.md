# Release Workflow

The `release-on-main.yml` workflow publishes conservative release artifacts for pushes to `main`.

This workflow exists because `main` is treated as the release branch for `stella-incident-profiler`. Maintainers should merge reviewed pull requests into `main`; direct pushes to `main` are prohibited by the project rules.

## Current behavior

The workflow:

- checks out the repository
- installs Temurin Java 21
- configures Gradle caching
- runs `./gradlew build --no-daemon`
- publishes the portable desktop distribution zip
- publishes the desktop application jar
- publishes schema, documentation, mock data, examples, and UI resource bundles
- generates SHA-256 checksums
- creates a GitHub Release for the main branch build

## Packaging limitations

The initial release workflow does not build native installers, DMGs, DEB packages, RPM packages, or Windows MSIX packages. Native packaging will be added after the JavaFX application shell and runtime image tasks are stable.

## Security notes

Release artifacts must not include credentials, private AWS configuration, production logs, captured payloads, customer data, or local `.env` files. The current workflow packages only repository documentation, schemas, synthetic examples, mock data, UI resources, and build outputs.
