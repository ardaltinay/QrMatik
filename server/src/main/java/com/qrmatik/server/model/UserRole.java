package com.qrmatik.server.model;

public enum UserRole {
    SUPERADMIN,
    ADMIN,
    KITCHEN,
    BAR,
    CASHIER,
    STAFF;

    public static UserRole fromString(String s) {
        if (s == null) return null;
        String norm = s.trim().toUpperCase();
        try {
            return UserRole.valueOf(norm);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
