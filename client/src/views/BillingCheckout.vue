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
  import { useAuthStore } from "@/stores/authStore";

  export default {
    name: "BillingCheckout",
    setup() {
      const router = useRouter();
      const token = ref("");
        const auth = useAuthStore();
        // Tenant context: try to extract from subdomain or localStorage
        function getTenantCode() {
          try {
            // Prefer subdomain
            const host = window.location.hostname;
            const parts = host.split(".");
            if (parts.length > 2) return parts[0];
            // Fallback: localStorage
            const raw = localStorage.getItem("qm_tenant");
            if (raw) return raw;
          } catch(e) {
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
        } catch {
          return null;
        }
      }

      function resolveUrl() {
        const t =
          (typeof sessionStorage !== "undefined" && sessionStorage.getItem("qm_checkout_token")) ||
          token.value;
        if (!t) return null;
        const tenant = getTenantCode();
        // detect dev (vite) environment: localhost or dev port 5173
        const host = window.location.hostname || "";
        const port = window.location.port || "";
        const isDev = host.includes("localhost") || host === "127.0.0.1" || port === "5173";
        if (!isDev && tenant) {
          // In production prefer path-style tenant so anonymous navigation resolves tenant server-side
          return "/r/" + encodeURIComponent(tenant) + "/api/public/checkout/html?token=" + encodeURIComponent(t);
        }
        // In dev (vite) proxy we must keep the /api path so the dev server forwards to backend
        return "/api/public/checkout/html?token=" + encodeURIComponent(t);
      }

      function openSameTab() {
        const url = resolveUrl();
        if (!url) return goBackToUpgrade();
        // Build headers
        const headers = { Accept: "text/html" };
        if (auth.token) headers["Authorization"] = "Bearer " + auth.token;
        const tenantCode = getTenantCode();
        if (tenantCode) headers["X-Tenant"] = tenantCode;

        fetch(url, { headers })
          .then(res => res.text())
          .then(html => {
            // Preferred: replace current document with fetched HTML
            try {
              document.open();
              document.write(html);
              document.close();
              return;
            } catch (writeErr) {
              console.debug("document.write failed for same-tab open", writeErr);
            }

            // Fallback: navigate to a Blob URL in the same tab
            try {
              const blob = new Blob([html], { type: "text/html" });
              const blobUrl = URL.createObjectURL(blob);
              try {
                window.location.href = blobUrl;
              } catch (navErr) {
                console.debug("navigating to blob URL failed, falling back to data URL", navErr);
                window.location.href = "data:text/html;charset=utf-8," + encodeURIComponent(html);
              }
              // Revoke after a short timeout to allow navigation to start
              setTimeout(() => {
                try { URL.revokeObjectURL(blobUrl); } catch (e) { /* ignore */ }
              }, 5000);
            } catch (e) {
              // Final fallback: navigate to the original backend URL
              try { window.location.href = url; } catch (e2) { /* ignore */ }
            }
          })
          .catch(() => {
            try { window.location.href = url; } catch (e) { /* ignore */ }
          });
      }

      function openNewTab() {
        const url = resolveUrl();
        if (!url) return goBackToUpgrade();
        // Build headers
        const headers = { Accept: "text/html" };
        if (auth.token) headers["Authorization"] = "Bearer " + auth.token;
        const tenantCode = getTenantCode();
        if (tenantCode) headers["X-Tenant"] = tenantCode;

        fetch(url, { headers })
          .then(res => res.text())
          .then(html => {
            const w = window.open();
            if (w) {
              // Preferred: write directly into the new window's document.
              try {
                w.document.open();
                w.document.write(html);
                w.document.close();
                return;
              } catch (writeErr) {
                // If direct document.write fails (some browsers/security), try blob/data URL fallback
                console.debug("direct write to new window failed", writeErr);
              }

              try {
                const blob = new Blob([html], { type: "text/html" });
                const blobUrl = URL.createObjectURL(blob);
                try {
                  w.location.href = blobUrl;
                } catch (navErr) {
                  // If setting location fails, try data URL
                  console.debug("navigating new window to blob URL failed", navErr);
                  w.location.href = "data:text/html;charset=utf-8," + encodeURIComponent(html);
                }
                // Revoke after a short timeout; ensure navigation had time to start.
                setTimeout(() => {
                  try { URL.revokeObjectURL(blobUrl); } catch (e) { /* ignore */ }
                }, 5000);
              } catch (e) {
                // If blob creation fails, try writing via data URL as last resort
                try {
                  w.location.href = "data:text/html;charset=utf-8," + encodeURIComponent(html);
                } catch (e2) {
                  try { w.close(); } catch (e3) { /* ignore */ }
                }
              }
            } else {
              // Popup blocked — navigate current tab
              window.location.href = url;
            }
          })
          .catch(() => { window.location.href = url; });
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
