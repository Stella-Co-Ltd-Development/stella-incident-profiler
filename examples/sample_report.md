# Incident Report: inc-20260623-001

## Summary

p95 latency increased while payment provider calls timed out.

## Impact

The checkout API experienced elevated latency and increased 5xx responses for `/api/orders`.

## Timeline

- 2026-06-23T12:03:00Z: Incident started.
- 2026-06-23T12:04:10Z: Payment timeout errors started.
- 2026-06-23T12:07:30Z: Latency reached peak.
- 2026-06-23T12:13:00Z: Incident ended.

## Likely causes

1. Payment provider latency regression.
2. Retry policy CPU overhead during timeout storm.
3. Secondary JVM allocation pressure in payment response mapping.

## Evidence

- `incident://inc-20260623-001/timeline.json`
- `incident://inc-20260623-001/logs.ndjson`
- `incident://inc-20260623-001/traces.json`
- `incident://inc-20260623-001/jfr-hotspots.json`
