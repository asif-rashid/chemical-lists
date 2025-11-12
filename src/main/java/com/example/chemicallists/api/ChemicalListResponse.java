package com.example.chemicallists.api;

import com.example.chemicallists.catalog.ChemicalList;

import java.time.Instant;

public record ChemicalListResponse(
        long listId,
        String label,
        String name,
        String description,
        String createdBy,
        Instant createdDate,
        String lastUpdatedBy,
        Instant lastUpdated
) {

    public static ChemicalListResponse fromDomain(ChemicalList list) {
        return new ChemicalListResponse(
                list.getListId(),
                list.getLabel(),
                list.getName(),
                list.getDescription(),
                list.getCreatedBy(),
                list.getCreatedDate(),
                list.getLastUpdatedBy(),
                list.getLastUpdated()
        );
    }
}
