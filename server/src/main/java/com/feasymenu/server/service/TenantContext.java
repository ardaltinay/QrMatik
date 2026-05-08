package com.feasymenu.server.service;

import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<String> CURRENT_CODE = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_ID = new ThreadLocal<>();

    public static void setTenant(String code) {
        CURRENT_CODE.set(code);
    }

    public static String getTenant() {
        return CURRENT_CODE.get();
    }

    public static void setTenantId(UUID id) {
        CURRENT_ID.set(id);
    }

    public static UUID getTenantId() {
        return CURRENT_ID.get();
    }

    public static void clear() {
        CURRENT_CODE.remove();
        CURRENT_ID.remove();
    }
}
