package com.example.chemicallists.idempotency;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyService {

    private final Map<String, StoredResponse<?>> store = new ConcurrentHashMap<>();
    private final Clock clock;

    public IdempotencyService(Clock clock) {
        this.clock = clock;
    }

    public void store(String key, Object payload) {
        if (key == null || key.isBlank()) {
            return;
        }
        store.putIfAbsent(key, new StoredResponse<>(key, payload, clock.instant()));
    }

    @SuppressWarnings("unchecked")
    public <T extends StoredResponse<?>> Optional<T> find(String key, Class<T> type) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(key))
                .filter(type::isInstance)
                .map(value -> (T) value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> findPayload(String key, Class<T> type) {
        return find(key, StoredResponse.class)
                .map(StoredResponse::payload)
                .filter(type::isInstance)
                .map(value -> (T) value);
    }
}
