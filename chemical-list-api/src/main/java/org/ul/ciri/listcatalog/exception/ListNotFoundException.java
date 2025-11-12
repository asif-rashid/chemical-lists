package org.ul.ciri.listcatalog.exception;

import java.util.UUID;

public class ListNotFoundException extends RuntimeException {
    public ListNotFoundException(UUID id) {
        super("Chemical list not found: " + id);
    }
}
