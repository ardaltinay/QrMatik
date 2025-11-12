package com.qrmatik.server.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static class Attempt {
        int failures;
        Instant windowStart;
        Instant blockedUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    // Policy: 5 failed attempts within 10 minutes -> block for 10 minutes
    private static final int MAX_FAILS = 5;
    private static final long WINDOW_SEC = 10 * 60;
    private static final long BLOCK_SEC = 10 * 60;

    public boolean isBlocked(String key) {
        Attempt a = attempts.get(key);
        if (a == null)
            return false;
        if (a.blockedUntil == null)
            return false;
        return Instant.now().isBefore(a.blockedUntil);
    }

    public void onFailure(String key) {
        Attempt a = attempts.computeIfAbsent(key, k -> new Attempt());
        Instant now = Instant.now();
        if (a.windowStart == null || now.isAfter(a.windowStart.plusSeconds(WINDOW_SEC))) {
            a.windowStart = now;
            a.failures = 0;
        }
        a.failures++;
        if (a.failures >= MAX_FAILS) {
            a.blockedUntil = now.plusSeconds(BLOCK_SEC);
        }
    }

    public void onSuccess(String key) {
        attempts.remove(key);
    }
}
