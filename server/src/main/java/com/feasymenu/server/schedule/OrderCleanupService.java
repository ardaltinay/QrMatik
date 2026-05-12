package com.feasymenu.server.schedule;

import com.feasymenu.server.model.OrderStatus;
import com.feasymenu.server.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupService {

    private final OrderRepository orderRepository;

    /**
     * Cleans up terminal state orders (PAYMENT_COMPLETED, CANCELED, EXPIRED) that
     * are older than 24 hours. Runs every hour.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour on the hour
    public void cleanupOldOrders() {
        log.info("Starting scheduled order cleanup...");

        List<OrderStatus> terminalStatuses = List.of(OrderStatus.PAYMENT_COMPLETED, OrderStatus.CANCELED,
                OrderStatus.EXPIRED);

        // Delete orders older than 24 hours
        Instant threshold = Instant.now().minus(24, ChronoUnit.HOURS);

        try {
            orderRepository.deleteByStatusInAndCreatedTimeBefore(terminalStatuses, threshold);
            log.info("Order cleanup completed successfully.");
        } catch (Exception e) {
            log.error("Error during order cleanup: {}", e.getMessage());
        }
    }
}
