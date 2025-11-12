package org.ul.ciri.setops.domain;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SetOperationRequest(@NotNull UUID leftListId, @NotNull UUID rightListId, @NotNull SetOperationType type) {
}
