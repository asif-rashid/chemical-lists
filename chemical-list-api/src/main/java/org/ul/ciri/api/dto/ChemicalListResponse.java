package org.ul.ciri.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ChemicalListResponse(UUID id, String label, String description, Instant updatedAt) {
}
