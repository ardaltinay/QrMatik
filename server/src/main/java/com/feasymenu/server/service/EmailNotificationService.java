package com.feasymenu.server.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendExpirationWarning(String email, String tenantName, String label) {
        log.info("Preparing expiration warning email for {}...", email);

        String subject = "Action Required: Your FeasyMenu Subscription is Expiring Soon";
        String content = String.format(
                "<h3>Dear %s,</h3>" +
                        "<p>This is a friendly reminder that your FeasyMenu subscription will expire in <b>%s</b>.</p>"
                        +
                        "<p>To ensure uninterrupted service and keep your premium features active, please renew your subscription via the admin dashboard.</p>"
                        +
                        "<br><p>Best regards,<br>The FeasyMenu Team</p>",
                tenantName, label);

        sendHtmlEmail(email, subject, content);
    }

    @Override
    public void sendPlanDowngradedNotification(String email, String tenantName) {
        log.warn("Preparing plan downgrade notification for {}...", email);

        String subject = "Your FeasyMenu Account Has Been Downgraded";
        String content = String.format(
                "<h3>Dear %s,</h3>" +
                        "<p>Your paid subscription has expired, and your account has been automatically moved to our <b>FREE</b> plan.</p>"
                        +
                        "<p>Your premium features are now disabled. To restore your access and branding, please make a new payment via the admin dashboard.</p>"
                        +
                        "<br><p>Best regards,<br>The FeasyMenu Team</p>",
                tenantName);

        sendHtmlEmail(email, subject, content);
    }

    @Override
    public void sendLoyaltyCouponEmail(String email, String tenantName, String couponCode, String prizeLabel) {
        log.info("Preparing loyalty reward email for {} from {}...", email, tenantName);

        String subject = String.format("Congratulations! Your reward from %s is here 🎁", tenantName);
        String content = String.format(
                "<h3>Congratulations!</h3>" +
                        "<p>You spun the wheel at <b>%s</b> and won a special prize: <b>%s</b>!</p>" +
                        "<p>Use the discount code below on your next visit or order:</p>" +
                        "<div style='padding: 20px; background: #f4f6f2; border: 2px dashed #788a68; text-align: center; font-size: 24px; font-weight: bold; color: #1b2118; margin: 20px 0;'>"
                        +
                        "  %s" +
                        "</div>" +
                        "<p>Don't forget to show this code to the waiter or enter it during checkout.</p>" +
                        "<br><p>See you soon at %s!<br>The FeasyMenu Team</p>",
                tenantName, prizeLabel, couponCode, tenantName);

        sendHtmlEmail(email, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("support@feasymenu.com", "FeasyMenu");

            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
