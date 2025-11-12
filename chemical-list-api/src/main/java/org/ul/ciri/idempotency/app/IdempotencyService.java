package org.ul.ciri.idempotency.app;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    private final Map<String, StoredResponse> storage = new ConcurrentHashMap<>();
    private final Clock clock;

    public IdempotencyService(Clock clock) {
        this.clock = clock;
    }

    public Optional<StoredResponse> find(String key) {
        return Optional.ofNullable(storage.get(key));
    }

    public StoredResponse store(String key, String body) {
        StoredResponse stored = new StoredResponse(key, body, Instant.now(clock));
        storage.put(key, stored);
        return stored;
    }
}
