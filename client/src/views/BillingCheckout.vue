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
        return "/api/public/checkout/html?token=" + encodeURIComponent(t);
      }

      function openSameTab() {
        const url = resolveUrl();
        if (!url) return goBackToUpgrade();
        try {
          window.location.href = url;
        } catch (e) {
          /* noop */
        }
      }

      function openNewTab() {
        const url = resolveUrl();
        if (!url) return goBackToUpgrade();
        try {
          window.open(url, "_blank");
        } catch (e) {
          try {
            window.location.href = url;
          } catch (e2) {
            /* noop */
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
