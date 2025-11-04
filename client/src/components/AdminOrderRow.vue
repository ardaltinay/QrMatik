<template>
  <div
    class="flex flex-col rounded-lg border bg-white p-3 transition hover:shadow-lg sm:flex-row sm:items-center sm:justify-between"
  >
    <div>
      <div class="font-medium">#{{ order.id }} — {{ order.table }}</div>
      <div class="text-sm text-gray-500">{{ order.createdAt }}</div>
      <div class="mt-1 text-sm text-gray-700">
        Toplam: <span class="font-medium">{{ formatMoney(order.total) }}</span>
      </div>
    </div>
    <div class="mt-3 flex items-center gap-2 sm:mt-0">
      <div class="rounded-full px-3 py-1 text-sm font-medium" :class="statusColor">
        {{ statusLabel }}
      </div>
      <button
        @click="$emit('open', order)"
        class="rounded-lg border bg-white px-3 py-1 shadow-sm hover:bg-gray-50"
      >
        Detay
      </button>
    </div>
  </div>
</template>

<script>
  import { computed } from "vue";
  import { formatMoney } from "@/utils/format";
  import { statusLabel as statusLabelUtil } from "@/utils/format";

  export default {
    name: "AdminOrderRow",
    props: { order: { type: Object, required: true } },
    setup(props) {
      const statusColor = computed(() => {
        const s = props.order.status;
        if (s === "new") return "bg-yellow-100 text-yellow-800";
        if (s === "preparing") return "bg-indigo-100 text-indigo-800";
        if (s === "ready") return "bg-green-100 text-green-800";
        if (s === "served") return "bg-gray-200 text-gray-800";
        if (s === "payment_completed") return "bg-blue-100 text-blue-800";
        if (s === "canceled") return "bg-red-100 text-red-700";
        if (s === "expired") return "bg-amber-100 text-amber-800";
        return "bg-gray-100 text-gray-700";
      });

      const statusLabel = computed(() => statusLabelUtil(props.order.status));

      return { statusColor, statusLabel, formatMoney };
    },
  };
</script>
