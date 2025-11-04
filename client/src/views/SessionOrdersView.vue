<template>
  <div class="mx-auto max-w-3xl p-4">
    <h1 class="mb-4 text-2xl font-semibold">Siparişlerim</h1>

    <!-- Masa bilgisi -->
    <div
      v-if="orders.length && tableInfo"
      class="mb-4 rounded-lg border bg-gray-50 p-3 text-gray-700"
    >
      <span class="font-medium">Masa:</span>
      <span>{{ tableInfo }}</span>
    </div>

    <div v-if="orders.length">
      <div
        v-for="o in orders"
        :key="o.id"
        class="mb-4 cursor-pointer rounded-lg bg-white p-4 shadow transition hover:shadow-md"
        @click="goDetail(o)"
      >
        <div class="flex justify-between">
          <div>
            <div class="text-sm text-gray-500">Sipariş No</div>
            <div class="text-xl font-medium">#{{ orderCodeFromId(o.id) }}</div>
            <div class="text-sm text-gray-500">{{ formatDate(o.createdAt) }}</div>
          </div>
          <div class="text-right">
            <div class="text-sm text-gray-500">Durum</div>
            <div class="mt-1">
              <span
                class="rounded-full px-2.5 py-1 text-xs font-medium"
                :class="statusBadge(o.status)"
              >
                {{ statusLabel(o.status) }}
              </span>
            </div>
            <div class="mt-3 text-sm text-gray-500">Toplam</div>
            <div class="font-medium">{{ formatMoney(o.total) }}</div>
          </div>
        </div>

        <!-- İptal butonu ve geri sayım: 2 dk içinde aktif, sonrası deaktif -->
        <div class="mt-3" v-if="isCancelableStatus(o)">
          <div class="flex items-center gap-3">
            <button
              class="rounded-md border border-red-200 bg-red-50 px-3 py-1.5 text-sm text-red-700 hover:bg-red-100 disabled:cursor-not-allowed disabled:opacity-50"
              :disabled="!canCancel(o)"
              @click.stop="cancelOrder(o)"
            >
              Siparişi İptal Et
            </button>
            <span class="text-xs text-gray-600" v-if="canCancel(o)">
              Kalan süre: {{ formatRemaining(o) }}
            </span>
            <span class="text-xs text-gray-500" v-else> Süre doldu </span>
          </div>
        </div>

        <div class="mt-4">
          <h4 class="mb-2 font-semibold">İçerik</h4>
          <ul class="space-y-1">
            <li v-for="it in o.items" :key="it.itemId" class="flex justify-between">
              <div>
                <div>{{ it.name || menuItemName(it.itemId) }}</div>
                <div v-if="it.note" class="mt-0.5 text-xs text-gray-600">Not: {{ it.note }}</div>
              </div>
              <div class="text-gray-600">x{{ it.qty }}</div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <div v-else class="text-center text-gray-600">Bu oturuma ait sipariş bulunamadı.</div>
  </div>
</template>

