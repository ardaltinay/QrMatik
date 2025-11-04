<template>
  <div>
    <div class="mb-4">
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h2 class="text-xl font-semibold">Mutfak - Gelen Yemek Siparişleri</h2>
        <div class="flex w-full items-center gap-2 sm:w-auto">
          <div class="w-full sm:w-48">
            <BaseSelect
              v-model="filter"
              :options="[
                { value: 'all', label: 'Hepsi' },
                { value: 'new', label: 'Yeni' },
                { value: 'preparing', label: 'Hazırlanıyor' },
                { value: 'ready', label: 'Hazır' },
              ]"
            />
          </div>
          <button
            class="rounded-md border border-red-300 px-3 py-1.5 text-red-600 hover:bg-red-50"
            @click="onLogout"
          >
            Çıkış
          </button>
        </div>
      </div>
    </div>

    <div v-if="filtered.length">
      <div class="grid gap-3">
        <OrderTicket
          v-for="o in filtered"
          :key="o.id"
          :order="o"
          department="kitchen"
          @updateStatus="updateStatus"
        />
      </div>
    </div>
    <div v-else>
      <p class="text-gray-500">Filtreye uygun sipariş yok.</p>
    </div>
  </div>
</template>

<script>
  import { useOrderStore } from "@/stores/orderStore";
  import OrderTicket from "@/components/OrderTicket.vue";
  import BaseSelect from "@/components/BaseSelect.vue";
  import { computed, ref, onMounted, onBeforeUnmount } from "vue";
  import { useAuthStore } from "@/stores/authStore";
  import { useRouter } from "vue-router";

  export default {
    name: "KitchenView",
    components: { OrderTicket, BaseSelect },
    setup() {
      const store = useOrderStore();
      const auth = useAuthStore();
      const router = useRouter();
      const filter = ref("all");
      const orders = computed(() => store.orders);
      const nowTick = ref(Date.now());
      const filtered = computed(() => {
        const isDrinkItem = (it) => {
          try {
            const c = String(it.category || "").toLowerCase();
            const s = String(it.subcategory || "").toLowerCase();
            return (
              c.includes("drink") ||
              c.includes("içecek") ||
              c.includes("icecek") ||
              c.includes("bar") ||
              s.includes("drink") ||
              s.includes("içecek") ||
              s.includes("icecek") ||
              s.includes("bar")
            );
          } catch {
            return false;
          }
        };
        const hasFood = (o) => Array.isArray(o.items) && o.items.some((it) => !isDrinkItem(it));
        // CANCELED, PAYMENT_COMPLETED, SERVED ve EXPIRED mutfakta görünmesin
        const base = orders.value
          .filter((o) => hasFood(o))
          .filter(
            (o) =>
              o.status !== "canceled" &&
              o.status !== "payment_completed" &&
              o.status !== "served" &&
              o.status !== "expired",
          )
          // Yeni siparişler 2 dakikalık iptal süresi bitmeden görünmesin
          .filter((o) => {
            if ((o.status || "").toLowerCase() !== "new") return true;
            const created = new Date(o.createdAt);
            if (isNaN(created.getTime())) return false;
            return nowTick.value - created.getTime() >= 2 * 60 * 1000;
          });
        if (filter.value === "all") return base;
        return base.filter((o) => o.status === filter.value);
      });

      function updateStatus(orderId, status) {
        store.updateOrderStatus(orderId, status);
      }

      function onLogout() {
        try {
          auth.logout();
        } finally {
          router.push({ name: "admin" });
        }
      }

      // Auto-refresh orders periodically for kiosk usage
      let intervalId = null;
      let tickId = null;
      onMounted(async () => {
        try {
          await store.loadOrders();
        } catch {
          /* ignore */
        }
        intervalId = setInterval(() => {
          store.loadOrders().catch(() => {});
        }, 10000); // 10 saniye
        // 2 dakikalık görünürlük eşiği için her saniye tetikle
        tickId = setInterval(() => {
          nowTick.value = Date.now();
        }, 1000);
      });
      onBeforeUnmount(() => {
        if (intervalId) clearInterval(intervalId);
        if (tickId) clearInterval(tickId);
      });

      return { filtered, filter, updateStatus, onLogout };
    },
  };
</script>
