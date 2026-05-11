package com.feasymenu.server.controller;

import com.feasymenu.server.dto.SpinRequestDto;
import com.feasymenu.server.dto.ValidateCodeResponseDto;
import com.feasymenu.server.service.LoyaltyService;
import com.feasymenu.server.service.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @PostMapping("/spin")
    public ResponseEntity<?> spin(@RequestBody SpinRequestDto request,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        String tenant = TenantContext.getTenant();
        String code = loyaltyService.spin(request, tenant, sessionId);
        return ResponseEntity.ok(Map.of("code", code));
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateCodeResponseDto> validate(@RequestParam String code) {
        String tenant = TenantContext.getTenant();
        ValidateCodeResponseDto res = loyaltyService.validateCode(code, tenant);
        if (!res.isValid()) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/campaign")
    public ResponseEntity<com.feasymenu.server.model.LoyaltyCampaignEntity> getPublicCampaign() {
        String tenant = TenantContext.getTenant();
        return ResponseEntity.of(loyaltyService.getCampaign(tenant));
    }
}
