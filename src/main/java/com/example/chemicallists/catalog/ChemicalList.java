package com.example.chemicallists.catalog;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable representation of a chemical list metadata entry.
 */
public final class ChemicalList {

    private final long listId;
    private final String label;
    private final String name;
    private final String description;
    private final String createdBy;
    private final Instant createdDate;
    private final String lastUpdatedBy;
    private final Instant lastUpdated;

    public ChemicalList(long listId,
                        String label,
                        String name,
                        String description,
                        String createdBy,
                        Instant createdDate,
                        String lastUpdatedBy,
                        Instant lastUpdated) {
        this.listId = listId;
        this.label = label;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdated = lastUpdated;
    }

    public long getListId() {
        return listId;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public ChemicalList withUpdatedMetadata(String label,
                                            String name,
                                            String description,
                                            String updatedBy,
                                            Instant updatedAt) {
        return new ChemicalList(listId,
                label != null ? label : this.label,
                name != null ? name : this.name,
                description != null ? description : this.description,
                createdBy,
                createdDate,
                updatedBy,
                updatedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalList that = (ChemicalList) o;
        return listId == that.listId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(listId);
    }
}
