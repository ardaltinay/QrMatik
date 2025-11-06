package com.qrmatik.server.exception;

public class TableUnavailableException extends RuntimeException {
    public TableUnavailableException(String message) {
        super(message);
    }
}
