package com.qrmatik.server.service;

public class TenantContext {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    public static void setTenant(String t) {
        CURRENT.set(t);
    }

    public static String getTenant() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
