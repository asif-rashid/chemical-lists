package org.ul.ciri.listcatalog.exception;

public class DuplicateLabelException extends RuntimeException {
    public DuplicateLabelException(String label) {
        super("Chemical list label already exists: " + label);
    }
}
