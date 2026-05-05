package com.feasymenu.server.model;

import java.util.Locale;

public enum OrderStatus {
    NEW, PREPARING, READY, SERVED, BILL_REQUESTED, PAYMENT_COMPLETED, CANCELED, EXPIRED;

    public static OrderStatus fromString(String s) {
        if (s == null)
            return null;
        String norm = s.trim().toUpperCase(Locale.ENGLISH).replace('-', '_').replace(' ', '_');
        // tolerate common typo "PREPAIRING"
        if ("PREPAIRING".equals(norm))
            norm = "PREPARING";
        try {
            return OrderStatus.valueOf(norm);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
