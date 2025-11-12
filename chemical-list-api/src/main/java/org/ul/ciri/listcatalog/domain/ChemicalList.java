package org.ul.ciri.listcatalog.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ChemicalList {

    private final UUID id;
    private String label;
    private String description;
    private Instant updatedAt;

    public ChemicalList(UUID id, String label, String description, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.label = Objects.requireNonNull(label, "label");
        this.description = description;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public UUID getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void rename(String label, String description, Instant updatedAt) {
        this.label = Objects.requireNonNull(label, "label");
        this.description = description;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }
}
