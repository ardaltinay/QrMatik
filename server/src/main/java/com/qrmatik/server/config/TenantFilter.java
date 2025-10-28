package com.qrmatik.server.config;

import com.qrmatik.server.service.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tenant = resolveTenant(request);
            if (tenant == null || tenant.isBlank())
                tenant = "default";
            TenantContext.setTenant(tenant);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveTenant(HttpServletRequest request) {
        // header override
        String t = request.getHeader("X-Tenant");
        if (t != null && !t.isBlank())
            return t.trim();
        // path style: /r/{tenant}/...
        String uri = request.getRequestURI();
        if (uri != null) {
            String[] parts = uri.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("r".equals(parts[i]) && i + 1 < parts.length)
                    return parts[i + 1];
            }
        }
        // host subdomain: tenant.example.com
        String host = request.getServerName();
        if (host != null) {
            String[] hostParts = host.split("\\.");
            if (hostParts.length > 2)
                return hostParts[0];
        }
        return null;
    }
}
