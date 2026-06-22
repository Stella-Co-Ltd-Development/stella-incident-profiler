# Target AWS Architecture

## Reference application architecture

```text
User Browser
  |
Route 53 + ACM
  |
CloudFront + AWS WAF
  |-- /static/* -> S3 frontend bucket
  |-- /api/*    -> ALB
                    |
                    v
              ECS Service
              Java backend tasks
                    |
                    v
              RDS Proxy
                    |
                    v
              Aurora PostgreSQL or Aurora MySQL
```

## Supported deployment models

### Primary model

- Frontend: TypeScript single-page application hosted on S3 and CloudFront
- Backend: Java application on ECS Fargate
- API entry: ALB
- Database: Aurora with RDS Proxy
- Logs: CloudWatch Logs
- Traces: AWS X-Ray or OTLP-compatible trace backend
- Metrics: CloudWatch metrics, OpenTelemetry metrics, Aurora metrics
- Artifacts: S3 buckets for JFR files, ALB logs, CloudFront logs, and incident reports

### Secondary model

- Backend on ECS on EC2
- Optional sidecar collector
- Optional remote control plane on ECS

### Future model

- EKS support
- Lambda edge-side or API Gateway variants
- Multi-account and multi-region support

## AWS resources to discover

The system should discover or be configured with:

- AWS account ID
- region
- environment name
- CloudFront distribution ID
- frontend S3 bucket
- ALB ARN
- ALB target group ARNs
- ECS cluster ARN
- ECS service ARN
- ECS task ARNs
- log group names
- Aurora cluster ARN
- RDS Proxy ARN
- S3 artifact bucket names
- trace backend identifiers

## Recommended tagging

All relevant AWS resources should include these tags:

| Tag | Example |
|---|---|
| `Application` | `retail-web` |
| `Service` | `checkout-api` |
| `Environment` | `prod` |
| `Owner` | `platform-team` |
| `ObservabilityProfile` | `aws-java-incident-profiler` |

## Data access design

The desktop application should not call every AWS service directly from UI controllers. All AWS access must be behind provider interfaces.

```text
JavaFX View
  -> ViewModel
    -> Application Service
      -> Port Interface
        -> Mock Adapter or AWS Adapter
```

## Live mode access rules

- Live mode must require explicit account and region selection.
- Live mode must show the currently selected AWS identity.
- Debug mode must not call AWS.
- Production access must prefer `AssumeRole` with least privilege.
- Direct use of long-lived access keys is discouraged.

## Required IAM capabilities for live mode

The exact policy must be refined during implementation, but live mode will need read access to:

- ECS service/task metadata
- CloudWatch metrics
- CloudWatch logs queries
- X-Ray traces if X-Ray is used
- S3 object reads for ALB logs, CloudFront logs, and JFR artifacts
- RDS/Aurora metadata and metrics

Write access should be limited to:

- optional incident report export bucket
- optional JFR artifact upload location

No write operation should be available through MCP by default.
