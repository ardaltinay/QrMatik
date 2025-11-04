<template>
  <div class="max-w-3xl mx-auto p-4">
    <h1 class="text-2xl font-semibold mb-4">Sipariş Takibi</h1>

    <div v-if="order" class="space-y-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="flex items-start justify-between">
          <div>
            <div class="text-sm text-gray-500">Sipariş No</div>
            <div class="text-xl font-medium">#{{ orderCodeFromId(order.id) }}</div>
            <div class="text-sm text-gray-500">{{ formatDate(order.createdAt) }}</div>
          </div>
          <div class="text-right">
            <div class="text-sm text-gray-500">Masa / Yer</div>
            <div class="font-medium">{{ order.table || "Misafir" }}</div>
          </div>
        </div>

        <div class="mt-4" v-if="showEta">
          <div class="text-sm text-gray-600 mb-2">Tahmini Süre</div>
          <div class="text-lg font-semibold">{{ etaText }}</div>
        </div>

        <!-- stepper: two-column grid on mobile, horizontal on md+ -->
        <div class="mt-4">
          <ol class="grid grid-cols-2 md:flex md:items-center md:space-x-4 gap-3">
            <li v-for="(s, idx) in steps" :key="s.value" class="flex items-center gap-3">
              <div
                :class="[
                  idx <= currentIdx ? 'bg-brand-500 text-white' : 'bg-gray-100 text-gray-600',
                  'flex items-center justify-center rounded-full',
                ]"
                :style="currentIdx >= idx ? mobileCircleFilledStyle : mobileCircleStyle"
              >
                <span
                  class="text-sm"
                  v-if="idx < currentIdx || (currentIdx === steps.length - 1 && idx === currentIdx)"
                  >✓</span
                >
                <span class="text-sm" v-else>{{ idx + 1 }}</span>
              </div>
              <div class="text-sm" :class="idx <= currentIdx ? 'text-gray-800' : 'text-gray-500'">
                {{ s.label }}
              </div>
            </li>
          </ol>
        </div>
      </div>

      <div class="bg-white p-4 rounded-lg shadow">
        <h3 class="font-semibold mb-2">Sipariş İçeriği</h3>
        <ul class="space-y-2">
          <li v-for="it in order.items" :key="it.itemId" class="flex justify-between">
            <div>{{ it.name || menuItemName(it.itemId) }}</div>
            <div class="text-gray-600">x{{ it.qty }}</div>
          </li>
        </ul>
        <div class="mt-4 flex items-center justify-between border-t pt-3">
          <div class="text-sm text-gray-600">Toplam</div>
          <div class="text-lg font-semibold">
            {{ formatMoney(order.total) }}
          </div>
        </div>
      </div>

      <!-- Expired banner -->
      <div v-if="isExpired" class="mt-4 p-3 rounded border bg-yellow-50 text-yellow-900">
        Oturum süreniz doldu. Siparişi görüntüleyebilirsiniz ancak işlem yapamazsınız.
      </div>

      <!-- Payment action appears after served, hidden if already paid or expired -->
      <div
        v-if="order && order.status === 'served' && !isExpired"
        class="mt-4 p-3 rounded border bg-blue-50 text-blue-900"
      >
        <div class="mb-2">
          Siparişiniz servis edildi. İsterseniz ödemenizi burdan yapabilirsiniz.
        </div>
        <button
          @click="completePayment"
          class="px-3 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Ödemeyi Yap
        </button>
      </div>
      <div
        v-else-if="order && order.status === 'payment_completed'"
        class="mt-4 p-3 rounded border bg-green-50 text-green-800"
      >
        Ödeme tamamlandı. Afiyet olsun!
      </div>
      <div
        v-else-if="order && order.status === 'canceled'"
        class="mt-4 p-3 rounded border bg-red-50 text-red-800"
      >
        Siparişiniz iptal edilmiştir!
      </div>

      <div
        class="bg-white p-4 rounded-lg shadow flex gap-4 flex-col md:flex-row items-start md:items-center"
      >
        <div class="md:w-48">
          <img :src="qrUrl" alt="QR" class="w-40 h-40 bg-gray-50 rounded" />
        </div>
        <div class="flex-1">
          <div class="mb-2">
            <div class="text-sm text-gray-500">Paylaşılabilir Link</div>
            <div class="text-sm break-all">{{ shareLink }}</div>
          </div>

          <div class="flex flex-col gap-2">
            <div class="flex gap-2">
              <button @click="copyLink" class="flex-1 px-3 py-2 bg-brand-500 text-white rounded">
                Linki Kopyala
              </button>
              <button @click="openQR" class="flex-1 px-3 py-2 border rounded">QR Aç</button>
            </div>
            <div class="flex flex-col md:flex-row md:gap-2">
              <button @click="sendEmail" class="flex-1 px-3 py-2 border rounded mb-2 md:mb-0">
                E-posta ile Gönder
              </button>
              <button @click="sendSMS" class="flex-1 px-3 py-2 border rounded">
                SMS ile Gönder
              </button>
            </div>
          </div>
          <div v-if="copied" class="text-sm text-green-600 mt-2">Link panoya kopyalandı.</div>
          <div class="text-sm text-gray-500 mt-2">
            Not: E-posta/SMS göndermek için gerçek sunucu entegrasyonu gerekir; butonlar varsayılan
            uygulamayı açar.
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center text-gray-600">Aktif sipariş bulunamadı.</div>
  </div>
