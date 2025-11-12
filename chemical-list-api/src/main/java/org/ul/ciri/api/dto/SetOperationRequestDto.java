package org.ul.ciri.api.dto;

import jakarta.validation.constraints.NotNull;
import org.ul.ciri.setops.domain.SetOperationType;

import java.util.UUID;

public record SetOperationRequestDto(@NotNull UUID leftListId, @NotNull UUID rightListId,
                                     @NotNull SetOperationType type) {
}
