package org.ul.ciri.listcatalog.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateListCommand(@NotNull UUID id, @NotBlank String label, String description) {
}
