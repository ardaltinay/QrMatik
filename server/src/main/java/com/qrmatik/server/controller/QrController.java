package com.qrmatik.server.controller;

import com.qrmatik.server.service.QrService;
import com.qrmatik.server.service.TenantContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final QrService qrService;

    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    @GetMapping(value = "/bulk", produces = "application/pdf")
    public ResponseEntity<byte[]> getBulkQrPdf(@RequestParam(value = "tenant", required = false) String tenant) {
        try {
            String ctxTenant = TenantContext.getTenant();
            if (ctxTenant == null || ctxTenant.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            if (tenant != null && !tenant.isBlank() && !ctxTenant.equals(tenant)) {
                // Do not allow generating for another tenant
                return ResponseEntity.status(403).build();
            }
            byte[] pdf = qrService.generateQrPdfForTenant(ctxTenant);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "qr-codes.pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrImage(@RequestParam("text") String text,
            @RequestParam(name = "size", required = false, defaultValue = "300") int size) {
        try {
            byte[] png = qrService.generateQrPng(text, size);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            return ResponseEntity.ok().headers(headers).body(png);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
