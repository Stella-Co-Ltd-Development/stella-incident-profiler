package com.stella.incidentprofiler.mock;

public class MockDataException extends RuntimeException {
    public MockDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MockDataException(String message) {
        super(message);
    }
}
