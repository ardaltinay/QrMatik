package com.feasymenu.server.security;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        RateLimitService.LimitType limitType = null;

        // 1. Sipariş Verme İşlemleri
        if (path.startsWith("/api/orders/call-waiter") && "POST".equalsIgnoreCase(method)) {
            limitType = RateLimitService.LimitType.WAITER_CALL;
        } else if (path.equals("/api/orders") && "POST".equalsIgnoreCase(method)) {
            limitType = RateLimitService.LimitType.ORDER;
        } 
        // 2. Auth İşlemleri (Giriş/Kayıt)
        else if ((path.contains("/api/auth/login") || path.contains("/api/auth/signup")) && "POST".equalsIgnoreCase(method)) {
            limitType = RateLimitService.LimitType.AUTH;
        }
        // 3. Şifre Sıfırlama
        else if (path.contains("/api/auth/forgot-password") && "POST".equalsIgnoreCase(method)) {
            limitType = RateLimitService.LimitType.FORGOT_PASS;
        }
        // 4. Ödeme Başlatma Bilgisi
        else if (path.startsWith("/api/billing/checkout/init") && "GET".equalsIgnoreCase(method)) {
            limitType = RateLimitService.LimitType.BILLING_INIT;
        }

        if (limitType != null) {
            String ip = getClientIP(request);
            Bucket bucket = rateLimitService.resolveBucket(ip, limitType);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (!probe.isConsumed()) {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                if (waitForRefill == 0) waitForRefill = 1; // En az 1 saniye göster
                
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json;charset=UTF-8");
                String errorMsg = limitType == RateLimitService.LimitType.ORDER 
                    ? "Sipariş limitine takıldınız. Lütfen " + waitForRefill + " saniye bekleyin."
                    : "Çok fazla deneme yaptınız. Lütfen " + waitForRefill + " saniye sonra tekrar deneyin.";
                    
                response.getWriter().write("{ \"error\": \"too_many_requests\", \"message\": \"" + errorMsg + "\", \"retryAfter\": " + waitForRefill + " }");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
