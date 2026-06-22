# Recommended Project Structure

```text
aws-java-incident-profiler/
  AGENTS.md
  .codex/environment.env
  settings.gradle.kts
  build.gradle.kts
  app-desktop/
    src/main/java/...
    src/main/resources/ui/messages_ja.properties
  core-domain/
    src/main/java/...
  core-application/
    src/main/java/...
  infrastructure-mock/
    src/main/java/...
    src/main/resources/mock-data/...
  infrastructure-aws/
    src/main/java/...
  infrastructure-jfr/
    src/main/java/...
  infrastructure-mcp/
    src/main/java/...
  docs/
  mock-data/
  schemas/
```

## Module responsibilities

| Module | Responsibility |
|---|---|
| `core-domain` | Pure domain model and enums |
| `core-application` | Use cases, provider ports, orchestration |
| `infrastructure-mock` | Mock provider implementations |
| `infrastructure-aws` | AWS provider implementations |
| `infrastructure-jfr` | JFR parsing and artifact handling |
| `infrastructure-mcp` | MCP server and tool bindings |
| `app-desktop` | JavaFX application shell and views |
