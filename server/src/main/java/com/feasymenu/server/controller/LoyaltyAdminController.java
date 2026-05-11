package com.feasymenu.server.controller;

import com.feasymenu.server.dto.LoyaltyPrizeDto;
import com.feasymenu.server.model.LoyaltyCampaignEntity;
import com.feasymenu.server.service.LoyaltyService;
import com.feasymenu.server.service.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loyalty")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LoyaltyAdminController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/campaign")
    public ResponseEntity<LoyaltyCampaignEntity> getCampaign() {
        String tenant = TenantContext.getTenant();
        return ResponseEntity.of(loyaltyService.getCampaign(tenant));
    }

    @PostMapping("/campaign")
    public ResponseEntity<LoyaltyCampaignEntity> saveCampaign(@RequestBody CampaignSaveRequest request) {
        String tenant = TenantContext.getTenant();
        return ResponseEntity.ok(loyaltyService.saveCampaign(tenant, request.getPrizes(), request.isActive()));
    }

    @lombok.Data
    public static class CampaignSaveRequest {
        private List<LoyaltyPrizeDto> prizes;
        private boolean active;
    }
}
