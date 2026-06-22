package com.stella.incidentprofiler.desktop;

import com.stella.incidentprofiler.core.port.ProviderRegistry;
import com.stella.incidentprofiler.core.runtime.RuntimeMode;
import com.stella.incidentprofiler.core.runtime.RuntimeSettings;
import com.stella.incidentprofiler.mock.MockProviderFactory;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class StellaIncidentProfilerApp extends Application {
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
        Button incidents = navigationButton("nav.incidents", () -> showSimpleScreen("incident.list.title"));
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
        content.getChildren().add(metricRow(uiText.get("dashboard.activeIncidents"), String.valueOf(providers.incidents().listIncidents(com.stella.incidentprofiler.core.port.IncidentQuery.all()).size())));
        content.getChildren().add(metricRow(uiText.get("dashboard.recentSpikes"), "3"));
        content.getChildren().add(metricRow(uiText.get("dashboard.errorRate"), "2.9%"));
        content.getChildren().add(metricRow(uiText.get("dashboard.latency"), "1,920 ms"));
        content.getChildren().add(metricRow(uiText.get("dashboard.instrumentationWarnings"), "2"));
        root.setCenter(new ScrollPane(content));
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
}
