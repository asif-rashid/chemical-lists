package org.ul.ciri.audit.app;

import java.time.Instant;

public interface AuditSink {
    void record(String message, Instant timestamp);
}
