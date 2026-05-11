package com.feasymenu.server.security;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    // Her IP ve işlem türü (key) için ayrı bir kova tutan bellek içi cache
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public enum LimitType {
        ORDER(1, Duration.ofMinutes(1)), // 1 dakikada 1 sipariş
        AUTH(20, Duration.ofMinutes(5)), // 5 dakikada 20 giriş/kayıt denemesi
        FORGOT_PASS(3, Duration.ofHours(1)), // Saatte 3 şifre sıfırlama talebi
        BILLING_INIT(5, Duration.ofMinutes(1)), // Dakikada 5 ödeme başlatma
        WAITER_CALL(1, Duration.ofMinutes(1)), // Dakikada 1 kez garson çağırma
        LOYALTY_SPIN(1, Duration.ofMinutes(1)); // Dakikada 1 kez çark çevirme

        final long capacity;
        final Duration duration;

        LimitType(long capacity, Duration duration) {
            this.capacity = capacity;
            this.duration = duration;
        }
    }

    /**
     * İlgili IP ve işlem türü için kova (bucket) oluşturur veya mevcut olanı döner.
     */
    public Bucket resolveBucket(String ip, LimitType type) {
        String key = ip + ":" + type.name();
        return cache.computeIfAbsent(key, k -> newBucket(type));
    }

    private Bucket newBucket(LimitType type) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(type.capacity)
                        .refillGreedy(type.capacity, type.duration)
                        .build())
                .build();
    }
}
