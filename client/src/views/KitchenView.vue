<template>
  <div>
    <div class="mb-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <h2 class="text-xl font-semibold">Mutfak - Gelen Siparişler</h2>
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
      </div>
    </div>

    <div v-if="filtered.length">
      <div class="grid gap-3">
        <OrderTicket v-for="o in filtered" :key="o.id" :order="o" @updateStatus="updateStatus" />
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
  import { computed, ref } from "vue";

  export default {
    name: "KitchenView",
    components: { OrderTicket, BaseSelect },
    setup() {
      const store = useOrderStore();
      const filter = ref("all");
      const orders = computed(() => store.orders);
      const filtered = computed(() => {
        const list = orders.value.filter((o) => (o.type || "kitchen") === "kitchen");
        if (filter.value === "all") return list;
        return list.filter((o) => o.status === filter.value);
      });

      function updateStatus(orderId, status) {
        store.updateOrderStatus(orderId, status);
      }

      return { filtered, filter, updateStatus };
    },
  };
</script>
