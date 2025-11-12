package org.ul.ciri.ratelimit.app;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiter {

    private final RateLimiterProperties properties;
    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimiter(RateLimiterProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public void checkRate(String key) {
        Instant now = Instant.now(clock);
        Window window = windows.computeIfAbsent(key, ignored -> new Window(now, 0));
        if (window.expiresAt.isBefore(now)) {
            window.expiresAt = now.plus(1, ChronoUnit.MINUTES);
            window.count = 0;
        }
        window.count++;
        if (window.count > properties.getRequestsPerMinute()) {
            throw new RateLimitExceededException(key);
        }
    }

    private static final class Window {
        private Instant expiresAt;
        private int count;

        private Window(Instant expiresAt, int count) {
            this.expiresAt = expiresAt;
            this.count = count;
        }
    }
}
