<template>
  <div class="p-6 font-mono">
    <h2>Sipariş #{{ order.id }}</h2>
    <div>Table: {{ order.table }}</div>
    <ul class="mt-4">
      <li v-for="it in order.items" :key="it.itemId">
        {{ it.name || menuName(it.itemId) }} x{{ it.qty }}
      </li>
    </ul>
    <div class="mt-4">Durum: {{ order.status }}</div>
    <div class="mt-2">Toplam: {{ formatMoney(order.total || sumItems(order.items)) }}</div>
  </div>
</template>

<script>
  import { useOrderStore } from "@/stores/orderStore";
  import { formatMoney } from "@/utils/format";
  export default {
    props: ["order"],
    setup() {
      const store = useOrderStore();
      function menuName(id) {
        const m = store.menu.find((x) => x.id === id);
        return m ? m.name : "Unknown";
      }
      function sumItems(items) {
        try {
          return (items || []).reduce((s, it) => s + Number(it.price || 0) * (it.qty || 1), 0);
        } catch {
          return 0;
        }
      }
      return { menuName, formatMoney, sumItems };
    },
  };
</script>
