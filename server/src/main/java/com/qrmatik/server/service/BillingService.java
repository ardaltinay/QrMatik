package com.qrmatik.server.service;

import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.Currency;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import com.qrmatik.server.config.IyzicoOptionsConfig.IyzicoProperties;
import com.qrmatik.server.model.PlanType;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.repository.TenantRepository;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BillingService {

    private final Options options;
    private final IyzicoProperties props;
    private final PricingService pricingService;
    private final TenantRepository tenantRepository;
    private final ZoneId appZoneId;

    public BillingService(Options options, IyzicoProperties props, PricingService pricingService,
            TenantRepository tenantRepository, ZoneId appZoneId) {
        this.options = options;
        this.props = props;
        this.pricingService = pricingService;
        this.tenantRepository = tenantRepository;
        this.appZoneId = appZoneId;
    }

    public static record InitResponse(String token, String checkoutFormContent) {
    }

    public InitResponse initializeHostedCheckout(TenantEntity tenant, String plan, String period, String baseUrl) {
        if (!props.isEnabled()) {
            throw new IllegalStateException("Payments disabled: set iyzico.enabled=true");
        }
        String planUpper = plan == null ? "" : plan.trim().toUpperCase(Locale.ROOT);
        if (!"STANDARD".equals(planUpper) && !"PRO".equals(planUpper)) {
            throw new IllegalArgumentException("Geçersiz plan: " + plan);
        }
        String billUpper = period == null ? "YEARLY" : period.trim().toUpperCase(Locale.ROOT);
        if (!"YEARLY".equals(billUpper)) {
            throw new IllegalArgumentException("Sadece yıllık faturalama desteklenmektedir");
        }
        // Enforce upgrade-only on server side (frontend can be manipulated)
        PlanType currentPlan = tenant.getPlan() == null ? PlanType.FREE : tenant.getPlan();
        PlanType targetPlan = PlanType.fromString(planUpper);
        // monthly dönem kaldırıldığı için currentPeriod kullanılmıyor
        boolean samePlan = targetPlan == currentPlan;
        boolean planUp = planRank(targetPlan) > planRank(currentPlan);
        if (samePlan) {
            throw new IllegalArgumentException("Zaten bu üyeliğe sahipsiniz.");
        }
        if (!planUp) {
            throw new IllegalArgumentException("Bu istek upgrade değil");
        }
        int amountTry = pricingService.priceFor(planUpper, billUpper);
        if (amountTry <= 0) {
            throw new IllegalStateException("Fiyat bulunamadı");
        }
        String basketId = String.join("|", List.of("UPGRADE", tenant.getCode(), planUpper, billUpper,
                Long.toString(Instant.now().toEpochMilli()), UUID.randomUUID().toString().substring(0, 8)));

        String callbackBase = StringUtils.hasText(props.getCallbackBaseUrl()) ? props.getCallbackBaseUrl() : baseUrl;
        // Dev safeguard: if callbackBase accidentally points to Vite dev server (5173),
        // rewrite to backend (8080)
        try {
            if (callbackBase != null) {
                String cb = callbackBase;
                // normalize whitespace
                cb = cb.trim();
                if (cb.contains("localhost:5173")) {
                    cb = cb.replace("localhost:5173", "localhost:8080");
                }
                if (cb.contains(".localhost:5173")) {
                    cb = cb.replace(".localhost:5173", ":8080");
                }
                // ensure http/https scheme present (fallback http for dev if missing)
                if (!cb.startsWith("http://") && !cb.startsWith("https://")) {
                    cb = "http://" + cb;
                }
                callbackBase = cb;
            }
        } catch (Throwable __ignored) {
        }
        if (!StringUtils.hasText(callbackBase)) {
            throw new IllegalStateException("Callback base URL eksik. iyzico.callbackBaseUrl ayarlayın.");
        }
        String callbackUrl = callbackBase.replaceAll("/+$", "") + "/api/public/iyzico/callback";
        // Include UI base in callback so server can 302 redirect browser to the correct
        // host (dev/prod)
        try {
            if (StringUtils.hasText(baseUrl)) {
                String enc = URLEncoder.encode(baseUrl, StandardCharsets.UTF_8);
                callbackUrl += (callbackUrl.contains("?") ? "&" : "?") + "ret=" + enc;
            }
        } catch (Throwable __ignored) {
        }

        CreateCheckoutFormInitializeRequest request = new CreateCheckoutFormInitializeRequest();
        request.setLocale(com.iyzipay.model.Locale.TR.getValue());
        request.setConversationId(UUID.randomUUID().toString());
        request.setPrice(BigDecimal.valueOf(amountTry));
        request.setPaidPrice(BigDecimal.valueOf(amountTry));
        request.setCurrency(Currency.TRY.name());
        request.setBasketId(basketId);
        request.setPaymentGroup(PaymentGroup.SUBSCRIPTION.name());
        request.setCallbackUrl(callbackUrl);

        Buyer buyer = new Buyer();
        buyer.setId(tenant.getCode());
        String owner = tenant.getOwnerName() == null ? "default" : tenant.getOwnerName().trim();
        String[] parts = owner.split(" ", 2);
        buyer.setName(parts.length > 0 ? parts[0] : tenant.getCode());
        buyer.setSurname(parts.length > 1 ? parts[1] : "default");
        buyer.setEmail(tenant.getOwnerEmail() == null || tenant.getOwnerEmail().isBlank()
                ? "default@example.com"
                : tenant.getOwnerEmail());
        // Phone optional in sandbox; we don't store it yet
        // buyer.setGsmNumber(null);
        buyer.setIdentityNumber("11111111111"); // sandbox default
        buyer.setRegistrationAddress("N/A");
        buyer.setIp("127.0.0.1");
        buyer.setCity("N/A");
        buyer.setCountry("TR");
        request.setBuyer(buyer);

        Address shipping = new Address();
        shipping.setContactName(owner);
        shipping.setCity("N/A");
        shipping.setCountry("TR");
        shipping.setAddress("N/A");
        request.setShippingAddress(shipping);

        Address billing = new Address();
        billing.setContactName(owner);
        billing.setCity("N/A");
        billing.setCountry("TR");
        billing.setAddress("N/A");
        request.setBillingAddress(billing);

        BasketItem item = new BasketItem();
        item.setId("PLAN-" + planUpper + "-" + billUpper);
        item.setName("QrMatik " + planUpper + " (Yıllık)");
        item.setCategory1("Subscription");
        item.setItemType(BasketItemType.VIRTUAL.name());
        item.setPrice(BigDecimal.valueOf(amountTry));
        request.setBasketItems(List.of(item));

        CheckoutFormInitialize init = CheckoutFormInitialize.create(request, options);
        if (!"success".equalsIgnoreCase(init.getStatus())) {
            throw new IllegalStateException("Ödeme başlatma başarısız: " + init.getErrorMessage());
        }
        return new InitResponse(init.getToken(), init.getCheckoutFormContent());
    }

    public String handleCallbackAndUpgrade(String token) {
        RetrieveCheckoutFormRequest req = new RetrieveCheckoutFormRequest();
        req.setLocale(com.iyzipay.model.Locale.TR.getValue());
        req.setConversationId(UUID.randomUUID().toString());
        req.setToken(token);
        CheckoutForm form = CheckoutForm.retrieve(req, options);
        if (!"success".equalsIgnoreCase(form.getStatus())) {
            return "FAIL";
        }
        if (!"SUCCESS".equalsIgnoreCase(form.getPaymentStatus())) {
            return "FAIL";
        }
        String basketId = form.getBasketId();
        // Expecting: UPGRADE|tenantCode|PLAN|PERIOD|timestamp|nonce
        if (!StringUtils.hasText(basketId))
            return "FAIL";
        String[] parts = basketId.split("\\|");
        if (parts.length < 4)
            return "FAIL";
        String kind = parts[0];
        if (!"UPGRADE".equals(kind))
            return "FAIL";
        String tenantCode = parts[1];
        String plan = parts[2];
        String period = parts[3];
        // Perform upgrade
        var opt = tenantRepository.findByCode(tenantCode);
        if (opt.isEmpty())
            return "FAIL";
        TenantEntity t = opt.get();
        PlanType newPlan = PlanType.fromString(plan);
        if (newPlan == null)
            return "FAIL";
        t.setPlan(newPlan);
        // billing tracking
        t.setBillingPeriod(period);
        ZoneId zone = appZoneId;
        if (t.getTimeZone() != null && !t.getTimeZone().isBlank()) {
            try {
                zone = ZoneId.of(t.getTimeZone());
            } catch (Throwable __ignored) {
            }
        }
        LocalDate now = LocalDate.now(zone);
        LocalDate until = "YEARLY".equalsIgnoreCase(period) ? now.plusYears(1) : now.plusMonths(1);
        t.setPlanPaidUntil(until);
        try {
            t.setLastPaymentId(form.getPaymentId());
        } catch (Throwable __ignored) {
            // older SDKs may differ; ignore if not available
        }
        t.setLastPaymentAt(Instant.now());
        tenantRepository.save(t);
        return "OK";
    }

    private int planRank(PlanType p) {
        return switch (p) {
            case FREE -> 0;
            case STANDARD -> 1;
            case PRO -> 2;
        };
    }
}
