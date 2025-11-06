<template>
  <div class="rounded bg-white p-4 shadow">
    <h2 class="mb-4 text-xl font-semibold">Ödeme</h2>
    <div v-if="!raw" class="text-sm text-gray-600">
      Ödeme içeriği bulunamadı. Lütfen tekrar deneyin.
    </div>
    <div v-else>
      <div v-if="!opened" class="flex w-full flex-col items-start gap-3">
        <p class="text-sm text-gray-600">
          Ödemeyi kapatırsanız plan yükseltme sayfasına geri dönebilirsiniz.
        </p>
        <p class="text-sm text-gray-600">
          Bu sayfada ya da yeni sekmede açabilirsiniz. Mobilde tarayıcılar yeni sekmeyi
          engelleyebilir.
        </p>
        <div class="flex w-full flex-col gap-2 sm:w-auto sm:flex-row sm:gap-2">
          <button @click="openHere" class="btn btn-primary w-full sm:w-auto">
            Bu Pencerede Aç
          </button>
          <button
            v-if="!isMobile"
            @click="openWindow"
            class="btn btn-primary"
            style="background-image: none; background-color: #f59e0b"
          >
            Yeni Sekmede Aç
          </button>
          <button
            v-if="!isMobile && canBlobFallback"
            @click="openWithBlob"
            class="btn btn-secondary"
          >
            Alternatif Aç (Blob)
          </button>
          <button @click="goBackToUpgrade" class="btn btn-secondary w-full sm:w-auto">
            Vazgeç ve Geri Dön
          </button>
        </div>
      </div>
      <div v-else class="flex items-center justify-between gap-3 text-sm text-gray-600">
        <span
          >Ödeme penceresi açıldı. Görmüyorsanız tarayıcınızın pop-up engelleyicisini kontrol
          edin.</span
        >
        <button @click="goBackAfterOpened" class="btn btn-secondary">Vazgeç ve Geri Dön</button>
      </div>
    </div>
  </div>
</template>