</template>

<script>
  import { useOrderStore } from "@/stores/orderStore";
  import { computed, ref, onMounted } from "vue";
  import { formatDate, orderCodeFromId, formatMoney } from "@/utils/format";
  import { fetchJson } from "@/utils/api";
  import { useRoute } from "vue-router";
  import { useUiStore } from "@/stores/uiStore";

  export default {
    name: "OrderStatusView",
    setup() {
      const store = useOrderStore();
      const ui = useUiStore();
      const route = useRoute();
      const id = String(route.params.id);
      const orderFromStore = computed(() => store.getOrderById(id));
      const loadedOrder = ref(null);
      const order = computed(() => orderFromStore.value || loadedOrder.value);

      // stepper and ETA
      const steps = [
        { value: "new", label: "Yeni" },
        { value: "preparing", label: "Hazırlanıyor" },
        { value: "ready", label: "Hazır" },
        { value: "served", label: "Servis Edildi" },
      ];

      const currentIdx = computed(() => {
        if (!order.value) return 0;
        const st = order.value.status;
        const idx = steps.findIndex((s) => s.value === st);
        if (idx >= 0) return idx;
        // payment_completed gibi adımlar stepper dışında; hepsini tamamlanmış göster
        if (st === "payment_completed") return steps.length - 1;
        if (st === "canceled") return -1; // iptal için hiçbir adım tamamlanmış görünmesin
        return 0;
      });

      // naive ETA calculation (example heuristics)
      const etaText = computed(() => {
        if (!order.value) return "-";
        const baseMinutes = 5; // baseline per new order
        const status = order.value.status;
        if (status === "new") return `${baseMinutes} - ${baseMinutes + 5} dk içinde`;
        if (status === "preparing")
          return `${Math.max(1, baseMinutes - 2)} - ${baseMinutes + 2} dk içinde`;
        if (status === "ready") return "Çok yakında (paspas hazır)";
        if (status === "payment_completed" || status === "canceled") return "";
        return "";
      });

      const showEta = computed(() => {
        if (!order.value) return false;
        const s = order.value.status;
        return s !== "served" && s !== "payment_completed" && s !== "canceled";
      });

      const isExpired = computed(() => {
        try {
          const exp = localStorage.getItem("qm_order_session_expires");
          if (!exp) return false;
          const t = new Date(exp).getTime();
          if (isNaN(t)) return false;
          return Date.now() > t;
        } catch {
          return false;
        }
      });

      function menuItemName(id) {
        const i = store.menu.find((m) => m.id === id);
        return i ? i.name : "Unknown";
      }

      // formatDate utils'ten kullanılır

      // share / QR
      // Include session id for privacy-aware deep link
      let sid = null;
      try {
        sid = localStorage.getItem("qm_order_session") || null;
        if (!sid && typeof document !== "undefined") {
          const n = encodeURIComponent("qm_order_session") + "=";
          const parts = (document.cookie || "").split("; ");
          for (const p of parts) {
            if (p.startsWith(n)) { sid = decodeURIComponent(p.substring(n.length)); break; }
          }
        }
      } catch (e) {
        /* ignore */
      }
      const shareLink = window.location.origin + "/order/" + id + (sid ? ("?sid=" + encodeURIComponent(sid)) : "");
      // Generate QR via backend endpoint (proxied by Vite)
      const qrUrl = "/api/qr/image?text=" + encodeURIComponent(shareLink) + "&size=300";

      const copied = ref(false);
      function copyLink() {
        try {
          navigator.clipboard.writeText(shareLink);
          copied.value = true;
          setTimeout(() => (copied.value = false), 2500);
        } catch (e) {
          console.debug("copy failed", e);
        }
      }

      function openQR() {
        window.open(qrUrl, "_blank");
      }

      // shallow mailto / sms fallback (real send needs server)
      function sendEmail() {
        const subject = encodeURIComponent("Sipariş Takip: #" + id);
        const body = encodeURIComponent("Siparişinizi takip etmek için link: " + shareLink);
        window.location.href = `mailto:?subject=${subject}&body=${body}`;
      }

      function sendSMS() {
        // sms: url scheme varies by platform; this opens default SMS app on mobile
        const body = encodeURIComponent("Siparişinizi takip et: " + shareLink);
        window.location.href = `sms:?body=${body}`;
      }

      // styles used for responsive circle sizes (inline style objects)
      const mobileCircleStyle = { width: "36px", height: "36px", fontSize: "14px" };
      const mobileCircleFilledStyle = { width: "36px", height: "36px", fontSize: "14px" };

      // Toplam tutarı yalnızca sunucu tarafından döndürülen order.total üzerinden gösteriyoruz

      async function loadOrderIfMissing() {
        if (orderFromStore.value) return;
        try {
          // Append sid if available to satisfy backend guard for unauthenticated access
          let q = "";
          try {
            const urlSid = new URLSearchParams(window.location.search).get("sid");
            sid = urlSid || sid;
          } catch (e) {
            /* ignore */
          }
          if (sid) q = "?sid=" + encodeURIComponent(sid);
          const o = await fetchJson("/api/orders/" + encodeURIComponent(id) + q);
          loadedOrder.value = {
            id: o.id,
            table: o.tableCode || o.table || "guest",
            items: (Array.isArray(o.lines) ? o.lines : []).map((l) => ({
              itemId: l.itemId,
              qty: l.quantity,
              name: l.name,
              price: l.price,
            })),
            status: (o.status || "").toLowerCase(),
            createdAt: o.createdAt || o.createdTime,
            total: typeof o.total === "number" ? o.total : Number(o.total || 0),
          };
        } catch (e) {
          /* zaten boş gösterilecek */
        }
      }

      async function completePayment() {
        if (!order.value) return;
        try {
          await store.updateOrderStatus(order.value.id, "payment_completed");
          if (loadedOrder.value && String(loadedOrder.value.id) === String(order.value.id)) {
            loadedOrder.value = { ...loadedOrder.value, status: "payment_completed" };
          }
          ui.toastSuccess("Ödeme tamamlandı");
        } catch {
          // hata toast'u fetchJson tarafından gösterilecek
        }
      }

      onMounted(() => {
        // Menü yüklemesini istemli olarak kaldırdık; öğe adları artık API'nin lines snapshot'ından gelir.
        // Eski siparişlerde name yoksa ve menü gerekirse, aşağıdaki satırı açabilirsin:
        // if (!store.menuLoaded) { void store.loadMenu() }
  void loadOrderIfMissing();
        try {
          window.addEventListener("qm:orderSessionExpired", () => {
            // localStorage expiry kontrolü ile birlikte banner tetiklenecek
          });
        } catch {
          /* ignore */
        }
      });

      return {
        order,
        menuItemName,
        steps,
        currentIdx,
        etaText,
        showEta,
        isExpired,
        formatDate,
        orderCodeFromId,
        formatMoney,
        shareLink,
        qrUrl,
        copyLink,
        openQR,
        sendEmail,
        sendSMS,
        copied,
        mobileCircleStyle,
        mobileCircleFilledStyle,
        completePayment,
      };
    },
  };
</script>
