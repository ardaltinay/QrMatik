package com.qrmatik.server.model;

public enum PlanType {
    FREE, STANDARD, PRO;

    public static PlanType fromString(String v) {
        if (v == null)
            return null;
        try {
            return PlanType.valueOf(v.trim().toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }
}
