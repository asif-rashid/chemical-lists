package org.ul.ciri.setops.domain.events;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.ul.ciri.setops.domain.SetOperationType;

public record SetOperationCompleted(UUID leftListId, UUID rightListId, SetOperationType type, Set<String> result,
                                    Instant occurredAt) {
}
