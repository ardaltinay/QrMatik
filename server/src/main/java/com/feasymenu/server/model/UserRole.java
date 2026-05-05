package com.feasymenu.server.model;

import java.util.Locale;

public enum UserRole {
    SUPERADMIN, ADMIN, KITCHEN, BAR, CASHIER, SALOON;

    public static UserRole fromString(String s) {
        if (s == null)
            return null;
        String norm = s.trim().toUpperCase(Locale.ENGLISH);
        try {
            return UserRole.valueOf(norm);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
