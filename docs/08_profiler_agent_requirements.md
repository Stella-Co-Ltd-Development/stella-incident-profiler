# Profiler Agent Requirements

## Goal

The profiler agent must collect JVM-level evidence from Java backend applications with low operational risk.

## Agent modes

### Java agent mode

The agent may be attached at JVM startup:

```text
-javaagent:/opt/aws-java-incident-profiler/profiler-agent.jar
```

### Dynamic attach mode

For environments that allow attach, the system may attach to a running JVM to start or dump JFR recordings.

### External command mode

The control plane may use JVM tools such as `jcmd` where permitted.

## Required capabilities

- Start JFR recording
- Stop JFR recording
- Dump JFR recording
- Report JFR recording status
- Configure profile templates
- Export JFR files to configured location
- Emit agent health status
- Correlate application metadata with JVM events where possible

## JFR policy

Default profile:

```text
name: continuous-low-overhead
mode: ring-buffer
maxAge: 10 minutes
maxSize: implementation-defined safe default
settings: profile or custom reduced overhead settings
```

Incident snapshot:

```text
trigger: incident detection or manual action
output: S3 or local artifact path
format: .jfr
metadata: incident ID, service, environment, task ID, timestamp
```

## Agent metadata

The agent must attach metadata to exported files:

- service name
- environment
- application version
- AWS account ID
- region
- ECS cluster name
- ECS service name
- ECS task ID
- container name
- Java version
- agent version
- recording start/end time

## Safety requirements

- The agent must not crash the application if the collector is unavailable.
- The agent must fail open.
- The agent must use bounded buffers.
- The agent must not capture request or response bodies.
- The agent must not log secrets.
- Agent commands must be authenticated when exposed remotely.

## Debug requirements

The desktop GUI must not require a real agent in debug mode. All agent-related screens must render from mock JFR and mock agent status data.

## Future capabilities

- async-profiler integration
- custom JFR event emission
- profile template editor
- per-endpoint profiling triggers
- ECS task exec or SSM-assisted diagnostics
