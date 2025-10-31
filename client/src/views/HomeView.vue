<template>
  <div>
  <section class="min-h-[60vh] flex items-center">
    <div class="container mx-auto px-6 py-20">
  <div :class="['grid gap-12 items-center', hasTenant ? 'md:grid-cols-2' : 'md:grid-cols-1']">
        <div>
          <h1 class="text-4xl sm:text-5xl font-bold text-gray-900 mb-4">
            QrMatik — Mobil Sipariş ve Yönetim
          </h1>
          <p class="text-gray-600 mb-6">
            QR ile menüye hızlı eriş, mobilden sipariş ver; mutfak ve bar ekibi anında bildirim
            alır. Hızlı, güvenilir ve mobil öncelikli.
          </p>
          <div class="flex gap-3">
            <!-- Menüyü Görüntüle: tek ise tam genişlik, ikili ise yarım genişlik -->
            <router-link
              v-if="hasTenant"
              to="/menu"
              :class="[
                'text-center px-3 sm:px-4 py-2 sm:py-3 text-sm sm:text-base bg-indigo-600 text-white rounded-md shadow hover:bg-indigo-700',
                showOrdersButton ? 'flex-1 min-w-0' : 'w-full'
              ]"
              >Menüyü Görüntüle</router-link
            >
            <!-- Siparişlerimi Gör: sadece aktif sipariş varsa göster -->
            <router-link
              v-if="showOrdersButton"
              :to="orderDetailLink"
              class="flex-1 min-w-0 text-center px-3 sm:px-4 py-2 sm:py-3 text-sm sm:text-base bg-yellow-500 text-white rounded-md shadow hover:bg-yellow-600"
              >Siparişlerimi Gör</router-link
            >
          </div>
          <div v-if="isAdmin && hasTenant" class="mt-3">
            <router-link
              to="/admin"
              class="block w-full text-center px-3 sm:px-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-200 rounded-md hover:bg-gray-50"
              >Admin Panel</router-link
            >
          </div>
          <div v-else-if="isSuperAdmin" class="mt-3">
            <router-link
              to="/super/tenants"
              class="block w-full text-center px-3 sm:px-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-200 rounded-md hover:bg-gray-50"
              >Admin</router-link
            >
          </div>
        </div>
        <div v-if="hasTenant" class="bg-white rounded-xl shadow p-6">
          <h3 class="font-semibold mb-4">Popüler Ürünler</h3>
          <div v-if="popular.length" class="grid grid-cols-2 gap-4">
            <div v-for="it in popular" :key="it.id || it.name" class="p-3 border rounded">
              <div class="font-medium truncate" :title="it.name">{{ it.name }}</div>
              <div class="text-sm text-gray-500">
                <span>{{ categoryLabel(it.category) }}</span>
                <span> · </span>
                <span>{{ formatMoney(it.price) }}</span>
              </div>
            </div>
          </div>
          <div v-else class="text-gray-500 text-sm">Popüler ürünler yükleniyor…</div>
        </div>
      </div>
    </div>
  </section>

  <FeatureGrid v-if="!hasTenant" />
  <HowItWorks v-if="!hasTenant" />
  <ScreenshotsGrid v-if="!hasTenant" />
  <FAQAccordion v-if="!hasTenant" />
  <PricingPlans v-if="!hasTenant" />
  <DemoContact v-if="!hasTenant" />
  </div>
</template>

