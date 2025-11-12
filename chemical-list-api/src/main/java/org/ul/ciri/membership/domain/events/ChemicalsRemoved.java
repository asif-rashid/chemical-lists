package org.ul.ciri.membership.domain.events;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ChemicalsRemoved(UUID listId, Set<String> chemicals, Instant occurredAt) {
}
