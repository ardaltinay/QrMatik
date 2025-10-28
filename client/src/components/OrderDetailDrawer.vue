<template>
  <transition name="slide-up">
    <div v-if="order" class="fixed right-0 top-0 h-full w-full md:w-96 bg-white shadow-xl z-50">
      <div class="p-4 border-b flex justify-between items-center">
        <div>
          <h3 class="font-semibold">Sipariş #{{ order.id }}</h3>
          <div class="text-sm text-gray-500">{{ order.createdAt }}</div>
        </div>
        <button @click="$emit('close')" class="p-2 rounded hover:bg-gray-100">✕</button>
      </div>
      <div class="p-4 space-y-3 overflow-auto max-h-[75vh]">
        <div v-for="it in order.items" :key="it.itemId" class="flex justify-between">
          <div>
            <div class="font-medium">{{ it.name || menuName(it.itemId) }}</div>
          </div>
          <div class="text-gray-600">x{{ it.qty }}</div>
        </div>

        <div class="mt-2 flex items-center justify-between border-t pt-3">
          <div class="text-sm text-gray-600">Toplam</div>
          <div class="text-lg font-semibold">
            {{ formatMoney(order.total || sumItems(order.items)) }}
          </div>
        </div>

        <div class="mt-4">
          <label class="block text-sm text-gray-600 mb-1">Durum</label>
          <div>
            <BaseSelect
              v-model="localStatus"
              :options="[
                { value: 'new', label: 'Yeni' },
                { value: 'preparing', label: 'Hazırlanıyor' },
                { value: 'ready', label: 'Hazır' },
                { value: 'served', label: 'Servis Edildi' },
                { value: 'payment_completed', label: 'Ödeme Tamamlandı' },
              ]"
            />
          </div>
        </div>

        <div class="flex gap-2 mt-4">
          <button @click="applyStatus" class="px-3 py-2 bg-brand-500 text-white rounded-lg shadow">
            Durum Güncelle
          </button>
          <button @click="$emit('print', order)" class="px-3 py-2 border rounded-lg">Yazdır</button>
          <button
            v-if="order && order.table"
            @click="closeTableForOrder"
            class="px-3 py-2 bg-red-50 text-red-700 border border-red-200 rounded-lg"
          >
            Masa Kapat
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
  import { ref } from "vue";
  import { useOrderStore } from "@/stores/orderStore";
  import BaseSelect from "@/components/BaseSelect.vue";
  import { formatMoney } from "@/utils/format";
  import { useUiStore } from "@/stores/uiStore";
  import { fetchJson } from "@/utils/api";

  export default {
    name: "OrderDetailDrawer",
    components: { BaseSelect },
    props: { order: { type: Object, default: null } },
    setup(props, { emit }) {
      const store = useOrderStore();
      const ui = useUiStore();
      const localStatus = ref(props.order ? props.order.status : "new");

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

      async function applyStatus() {
        if (!props.order) return;
        try {
          await store.updateOrderStatus(props.order.id, localStatus.value);
          ui.toastSuccess("Durum güncellendi");
          emit("updated", props.order.id);
          emit("close");
        } catch {
          // fetchJson already shows an error toast; no-op
        }
      }

      async function closeTableForOrder() {
        if (!props.order || !props.order.table) return;
        try {
          await fetchJson("/api/orders/close-table/" + encodeURIComponent(props.order.table), {
            method: "POST",
          });
          ui.toastSuccess("Masa kapatıldı");
          emit("close");
        } catch {
          // error toast already shown
        }
      }

      return { menuName, localStatus, applyStatus, formatMoney, sumItems, closeTableForOrder };
    },
  };
</script>