<script>
  import { ref, onMounted, onBeforeUnmount, computed } from "vue";
  import { apiFetch, fetchJson } from "@/utils/api";
  import { useAuthStore } from "@/stores/authStore";
  import { formatMoney, categoryLabel } from "@/utils/format";
  import FeatureGrid from "@/components/FeatureGrid.vue";
  import HowItWorks from "@/components/HowItWorks.vue";
  import ScreenshotsGrid from "@/components/ScreenshotsGrid.vue";
  import FAQAccordion from "@/components/FAQAccordion.vue";
  import PricingPlans from "@/components/PricingPlans.vue";
  import DemoContact from "@/components/DemoContact.vue";

  export default {
    name: "HomeView",
  components: { FeatureGrid, HowItWorks, ScreenshotsGrid, FAQAccordion, PricingPlans, DemoContact },
    setup() {
  const orderSession = ref(null);
  const showMyOrders = ref(false);
  const sessionCheckDone = ref(false);
  let evalInFlight = false;
  let evalTimer = null;
      const auth = useAuthStore();
      const isAdmin = computed(() => auth.user && String(auth.user.role).toLowerCase() === "admin");
      const isSuperAdmin = computed(() => auth.user && String(auth.user.role).toLowerCase() === "superadmin");
      function detectTenantFromLocation() {
        try {
          const params = new URLSearchParams(window.location.search);
          const q = params.get("tenant") || params.get("t") || params.get("code");
          if (q) return q;
          const path = window.location.pathname || "";
          const parts = path.split("/");
          const idx = parts.indexOf("r");
          if (idx >= 0 && parts.length > idx + 1) return parts[idx + 1];
          const host = window.location.hostname || "";
          if (host.endsWith(".localhost")) {
            const dot = host.indexOf(".");
            if (dot > 0) return host.slice(0, dot);
          } else {
            const hostParts = host.split(".");
            if (hostParts.length > 2) return hostParts[0];
          }
        } catch { /* ignore */ }
        return null;
      }
      const hasTenant = computed(() => !!detectTenantFromLocation());
  const popular = ref([]);

      function getCookie(name) {
        try {
          const n = encodeURIComponent(name) + "=";
          const parts = (document.cookie || "").split("; ");
          for (const p of parts) {
            if (p.startsWith(n)) return decodeURIComponent(p.substring(n.length));
          }
          return null;
        } catch { return null; }
      }

      function readOrderSession() {
        try {
          let sid = localStorage.getItem("qm_order_session");
          if (!sid && typeof document !== "undefined") sid = getCookie("qm_order_session");
          orderSession.value = sid;
        } catch {
          orderSession.value = null;
        }
      }

      const lastOrderId = ref(null);
      function readLastOrder() {
        try {
          lastOrderId.value = localStorage.getItem("qm_last_order");
        } catch {
          lastOrderId.value = null;
        }
      }

      const orderDetailLink = computed(() => {
        if (orderSession.value)
          return { name: "my-orders", params: { sessionId: orderSession.value } };
        return { path: "/menu" };
      });

      // İkili buton düzeni gösterilecek mi?
      const showOrdersButton = computed(() =>
        !!hasTenant.value && !!sessionCheckDone.value && !!showMyOrders.value,
      );

      // Yerel heuristikleri kaldırıyoruz; yalnızca sunucu doğrulamasından sonra gösterilecek

      async function evaluateSessionActivity() {
        if (evalInFlight) return;
        evalInFlight = true;
        try {
          // check session existence
          if (!orderSession.value) { showMyOrders.value = false; return; }
          // check expiry from localStorage
          try {
            const exp = localStorage.getItem("qm_order_session_expires");
            if (!exp) return;
            const now = Date.now();
            const expTs = Date.parse(exp);
            if (!Number.isFinite(expTs) || expTs <= now) {
              // expired -> clear locally
              try {
                localStorage.removeItem("qm_order_session");
                localStorage.removeItem("qm_order_session_expires");
              } catch {
                /* ignore */
              }
              orderSession.value = null;
              showMyOrders.value = false;
              return;
            }
          } catch {
            /* ignore */
          }
          // fetch session orders to check active ones
          try {
            const res = await apiFetch(
              "/api/orders/session/" + encodeURIComponent(orderSession.value),
            );
            if (res.status === 410) {
              // expired on server -> clear
              try {
                localStorage.removeItem("qm_order_session");
                localStorage.removeItem("qm_order_session_expires");
              } catch {
                /* ignore */
              }
              orderSession.value = null;
              showMyOrders.value = false;
              return;
            }
            if (!res.ok) {
              showMyOrders.value = false;
              return;
            }
            const data = await res.json();
            const hasActive =
              Array.isArray(data) &&
              data.some((o) => String(o.status || "").toLowerCase() !== "payment_completed");
            showMyOrders.value = !!hasActive;
          } catch {
            showMyOrders.value = false;
          }
        } finally {
          sessionCheckDone.value = true;
          evalInFlight = false;
        }
      }

      function scheduleEvaluate() {
        try { if (evalTimer) clearTimeout(evalTimer); } catch { /* ignore */ }
        evalTimer = setTimeout(() => {
          evaluateSessionActivity();
        }, 80);
      }

      function onOrderSession(ev) {
        try {
          const sid = ev && ev.detail && ev.detail.sessionId;
          if (sid) orderSession.value = sid;
          const oid = ev && ev.detail && ev.detail.orderId;
          if (oid) {
            try {
              localStorage.setItem("qm_last_order", String(oid));
            } catch {
              /* ignore */
            }
          }
        } catch {
          /* ignore */
        }
        // event sonrası depodan en güncel değerleri oku
        readOrderSession();
        readLastOrder();
  // Doğrudan sunucu doğrulamasına bırak (UI'ı tekrar gizlemeyelim)
  scheduleEvaluate();
      }

      function onStorage(ev) {
        if (!ev) return;
        if (ev.key === "qm_order_session") {
          orderSession.value = ev.newValue;
          // initial render tamamlandıktan sonra, tekrar gizlemeye gerek yok
          scheduleEvaluate();
        }
        if (ev.key === "qm_last_order") {
          readLastOrder();
        }
      }

      onMounted(() => {
  readOrderSession();
  readLastOrder();
  // Sunucu doğrulaması tamamlanana kadar butonu gizli tut
  showMyOrders.value = false;
  scheduleEvaluate();
        // Load popular menu items (top by sales)
        fetchJson("/api/menu/popular?limit=4")
          .then((items) => {
            try {
              const arr = Array.isArray(items) ? items : [];
              popular.value = arr.slice(0, 4);
            } catch {
              popular.value = [];
            }
          })
          .catch(() => {
            popular.value = [];
          });
        try {
          window.addEventListener("qm:orderSession", onOrderSession);
        } catch {
          /* ignore */
        }
        try {
          window.addEventListener("storage", onStorage);
        } catch {
          /* ignore */
        }
      });

      onBeforeUnmount(() => {
        try {
          window.removeEventListener("qm:orderSession", onOrderSession);
        } catch {
          /* ignore */
        }
        try {
          window.removeEventListener("storage", onStorage);
        } catch {
          /* ignore */
        }
      });

  return { orderSession, isAdmin, isSuperAdmin, hasTenant, orderDetailLink, showMyOrders, sessionCheckDone, showOrdersButton, popular, formatMoney, categoryLabel };
    },
  };
</script>
