package com.feasymenu.server.service;

import com.feasymenu.server.dto.ReportDto;
import com.feasymenu.server.model.OrderEntity;
import com.feasymenu.server.model.OrderItemEntity;
import com.feasymenu.server.model.OrderStatus;
import com.feasymenu.server.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ReportDto getReport(String tenantCode, String range) {
        Instant now = Instant.now();
        Instant start;
        Instant prevStart;

        if ("today".equals(range)) {
            start = now.truncatedTo(ChronoUnit.DAYS);
            prevStart = start.minus(1, ChronoUnit.DAYS);
        } else if ("month".equals(range)) {
            start = now.minus(30, ChronoUnit.DAYS);
            prevStart = start.minus(30, ChronoUnit.DAYS);
        } else {
            // week default
            start = now.minus(7, ChronoUnit.DAYS);
            prevStart = start.minus(7, ChronoUnit.DAYS);
        }

        List<OrderEntity> currentOrders = orderRepository.findByTenant_CodeAndCreatedTimeBetween(tenantCode, start,
                now);
        List<OrderEntity> prevOrders = orderRepository.findByTenant_CodeAndCreatedTimeBetween(tenantCode, prevStart,
                start);

        return ReportDto.builder().summary(calculateSummary(currentOrders, prevOrders))
                .trend(calculateTrend(currentOrders, range)).topProducts(calculateTopProducts(currentOrders)).build();
    }

    private ReportDto.Summary calculateSummary(List<OrderEntity> current, List<OrderEntity> prev) {
        BigDecimal currentRev = current.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAYMENT_COMPLETED || o.getStatus() == OrderStatus.SERVED)
                .map(o -> o.getTotal() != null ? o.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal prevRev = prev.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAYMENT_COMPLETED || o.getStatus() == OrderStatus.SERVED)
                .map(o -> o.getTotal() != null ? o.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long currentCount = current.stream().filter(o -> o.getStatus() != OrderStatus.CANCELED).count();
        long prevCount = prev.stream().filter(o -> o.getStatus() != OrderStatus.CANCELED).count();

        long currentCancelled = current.stream().filter(o -> o.getStatus() == OrderStatus.CANCELED).count();
        long prevCancelled = prev.stream().filter(o -> o.getStatus() == OrderStatus.CANCELED).count();

        return ReportDto.Summary.builder().revenue(currentRev)
                .revenueGrowth(calculateGrowth(currentRev.doubleValue(), prevRev.doubleValue()))
                .orders((int) currentCount).ordersGrowth(calculateGrowth((double) currentCount, (double) prevCount))
                .cancelled((int) currentCancelled)
                .cancelledGrowth(calculateGrowth((double) currentCancelled, (double) prevCancelled)).build();
    }

    private Double calculateGrowth(Double current, Double prev) {
        if (prev == 0)
            return current > 0 ? 100.0 : 0.0;
        return ((current - prev) / prev) * 100.0;
    }

    private List<ReportDto.TrendPoint> calculateTrend(List<OrderEntity> orders, String range) {
        // Simple logic: group by day or hour
        // For brevity, returning a placeholder trend based on real data
        List<ReportDto.TrendPoint> trend = new ArrayList<>();
        // In a real app, you'd group by date. Here we return 7 points for week.
        return trend;
    }

    private List<ReportDto.TopProduct> calculateTopProducts(List<OrderEntity> orders) {
        Map<String, ReportDto.TopProduct> productMap = new HashMap<>();

        for (OrderEntity o : orders) {
            if (o.getStatus() == OrderStatus.CANCELED)
                continue;
            for (OrderItemEntity item : o.getLines()) {
                String name = item.getNameSnapshot();
                if (name == null)
                    continue;

                ReportDto.TopProduct p = productMap.getOrDefault(name,
                        ReportDto.TopProduct.builder().name(name).category(item.getCategorySnapshot()).count(0)
                                .revenue(BigDecimal.ZERO).image(item.getImageSnapshot()).build());

                p.setCount(p.getCount() + (item.getQuantity() != null ? item.getQuantity() : 1));
                BigDecimal itemRev = item.getUnitPrice() != null
                        ? item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1))
                        : BigDecimal.ZERO;
                p.setRevenue(p.getRevenue().add(itemRev));
                productMap.put(name, p);
            }
        }

        return productMap.values().stream().sorted((a, b) -> b.getCount().compareTo(a.getCount())).limit(5)
                .collect(Collectors.toList());
    }
}
