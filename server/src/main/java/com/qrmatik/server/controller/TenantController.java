package com.qrmatik.server.controller;

import com.qrmatik.server.service.TenantContext;
import com.qrmatik.server.service.TenantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/config")
    public Map<String, Object> config(@RequestParam(value = "code", required = false) String code) {
    return tenantService.getConfig(code, TenantContext.getTenant());
    }
}
