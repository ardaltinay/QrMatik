package com.feasymenu.server.exception;

public class TableUnavailableException extends RuntimeException {
    public TableUnavailableException(String message) {
        super(message);
    }
}
