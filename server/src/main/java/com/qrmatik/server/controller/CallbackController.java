package com.qrmatik.server.controller;

import com.qrmatik.server.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CallbackController {

    private final BillingService billingService;

    public CallbackController(BillingService billingService) {
        this.billingService = billingService;
    }

    @CrossOrigin(origins = "*", allowCredentials = "false")
    @RequestMapping(value = "/api/public/iyzico/callback", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Void> iyzicoCallback(@RequestParam(name = "token", required = false) String token,
            @RequestParam(name = "ret", required = false) String retBase) {
        String status = (token != null && !token.isBlank()) ? billingService.handleCallbackAndUpgrade(token) : "FAIL";
        String suffix = "/admin?upgrade=" + ("OK".equals(status) ? "success" : "fail");
        String location;
        if (retBase != null && !retBase.isBlank()) {
            try {
                String base = java.net.URLDecoder.decode(retBase, java.nio.charset.StandardCharsets.UTF_8);
                base = base.replaceAll("/+$", "");
                if (!base.startsWith("http://") && !base.startsWith("https://")) {
                    base = "http://" + base;
                }
                location = base + suffix;
            } catch (Throwable t) {
                location = suffix;
            }
        } else {
            location = suffix;
        }
        return ResponseEntity.status(302).header("Location", location).build();
    }
}