<script>
  import { ref, onMounted, onBeforeUnmount } from "vue";
  import { useRouter } from "vue-router";

  export default {
    name: "BillingCheckout",
    setup() {
      const router = useRouter();
      const raw = ref("");
      const container = ref(null);
      const opened = ref(false);
      const canBlobFallback = ref(false);
      const popupRef = ref(null);
      const isMobile = ref(false);
      let pollTimer = null;
      // Not auto-opening anymore; flag removed

      function buildHtml(content) {
        const trimmed = (content || "").trim();
        const looksLikeFullDoc = /<html[\s>]/i.test(trimmed) || /<!doctype/i.test(trimmed);
        const bar =
          '<div id="qm-topbar" style="position:sticky;top:0;left:0;right:0;background:#f8fafc;border-bottom:1px solid #e5e7eb;padding:8px 12px;display:flex;justify-content:space-between;align-items:center;z-index:9999;font-family:ui-sans-serif,system-ui,-apple-system;">' +
          "<strong>Ödeme</strong>" +
          '<button id="qm-cancel-btn" style="border:1px solid #d1d5db;border-radius:6px;padding:6px 10px;background:#fff;cursor:pointer">Vazgeç ve Geri Dön</button>' +
          "</div>";
        const guardScript =
          "<scr" +
          "ipt>" +
          "(function(){" +
          "  function back(){ try { location.href='/admin/upgrade?checkout=cancel'; } catch(e){} }" +
          "  document.addEventListener('DOMContentLoaded', function(){" +
          "    try { var b = document.getElementById('qm-cancel-btn'); if (b) { b.addEventListener('click', function(e){ e.preventDefault(); back(); }); } } catch(e){}" +
          "    try { setInterval(function(){ try {" +
          "      var body = document.body; if (!body) return;" +
          "      var hasIframe = body.querySelector('iframe');" +
          '      var hasIyzi = body.querySelector(\'#iyzipay-checkout-form, script[src*="iyzipay"], div[class*="iyzi"]\');' +
          "      var docEl = document.documentElement;" +
          "      var h = Math.max(body.scrollHeight || 0, body.offsetHeight || 0, docEl ? docEl.scrollHeight || 0 : 0);" +
          "      if (!hasIframe && !hasIyzi && h < 150) { back(); }" +
          "    } catch(e){} }, 2000); } catch(e){}" +
          "  });" +
          "})();" +
          "</scr" +
          "ipt>";
        if (looksLikeFullDoc) {
          // Inject our bar and guard script right after opening <body>
          try {
            return trimmed.replace(/<body[^>]*>/i, function (m) {
              return m + bar + guardScript;
            });
          } catch (e) {
            // Fallback: append to end
            return trimmed + bar + guardScript;
          }
        }
        // Wrap fragment into a minimal standalone HTML with cancel bar and guard script
        return (
          "<!doctype html><html><head><meta charset='utf-8'>" +
          "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
          "<title>Ödeme</title>" +
          "<style>body{margin:0;padding:0;background:#fff}</style>" +
          "</head><body>" +
          bar +
          guardScript +
          '<div style="padding:12px">' +
          trimmed +
          "</div>" +
          "</body></html>"
        );
      }

      function openWindow() {
        if (!raw.value) return;
        // Try without features first for maximum compatibility (and keep opener)
        let w = window.open("", "_blank");
        if (!w) {
          // Fallback to explicit features in case some browsers require
          w = window.open("", "_blank", "popup");
        }
        if (!w) {
          // Pop-up engellendi: aynı sekmede açmayı dene
          openHere();
          return;
        }
        try {
          const html = buildHtml(raw.value);
          w.document.open();
          // Write the HTML snippet directly; it will load gateway scripts inside the new context
          w.document.write(html);
          w.document.close();
          opened.value = true;
          popupRef.value = w;
          // If the window remains visually empty (potential CSP/script block), enable blob fallback
          setTimeout(() => {
            try {
              const bodyLen = (
                (w.document && w.document.body && w.document.body.innerHTML) ||
                ""
              ).trim().length;
              if (!bodyLen) canBlobFallback.value = true;
            } catch {
              canBlobFallback.value = true;
            }
          }, 800);
        } catch (e) {
          opened.value = false;
        }
      }

      function openWithBlob() {
        if (!raw.value) return;
        const html = buildHtml(raw.value);
        try {
          const blob = new Blob([html], { type: "text/html;charset=utf-8" });
          const url = URL.createObjectURL(blob);
          const w = window.open(url, "_blank");
          popupRef.value = w;
        } catch {
          // noop
        }
      }

      function openHere() {
        if (!raw.value) return;
        const html = buildHtml(raw.value);
        try {
          // Replace the current SPA document with iyzico content; callback will redirect back to /admin
          try {
            sessionStorage.setItem("qm_checkout_auto_opened", "1");
          } catch (e) {
            /* noop */
          }
          document.open();
          document.write(html);
          document.close();
        } catch {
          // noop
        }
      }

      function goBackToUpgrade() {
        try {
          sessionStorage.removeItem("qm_checkout_content");
        } catch (e) {
          /* noop */
        }
        router.replace("/admin/upgrade");
      }

      function goBackAfterOpened() {
        try {
          if (popupRef.value && !popupRef.value.closed) popupRef.value.close();
        } catch (e) {
          /* noop */
        }
        router.replace("/admin/upgrade?checkout=cancel");
      }

      function onMsg(ev) {
        try {
          const d = ev && ev.data;
          if (d && d.type === "qm_upgrade") {
            const s = d.status === "success" ? "success" : "fail";
            try {
              if (popupRef.value && !popupRef.value.closed) popupRef.value.close();
            } catch (e) {
              /* noop */
            }
            router.replace(`/admin?upgrade=${s}`);
          }
        } catch (e) {
          /* noop */
        }
      }

      onMounted(() => {
        try {
          raw.value = sessionStorage.getItem("qm_checkout_content") || "";
        } catch (e) {
          raw.value = "";
        }
        try {
          // Basit mobil tespiti: UA ya da küçük viewport
          isMobile.value =
            /iPhone|Android|Mobile/i.test(navigator.userAgent || "") ||
            (typeof window !== "undefined" &&
              window.matchMedia &&
              window.matchMedia("(max-width: 640px)").matches);
        } catch (e) {
          /* noop */
        }
        try {
          sessionStorage.removeItem("qm_checkout_auto_opened");
        } catch (e) {
          /* noop */
        }
        // Listen for callback message
        window.addEventListener("message", onMsg);
        // Poll for popup close and localStorage flag as a fallback
        pollTimer = window.setInterval(() => {
          try {
            const w = popupRef.value;
            if (w && w.closed) {
              let res = null;
              try {
                res = localStorage.getItem("qm_upgrade_result");
                localStorage.removeItem("qm_upgrade_result");
              } catch (e) {
                /* noop */
              }
              if (res === "success" || res === "fail") {
                router.replace(`/admin?upgrade=${res}`);
              } else {
                // Kullanıcı kapattı: Plan Yükselt sayfasına dön ve cancel işaretini gönder
                router.replace("/admin/upgrade?checkout=cancel");
              }
              window.clearInterval(pollTimer);
            }
          } catch (e) {
            /* noop */
          }
        }, 800);
        // Otomatik açma kaldırıldı: kullanıcıdan seçim yapmasını istiyoruz (popup engelleyiciler ve iptal akışı için daha güvenilir)
      });

      // Cleanup
      onBeforeUnmount(() => {
        try {
          window.removeEventListener("message", onMsg);
        } catch (e) {
          /* noop */
        }
        try {
          if (pollTimer) window.clearInterval(pollTimer);
        } catch (e) {
          /* noop */
        }
      });

      return {
        raw,
        container,
        opened,
        openWindow,
        openWithBlob,
        canBlobFallback,
        openHere,
        goBackToUpgrade,
        goBackAfterOpened,
      };
    },
  };
</script>
