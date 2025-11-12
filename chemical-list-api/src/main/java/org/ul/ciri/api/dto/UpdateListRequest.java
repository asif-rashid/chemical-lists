package org.ul.ciri.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateListRequest(@NotBlank String label, String description) {
}
