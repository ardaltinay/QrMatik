<template>
  <div>
    <section
      :class="[
        'flex min-h-[60vh] items-center',
        !hasTenant ? 'bg-gradient-to-b from-indigo-50/40 to-white' : '',
      ]"
    >
      <div class="container mx-auto px-6 py-20">
        <div :class="['grid items-center gap-12', hasTenant ? 'md:grid-cols-2' : 'md:grid-cols-1']">
          <div>
            <div class="mb-2 flex items-center gap-3" v-if="tenantLogo && hasTenant">
              <img :src="tenantLogo" alt="Logo" class="h-10 w-10 rounded" />
              <div class="text-sm text-gray-600">{{ tenantName || "Restoran" }}</div>
            </div>
            <h1 class="mb-3 text-4xl font-bold text-gray-900 sm:text-5xl">{{ heroTitle }}</h1>
            <p class="mb-4 text-gray-600">
              Müşteriniz menüyü telefonundan inceler, birkaç dokunuşla sipariş verir; mutfak ve bar
              anında haberdar olur. Daha hızlı servis, daha az bekleme ve hatasız akış için
              tasarlandı.
            </p>
            <ul class="mb-4 space-y-1 text-sm text-gray-600">
              <li class="flex items-center gap-2">
                <span class="inline-block h-1.5 w-1.5 rounded-full bg-brand-500"></span> Dijital
                menü ve QR sipariş ile bekleme süresini kısaltın
              </li>
              <li class="flex items-center gap-2">
                <span class="inline-block h-1.5 w-1.5 rounded-full bg-brand-500"></span> Mutfak &
                Bar panolarıyla iş akışını hızlandırın
              </li>
              <li class="flex items-center gap-2">
                <span class="inline-block h-1.5 w-1.5 rounded-full bg-brand-500"></span> Masaya özel
                karekod ile siparişleri doğru masaya yönlendirin
              </li>
              <li v-if="!hasTenant" class="flex items-center gap-2">
                <span class="inline-block h-1.5 w-1.5 rounded-full bg-brand-500"></span> Kolay menü
                yönetimi: Fiyat ve içerik değişiklikleri anında yayında
              </li>
              <li v-if="!hasTenant" class="flex items-center gap-2">
                <span class="inline-block h-1.5 w-1.5 rounded-full bg-brand-500"></span> Stok
                yönetimi: Gelişmiş stok kontrolü ile tükenen ürünleri otomatik gizleyin
              </li>
              <li v-if="!hasTenant" class="flex items-center gap-2">
                <span class="inline-block h-1.5 w-1.5 rounded-full bg-brand-500"></span> Daha az
                hata: Elle aktarım yok; mutfak & bar panoları net
              </li>
            </ul>
            <!-- Apex: küçük rozetler -->
            <div v-if="!hasTenant" class="mb-6 flex flex-wrap items-center gap-2">
              <span class="rounded-full border bg-white px-2.5 py-1 text-xs shadow-sm"
                >Temassız</span
              >
              <span class="rounded-full border bg-white px-2.5 py-1 text-xs shadow-sm"
                >Hızlı Kurulum</span
              >
              <span class="rounded-full border bg-white px-2.5 py-1 text-xs shadow-sm"
                >Mutfak & Bar Panoları</span
              >
              <span class="rounded-full border bg-white px-2.5 py-1 text-xs shadow-sm"
                >Stok Kontrolü</span
              >
              <span class="rounded-full border bg-white px-2.5 py-1 text-xs shadow-sm"
                >Çoklu İşletme Desteği</span
              >
            </div>
            <!-- Tenant görünümleri için butonlar: mobilde dikey, sm+ yatay -->
            <div v-if="hasTenant" class="flex flex-col gap-3 sm:flex-row sm:items-stretch">
              <!-- Menüyü Görüntüle: tek ise tam genişlik, ikili ise yarım genişlik -->
              <router-link
                to="/menu"
                :class="[
                  'w-full rounded-md bg-indigo-600 px-3 py-2 text-center text-sm text-white shadow hover:bg-indigo-700 sm:min-w-0 sm:px-4 sm:py-3 sm:text-base',
                  hasSecondaryButtons ? 'sm:flex-1' : '',
                ]"
                >Menüyü Görüntüle</router-link
              >
              <!-- Siparişlerimi Gör: sadece aktif sipariş varsa göster -->
              <router-link
                v-if="showOrdersButton"
                :to="orderDetailLink"
                class="w-full rounded-md bg-yellow-500 px-3 py-2 text-center text-sm text-white shadow hover:bg-yellow-600 sm:min-w-0 sm:flex-1 sm:px-4 sm:py-3 sm:text-base"
                >Siparişlerimi Gör</router-link
              >
              <router-link
                v-if="isAdmin || isSuperAdmin"
                to="/admin"
                class="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-center text-sm text-gray-800 shadow hover:bg-gray-50 sm:min-w-0 sm:flex-1 sm:px-4 sm:py-3 sm:text-base"
                >Admin Panel</router-link
              >
            </div>

            <!-- Apex (tenant yok) için iki CTA: md+ yatay, mobilde dikey, benzer tasarım -->
            <div v-else class="flex flex-col gap-3 sm:flex-row">
              <!-- Yeni işletme kaydı: üstte (mobil), birincil (gradient) -->
              <router-link
                to="/signup/tenant"
                class="bg-brand-gradient inline-flex items-center justify-center rounded-md px-4 py-2 text-sm text-white shadow hover:opacity-95 sm:text-base"
                >Yeni işletme kaydı oluştur</router-link
              >
              <!-- Keşfet: ikincil (beyaz/border) -->
              <router-link
                to="/qr-menu"
                class="inline-flex items-center justify-center rounded-md border bg-white px-4 py-2 text-sm text-gray-800 shadow hover:bg-gray-50 sm:text-base"
                >Dijital QR menüyü keşfedin</router-link
              >
            </div>
            <div v-if="isSuperAdmin && !hasTenant" class="mt-3">
              <router-link
                to="/super/tenants"
                class="block w-full rounded-md border border-gray-200 px-3 py-2 text-center text-sm hover:bg-gray-50 sm:px-4 sm:py-3 sm:text-base"
                >Tenant Yönetimi</router-link
              >
            </div>
          </div>
          <div v-if="hasTenant && isPaidPlan" class="rounded-xl bg-white p-6 shadow">
            <h3 class="mb-4 font-semibold">Popüler Ürünler</h3>
            <div v-if="popular.length" class="grid grid-cols-2 gap-4">
              <div v-for="it in popular" :key="it.id || it.name" class="rounded border p-3">
                <div class="truncate font-medium" :title="it.name">{{ it.name }}</div>
                <div class="text-sm text-gray-500">
                  <span>{{ categoryLabel(it.category) }}</span>
                  <span> · </span>
                  <span>{{ formatMoney(it.price) }}</span>
                </div>
              </div>
            </div>
            <div v-else class="text-sm text-gray-500">Popüler ürünler yükleniyor…</div>
          </div>
        </div>
      </div>
    </section>

    <FeatureGrid v-if="!hasTenant" />
    <HowItWorks v-if="!hasTenant" />
    <ScreenshotsGrid v-if="!hasTenant" />

    <FAQAccordion v-if="!hasTenant" />
    <PricingPlans v-if="!hasTenant" />
    <ContactUs v-if="!hasTenant" />
    <!-- Sticky CTA (mobile only, apex) -->
    <div
      v-if="!hasTenant"
      class="fixed inset-x-0 bottom-0 z-50 border-t bg-white/90 backdrop-blur md:hidden"
    >
      <div class="container mx-auto flex items-center gap-2 px-4 py-3">
        <router-link
          to="/qr-menu"
          class="bg-brand-gradient inline-flex flex-1 items-center justify-center rounded-md px-4 py-2 text-sm text-white shadow hover:opacity-95"
          >Dijital QR menüyü keşfedin</router-link
        >
        <a href="#demo" class="rounded-md border bg-white px-4 py-2 text-gray-800 hover:bg-gray-50"
          >Demo</a
        >
      </div>
    </div>
    <!-- Spacer to avoid overlap with sticky bar -->
    <div v-if="!hasTenant" class="h-16 md:hidden"></div>
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
  import ContactUs from "@/components/ContactUs.vue";
  import { useHead } from "@unhead/vue";

  export default {
    name: "HomeView",
    components: {
      FeatureGrid,
      HowItWorks,
      ScreenshotsGrid,
      FAQAccordion,
      PricingPlans,
      ContactUs,
    },
    setup() {
      const orderSession = ref(null);
      const tenantLogo = ref(null);
      const tenantName = ref(null);
      const heroTitle = ref("QrMatik — Mobil Sipariş ve Yönetim");
      const showMyOrders = ref(false);
      const sessionCheckDone = ref(false);
      let evalInFlight = false;
      let evalTimer = null;
      const auth = useAuthStore();
      const isAdmin = computed(() => auth.user && String(auth.user.role).toLowerCase() === "admin");
      const isSuperAdmin = computed(
        () => auth.user && String(auth.user.role).toLowerCase() === "superadmin",
      );
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
        } catch {
          /* ignore */
        }
        return null;
      }
      const hasTenant = computed(() => !!detectTenantFromLocation());
      const popular = ref([]);
      const isPaidPlan = computed(() => {
        try {
          const raw = localStorage.getItem("qm_tenant_cfg");
          if (!raw) return false; // conservative: hide on unknown
          const cfg = JSON.parse(raw);
          const plan = String(cfg?.plan || "").toUpperCase();
          return plan && plan !== "FREE";
        } catch {
          return false;
        }
      });

      function getCookie(name) {
        try {
          const n = encodeURIComponent(name) + "=";
          const parts = (document.cookie || "").split("; ");
          for (const p of parts) {
            if (p.startsWith(n)) return decodeURIComponent(p.substring(n.length));
          }
          return null;
        } catch {
          return null;
        }
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
      const showOrdersButton = computed(
        () => !!hasTenant.value && !!sessionCheckDone.value && !!showMyOrders.value,
      );

      // En az iki buton varsa (Siparişlerim veya Admin), birincil buton da esnek genişlik almalı
      const hasSecondaryButtons = computed(
        () => !!showOrdersButton.value || !!isAdmin.value || !!isSuperAdmin.value,
      );

      // Yerel heuristikleri kaldırıyoruz; yalnızca sunucu doğrulamasından sonra gösterilecek

      async function evaluateSessionActivity() {
        if (evalInFlight) return;
        evalInFlight = true;
        try {
          // check session existence
          if (!orderSession.value) {
            showMyOrders.value = false;
            return;
          }
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
        try {
          if (evalTimer) clearTimeout(evalTimer);
        } catch {
          /* ignore */
        }
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
        // Add noindex for tenant-context pages to avoid indexing customer/tenant UIs
        if (hasTenant.value) {
          try {
            useHead({
              meta: [{ name: "robots", content: "noindex, nofollow" }],
            });
          } catch {
            /* ignore */
          }
        }
        // Global marketing (apex) head tags for homepage when no tenant context is detected
        if (!hasTenant.value) {
          const baseUrl = typeof window !== "undefined" ? window.location.origin : "https://qrmatik.cloud";
          const canonical = baseUrl + "/";
          const title = "QR Restoran Dijital Menü ve QR Sipariş Sistemi | QrMatik";
          const description =
            "QrMatik ile dijital menü (akıllı QR menü), QR sipariş ve restoran karekod sistemi: masa bazlı takip, mutfak & bar panoları, stok kontrolü.";
          const keywords =
            "qr restoran, dijital menü, qr menü, restoran bar menü, karekod menü, qr sipariş, akıllı qr menü";
          useHead({
            title,
            meta: [
              { name: "description", content: description },
              { name: "keywords", content: keywords },
              { property: "og:title", content: title },
              { property: "og:description", content: description },
              { property: "og:type", content: "website" },
              { property: "og:url", content: canonical },
              { property: "og:site_name", content: "QrMatik" },
              { name: "twitter:card", content: "summary_large_image" },
              { name: "twitter:title", content: title },
              { name: "twitter:description", content: description },
              // Primary image (can be replaced by dynamic CDN later)
              { property: "og:image", content: baseUrl + "/og-image.svg" },
              { name: "twitter:image", content: baseUrl + "/og-image.svg" },
              // Indexing directives
              { name: "robots", content: "index,follow" },
              // Language meta (helpful for multi-lingual expansion later)
              { name: "language", content: "tr" },
            ],
            link: [
              { rel: "canonical", href: canonical },
              { rel: "alternate", href: canonical, hreflang: "tr" },
            ],
            script: [
              {
                type: "application/ld+json",
                children: JSON.stringify({
                  "@context": "https://schema.org",
                  "@type": "WebApplication",
                  name: "QrMatik",
                  applicationCategory: "BusinessApplication",
                  operatingSystem: "Web",
                  url: canonical,
                  description,
                  keywords,
                  offers: {
                    "@type": "Offer",
                    priceCurrency: "TRY",
                    price: "249",
                    category: "STANDARD",
                    availability: "https://schema.org/InStock",
                  },
                  creator: {
                    "@type": "Organization",
                    name: "QrMatik",
                    url: canonical,
                  },
                }),
              },
            ],
          });
        }
        // Apex-only structured data and richer OG
        if (!hasTenant.value) {
          try {
            const org = {
              "@context": "https://schema.org",
              "@type": "Organization",
              name: "QrMatik",
              url: typeof window !== "undefined" ? window.location.origin : "https://qrmatik.cloud",
              logo: "/favicon.svg",
              description:
                "QR ile menüye hızlı eriş, mobilden sipariş ver; mutfak ve bar ekibi anında bildirim alır.",
            };
            useHead({
              script: [{ type: "application/ld+json", children: JSON.stringify(org) }],
              meta: [
                { property: "og:image", content: "/og-image.svg" },
                { property: "og:image:width", content: "1200" },
                { property: "og:image:height", content: "630" },
                { name: "twitter:image", content: "/og-image.svg" },
              ],
            });
          } catch {
            /* ignore */
          }
        }
        // tenant gösterimi için yerel yapılandırmayı oku
        try {
          const raw = localStorage.getItem("qm_tenant_cfg");
          if (raw) {
            const cfg = JSON.parse(raw);
            const name = cfg?.displayName || cfg?.name || cfg?.title;
            if (name && hasTenant.value) heroTitle.value = name + " — Mobil Sipariş";
            if (name && hasTenant.value) tenantName.value = name;
            // Logo gösterimi: yalnızca ücretli planlarda etkin
            const plan = String(cfg?.plan || "").toUpperCase();
            const paid = plan && plan !== "FREE";
            const logo = cfg?.logoUrl || cfg?.logo;
            if (paid && logo) tenantLogo.value = logo;
          }
        } catch {
          /* ignore */
        }
        readOrderSession();
        readLastOrder();
        // Sunucu doğrulaması tamamlanana kadar butonu gizli tut
        showMyOrders.value = false;
        scheduleEvaluate();
        // Load popular menu items (only when a tenant is detected AND on paid plans)
        if (hasTenant.value && isPaidPlan.value) {
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
        }
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

      return {
        orderSession,
        tenantLogo,
        tenantName,
        heroTitle,
        isAdmin,
        isSuperAdmin,
        hasTenant,
        orderDetailLink,
        showMyOrders,
        sessionCheckDone,
        showOrdersButton,
        hasSecondaryButtons,
        popular,
        isPaidPlan,
        formatMoney,
        categoryLabel,
      };
    },
  };
</script>
