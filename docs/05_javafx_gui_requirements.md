# JavaFX GUI Requirements

## Technology decision

The primary desktop GUI must be implemented with JavaFX / OpenJFX.

## Runtime baseline

- Java 21 or later
- JavaFX compatible with the selected Java version
- Gradle as the default build system
- jlink for custom runtime images
- jpackage for OS-specific installers

## UI language policy

All visible UI text must be Japanese. Engineering documentation, source code comments, logs, and internal identifiers must be English.

## Recommended architecture

```text
app-desktop
  presentation
    views
    viewmodels
    controls
    resources
      messages_ja.properties
  application
    services
    usecases
  domain
    model
    ports
  infrastructure
    mock
    aws
    localdb
    mcp
    jfr
```

## UI screens

### Dashboard

Japanese screen title: `ダッシュボード`

Required panels:

- service health summary
- active incidents
- recent latency spikes
- error rate trend
- dependency warning summary
- instrumentation coverage warning

### Service map

Japanese screen title: `サービス構成`

Required panels:

- CloudFront
- S3 frontend
- ALB
- ECS service
- Java backend tasks
- RDS Proxy
- Aurora
- external dependencies

### Incident list

Japanese screen title: `インシデント一覧`

Required columns:

- severity: `重要度`
- status: `状態`
- service: `サービス`
- trigger type: `検知条件`
- start time: `開始時刻`
- peak time: `ピーク時刻`
- end time: `終了時刻`
- summary: `概要`

### Incident detail

Japanese screen title: `インシデント詳細`

Required sections:

- summary header
- unified timeline
- likely cause candidates
- related traces
- related logs
- JFR hotspots
- ALB/CloudFront analysis
- Aurora analysis
- external API analysis
- report export

### Unified timeline

Japanese screen title: `統合タイムライン`

The timeline must support:

- zooming
- panning
- selecting time ranges
- overlay toggles
- linked filtering across logs, traces, and hotspots

Required overlays:

- latency
- error count
- CPU
- heap
- GC pause
- blocked threads
- DB latency
- external API latency
- ALB target response time
- log markers
- JFR event markers

### Trace waterfall

Japanese screen title: `トレースウォーターフォール`

Required features:

- hierarchical span display
- duration bar
- error span highlighting
- details pane
- filtering by service, operation, and status

### Log viewer

Japanese screen title: `ログビューア`

Required features:

- time range filter
- log level filter
- text search
- trace ID filter
- JSON detail pane
- sensitive value redaction display

### JFR hotspot view

Japanese screen title: `JFRホットスポット`

Required tabs:

- `CPU`
- `アロケーション`
- `GC`
- `ロック競合`
- `スレッド`
- `例外`

### MCP console

Japanese screen title: `MCPコンソール`

Required features:

- list available tools
- show tool descriptions in Japanese UI labels
- execute read-only local tools for testing
- show request/response JSON
- show resource URIs

### Settings

Japanese screen title: `設定`

Required sections:

- runtime mode
- mock data directory
- AWS profile/account/region
- SSO/AssumeRole settings
- JFR policy
- data retention
- MCP server settings
- security and redaction settings

## JavaFX implementation guidance

### Recommended UI pattern

Use MVVM-like separation:

```text
View.fxml or JavaFX builder
  -> ViewModel with JavaFX properties
    -> Application service
      -> Domain port
        -> Mock or live adapter
```

### Rendering large datasets

For large logs and event tables:

- use virtualized controls
- paginate when querying live providers
- avoid loading entire production datasets into memory
- keep timeline aggregate data separate from raw event details

### Charting approach

Default:

- JavaFX controls and Canvas for standard charts

Allowed:

- JavaFX WebView + embedded ECharts for dense charts, trace waterfalls, and advanced timelines

If WebView is used:

- keep all data passed through typed DTOs
- avoid arbitrary remote scripts
- bundle static chart assets locally
- disable external network access for chart rendering

## Accessibility requirements

- Keyboard navigation for primary screens
- Meaningful labels for controls
- High-contrast status indicators
- Japanese text must be readable at default font size
- Timezone must be visible in timestamps

## UI label source of truth

UI labels must be stored in `ui/ja-JP.json` or Java resource bundles generated from it.
