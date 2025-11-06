package com.qrmatik.server.service;

public class TableUnavailableException extends RuntimeException {
    public TableUnavailableException(String message) {
        super(message);
    }
}
