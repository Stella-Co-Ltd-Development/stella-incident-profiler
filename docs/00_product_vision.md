# Product Vision

## Product name

AWS Java Incident Profiler

## Vision

Provide a Java-based desktop investigation platform that allows engineers to understand production and pre-production incidents in AWS-hosted Java web applications by correlating JVM performance, application traces, logs, database behavior, edge/API traffic, and external dependencies on a single incident timeline.

## Problem statement

Modern AWS web applications often distribute relevant debugging signals across many systems:

- CloudFront and ALB logs show entry behavior.
- Java logs show application errors.
- OpenTelemetry traces show request flow.
- JFR shows JVM internals.
- Aurora metrics show database bottlenecks.
- CloudWatch metrics show infrastructure health.
- External API calls create remote latency and failure modes.

Engineers lose time switching tools, manually aligning timestamps, and asking an AI assistant questions without structured access to the underlying evidence.

## Product outcome

The product must allow an engineer to answer the following questions quickly:

1. What happened?
2. When did it start and end?
3. Which service, endpoint, task, thread, DB query, or external call changed?
4. Was the likely cause inside the JVM, at the database, at an external API, at the edge/load balancer, or in infrastructure?
5. What evidence supports each hypothesis?
6. What should be included in the incident report?

## Core differentiator

The platform is not only a Java profiler. It is an incident debugger that combines:

- JavaFX desktop GUI
- JVM profiling through JFR
- AWS topology and metrics
- logs and distributed traces
- ALB/CloudFront/Aurora analysis
- mock-data-first debug mode
- MCP server integration for AI investigation workflows

## Product principles

1. **Evidence first**: Every AI-generated summary must link back to concrete logs, spans, metrics, JFR events, or AWS records.
2. **Safe by default**: Payload capture must be disabled by default. MCP tools must be read-only by default.
3. **Debug mode first**: The GUI must be usable without AWS access by using injected mock providers.
4. **Java-first**: The desktop application, domain model, provider interfaces, and MCP server must be implemented primarily in Java.
5. **Japanese UI, English engineering artifacts**: User-facing UI must be Japanese, while all engineering documentation and source explanations must be English.


## Open-source positioning

`stella-incident-profiler` is designed as a public open-source project published by Stella International Co Ltd and maintained by `github.com/xxvw`.

The product must be useful to external Java/AWS teams without requiring access to private Stella International Co Ltd systems. Debug/mock mode, synthetic sample data, public documentation, and safe defaults are first-class requirements.

The project should invite contributions around JavaFX UI improvements, MCP tools, observability integrations, JFR parsing, AWS provider adapters, test fixtures, documentation, and localization quality.
