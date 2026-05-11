package com.feasymenu.server.service;

import com.feasymenu.server.dto.SpinRequestDto;
import com.feasymenu.server.dto.ValidateCodeResponseDto;
import com.feasymenu.server.exception.BadRequestException;
import com.feasymenu.server.model.CustomerRewardEntity;
import com.feasymenu.server.model.LoyaltyCampaignEntity;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.repository.CustomerRewardRepository;
import com.feasymenu.server.repository.LoyaltyCampaignRepository;
import com.feasymenu.server.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final CustomerRewardRepository rewardRepository;
    private final TenantRepository tenantRepository;
    private final LoyaltyCampaignRepository campaignRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Transactional
    public String spin(SpinRequestDto request, String tenantCode, String sessionId) {
        TenantEntity tenant = null;
        if (tenantCode != null) {
            tenant = tenantRepository.findByCode(tenantCode).orElse(null);
        }

        TenantEntity finalTenant = tenant;
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;

        if (email != null && !email.isBlank()) {
            List<CustomerRewardEntity> existing = rewardRepository.findByEmail(email);
            boolean alreadySpun = existing.stream().anyMatch(r -> {
                if (finalTenant == null)
                    return r.getTenant() == null;
                return r.getTenant() != null && finalTenant.getId().equals(r.getTenant().getId());
            });
            if (alreadySpun) {
                throw new BadRequestException("error.loyalty.alreadySpun");
            }
        }

        if (sessionId != null && !sessionId.isBlank()) {
            List<CustomerRewardEntity> existing = rewardRepository.findBySessionId(sessionId);
            if (!existing.isEmpty()) {
                throw new BadRequestException("error.loyalty.alreadySpun");
            }
        }

        // Generate a random 6-character alphanumeric code
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        CustomerRewardEntity reward = CustomerRewardEntity.builder().email(email)
                .sessionId(sessionId)
                .prizeLabel(request.getPrizeLabel()).discountPercent(request.getDiscountPercent()).rewardCode(code)
                .isUsed(false).tenant(tenant).build();

        rewardRepository.save(reward);

        // Send email asynchronously or synchronously
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String tName = tenant != null ? tenant.getName() : "FeasyMenu";
            try {
                notificationService.sendLoyaltyCouponEmail(request.getEmail(), tName, code, request.getPrizeLabel());
            } catch (Exception e) {
                log.error("Failed to send loyalty email: {}", e.getMessage());
            }
        }

        return code;
    }

    @Transactional(readOnly = true)
    public ValidateCodeResponseDto validateCode(String code, String tenantCode) {
        Optional<CustomerRewardEntity> opt = rewardRepository.findByRewardCode(code);
        if (opt.isEmpty()) {
            return ValidateCodeResponseDto.builder().valid(false).build();
        }

        CustomerRewardEntity reward = opt.get();

        // Check if used
        if (reward.getIsUsed() != null && reward.getIsUsed()) {
            return ValidateCodeResponseDto.builder().valid(false).build();
        }

        // Check if it belongs to the correct tenant
        if (tenantCode != null && reward.getTenant() != null && !tenantCode.equals(reward.getTenant().getCode())) {
            return ValidateCodeResponseDto.builder().valid(false).build();
        }

        return ValidateCodeResponseDto.builder().valid(true).code(reward.getRewardCode())
                .description(reward.getPrizeLabel()).discountPercent(reward.getDiscountPercent()).build();
    }

    @Transactional
    public void markCodeAsUsed(String code) {
        Optional<CustomerRewardEntity> opt = rewardRepository.findByRewardCode(code);
        if (opt.isPresent()) {
            CustomerRewardEntity reward = opt.get();
            reward.setIsUsed(true);
            rewardRepository.save(reward);
        }
    }

    @Transactional(readOnly = true)
    public Optional<LoyaltyCampaignEntity> getCampaign(String tenantCode) {
        Optional<LoyaltyCampaignEntity> campaign = campaignRepository.findByTenant_Code(tenantCode);
        if (campaign.isPresent() && campaign.get().getActive() != null && !campaign.get().getActive()) {
            return Optional.empty();
        }
        return campaign;
    }

    @Transactional
    public LoyaltyCampaignEntity saveCampaign(String tenantCode, List<com.feasymenu.server.dto.LoyaltyPrizeDto> prizes,
            boolean active) {
        TenantEntity tenant = tenantRepository.findByCode(tenantCode)
                .orElseThrow(() -> new BadRequestException("Tenant not found"));

        // Plan check: Only PRO or higher can use Loyalty
        if (tenant.getPlan() == null || tenant.getPlan() == com.feasymenu.server.model.PlanType.FREE) {
            throw new com.feasymenu.server.exception.PlanFeatureUnavailableException(
                    "Sadakat programı sadece PRO paketlerde mevcuttur.");
        }

        LoyaltyCampaignEntity campaign = campaignRepository.findByTenant(tenant)
                .orElse(LoyaltyCampaignEntity.builder().tenant(tenant).build());

        try {
            campaign.setPrizesJson(objectMapper.writeValueAsString(prizes));
        } catch (Exception e) {
            throw new BadRequestException("Invalid prizes data");
        }
        campaign.setActive(active);

        return campaignRepository.save(campaign);
    }
}
