package com.example.chemicallists.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChemicalListRequest(
        @NotBlank @Size(max = 30) String label,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description
) {
}