<script setup>
  import { ref, onMounted, onBeforeUnmount, watch, computed } from "vue";
  import { formatDate, orderCodeFromId, formatMoney, statusLabel } from "@/utils/format";
  import { useRoute, useRouter } from "vue-router";
  import { useOrderStore } from "@/stores/orderStore";
  import { useUiStore } from "@/stores/uiStore";

  const route = useRoute();
  const router = useRouter();
  const store = useOrderStore();
  const ui = useUiStore();

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

  const sessionId = ref(
    route.params.sessionId ||
      (typeof localStorage !== "undefined" ? localStorage.getItem("qm_order_session") : null) ||
      (typeof document !== "undefined" ? getCookie("qm_order_session") : null),
  );
  const orders = ref([]);

  const tableInfo = computed(() => {
    try {
      const codes = Array.from(new Set((orders.value || []).map((o) => o.table).filter(Boolean)));
      if (codes.length === 0) return "";
      if (codes.length === 1) return codes[0];
      return codes.join(", ");
    } catch {
      return "";
    }
  });

  function menuItemName(id) {
    const i = store.menu.find((m) => m.id === id);
    return i ? i.name : "Unknown";
  }
  // Toplam hesaplamasını yalnızca sunucu dönen toplam üzerinden gösteriyoruz
  function goDetail(o) {
    if (!o || !o.id) return;
    try {
      store.ordersById[String(o.id)] = o;
    } catch {
      /* ignore */
    }
    router.push({ name: "order-status", params: { id: String(o.id) } });
  }

  function canCancel(o) {
    try {
      const status = String(o.status || "").toLowerCase();
      if (status === "payment_completed" || status === "canceled") return false;
      const created = new Date(o.createdAt);
      if (isNaN(created.getTime())) return false;
      const diffMs = Date.now() - created.getTime();
      return diffMs <= 2 * 60 * 1000; // 2 dakika
    } catch {
      return false;
    }
  }

  function isCancelableStatus(o) {
    try {
      const status = String(o.status || "").toLowerCase();
      return status === "new";
    } catch {
      return false;
    }
  }

  const now = ref(Date.now());
  let countdownInterval = null;

  function remainingMs(o) {
    try {
      const created = new Date(o.createdAt);
      if (isNaN(created.getTime())) return 0;
      const elapsed = now.value - created.getTime();
      const left = 2 * 60 * 1000 - elapsed; // 2 dakika
      return Math.max(0, left);
    } catch {
      return 0;
    }
  }

  function formatRemaining(o) {
    const ms = remainingMs(o);
    const totalSec = Math.ceil(ms / 1000);
    const mm = String(Math.floor(totalSec / 60)).padStart(2, "0");
    const ss = String(totalSec % 60).padStart(2, "0");
    return `${mm}:${ss}`;
  }

  function statusBadge(status) {
    try {
      const s = String(status || "").toLowerCase();
      if (s === "new") return "bg-yellow-100 text-yellow-800";
      if (s === "preparing") return "bg-indigo-100 text-indigo-800";
      if (s === "ready") return "bg-green-100 text-green-800";
      if (s === "served") return "bg-gray-200 text-gray-800";
      if (s === "payment_completed") return "bg-blue-100 text-blue-800";
      if (s === "canceled") return "bg-red-100 text-red-700";
      if (s === "expired") return "bg-amber-100 text-amber-800";
      return "bg-gray-100 text-gray-700";
    } catch {
      return "bg-gray-100 text-gray-700";
    }
  }

  async function cancelOrder(o) {
    if (!o || !o.id) return;
    try {
      await store.updateOrderStatus(o.id, "canceled");
      ui.toastSuccess("Sipariş iptal edildi");
    } catch (e) {
      ui.toastError("Sipariş iptali başarısız veya süre doldu");
    }
  }

  // orderCodeFromId util'den kullanılıyor

  async function load() {
    if (!sessionId.value) {
      orders.value = [];
      return;
    }
    orders.value = await store.loadSessionOrders(sessionId.value);
  }

  function onOrderSession(ev) {
    try {
      const sid = ev && ev.detail && ev.detail.sessionId;
      if (sid) sessionId.value = sid;
    } catch {
      /* ignore */
    }
    load();
  }

  function onStorage(ev) {
    try {
      if (ev && ev.key === "qm_order_session") {
        sessionId.value = ev.newValue;
        load();
      }
    } catch {
      /* ignore */
    }
  }

  onMounted(() => {
    // Ensure we also pick up cookie-based session if localStorage is empty
    if (!sessionId.value && typeof document !== "undefined") {
      sessionId.value = getCookie("qm_order_session");
    }
    // İsimler artık DTO lines üzerinden geldiği için menüyü otomatik yüklemiyoruz; gerekirse fallback için yüklenebilir.
    load();
    try {
      window.addEventListener("qm:orderSession", onOrderSession);
    } catch {
      /* ignore */
    }
    // Geri sayım için her saniye 'now' güncelle
    countdownInterval = setInterval(() => {
      now.value = Date.now();
    }, 1000);
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
    if (countdownInterval) clearInterval(countdownInterval);
  });

  watch(
    () => route.params.sessionId,
    (v) => {
      if (v && v !== sessionId.value) {
        sessionId.value = v;
        load();
      }
    },
  );
</script>
