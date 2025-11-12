package org.ul.ciri.listcatalog.domain.command;

import jakarta.validation.constraints.NotBlank;

public record CreateListCommand(@NotBlank String label, String description) {
}
