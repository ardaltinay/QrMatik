package com.feasymenu.server.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:${REDIS_PASSWORD:${REDIS_PASS:}}}")
    private String redisPassword;

    private ProxyManager<byte[]> proxyManager;

    public enum LimitType {
        ORDER(1, Duration.ofMinutes(1)), AUTH(20, Duration.ofMinutes(5)), FORGOT_PASS(3,
                Duration.ofHours(1)),
        BILLING_INIT(5, Duration.ofMinutes(1)), WAITER_CALL(1,
                Duration.ofMinutes(1)),
        LOYALTY_SPIN(1, Duration.ofMinutes(1));

        final long capacity;
        final Duration duration;

        LimitType(long capacity, Duration duration) {
            this.capacity = capacity;
            this.duration = duration;
        }
    }

    @PostConstruct
    public void init() {
        try {
            System.out.println("DEBUG: RateLimitService init started");
            System.out.println("DEBUG: Redis Host: " + redisHost);
            System.out.println("DEBUG: Redis Port: " + redisPort);

            RedisURI.Builder builder = RedisURI.builder().withHost(redisHost).withPort(redisPort);

            if (redisPassword != null && !redisPassword.isBlank()) {
                builder.withPassword(redisPassword.toCharArray());
                System.out.println("DEBUG: Redis Password set");
            }

            RedisClient redisClient = RedisClient.create(builder.build());
            this.proxyManager = LettuceBasedProxyManager.builderFor(redisClient)
                    .withClientSideConfig(ClientSideConfig.getDefault().withExpirationAfterWriteStrategy(
                            ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1))))
                    .build();
            System.out.println("DEBUG: RateLimitService initialized successfully");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: RateLimitService failed to initialize!");
            e.printStackTrace();
            throw e;
        }
    }

    public Bucket resolveBucket(String ip, LimitType type) {
        String key = "rate-limit:" + type.name() + ":" + ip;
        byte[] keyBytes = key.getBytes();
        BucketConfiguration config = BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder().capacity(type.capacity).refillGreedy(type.capacity, type.duration).build())
                .build();

        return proxyManager.builder().build(keyBytes, () -> config);
    }
}
