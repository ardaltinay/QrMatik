<template>
  <transition name="slide-up">
    <div v-if="order" class="fixed right-0 top-0 z-50 h-full w-full bg-white shadow-xl md:w-96">
      <div class="flex items-center justify-between border-b p-4">
        <div>
          <h3 class="font-semibold">Sipariş #{{ order.id }}</h3>
          <div class="text-sm text-gray-500">
            {{ formatDateTz(order.createdAt || order.createdTime) }}
          </div>
        </div>
        <button @click="$emit('close')" class="rounded p-2 hover:bg-gray-100">✕</button>
      </div>
      <div class="max-h-[75vh] space-y-3 overflow-auto p-4">
        <div v-for="it in order.items" :key="it.itemId" class="flex justify-between">
          <div>
            <div class="font-medium">{{ it.name || menuName(it.itemId) }}</div>
          </div>
          <div class="text-gray-600">x{{ it.qty }}</div>
        </div>

        <div class="mt-2 flex items-center justify-between border-t pt-3">
          <div class="text-sm text-gray-600">Toplam</div>
          <div class="text-lg font-semibold">
            {{ formatMoney(order.total) }}
          </div>
        </div>

        <div class="mt-4">
          <label class="mb-1 block text-sm text-gray-600">Durum</label>
          <div>
            <BaseSelect
              v-model="localStatus"
              :options="statusOptions"
            />
          </div>
        </div>

        <div class="mt-4 flex flex-wrap gap-2">
          <button @click="applyStatus" class="rounded-lg bg-brand-500 px-3 py-2 text-white shadow">
            Durum Güncelle
          </button>
          <button @click="$emit('print', order)" class="rounded-lg border px-3 py-2">Yazdır</button>
          <button
            v-if="canCancel"
            @click="cancelOrder"
            class="rounded-lg bg-red-600 px-3 py-2 text-white shadow hover:bg-red-700"
          >
            İptal Et
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
  import { ref, computed, watch } from "vue";
  import { useOrderStore } from "@/stores/orderStore";
  import BaseSelect from "@/components/BaseSelect.vue";
  import { formatMoney, formatDateTz } from "@/utils/format";
  import { useUiStore } from "@/stores/uiStore";

  export default {
    name: "OrderDetailDrawer",
    components: { BaseSelect },
    props: { order: { type: Object, default: null }, onlyPayment: { type: Boolean, default: false } },
    setup(props, { emit }) {
      const store = useOrderStore();
      const ui = useUiStore();
      const localStatus = ref(props.order ? props.order.status : "new");
      const canCancel = computed(() => {
        const s = String((props.order && props.order.status) || "").toLowerCase();
        // Temel iptal koşulları
        if (s === "new" || s === "preparing" || s === "ready") return true;
        // Kasa ekranı (onlyPayment) için hesap isteği durumunda da iptale izin ver
        if (props.onlyPayment && s === "bill_requested") return true;
        return false;
      });

      // keep localStatus in sync when order changes
      watch(
        () => props.order,
        (o) => {
          if (o && o.status) localStatus.value = o.status;
        },
      );

      const statusOptions = computed(() => {
        const all = [
          { value: "new", label: "Yeni" },
          { value: "preparing", label: "Hazırlanıyor" },
          { value: "ready", label: "Hazır" },
          { value: "served", label: "Servis Edildi" },
          { value: "payment_completed", label: "Ödeme Tamamlandı" },
          { value: "canceled", label: "İptal Edildi" },
        ];
        if (props.onlyPayment) {
          return all.filter((o) => o.value === "payment_completed");
        }
        return all;
      });

      function menuName(id) {
        const m = store.menu.find((x) => x.id === id);
        return m ? m.name : "Unknown";
      }

      // Toplam tutarı yalnızca sunucudan gelen order.total üzerinden gösteriyoruz

      async function applyStatus() {
        if (!props.order) return;
        if (props.onlyPayment && localStatus.value !== "payment_completed") {
          try {
            ui.toastError("Kasa ekranında yalnızca 'Ödeme Tamamlandı' güncellemesi yapılabilir.");
          } catch {
            /* ignore */
          }
          return;
        }
        try {
          await store.updateOrderStatus(props.order.id, localStatus.value);
          ui.toastSuccess("Durum güncellendi");
          emit("updated", props.order.id);
          emit("close");
        } catch {
          // fetchJson already shows an error toast; no-op
        }
      }

      async function cancelOrder() {
        if (!props.order) return;
        try {
          await store.updateOrderStatus(props.order.id, "canceled");
          ui.toastSuccess("Sipariş iptal edildi");
          try {
            await store.loadOrders();
          } catch (e) {
            /* ignore */
          }
          emit("updated", props.order.id);
          emit("close");
        } catch {
          // error toast already shown in fetchJson
        }
      }

      return {
        menuName,
        localStatus,
        applyStatus,
        formatMoney,
        formatDateTz,
        canCancel,
        cancelOrder,
        statusOptions,
      };
    },
  };
</script>
