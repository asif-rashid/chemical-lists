package org.ul.ciri.listcatalog.domain.events;

import java.time.Instant;
import java.util.UUID;

public record ListDeleted(UUID listId, Instant occurredAt) {
}
