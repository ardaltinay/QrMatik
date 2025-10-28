package com.qrmatik.server.model;

public enum OrderStatus {
    NEW,
    PREPARING,
    READY,
    SERVED,
    PAYMENT_COMPLETED;

    public static OrderStatus fromString(String s) {
        if (s == null) return null;
        String norm = s.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        // tolerate common typo "PREPAIRING"
        if ("PREPAIRING".equals(norm)) norm = "PREPARING";
        try { return OrderStatus.valueOf(norm); }
        catch (IllegalArgumentException ex) { return null; }
    }
}
