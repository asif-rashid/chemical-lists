package org.ul.ciri.ratelimit.app;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String key) {
        super("Rate limit exceeded for key " + key);
    }
}
