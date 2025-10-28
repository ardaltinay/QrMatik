<template>
  <div class="max-w-3xl mx-auto p-4">
    <h1 class="text-2xl font-semibold mb-4">Siparişlerim</h1>

    <!-- Masa bilgisi -->
    <div
      v-if="orders.length && tableInfo"
      class="mb-4 p-3 rounded-lg bg-gray-50 border text-gray-700"
    >
      <span class="font-medium">Masa:</span>
      <span>{{ tableInfo }}</span>
    </div>

    <div v-if="orders.length">
      <div
        v-for="o in orders"
        :key="o.id"
        class="bg-white p-4 rounded-lg shadow mb-4 cursor-pointer hover:shadow-md transition"
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
            <div class="font-medium">{{ statusLabel(o.status) }}</div>
            <div class="text-sm text-gray-500 mt-2">Toplam</div>
            <div class="font-medium">{{ formatMoney(o.total || sumItems(o.items)) }}</div>
          </div>
        </div>

        <div class="mt-4">
          <h4 class="font-semibold mb-2">İçerik</h4>
          <ul class="space-y-1">
            <li v-for="it in o.items" :key="it.itemId" class="flex justify-between">
              <div>{{ it.name || menuItemName(it.itemId) }}</div>
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

  const route = useRoute();
  const router = useRouter();
  const store = useOrderStore();
  const sessionId = ref(
    route.params.sessionId ||
      (typeof localStorage !== "undefined" ? localStorage.getItem("qm_order_session") : null),
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
  function sumItems(items) {
    try {
      return (items || []).reduce((s, it) => s + Number(it.price || 0) * (it.qty || 1), 0);
    } catch {
      return 0;
    }
  }
  function goDetail(o) {
    if (!o || !o.id) return;
    try {
      store.ordersById[String(o.id)] = o;
    } catch {
      /* ignore */
    }
    router.push({ name: "order-status", params: { id: String(o.id) } });
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
    // İsimler artık DTO lines üzerinden geldiği için menüyü otomatik yüklemiyoruz; gerekirse fallback için yüklenebilir.
    load();
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
