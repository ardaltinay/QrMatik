<template>
  <div>
    <h2 class="mb-3 text-lg font-semibold">QR Kodları</h2>
    <div class="mb-2 text-sm text-gray-600">
      İşletme (tenant) kodu — yalnızca kendi işletmenizin QR kodlarını indirebilirsiniz:
    </div>
    <input
      v-model="qrTenant"
      placeholder="işletme kodu"
      @blur="validateTenant"
      class="mb-2 w-full rounded border p-2"
    />
    <div class="mb-3 flex flex-wrap gap-2">
      <button @click="fillFromContext" type="button" class="rounded border px-3 py-2">
        İşletme kodumu bul
      </button>
      <button
        @click="downloadQrs"
        :disabled="loading"
        class="rounded bg-brand-500 px-3 py-2 text-white disabled:opacity-50"
      >
        {{ loading ? "İndiriliyor..." : "PDF İndir" }}
      </button>
    </div>
    <div v-if="qrStatus" class="mt-2 text-sm text-gray-600">{{ qrStatus }}</div>
    <div v-if="qrError" class="mt-2 text-sm text-red-600">{{ qrError }}</div>
  </div>
</template>

<script>
  import { ref } from "vue";
  import { apiFetch } from "@/utils/api";

  export default {
    name: "AdminQrView",
    setup() {
      // We no longer allow blank tenant (previously meant all tenants)
      const qrTenant = ref("");
      const qrStatus = ref("");
      const qrError = ref("");
      const loading = ref(false);

      function currentSessionTenant() {
        // Derive tenant from hostname or path (same heuristic as login tenant presence check)
        try {
          const host = window.location.hostname || "";
          // localhost multi-tenant pattern: {tenant}.localhost
          if (host.endsWith(".localhost")) {
            const part = host.split(".")[0];
            if (part && part !== "localhost") return part;
          } else {
            // production: subdomain depth >= 2 => first chunk is tenant (excluding www/app)
            const segments = host.split(".");
            if (segments.length > 2) {
              const first = segments[0];
              if (first && first !== "www" && first !== "app") return first;
            }
          }
          // path-based /r/{tenant}
          const path = window.location.pathname || "";
          const pParts = path.split("/");
          const rIdx = pParts.indexOf("r");
          if (rIdx >= 0 && pParts.length > rIdx + 1) {
            const t = pParts[rIdx + 1];
            if (t) return t;
          }
        } catch {
          /* ignore */
        }
        // Fallback to localStorage (legacy) if present
        try {
          const ls = localStorage.getItem("qm_tenant");
          if (ls) return ls;
        } catch {
          /* ignore */
        }
        return null;
      }

      function fillFromContext() {
        const t = currentSessionTenant();
        if (t) {
          qrTenant.value = t;
          qrError.value = "";
        } else {
          qrError.value = "İşletme kodu tespit edilemedi. Alt alan adından erişmeyi deneyin.";
        }
      }

      function validateTenant() {
        qrError.value = "";
        if (!qrTenant.value || !qrTenant.value.trim()) {
          qrError.value = "İşletme kodu boş bırakılamaz.";
          return false;
        }
        const ctxTenant = currentSessionTenant();
        if (ctxTenant && ctxTenant !== qrTenant.value.trim()) {
          qrError.value = `Girilen kod aktif işletme ile uyuşmuyor (${ctxTenant}).`;
          return false;
        }
        return true;
      }

      async function downloadQrs() {
        qrStatus.value = "";
        qrError.value = "";
        if (!validateTenant()) return;
        loading.value = true;
        try {
          const tenant = qrTenant.value.trim();
          // Backend will derive tenant from context; do not allow omission; pass param for clarity if needed.
          const resp = await apiFetch(`/api/qr/bulk?tenant=${encodeURIComponent(tenant)}`, {
            method: "GET",
          });
          if (!resp.ok) {
            let msg = "Sunucudan PDF alınamadı";
            try {
              const text = await resp.text();
              if (text) {
                try {
                  const j = JSON.parse(text);
                  msg = j.error || j.message || msg;
                } catch {
                  msg = text || msg;
                }
              }
            } catch {
              /* ignore */
            }
            throw new Error(msg);
          }
          const blob = await resp.blob();
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement("a");
          a.href = url;
          a.download = `qr-codes-${tenant}.pdf`;
          document.body.appendChild(a);
          a.click();
          a.remove();
          window.URL.revokeObjectURL(url);
          qrStatus.value = "İndirme tamamlandı";
          setTimeout(() => (qrStatus.value = ""), 4000);
        } catch (e) {
          console.error(e);
          qrError.value = e.message || String(e);
        } finally {
          loading.value = false;
        }
      }

      return { qrTenant, qrStatus, qrError, loading, downloadQrs, fillFromContext, validateTenant };
    },
  };
</script>

<style scoped>
  /* minimal */
</style>
