package com.qrmatik.server.controller;

import com.qrmatik.server.dto.PricingDto;
import com.qrmatik.server.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicPricingController {

    private final PricingService pricingService;

    public PublicPricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/pricing")
    public ResponseEntity<PricingDto> pricing() {
        return ResponseEntity.ok(pricingService.currentPricing());
    }
}
