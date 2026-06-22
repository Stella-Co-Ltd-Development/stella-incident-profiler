package com.stella.incidentprofiler.desktop;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class UiTextTest {
    @Test
    void loadsRequiredJapaneseUiKeys() {
        UiText uiText = assertDoesNotThrow(UiText::loadDefault);
        List<String> requiredKeys = List.of(
            "app.title",
            "nav.dashboard",
            "nav.serviceMap",
            "nav.incidents",
            "nav.mcpConsole",
            "nav.settings",
            "status.mockMode",
            "dashboard.activeIncidents",
            "incident.list.title",
            "mcp.title",
            "settings.title"
        );

        requiredKeys.forEach(key -> assertTrue(uiText.containsKey(key), "Missing key: " + key));
    }
}
