package com.stella.incidentprofiler.desktop;

import com.stella.incidentprofiler.core.port.ProviderRegistry;
import com.stella.incidentprofiler.core.model.Incident;
import com.stella.incidentprofiler.core.port.IncidentQuery;
import com.stella.incidentprofiler.core.runtime.RuntimeMode;
import com.stella.incidentprofiler.core.runtime.RuntimeSettings;
import com.stella.incidentprofiler.mock.MockProviderFactory;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class StellaIncidentProfilerApp extends Application {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    private UiText uiText;
    private ProviderRegistry providers;
    private BorderPane root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        uiText = UiText.loadDefault();
        RuntimeSettings settings = RuntimeSettings.fromEnvironment(
            System.getenv(),
            getParameters().getRaw().toArray(String[]::new),
            Path.of("mock-data")
        );
        providers = settings.mode() == RuntimeMode.DEBUG
            ? MockProviderFactory.create(settings.mockDataDirectory())
            : MockProviderFactory.create(settings.mockDataDirectory());

        root = new BorderPane();
        root.setTop(createHeader(settings));
        root.setLeft(createNavigation());
        showDashboard();

        Scene scene = new Scene(root, 1180, 760);
        stage.setTitle(uiText.get("app.title"));
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader(RuntimeSettings settings) {
        Label title = new Label(uiText.get("app.title"));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label mode = new Label(settings.mode() == RuntimeMode.DEBUG ? uiText.get("status.mockMode") : uiText.get("status.liveMode"));
        mode.setStyle("-fx-background-color: #f6d365; -fx-padding: 4 10 4 10; -fx-background-radius: 4;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, title, spacer, mode);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12));
        header.setStyle("-fx-border-color: #d8dee9; -fx-border-width: 0 0 1 0; -fx-background-color: #ffffff;");
        return header;
    }

    private VBox createNavigation() {
        Button dashboard = navigationButton("nav.dashboard", this::showDashboard);
        Button serviceMap = navigationButton("nav.serviceMap", () -> showSimpleScreen("nav.serviceMap"));
        Button incidents = navigationButton("nav.incidents", this::showIncidents);
        Button mcpConsole = navigationButton("nav.mcpConsole", () -> showSimpleScreen("mcp.title"));
        Button settings = navigationButton("nav.settings", () -> showSimpleScreen("settings.title"));

        VBox navigation = new VBox(8, dashboard, serviceMap, incidents, mcpConsole, settings);
        navigation.setPadding(new Insets(12));
        navigation.setPrefWidth(220);
        navigation.setStyle("-fx-border-color: #d8dee9; -fx-border-width: 0 1 0 0; -fx-background-color: #f7f9fb;");
        return navigation;
    }

    private Button navigationButton(String key, Runnable action) {
        Button button = new Button(uiText.get(key));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(event -> action.run());
        return button;
    }

    private void showDashboard() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.getChildren().add(sectionTitle("nav.dashboard"));
        content.getChildren().add(metricRow(uiText.get("dashboard.activeIncidents"), String.valueOf(providers.incidents().listIncidents(IncidentQuery.all()).size())));
        content.getChildren().add(metricRow(uiText.get("dashboard.recentSpikes"), "3"));
        content.getChildren().add(metricRow(uiText.get("dashboard.errorRate"), "2.9%"));
        content.getChildren().add(metricRow(uiText.get("dashboard.latency"), "1,920 ms"));
        content.getChildren().add(metricRow(uiText.get("dashboard.instrumentationWarnings"), "2"));
        root.setCenter(new ScrollPane(content));
    }

    private void showIncidents() {
        List<Incident> incidents = providers.incidents().listIncidents(IncidentQuery.all());
        TableView<Incident> table = new TableView<>();
        table.getItems().setAll(incidents);
        table.getColumns().setAll(List.of(
            textColumn("incident.column.severity", incident -> localizeSeverity(incident.severity().name()), 90),
            textColumn("incident.column.status", incident -> localizeStatus(incident.status().name()), 140),
            textColumn("incident.column.service", Incident::service, 140),
            textColumn("incident.column.trigger", incident -> localizeTrigger(incident.triggerType().name()), 170),
            textColumn("incident.column.startTime", incident -> formatTime(incident.startTime()), 170),
            textColumn("incident.column.peakTime", incident -> formatTime(incident.peakTime()), 170),
            textColumn("incident.column.endTime", incident -> incident.endTime() == null ? "" : formatTime(incident.endTime()), 170),
            textColumn("incident.column.summary", Incident::summary, 320)
        ));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        VBox listPane = new VBox(12, sectionTitle("incident.list.title"), table);
        listPane.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox detailPane = new VBox(12);
        detailPane.setPadding(new Insets(20));
        detailPane.getChildren().add(sectionTitle("incident.detail.title"));

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> renderIncidentDetail(detailPane, selected));
        if (!incidents.isEmpty()) {
            table.getSelectionModel().selectFirst();
            renderIncidentDetail(detailPane, incidents.getFirst());
        }

        SplitPane splitPane = new SplitPane(listPane, new ScrollPane(detailPane));
        splitPane.setDividerPositions(0.58);
        root.setCenter(splitPane);
    }

    private TableColumn<Incident, String> textColumn(String labelKey, java.util.function.Function<Incident, String> valueFactory, double width) {
        TableColumn<Incident, String> column = new TableColumn<>(uiText.get(labelKey));
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(valueFactory.apply(cell.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    private void renderIncidentDetail(VBox detailPane, Incident incident) {
        detailPane.getChildren().setAll(sectionTitle("incident.detail.title"));
        if (incident == null) {
            detailPane.getChildren().add(new Label(uiText.get("empty.noIncidents")));
            return;
        }

        GridPane summary = new GridPane();
        summary.setHgap(12);
        summary.setVgap(8);
        addSummaryRow(summary, 0, uiText.get("incident.column.service"), incident.service());
        addSummaryRow(summary, 1, uiText.get("incident.column.severity"), localizeSeverity(incident.severity().name()));
        addSummaryRow(summary, 2, uiText.get("incident.column.status"), localizeStatus(incident.status().name()));
        addSummaryRow(summary, 3, uiText.get("incident.column.trigger"), localizeTrigger(incident.triggerType().name()));
        addSummaryRow(summary, 4, uiText.get("incident.column.startTime"), formatTime(incident.startTime()));
        addSummaryRow(summary, 5, uiText.get("incident.column.peakTime"), formatTime(incident.peakTime()));
        addSummaryRow(summary, 6, uiText.get("incident.column.endTime"), incident.endTime() == null ? "" : formatTime(incident.endTime()));

        TextArea summaryText = new TextArea(incident.summary());
        summaryText.setEditable(false);
        summaryText.setWrapText(true);
        summaryText.setPrefRowCount(3);

        detailPane.getChildren().addAll(
            summary,
            summaryText,
            metricRow(uiText.get("incident.detail.timeline"), safeValue(() -> String.valueOf(providers.timelines().getTimeline(incident.id(), incident.evidenceWindow().from(), incident.evidenceWindow().to()).points().size()))),
            metricRow(uiText.get("incident.detail.relatedLogs"), safeValue(() -> String.valueOf(providers.logs().searchLogs(new com.stella.incidentprofiler.core.port.LogQuery(incident.service(), null, null, null, incident.evidenceWindow().from(), incident.evidenceWindow().to(), 100)).items().size()))),
            metricRow(uiText.get("incident.detail.relatedTraces"), safeValue(() -> String.valueOf(providers.traces().findSlowTraces(new com.stella.incidentprofiler.core.port.TraceQuery(incident.service(), incident.evidenceWindow().from(), incident.evidenceWindow().to(), 10)).size()))),
            metricRow(uiText.get("incident.detail.jfrHotspots"), safeValue(() -> String.valueOf(providers.jfr().getHotspots(incident.id(), null).hotspots().size()))),
            metricRow(uiText.get("incident.detail.albAnalysis"), safeValue(() -> providers.analysis().compareAlbBeforeAfter(incident.id()).during().targetResponseP95Ms() + " ms")),
            metricRow(uiText.get("incident.detail.auroraAnalysis"), safeValue(() -> String.valueOf(providers.analysis().getAuroraMetrics(incident.id()).metrics().size()))),
            metricRow(uiText.get("incident.detail.externalApiAnalysis"), safeValue(() -> String.valueOf(providers.analysis().compareExternalCallsBeforeAfter(incident.id()).dependencies().size())))
        );
    }

    private void addSummaryRow(GridPane gridPane, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold;");
        gridPane.add(labelNode, 0, row);
        gridPane.add(new Label(value), 1, row);
    }

    private void showSimpleScreen(String titleKey) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.getChildren().add(sectionTitle(titleKey));

        ListView<String> list = new ListView<>();
        list.getItems().add(uiText.get("status.mockMode"));
        list.getItems().add(uiText.get("action.refresh"));
        list.setPrefHeight(220);
        content.getChildren().add(list);

        root.setCenter(new ScrollPane(content));
    }

    private Label sectionTitle(String key) {
        Label label = new Label(uiText.get(key));
        label.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        return label;
    }

    private HBox metricRow(String labelText, String valueText) {
        Label label = new Label(labelText);
        Label value = new Label(valueText);
        value.setStyle("-fx-font-weight: bold;");
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(12, label, spacer, value);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color: #e5e7eb; -fx-border-radius: 4; -fx-background-radius: 4;");
        return row;
    }

    private String formatTime(java.time.Instant instant) {
        return TIME_FORMATTER.withZone(ZoneId.systemDefault()).format(instant);
    }

    private String localizeSeverity(String severity) {
        return switch (severity) {
            case "LOW" -> "低";
            case "MEDIUM" -> "中";
            case "HIGH" -> "高";
            case "CRITICAL" -> "重大";
            default -> severity;
        };
    }

    private String localizeStatus(String status) {
        return switch (status) {
            case "DETECTED" -> "検知済み";
            case "COLLECTING_EVIDENCE" -> "証拠収集中";
            case "READY_FOR_ANALYSIS" -> "分析可能";
            case "ACKNOWLEDGED" -> "確認済み";
            case "MITIGATED" -> "緩和済み";
            case "RESOLVED" -> "解決済み";
            case "ARCHIVED" -> "アーカイブ済み";
            default -> status;
        };
    }

    private String localizeTrigger(String trigger) {
        return switch (trigger) {
            case "P95_LATENCY_SPIKE" -> "P95レイテンシ急増";
            case "ERROR_RATE_SPIKE" -> "エラー率急増";
            case "CPU_SPIKE" -> "CPU急増";
            case "GC_PAUSE_SPIKE" -> "GC停止急増";
            case "HEAP_PRESSURE" -> "ヒープ圧迫";
            case "BLOCKED_THREAD_SPIKE" -> "ブロックスレッド急増";
            case "AURORA_LATENCY_SPIKE" -> "Auroraレイテンシ急増";
            case "EXTERNAL_API_LATENCY_SPIKE" -> "外部APIレイテンシ急増";
            case "MANUAL" -> "手動";
            default -> trigger;
        };
    }

    private String safeValue(java.util.function.Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException exception) {
            return "0";
        }
    }
}
