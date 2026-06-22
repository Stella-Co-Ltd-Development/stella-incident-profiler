package com.stella.incidentprofiler.desktop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stella.incidentprofiler.core.json.JsonSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class UiText {
    private final Map<String, String> messages;

    private UiText(Map<String, String> messages) {
        this.messages = Map.copyOf(messages);
    }

    public static UiText loadDefault() {
        ObjectMapper objectMapper = JsonSupport.createObjectMapper();
        try (InputStream stream = UiText.class.getResourceAsStream("/ui/ja-JP.json")) {
            if (stream == null) {
                throw new IllegalStateException("Missing UI resource: /ui/ja-JP.json");
            }
            return new UiText(objectMapper.readValue(stream, new TypeReference<>() {
            }));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load Japanese UI resources", exception);
        }
    }

    public String get(String key) {
        String message = messages.get(key);
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Missing UI text key: " + key);
        }
        return message;
    }

    public boolean containsKey(String key) {
        return messages.containsKey(key);
    }
}
