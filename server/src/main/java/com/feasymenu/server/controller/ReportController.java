package com.feasymenu.server.controller;

import com.feasymenu.server.dto.ReportDto;
import com.feasymenu.server.service.ReportService;
import com.feasymenu.server.service.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<ReportDto> getReport(@RequestParam(value = "range", defaultValue = "week") String range) {
        String tenant = TenantContext.getTenant();
        if (tenant == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reportService.getReport(tenant, range));
    }
}
