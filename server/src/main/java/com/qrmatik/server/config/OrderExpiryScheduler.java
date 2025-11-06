package com.qrmatik.server.config;

import com.qrmatik.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderExpiryScheduler {
    private static final Logger log = LoggerFactory.getLogger(OrderExpiryScheduler.class);

    private final OrderService orderService;

    public OrderExpiryScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    // Run every 30 minutes to mark stale orders as EXPIRED (aligned to app
    // timezone)
    @Scheduled(cron = "0 */30 * * * *", zone = "${app.time-zone:Europe/Istanbul}")
    public void sweepAndExpire() {
        try {
            int updated = orderService.markExpiredOrders();
            if (updated > 0 && log.isInfoEnabled()) {
                log.info("Order expiry sweep updated {} orders to EXPIRED", updated);
            }
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Order expiry sweep failed: {}", e.getMessage());
            }
        }
    }
}
