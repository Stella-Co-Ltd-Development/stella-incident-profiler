package com.stella.incidentprofiler.core.runtime;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public record RuntimeSettings(
    RuntimeMode mode,
    Path mockDataDirectory,
    Locale uiLocale,
    boolean awsEnabled,
    boolean mcpEnabled
) {
    public static RuntimeSettings debug(Path mockDataDirectory) {
        return new RuntimeSettings(RuntimeMode.DEBUG, mockDataDirectory, Locale.forLanguageTag("ja-JP"), false, false);
    }

    public static RuntimeSettings fromEnvironment(Map<String, String> environment, String[] args, Path defaultMockDataDirectory) {
        boolean mockArg = java.util.Arrays.asList(args).contains("--mock");
        String appProfile = environment.getOrDefault("APP_PROFILE", "");
        String dataMode = environment.getOrDefault("APP_DATA_MODE", "");
        RuntimeMode mode = mockArg
            || "debug".equalsIgnoreCase(appProfile)
            || "mock".equalsIgnoreCase(dataMode)
            ? RuntimeMode.DEBUG
            : RuntimeMode.LIVE;

        Path mockDataDirectory = Path.of(environment.getOrDefault("APP_MOCK_DATA_DIR", defaultMockDataDirectory.toString()));
        Locale uiLocale = Locale.forLanguageTag(environment.getOrDefault("APP_UI_LOCALE", "ja-JP"));
        boolean awsEnabled = Boolean.parseBoolean(environment.getOrDefault("APP_AWS_ENABLED", "false"));
        boolean mcpEnabled = Boolean.parseBoolean(environment.getOrDefault("APP_MCP_ENABLED", "false"));

        if (mode == RuntimeMode.DEBUG) {
            awsEnabled = false;
        }

        return new RuntimeSettings(mode, mockDataDirectory, uiLocale, awsEnabled, mcpEnabled);
    }
}
