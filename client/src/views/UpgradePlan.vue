<template>
  <div class="max-w-xl">
    <h2 class="mb-2 text-xl font-semibold">Planı Yükselt</h2>
    <p v-if="current.plan" class="mb-4 text-sm text-gray-700">
      Mevcut planınız: <strong>{{ planLabel(current.plan) }}</strong>
      <template v-if="current.billing">
        — <span class="font-medium">{{ billingLabel() }}</span>
      </template>
      <template v-if="current.paidUntil">
        <span class="text-gray-500"> ({{ current.paidUntil }})</span>
      </template>
    </p>
    <p v-else class="mb-4 text-sm text-gray-600">Hesabınız şu anda ücretsiz planda.</p>
    <p class="mb-6 text-sm text-gray-600">Aşağıdan planı seçip yıllık ödeme ile devam edin.</p>

    <div class="mb-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
      <button
        :class="sel.plan === 'STANDARD' ? selClass(true) : selClass(false)"
        @click="sel.plan = 'STANDARD'"
      >
        <div class="flex items-center justify-center gap-2">
          <span class="font-medium">Standart</span>
          <span v-if="prices.standard" class="text-xs text-gray-600"
            >{{ prices.standard }}/yıl</span
          >
        </div>
      </button>
      <button
        :class="sel.plan === 'PRO' ? selClass(true) : selClass(false)"
        @click="sel.plan = 'PRO'"
      >
        <div class="flex items-center justify-center gap-2">
          <span class="font-medium">Pro</span>
          <span v-if="prices.pro" class="text-xs text-gray-600">{{ prices.pro }}/yıl</span>
        </div>
      </button>
    </div>

    <div class="mb-6">
      <div
        class="inline-flex items-center gap-2 rounded border bg-gray-50 px-3 py-2 text-sm text-gray-700"
      >
        <span>Faturalama:</span>
        <strong>Yıllık</strong>
      </div>
    </div>

    <p class="mb-6 text-xs text-gray-600">
      Tüm planlar yıllık faturalandırılır; aylık abonelik seçeneği bulunmamaktadır. Yükseltmeler
      anında uygulanır, plan düşürme talepleri mevcut dönem sonunda geçerlidir.
    </p>

    <div class="flex items-center justify-center gap-3 md:justify-start">
      <button
        @click="goCheckout"
        :disabled="loading"
        class="btn btn-primary order-1 gap-2 disabled:opacity-50"
      >
        <span v-if="!loading">Ödemeye Git</span>
        <span v-else>Hazırlanıyor…</span>
        <svg
          v-if="!loading"
          xmlns="http://www.w3.org/2000/svg"
          class="h-4 w-4 opacity-90"
          viewBox="0 0 20 20"
          fill="currentColor"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z"
            clip-rule="evenodd"
          />
        </svg>
      </button>
      <router-link class="btn btn-secondary order-2" to="/admin">İptal</router-link>
    </div>
    <!-- Downgrade Confirmation Modal -->
    <div
      v-if="showConfirm"
      class="fixed inset-0 z-50 flex items-center justify-center p-4"
      role="dialog"
      aria-modal="true"
    >
      <div
        class="absolute inset-0 bg-black/40 backdrop-blur-sm"
        @click="closeConfirm"
        aria-hidden="true"
      ></div>
      <div class="relative w-full max-w-md rounded-lg border border-gray-200 bg-white shadow-lg">
        <div class="border-b px-5 py-3">
          <h3 class="text-base font-semibold">Plan Düşürmeyi Onayla</h3>
        </div>
        <div class="space-y-3 px-5 py-4 text-sm text-gray-700">
          <p>
            Seçtiğiniz plan mevcut planınızdan daha düşük bir seviyede. Plan düşürme talebi hemen
            uygulanmaz; <strong>mevcut fatura dönemi sonunda</strong> geçerli olacaktır.
          </p>
          <p>
            Bu işlem sonrasında o döneme kadar mevcut haklarınız devam eder. Dönem bitince yeni plan
            limitleri uygulanacak.
          </p>
          <p class="text-xs text-gray-500">
            Onaylarsanız talep kaydedilir ve yönetici paneline yönlendirileceksiniz.
          </p>
        </div>
        <div class="flex items-center justify-end gap-2 px-5 py-3">
          <button class="btn btn-secondary" type="button" :disabled="loading" @click="closeConfirm">
            Vazgeç
          </button>
          <button
            class="btn btn-primary"
            type="button"
            :disabled="loading"
            @click="confirmDowngrade"
          >
            Onayla
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { reactive, ref, onMounted } from "vue";
  import { fetchJson } from "@/utils/api";
  import { useRouter, useRoute } from "vue-router";
  import { useUiStore } from "@/stores/uiStore";

  export default {
    name: "UpgradePlan",
    setup() {
      const router = useRouter();
      const route = useRoute();
      const sel = reactive({ plan: "STANDARD", billing: "YEARLY" });
      const current = reactive({ plan: null, billing: null, paidUntil: null });
      const prices = reactive({ standard: "", pro: "" });
      const ui = useUiStore();
      const loading = ref(false);
      const showConfirm = ref(false);

      function selClass(active) {
        return (
          "rounded px-3 py-2 text-center border transition " +
          (active
            ? "border-brand-500 bg-brand-50 text-brand-700"
            : "border-gray-300 hover:bg-gray-50")
        );
      }

      function planLabel(p) {
        const u = String(p || "").toUpperCase();
        if (u === "PRO") return "Pro";
        if (u === "STANDARD") return "Standart";
        return "Ücretsiz";
      }
      function billingLabel() {
        return "Yıllık";
      }

      function formatTRY(n) {
        try {
          return new Intl.NumberFormat("tr-TR").format(Number(n || 0)) + "₺";
        } catch {
          return (n || 0) + "₺";
        }
      }

      function normalizeName(name) {
        return String(name || "")
          .trim()
          .toUpperCase();
      }

      function findTier(tiers, keys, fallbackPrefix) {
        if (!Array.isArray(tiers)) return null;
        const upperKeys = (keys || []).map((k) => String(k || "").toUpperCase());
        let hit = tiers.find((t) => upperKeys.includes(normalizeName(t?.name)));
        if (!hit && fallbackPrefix) {
          const pref = String(fallbackPrefix).toUpperCase();
          hit = tiers.find((t) => normalizeName(t?.name).startsWith(pref));
        }
        return hit || null;
      }

      function readCachedPricing() {
        try {
          const raw = localStorage.getItem("qm_pricing_cache");
          if (!raw) return null;
          const obj = JSON.parse(raw);
          if (!obj || !obj.tiers || !obj.ts) return null;
          const ttlMs = 60 * 60 * 1000; // 1 saat TTL
          if (Date.now() - Number(obj.ts || 0) > ttlMs) return null;
          return obj;
        } catch {
          return null;
        }
      }

      function writeCachedPricing(data) {
        try {
          const toStore = { tiers: data?.tiers || [], ts: Date.now() };
          localStorage.setItem("qm_pricing_cache", JSON.stringify(toStore));
        } catch {
          /* ignore */
        }
      }

      async function loadCurrent() {
        try {
          const cfg = await fetchJson("/api/tenant/config", { silentError: true });
          const plan = cfg && cfg.plan ? String(cfg.plan).toUpperCase() : "FREE";
          const billing = cfg && cfg.billingPeriod ? String(cfg.billingPeriod).toUpperCase() : null;
          const untilRaw = cfg && cfg.planPaidUntil ? String(cfg.planPaidUntil) : null;
          current.plan = plan;
          current.billing = billing;
          current.paidUntil = untilRaw;
          // Eğer FREE değilse, varsayılan seçimleri mevcut plana göre ayarla
          if (plan === "STANDARD" || plan === "PRO") {
            sel.plan = plan;
            sel.billing = "YEARLY";
          }
        } catch {
          /* ignore */
        }
        // Fiyatları cache'den oku; yoksa fetch et ve cache'le
        let pricing = readCachedPricing();
        if (!pricing) {
          try {
            const data = await fetchJson("/api/public/pricing", { silentError: true });
            if (data && Array.isArray(data.tiers)) {
              pricing = { tiers: data.tiers, ts: Date.now() };
              writeCachedPricing(pricing);
            }
          } catch {
            /* ignore */
          }
        }
        if (pricing && Array.isArray(pricing.tiers)) {
          const tiers = pricing.tiers;
          const std = findTier(tiers, ["STANDARD", "STANDART", "STD"], "STAND");
          const pro = findTier(tiers, ["PRO", "PROFESSIONAL"], "PRO");
          prices.standard = std && std.yearly != null ? formatTRY(std.yearly) : "";
          prices.pro = pro && pro.yearly != null ? formatTRY(pro.yearly) : "";
        }
        // Eğer checkout iptal ile dönüldüyse kullanıcıyı bilgilendir
        try {
          const q = route.query || {};
          if (q.checkout === "cancel") {
            ui.toast("Ödeme iptal edildi.", "info");
            router.replace({ query: Object.assign({}, q, { checkout: undefined }) });
          }
        } catch {
          /* ignore */
        }
      }

      function rank(p) {
        const u = String(p || "").toUpperCase();
        if (u === "PRO") return 2;
        if (u === "STANDARD") return 1;
        return 0; // FREE or unknown
      }

      function isDowngrade(targetPlan) {
        const curPlan = current.plan || "FREE";
        const planDown = rank(curPlan) > rank(targetPlan);
        return planDown;
      }

      function closeConfirm() {
        showConfirm.value = false;
      }

      async function confirmDowngrade() {
        try {
          loading.value = true;
          await fetchJson("/api/billing/schedule-downgrade", {
            method: "POST",
            body: JSON.stringify({ plan: sel.plan, billingPeriod: sel.billing }),
          });
          ui.toastSuccess("Plan düşürme talebi kaydedildi. Dönem sonunda uygulanacak.");
          showConfirm.value = false;
          router.push("/admin");
        } catch (e) {
          console.debug("downgrade fail", e);
        } finally {
          loading.value = false;
        }
      }

      async function goCheckout() {
        try {
          loading.value = true;
          // Aynı plan için ödeme başlatmayı engelle (erken geri bildirim)
          if ((current.plan || "FREE") === sel.plan) {
            ui.toast("Zaten bu üyeliğe sahipsiniz.", "info");
            return;
          }
          if (isDowngrade(sel.plan)) {
            // Show confirmation modal instead of immediate request
            showConfirm.value = true;
            return;
          } else {
            const res = await fetchJson("/api/billing/checkout/init", {
              method: "POST",
              body: JSON.stringify({ plan: sel.plan, billingPeriod: sel.billing }),
            });
            try {
              // If payments are disabled server-side, the API will return
              // { paymentDisabled: true, contactEmail: ... }
              if (res && res.paymentDisabled) {
                const mail = res.contactEmail || "support@qrmatik.cloud";
                ui.toast("Online ödeme devre dışı. Ödeme talebi için: " + mail, "info");
                router.push({ path: "/billing/manual", query: { email: mail } });
                return;
              }

              sessionStorage.setItem("qm_checkout_content", res.checkoutFormContent || "");
              if (res && res.token) sessionStorage.setItem("qm_checkout_token", res.token || "");
            } catch (err) {
              /* ignore */
            }
            router.push("/billing/checkout");
          }
        } catch (e) {
          // toast already shown by fetchJson
          console.debug("init fail", e);
        } finally {
          loading.value = false;
        }
      }
      onMounted(loadCurrent);

      return {
        sel,
        current,
        prices,
        loading,
        selClass,
        goCheckout,
        planLabel,
        billingLabel,
        isDowngrade,
        showConfirm,
        closeConfirm,
        confirmDowngrade,
      };
    },
  };
</script>
