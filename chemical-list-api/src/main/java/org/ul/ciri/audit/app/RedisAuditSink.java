package org.ul.ciri.audit.app;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RedisAuditSink implements AuditSink {

    private final List<String> events = Collections.synchronizedList(new ArrayList<>());
    private final Clock clock;

    public RedisAuditSink(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void record(String message, Instant timestamp) {
        events.add(timestamp.toString() + " " + message);
    }

    public List<String> getEvents() {
        return List.copyOf(events);
    }
}
