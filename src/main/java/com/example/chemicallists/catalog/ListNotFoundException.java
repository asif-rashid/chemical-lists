package com.example.chemicallists.catalog;

public class ListNotFoundException extends RuntimeException {
    private final long listId;

    public ListNotFoundException(long listId) {
        super("List not found: " + listId);
        this.listId = listId;
    }

    public long getListId() {
        return listId;
    }
}
