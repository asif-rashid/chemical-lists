package com.example.chemicallists.catalog;

public class ListLabelAlreadyExistsException extends RuntimeException {
    private final String label;

    public ListLabelAlreadyExistsException(String label) {
        super("List label already exists: " + label);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
