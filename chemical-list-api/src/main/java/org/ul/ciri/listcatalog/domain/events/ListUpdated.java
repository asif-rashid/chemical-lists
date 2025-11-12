package org.ul.ciri.listcatalog.domain.events;

import java.time.Instant;
import java.util.UUID;

public record ListUpdated(UUID listId, String label, Instant occurredAt) {
}
