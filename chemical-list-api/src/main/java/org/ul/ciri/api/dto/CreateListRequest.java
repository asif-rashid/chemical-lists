package org.ul.ciri.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateListRequest(@NotBlank String label, String description) {
}
