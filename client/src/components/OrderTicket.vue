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
      <li v-for="it in visibleItems" :key="it.itemId + '::' + (it.note || '')" class="flex justify-between text-sm">
        <div class="flex flex-col">
          <span class="font-medium">{{ it.name || menuName(it.itemId) }}</span>
          <span v-if="it.note" class="text-xs text-gray-600 mt-0.5">Not: {{ it.note }}</span>
        </div>
        <span class="text-gray-600">x{{ it.qty }}</span>
      </li>
    </ul>
    <div class="mt-4 flex flex-wrap gap-2">
      <button
        v-if="canToPreparing(order.status)"
        @click="$emit('updateStatus', order.id, 'preparing')"
        class="px-3 py-1 bg-yellow-400 text-white rounded-lg"
      >
        Hazırlanıyor
      </button>
      <button
        v-if="canToReady(order.status)"
        @click="$emit('updateStatus', order.id, 'ready')"
        class="px-3 py-1 bg-green-500 text-white rounded-lg"
      >
        Hazır
      </button>
      <button
        v-if="canToServed(order.status)"
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
  import { computed } from "vue";

  export default {
    name: "OrderTicket",
    props: { order: { type: Object, required: true }, department: { type: String, default: "kitchen" } },
    setup(props) {
      const store = useOrderStore();
      function menuName(id) {
        const m = store.menu.find((x) => x.id === id);
        return m ? m.name : "Unknown";
      }
      function isDrinkItem(it) {
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
      }
      const visibleItems = computed(() => {
        const items = Array.isArray(props.order.items) ? props.order.items : [];
        if (props.department === "bar") return items.filter((it) => isDrinkItem(it));
        return items.filter((it) => !isDrinkItem(it));
      });
      function canToPreparing(s) {
        const k = String(s || "").toLowerCase();
        return k === "new"; // only from NEW
      }
      function canToReady(s) {
        const k = String(s || "").toLowerCase();
        return k === "new" || k === "preparing"; // forward only
      }
      function canToServed(s) {
        const k = String(s || "").toLowerCase();
        return k === "new" || k === "preparing" || k === "ready"; // forward only
      }
      return { menuName, statusLabel, canToPreparing, canToReady, canToServed, visibleItems };
    },
  };
</script>
