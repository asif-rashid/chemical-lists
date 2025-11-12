package com.example.chemicallists.api;

import jakarta.validation.constraints.Size;

public record UpdateChemicalListRequest(
        @Size(max = 30) String label,
        @Size(max = 200) String name,
        @Size(max = 2000) String description
) {
    public boolean isEmpty() {
        return label == null && name == null && description == null;
    }
}
