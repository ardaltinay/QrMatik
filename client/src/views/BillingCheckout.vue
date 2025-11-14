<template>
  <div class="rounded bg-white p-4 shadow">
    <h2 class="mb-4 text-xl font-semibold">Ödeme</h2>
    <p class="mb-4 text-sm text-gray-600">
      Ödemeyi bu sekmede veya yeni sekmede açabilirsiniz. Eğer açılmazsa ağ/DNS engellemelerini
      kontrol edin.
    </p>
    <div class="flex w-full flex-col gap-2 sm:w-auto sm:flex-row sm:gap-2">
      <button @click="openSameTab" class="btn btn-primary w-full sm:w-auto">Bu Sekmede Aç</button>
      <button @click="openNewTab" class="btn btn-secondary w-full sm:w-auto">
        Yeni Sekmede Aç
      </button>
      <button @click="goBackToUpgrade" class="btn btn-secondary w-full sm:w-auto">
        Vazgeç ve Geri Dön
      </button>
    </div>
  </div>
</template>

<script>
  import { ref, onMounted } from "vue";
  import { useRouter } from "vue-router";

  export default {
    name: "BillingCheckout",
    setup() {
      const router = useRouter();
      const token = ref("");

      function getTenantCode() {
        try {
          const host = window.location.hostname;
          const parts = host.split(".");
          if (parts.length > 2) return parts[0];
          const raw = localStorage.getItem("qm_tenant");
          if (raw) return raw;
        } catch (e) {
          /* noop */
        }
        return null;
      }

      function goBackToUpgrade() {
        try {
          sessionStorage.removeItem("qm_checkout_content");
        } catch (e) {
          /* noop */
        }
        router.replace("/admin/upgrade");
      }

      function extractTokenFromHtml(html) {
        try {
          const m = String(html || "").match(/token=([A-Za-z0-9_-]{16,})/);
          return m && m[1] ? m[1] : null;
        } catch (e) {
          return null;
        }
      }

      function resolveUrl() {
        const t =
          (typeof sessionStorage !== "undefined" && sessionStorage.getItem("qm_checkout_token")) ||
          token.value;
        if (!t) return null;
        const tenant = getTenantCode();
        let url = "/api/public/checkout/html?token=" + encodeURIComponent(t);
        if (tenant) url += "&tenant=" + encodeURIComponent(tenant);
        return url;
      }

      async function openSameTab() {
        const url = resolveUrl();
        if (!url) return goBackToUpgrade();
        // Prefer direct navigation to the backend URL. This lets the browser
        // load the page normally and avoids document.write/blob/data URL issues
        // that often fail on mobile webviews.
        try {
          window.location.href = url;
          return;
        } catch (e) {
          // if direct navigation fails for any reason, fall back to the
          // fetch+blob approach below
        }

        const headers = { Accept: "text/html" };
        const tenantCode = getTenantCode();
        if (tenantCode) headers["X-Tenant"] = tenantCode;

        try {
          const res = await fetch(url, { headers });
          const buf = await res.arrayBuffer();
          const decoder = new TextDecoder("utf-8");
          const html = decoder.decode(buf);

          try {
            document.open();
            document.write(html);
            document.close();
            return;
          } catch (writeErr) {
            console.debug("document.write failed for same-tab open", writeErr, {
              len: html ? html.length : 0,
              preview: html ? html.slice(0, 200) : null,
            });
          }

          // blob fallback
          try {
            const blob = new Blob([buf], { type: "text/html; charset=utf-8" });
            const blobUrl = URL.createObjectURL(blob);
            try {
              window.location.href = blobUrl;
            } catch (navErr) {
              console.debug(
                "navigating to blob URL failed, falling back to base64/data URL",
                navErr,
              );
              try {
                const base64 = btoa(unescape(encodeURIComponent(html)));
                window.location.href = "data:text/html;charset=utf-8;base64," + base64;
              } catch (b64err) {
                window.location.href = "data:text/html;charset=utf-8," + encodeURIComponent(html);
              }
            }
            setTimeout(() => {
              try {
                URL.revokeObjectURL(blobUrl);
              } catch (e) {
                /* ignore */
              }
            }, 5000);
            return;
          } catch (e) {
            try {
              window.location.href = url;
            } catch (e2) {
              /* ignore */
            }
          }
        } catch (e) {
          try {
            window.location.href = url;
          } catch (e2) {
            /* ignore */
          }
        }
      }

      async function openNewTab() {
        const url = resolveUrl();
        if (!url) return goBackToUpgrade();
        const headers = { Accept: "text/html" };
        const tenantCode = getTenantCode();
        if (tenantCode) headers["X-Tenant"] = tenantCode;

        try {
          const res = await fetch(url, { headers });
          const buf = await res.arrayBuffer();
          const decoder = new TextDecoder("utf-8");
          const html = decoder.decode(buf);

          // Try opening a new tab to the backend URL directly. If this succeeds
          // the browser will load the page natively which avoids injection
          // problems on mobile. If the popup is blocked (w == null) we'll
          // fall back to the fetch+blob approach below.
          const w = window.open(url);
          if (w) return;

          try {
            w.document.open();
            w.document.write(html);
            w.document.close();
            return;
          } catch (writeErr) {
            console.debug("direct write to new window failed", writeErr, {
              len: html ? html.length : 0,
              preview: html ? html.slice(0, 200) : null,
            });
          }

          try {
            const blob = new Blob([buf], { type: "text/html; charset=utf-8" });
            const blobUrl = URL.createObjectURL(blob);
            try {
              w.location.href = blobUrl;
            } catch (navErr) {
              console.debug("navigating new window to blob URL failed", navErr);
              try {
                const base64 = btoa(unescape(encodeURIComponent(html)));
                w.location.href = "data:text/html;charset=utf-8;base64," + base64;
              } catch (b64err) {
                w.location.href = "data:text/html;charset=utf-8," + encodeURIComponent(html);
              }
            }
            setTimeout(() => {
              try {
                URL.revokeObjectURL(blobUrl);
              } catch (e) {
                /* ignore */
              }
            }, 5000);
            return;
          } catch (e) {
            try {
              const base64 = btoa(unescape(encodeURIComponent(html)));
              w.location.href = "data:text/html;charset=utf-8;base64," + base64;
            } catch (b64err) {
              try {
                w.location.href = "data:text/html;charset=utf-8," + encodeURIComponent(html);
              } catch (e2) {
                try {
                  w.close();
                } catch (e3) {
                  /* ignore */
                }
              }
            }
          }
        } catch (e) {
          try {
            window.location.href = url;
          } catch (e2) {
            /* ignore */
          }
        }
      }

      onMounted(() => {
        try {
          const stored = sessionStorage.getItem("qm_checkout_token");
          if (stored) token.value = stored;
          if (!token.value) {
            const raw = sessionStorage.getItem("qm_checkout_content") || "";
            const t = extractTokenFromHtml(raw);
            if (t) token.value = t;
          }
        } catch (e) {
          /* noop */
        }
      });

      return { openSameTab, openNewTab, goBackToUpgrade };
    },
  };
</script>
