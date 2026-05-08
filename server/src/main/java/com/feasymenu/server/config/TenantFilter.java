package com.feasymenu.server.config;

import com.feasymenu.server.repository.TenantRepository;
import com.feasymenu.server.service.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {
    private final TenantRepository tenantRepository;
    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    public TenantFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Early diagnostic logging to help trace 403/tenant resolution issues.
            try {
                String method = request.getMethod();
                String uri = request.getRequestURI();
                String auth = request.getHeader("Authorization");
                String xTenant = request.getHeader("X-Tenant");
                log.debug("Incoming request {} {} AuthorizationPresent={} X-Tenant={}", method, uri,
                        auth != null && !auth.isBlank(), xTenant);
            } catch (Throwable __t) {
                // ignore logging failures
            }
            // Eğer Authorization: Bearer ... varsa, tenant'ı JWT filtresi belirlesin
            // (override etme)
            String auth = request.getHeader("Authorization");
            boolean hasJwt = auth != null && auth.startsWith("Bearer ");

            if (!hasJwt) {
                // Anonim isteklerde (menü görüntüleme vb.) kiracı bilgisini path, host veya
                // subdomain'den çöz.
                // X-Tenant header'ı anonim isteklerde SPOOFING riskine karşı GÖRMEZDEN GELİNİR.
                if (TenantContext.getTenant() == null) {
                    String tenant = resolveTenantAnonymous(request);
                    if (tenant != null && !tenant.isBlank()) {
                        TenantContext.setTenant(tenant);
                        // Resolve UUID to avoid per-row subqueries in database filters
                        tenantRepository.findByCode(tenant).ifPresent(t -> TenantContext.setTenantId(t.getId()));
                    }
                }
            } else {
                // JWT doğrulanmışsa, kiracı bilgisi JwtAuthFilter tarafından zaten set
                // edilmiştir.
                // Burada ekstra bir çözümleme yapmaya gerek yok; JWT içindeki kiracı bilgisi
                // TEK KAYNAKTIR.
                // Eğer JwtAuthFilter kiracıyı set edemediyse (token hatalıysa), isteği devam
                // ettir.
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveTenantAuthenticated(HttpServletRequest request) {
        // Header öncelikli: yalnızca doğrulanmış isteklerde kabul et
        String t = request.getHeader("X-Tenant");
        if (t != null && !t.isBlank())
            return t.trim();
        return resolveTenantByPathOrHost(request);
    }

    private String resolveTenantAnonymous(HttpServletRequest request) {
        // Anonim: header'ı görmezden gel, sadece path/host
        return resolveTenantByPathOrHost(request);
    }

    private String resolveTenantByPathOrHost(HttpServletRequest request) {
        // path style: /r/{tenant}/...
        String uri = request.getRequestURI();
        if (uri != null) {
            String[] parts = uri.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("r".equals(parts[i]) && i + 1 < parts.length)
                    return parts[i + 1];
            }
        }
        // host subdomain: tenant.example.com (and dev: tenant.localhost)
        String host = request.getServerName();
        if (host != null) {
            String[] hostParts = host.split("\\.");
            // e.g. foo.bar.example.com -> foo
            if (hostParts.length > 2)
                return hostParts[0];
            // Dev convenience: test.localhost -> test
            if (hostParts.length == 2 && "localhost".equalsIgnoreCase(hostParts[1]))
                return hostParts[0];
            // custom domain mapping (e.g., menu.mybistro.com, mybistro.com)
            var tOpt = tenantRepository.findByCustomDomain(host.toLowerCase());
            if (tOpt.isPresent())
                return tOpt.get().getCode();
        }
        return null;
    }
}
