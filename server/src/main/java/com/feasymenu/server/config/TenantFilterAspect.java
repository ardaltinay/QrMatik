package com.feasymenu.server.config;

import com.feasymenu.server.service.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Repository metotları çağrılmadan önce Hibernate Tenant Filtresini
     * etkinleştirir. Bu sayede manuel "WHERE tenant_id = ..." yazmaya gerek
     * kalmadan tüm sorgular otomatik olarak o anki kiracıya (TenantContext) göre
     * filtrelenir.
     */
    @Before("execution(* com.feasymenu.server.repository.*.*(..))")
    public void enableTenantFilter() {
        String tenant = TenantContext.getTenant();
        if (tenant != null && !tenant.trim().isEmpty()) {
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenantId", tenant);
        }
    }
}
