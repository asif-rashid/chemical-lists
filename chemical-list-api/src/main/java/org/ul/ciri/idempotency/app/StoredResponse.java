package org.ul.ciri.idempotency.app;

import java.time.Instant;

public record StoredResponse(String key, String body, Instant storedAt) {
}
