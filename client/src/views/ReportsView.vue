<template>
  <div>
    <h2 class="mb-4 text-xl font-semibold">Raporlar</h2>

    <!-- FREE plan: full-page lock with CTA -->
    <div v-if="isFreePlan" class="rounded-lg border bg-white p-8 text-center shadow-sm">
      <div
        class="mx-auto mb-3 flex h-10 w-10 items-center justify-center rounded-full bg-gray-100 text-gray-500"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-5 w-5"
          viewBox="0 0 24 24"
          fill="currentColor"
        >
          <path
            fill-rule="evenodd"
            d="M12 1.5a5.25 5.25 0 00-5.25 5.25v2.25H6a3 3 0 00-3 3v6A3 3 0 006 21h12a3 3 0 003-3v-6a3 3 0 00-3-3h-.75V6.75A5.25 5.25 0 0012 1.5zm3.75 6.75V6.75a3.75 3.75 0 10-7.5 0v1.5h7.5z"
            clip-rule="evenodd"
          />
        </svg>
      </div>
      <p class="mb-4 text-sm text-gray-700">
        Raporlar sekmesi Standart/Pro yıllık planlarda mevcuttur.
      </p>
      <router-link to="/admin/upgrade" class="btn btn-primary">Planı Yükselt</router-link>
    </div>

    <!-- Paid plans: show actual reports -->
    <div v-else class="grid grid-cols-1 gap-4 md:grid-cols-3">
      <div class="rounded-lg border bg-white p-4 shadow-sm">
        <h3 class="mb-2 font-semibold">Günlük Sipariş</h3>
        <div class="text-3xl font-bold">{{ orders.length }}</div>
      </div>
      <div class="rounded-lg border bg-white p-4 shadow-sm">
        <h3 class="mb-2 font-semibold">Tahmini Ciro</h3>
        <div class="text-3xl font-bold">{{ revenueTRY }}</div>
      </div>
      <div class="rounded-lg border bg-white p-4 shadow-sm">
        <h3 class="mb-2 font-semibold">Popüler Ürünler</h3>
        <ul class="list-inside list-disc">
          <li v-for="p in popular" :key="p.name">{{ p.name }} ({{ p.count }})</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
  import { computed } from "vue";
  import { useOrderStore } from "@/stores/orderStore";

  export default {
    setup() {
      const store = useOrderStore();
      const orders = computed(() => store.orders);

      const revenue = computed(() => {
        // prefer server-provided order totals; fallback to summing snapshot lines
        let sum = 0;
        for (const o of orders.value) {
          if (typeof o.total === "number" && !isNaN(o.total)) {
            sum += o.total;
          } else {
            for (const it of o.items || []) {
              const price = typeof it.price === "number" ? it.price : Number(it.price || 0);
              sum += (price || 0) * (it.qty || 1);
            }
          }
        }
        return sum;
      });

      const revenueTRY = computed(() => {
        try {
          return new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" }).format(
            Number(revenue.value || 0),
          );
        } catch {
          return Number(revenue.value || 0).toFixed(0) + "₺";
        }
      });

      const popular = computed(() => {
        const map = new Map();
        for (const o of orders.value) {
          for (const it of o.items || []) {
            const name = it.name || store.menu.find((m) => m.id === it.itemId)?.name || "Unknown";
            map.set(name, (map.get(name) || 0) + (it.qty || 1));
          }
        }
        return Array.from(map.entries())
          .map(([name, count]) => ({ name, count }))
          .sort((a, b) => b.count - a.count)
          .slice(0, 5);
      });

      const isFreePlan = computed(() => {
        try {
          const raw = localStorage.getItem("qm_tenant_cfg");
          const cfg = raw ? JSON.parse(raw) : null;
          const plan = String(cfg?.plan || "FREE").toUpperCase();
          return !plan || plan === "FREE";
        } catch {
          return true;
        }
      });

      return { orders, revenueTRY, popular, isFreePlan };
    },
  };
</script>
