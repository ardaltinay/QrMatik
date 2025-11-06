<template>
  <div>
    <div class="mb-4">
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h2 class="text-xl font-semibold">Kasa - Hesap İstekleri</h2>
        <div class="flex w-full items-center gap-2 sm:w-auto">
          <button
            class="rounded-md border border-red-300 px-3 py-1.5 text-red-600 hover:bg-red-50"
            @click="onLogout"
          >
            Çıkış
          </button>
        </div>
      </div>
    </div>

    <div v-if="requests.length">
      <div class="grid gap-3">
        <div
          v-for="o in requests"
          :key="o.id"
          class="flex flex-col rounded-lg border bg-white p-3 shadow-sm sm:flex-row sm:items-center sm:justify-between"
        >
          <div>
            <div class="flex flex-wrap items-center gap-x-2 text-sm sm:text-base">
              <span class="font-medium">#{{ orderCodeFromId(o.id) }}</span>
              <span class="text-gray-400">•</span>
              <span class="text-gray-700">Masa {{ o.table }}</span>
              <span class="text-gray-400">•</span>
              <span class="text-gray-500">{{ formatDateTz(o.createdAt || o.createdTime) }}</span>
            </div>
            <div class="mt-1 text-sm text-gray-700">
              Toplam: <span class="font-medium">{{ formatMoney(o.total) }}</span>
            </div>
          </div>
          <div class="mt-3 flex items-center gap-2 sm:mt-0">
            <button class="btn btn-secondary" @click="markPaid(o.id)">Ödeme Alındı</button>
            <button class="btn" @click="openDetail(o)">Detay</button>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <p class="text-gray-500">Bekleyen hesap isteği yok.</p>
    </div>

    <OrderDetailDrawer :order="selected" @close="selected = null" @updated="refresh" />
  </div>
</template>

<script>
  import { computed, ref, onMounted, onBeforeUnmount } from "vue";
  import { useOrderStore } from "@/stores/orderStore";
  import { useAuthStore } from "@/stores/authStore";
  import { useRouter } from "vue-router";
  import { formatMoney, formatDateTz, orderCodeFromId } from "@/utils/format";
  import OrderDetailDrawer from "@/components/OrderDetailDrawer.vue";

  export default {
    name: "CashierView",
    components: { OrderDetailDrawer },
    setup() {
      const store = useOrderStore();
      const auth = useAuthStore();
      const router = useRouter();
      const orders = computed(() => store.orders);
      const selected = ref(null);

      const requests = computed(() => {
        return (orders.value || []).filter((o) => o.status === "bill_requested");
      });

      async function refresh() {
        try {
          await store.loadOrders();
        } catch (e) {
          /* ignore */
        }
      }

      async function markPaid(id) {
        try {
          await store.updateOrderStatus(id, "payment_completed");
          await refresh();
        } catch (e) {
          /* ignore */
        }
      }

      function openDetail(o) {
        selected.value = o;
      }

      function onLogout() {
        try {
          auth.logout();
        } finally {
          router.push({ name: "admin" });
        }
      }

      let intervalId = null;
      onMounted(async () => {
        await refresh();
        intervalId = setInterval(refresh, 8000);
      });
      onBeforeUnmount(() => { if (intervalId) clearInterval(intervalId); });

      return { requests, markPaid, formatMoney, formatDateTz, orderCodeFromId, onLogout, selected, openDetail, refresh };
    },
  };
</script>
