package com.stella.incidentprofiler.core.model;

public enum TriggerType {
    P95_LATENCY_SPIKE,
    ERROR_RATE_SPIKE,
    CPU_SPIKE,
    GC_PAUSE_SPIKE,
    HEAP_PRESSURE,
    BLOCKED_THREAD_SPIKE,
    AURORA_LATENCY_SPIKE,
    EXTERNAL_API_LATENCY_SPIKE,
    MANUAL
}
