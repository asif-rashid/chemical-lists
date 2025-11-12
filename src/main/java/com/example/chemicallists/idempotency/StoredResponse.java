package com.example.chemicallists.idempotency;

import java.time.Instant;

public record StoredResponse<T>(String key, T payload, Instant storedAt) {
}
