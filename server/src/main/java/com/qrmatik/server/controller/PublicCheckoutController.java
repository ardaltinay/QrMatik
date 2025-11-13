package com.qrmatik.server.controller;

import com.qrmatik.server.service.BillingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * Public endpoint to serve cached checkout snippet as a standalone HTML
 * document. Mobile tarayıcılarda popup / inline script bloklanması durumunda
 * fallback olarak kullanılır.
 */
@RestController
public class PublicCheckoutController {

    private final BillingService billingService;

    public PublicCheckoutController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping(value = "/api/public/checkout/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> checkoutHtml(@RequestParam("token") String token,
                                               @RequestParam(value = "debug", required = false) Boolean debug) {
        String snippet = billingService.getCachedSnippet(token);
        if (snippet == null || snippet.isBlank()) {
            String notFound = "<!doctype html><html><head><meta charset='utf-8'><title>Geçersiz Token</title></head><body>Token bulunamadı veya süresi doldu.</body></html>";
            return ResponseEntity.status(404).header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                    .body(notFound.getBytes(StandardCharsets.UTF_8));
        }
        StringBuilder doc = new StringBuilder(4096);
        doc.append("<!doctype html><html><head><meta charset='utf-8'>");
        doc.append("<meta name='viewport' content='width=device-width,initial-scale=1'>");
        doc.append("<title>Ödeme</title>");
        doc.append("<style>html,body{height:100%;}body{margin:0;font-family:ui-sans-serif,system-ui,-apple-system;}#top{position:sticky;top:0;padding:8px 12px;background:#f8fafc;border-bottom:1px solid #e5e7eb;display:flex;justify-content:space-between;align-items:center;z-index:9999}button{cursor:pointer}#wrap{padding:12px;min-height:calc(100% - 44px)}iframe{width:100%;min-height:calc(100vh - 64px);border:0;}</style>");
        doc.append("</head><body>");
        doc.append("<div id='top'><strong>Ödeme</strong><button onclick=\"location.href='/admin/upgrade?checkout=cancel'\">İptal</button></div>");
        doc.append("<div id='wrap'>").append(snippet).append("</div>");
        doc.append("<div id='hint' style='display:none;margin:12px;padding:8px;background:#fff3cd;border:1px solid #ffeeba;color:#856404;border-radius:6px'>Sayfa boş görünüyorsa, mobil ağınızda ödeme sağlayıcısına erişim (özellikle sandbox) engelleniyor olabilir. Farklı bir ağ (Wi‑Fi) veya DNS ile tekrar deneyin.</div>");
        doc.append("<noscript><div style='margin:12px;padding:8px;background:#fee2e2;border:1px solid #fecaca;color:#991b1b;border-radius:6px'>Ödeme sayfası için JavaScript gerekli. Lütfen JavaScript’i etkinleştirin.</div></noscript>");
        doc.append("<script>(function(){\n");
        doc.append("  function showHint(){var h=document.getElementById('hint'); if(h) h.style.display='block';}\n");
        doc.append("  function hideHint(){var h=document.getElementById('hint'); if(h) h.style.display='none';}\n");
        doc.append("  function addDirectLink(url){\n    if(!url) return;\n");
        doc.append("    var ex=document.getElementById('direct-link'); if(ex) return;\n");
        doc.append("    var a=document.createElement('a'); a.id='direct-link'; a.href=url; a.target='_blank'; a.rel='noopener';\n");
        doc.append("    a.textContent='Ödeme sayfasını doğrudan aç';\n");
        doc.append("    a.style.display='inline-block'; a.style.margin='12px'; a.style.padding='10px 14px'; a.style.background='#e0e7ff'; a.style.border='1px solid #c7d2fe'; a.style.color='#1e40af'; a.style.borderRadius='6px'; a.style.textDecoration='none';\n");
        doc.append("    document.body.appendChild(a);\n  }\n");
        doc.append("  var start=Date.now();\n");
        doc.append("  var tries=0;\n");
        doc.append("  var iv=setInterval(function(){\n    try{\n      tries++;\n");
        doc.append("      var form=document.querySelector('form[action*=\"iyzi\"], form[action*=\"iyzipay\"]');\n");
        doc.append("      var ifr=document.querySelector('iframe[src*=\"iyzi\"], iframe[src*=\"iyzipay\"]');\n");
        doc.append("      if(form||ifr){ hideHint(); addDirectLink(form?form.action:ifr.src); clearInterval(iv); return; }\n");
        doc.append("      if(Date.now()-start>6000){ showHint(); clearInterval(iv); }\n");
        doc.append("      if(tries>30){ clearInterval(iv); }\n    }catch(e){}\n  }, 300);\n})();</script>");
        // If debug requested, append a small diagnostic banner showing tenant/snippet info
        try {
            if (Boolean.TRUE.equals(debug)) {
                String tenant = com.qrmatik.server.service.TenantContext.getTenant();
                int len = snippet == null ? 0 : snippet.length();
                doc.insert(doc.indexOf("</body>"), "<div style='position:fixed;right:8px;bottom:8px;z-index:99999;background:#111;color:#fff;padding:8px;border-radius:6px;font-size:12px;opacity:0.95'>DEBUG: tenant=" + (tenant==null?"(null)":tenant) + " | snippetLen=" + len + "</div>");
            }
        } catch (Throwable __ignored) {
            // ignore diagnostics failures
        }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
            .body(doc.toString().getBytes(StandardCharsets.UTF_8));
    }
}
