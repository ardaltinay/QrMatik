<template>
  <div>
    <h2 class="mb-4 text-xl font-semibold">Raporlar</h2>
    <div class="grid grid-cols-1 gap-4 md:grid-cols-3">
      <div class="rounded-lg border bg-white p-4 shadow-sm">
        <h3 class="mb-2 font-semibold">Günlük Sipariş</h3>
        <div class="text-3xl font-bold">{{ orders.length }}</div>
      </div>
      <div class="rounded-lg border bg-white p-4 shadow-sm">
        <h3 class="mb-2 font-semibold">Tahmini Ciro</h3>
        <div class="text-3xl font-bold">{{ revenue }}₺</div>
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
            for (const it of o.items) {
              const price = typeof it.price === "number" ? it.price : Number(it.price || 0);
              sum += (price || 0) * (it.qty || 1);
            }
          }
        }
        return sum;
      });

      const popular = computed(() => {
        const map = new Map();
        for (const o of orders.value) {
          for (const it of o.items) {
            const name = it.name || store.menu.find((m) => m.id === it.itemId)?.name || "Unknown";
            map.set(name, (map.get(name) || 0) + it.qty);
          }
        }
        return Array.from(map.entries())
          .map(([name, count]) => ({ name, count }))
          .sort((a, b) => b.count - a.count)
          .slice(0, 5);
      });

      return { orders, revenue, popular };
    },
  };
</script>
