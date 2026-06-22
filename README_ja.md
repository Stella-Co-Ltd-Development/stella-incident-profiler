# Stella Incident Profiler

[English README](README.md)

Stella Incident Profiler は、AWS 上で動作する Java Web アプリケーションのインシデント調査を支援する、JavaFX デスクトップアプリケーションおよび MCP 対応のオープンソースプロジェクトです。JFR、OpenTelemetry、CloudWatch、ALB ログ、Aurora メトリクスなどの証跡を関連付けて、少人数の AI 駆動開発会社でも扱いやすい調査ワークベンチを目指します。

本プロジェクトは Stella International Co Ltd が公開し、[github.com/xxvw](https://github.com/xxvw) が主要メンテナです。

## 目指しているもの

少人数の AI 駆動開発会社では、専任の大規模な SRE 組織や内製 observability 基盤を持たずに、Java サービス、AWS、AI アシスタントを組み合わせて運用することがあります。そのような環境では、障害時の証跡がログ、トレース、JFR、ALB、Aurora、外部 API などに分散しがちです。

Stella Incident Profiler は、その分散した証跡をローカルで安全に整理し、AI アシスタントが読み取り専用で問い合わせできる形にすることを目的としています。

基本思想は次のとおりです。

- **モックファースト**: AWS 認証情報なしで開発・検証できること。
- **Provider インターフェース優先**: JavaFX UI が AWS や JFR ファイルへ直接依存しないこと。
- **MCP は読み取り専用から開始**: AI に証跡を渡しても、本番操作はさせないこと。
- **UI は日本語**: 画面上の表示は日本語、開発者向けの文章や識別子は英語。
- **小さな PR**: AI が生成した差分でも、人間がレビューしやすく戻しやすい単位にすること。
- **OSS として安全なデータ**: 公開リポジトリには合成・サニタイズ済みデータだけを置くこと。

## 対象ユーザー

このプロジェクトは、次のようなチームを想定しています。

- AWS 上で Java サービスを運用する小規模な AI ネイティブ開発会社
- ライブ AWS 連携より先に、ローカルのインシデント調査体験を固めたいチーム
- AI アシスタントに安全な形で障害証跡を読ませたいチーム
- UI から直接 SDK を呼ぶのではなく、Provider インターフェースで境界を明確にしたい開発者
- 非公開インフラにアクセスせず、公開モックデータだけで貢献したい OSS コントリビューター

デフォルトでは、本番サービスの再起動、ALB/ECS 設定変更、ログ削除、保持期間変更、payload body の取得などは扱いません。

## 現在の状態

`main` ブランチには、公開仕様、スキーマ、サニタイズ済みモックデータ、図、サンプル、UI 文言、OSS ガバナンス文書が含まれています。

実装は小さな Pull Request の積み重ねで進めています。現在の実装スタックでは、次の内容を追加する方向です。

- Gradle マルチモジュール構成
- JavaFX デスクトップシェル
- ドメインモデルと Provider インターフェース
- モックデータ Provider
- インシデント一覧・詳細画面
- タイムライン、ログ、トレース、JFR、ALB、Aurora、外部 API パネル
- 読み取り専用 MCP ツールのスケルトン
- CI と release workflow

アプリケーションロジックや GUI 挙動を含む PR は、人間のレビューのために open のまま残します。

## アーキテクチャ

想定している Gradle モジュールは次のとおりです。

| Module | 役割 |
|---|---|
| `app-desktop` | JavaFX デスクトップアプリケーションと日本語 UI |
| `app-core` | ドメインモデル、ユースケース、Provider インターフェース、実行時設定 |
| `app-mock` | `mock-data/*` を読む debug mode 用 Provider |
| `app-aws` | 将来の AWS adapter |
| `app-jfr` | 将来の JFR parser / profiler interface |
| `app-mcp` | MCP tool 定義、DTO、将来の server transport |
| `app-storage` | ローカル設定、監査ログ、incident index |
| `app-test-support` | 共通テスト fixture |

中心になる境界は Provider Registry です。GUI、MCP、AWS、JFR、storage は Provider interface を通じて接続されます。

```java
public record ProviderRegistry(
    RuntimeMode mode,
    IncidentProvider incidents,
    TimelineProvider timelines,
    LogProvider logs,
    TraceProvider traces,
    JfrProvider jfr,
    AwsTopologyProvider topology,
    AnalysisProvider analysis
) {
}
```

モックモードでは AWS を使わずに Provider を構成します。

```java
public static ProviderRegistry create(Path dataDirectory) {
    MockDataRepository repository = new MockDataRepository(dataDirectory);
    return new ProviderRegistry(
        RuntimeMode.DEBUG,
        new MockIncidentProvider(repository),
        new MockTimelineProvider(repository),
        new MockLogProvider(repository),
        new MockTraceProvider(repository),
        new MockJfrProvider(repository),
        new MockAwsTopologyProvider(repository),
        new MockAnalysisProvider(repository)
    );
}
```

JavaFX シェルは実行時設定から起動し、mock mode ではモック Provider を使います。

```java
RuntimeSettings settings = RuntimeSettings.fromEnvironment(
    System.getenv(),
    getParameters().getRaw().toArray(String[]::new),
    Path.of("mock-data")
);
providers = settings.mode() == RuntimeMode.DEBUG
    ? MockProviderFactory.create(settings.mockDataDirectory())
    : MockProviderFactory.create(settings.mockDataDirectory());
```

将来の live AWS Provider は同じ interface の裏側で差し替える方針です。UI はどの Provider 実装が使われているかを知る必要がありません。

## MCP の方針

MCP は、AI アシスタントがインシデント証跡を安全に読むための入口です。最初の実装では、transport や SDK 連携より先に、tool contract と Provider-backed executor を定義します。

読み取り専用 tool registry は次のような形です。

```java
public static List<McpToolDefinition> readOnlyTools() {
    return List.of(
        tool("list_services", "List known services.", Map.of("environment", "Environment name.")),
        tool("list_incidents", "List incidents within a time range.", Map.of("service", "Service name.", "from", "Start time.", "to", "End time.")),
        tool("get_incident_summary", "Return incident summary and evidence bounds.", Map.of("incidentId", "Incident identifier.")),
        tool("get_incident_timeline", "Return downsampled timeline points.", Map.of("incidentId", "Incident identifier.")),
        tool("search_logs", "Search redacted log events.", Map.of("service", "Service name.", "query", "Text query.", "limit", "Result limit.")),
        tool("get_trace", "Return trace summary and spans.", Map.of("traceId", "Trace identifier.")),
        tool("get_jfr_hotspots", "Return JFR hotspot summaries.", Map.of("incidentId", "Incident identifier.", "type", "Hotspot type.")),
        tool("compare_alb_before_after", "Compare ALB behavior before and during an incident.", Map.of("incidentId", "Incident identifier.")),
        tool("compare_external_calls_before_after", "Compare external dependency behavior before and during an incident.", Map.of("incidentId", "Incident identifier.")),
        tool("generate_incident_report", "Generate a Markdown incident report from available evidence.", Map.of("incidentId", "Incident identifier."))
    );
}
```

MCP の基本方針:

- tool はデフォルトで読み取り専用
- response は redacted かつ bounded
- 大きな結果には limit または pagination を設ける
- payload body は明示的な将来ポリシーなしには返さない
- local MCP 実行は監査可能にする
- write operation は別ポリシーと UI confirmation が必要

## Debug Mode

Debug mode は次のいずれかで有効になります。

- `APP_PROFILE=debug`
- `APP_DATA_MODE=mock`
- `--mock` 起動
- 選択された Provider が mock provider

Debug mode では AWS client を初期化せず、AWS 認証情報を要求せず、本番ネットワーク接続を開かないことが必須です。

画面には次の表示を出します。

```text
モックデータ表示中
```

## UI

デスクトップ UI は JavaFX/OpenJFX で実装します。画面上の表示、メニュー、tooltip、エラー、report title は日本語です。開発者向け文書、コードコメント、ログ、識別子、commit message、PR、issue は英語です。

主な画面:

- `ダッシュボード`
- `サービス構成`
- `インシデント一覧`
- `インシデント詳細`
- `統合タイムライン`
- `ログビューア`
- `トレースウォーターフォール`
- `JFRホットスポット`
- `MCPコンソール`
- `設定`

UI 文言は `ui/ja-JP.json` から読み込みます。

## リポジトリ構成

- `docs/`: product, architecture, security, testing, release, MCP, implementation requirements
- `schemas/`: incident, log, metric, trace, JFR, MCP data schemas
- `mock-data/`: debug mode 用の合成 fixture
- `examples/`: 公開可能な合成 sample
- `ui/ja-JP.json`: 日本語 UI 文言
- `diagrams/`: Mermaid diagrams
- `blueprint/`: 実装メモと project structure
- `CONTRIBUTING.md`: contribution workflow
- `SECURITY.md`: responsible disclosure guidance
- `CODE_OF_CONDUCT.md`: community policy
- `LICENSE_DECISION.md`: license decision status

## セキュリティとプライバシー

credential、private AWS account details、production logs、captured payload bodies、customer data、private incident reports、internal network names は commit しないでください。公開 examples、tests、screenshots、docs には合成・サニタイズ済みデータだけを使います。

デフォルトの姿勢:

- MCP tool は読み取り専用
- payload capture は無効
- sensitive header は redacted
- debug mode は credential 不要
- live AWS access は明示的な user configuration が必要

## 貢献

Pull Request の title と description は英語で書いてください。アプリケーションの visible UI text は日本語のままにしてください。詳細は [CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。

## ライセンス状態

推奨ライセンスは Apache License 2.0 ですが、Stella International Co Ltd の最終承認までは `TBD: Apache-2.0 recommended` として扱います。[LICENSE_DECISION.md](LICENSE_DECISION.md) と [docs/20_open_source_governance.md](docs/20_open_source_governance.md) を参照してください。
