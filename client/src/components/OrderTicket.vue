<template>
  <div
    class="p-4 border rounded-lg bg-white shadow-sm transform transition motion-safe:animate-fadeIn"
  >
    <div class="flex flex-col sm:flex-row sm:justify-between gap-3">
      <div>
        <h4 class="font-semibold">Sipariş #{{ order.id }}</h4>
        <div class="text-sm text-gray-500">Table: {{ order.table }}</div>
      </div>
      <div class="text-sm font-medium text-gray-700 self-start sm:self-center">
        {{ statusLabel(order.status) }}
      </div>
    </div>
    <ul class="mt-3 space-y-2">
      <li v-for="it in order.items" :key="it.itemId" class="flex justify-between text-sm">
        <span>{{ it.name || menuName(it.itemId) }}</span>
        <span class="text-gray-600">x{{ it.qty }}</span>
      </li>
    </ul>
    <div class="mt-4 flex flex-wrap gap-2">
      <button
        @click="$emit('updateStatus', order.id, 'preparing')"
        class="px-3 py-1 bg-yellow-400 text-white rounded-lg"
      >
        Hazırlanıyor
      </button>
      <button
        @click="$emit('updateStatus', order.id, 'ready')"
        class="px-3 py-1 bg-green-500 text-white rounded-lg"
      >
        Hazır
      </button>
      <button
        @click="$emit('updateStatus', order.id, 'served')"
        class="px-3 py-1 bg-gray-700 text-white rounded-lg"
      >
        Servis
      </button>
    </div>
  </div>
</template>

<script>
  import { useOrderStore } from "@/stores/orderStore";
  import { statusLabel } from "@/utils/format";

  export default {
    name: "OrderTicket",
    props: { order: { type: Object, required: true } },
    setup() {
      const store = useOrderStore();
      function menuName(id) {
        const m = store.menu.find((x) => x.id === id);
        return m ? m.name : "Unknown";
      }
      return { menuName, statusLabel };
    },
  };
</script>
