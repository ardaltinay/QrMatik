package com.feasymenu.server.service;

public interface NotificationService {
    void sendExpirationWarning(String email, String tenantName, String daysLeft);
    void sendPlanDowngradedNotification(String email, String tenantName);
    void sendLoyaltyCouponEmail(String email, String tenantName, String couponCode, String prizeLabel);
}
