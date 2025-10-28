<template>
  <div>
    <div class="mb-3 grid grid-cols-1 sm:grid-cols-3 gap-2 items-start">
      <input
        v-model="q"
        placeholder="Ara: sipariş no veya masa"
        class="w-full p-3 rounded-lg border shadow-sm col-span-2 focus:ring-2 focus:ring-brand-200"
      />
      <div class="relative w-full">
        <label class="sr-only">Filtre</label>
        <div class="relative">
          <label class="sr-only">Filtre</label>
          <div class="w-full">
            <BaseSelect
              v-model="filter"
              :options="[
                { value: 'all', label: 'Hepsi' },
                { value: 'new', label: 'Yeni' },
                { value: 'preparing', label: 'Hazırlanıyor' },
                { value: 'ready', label: 'Hazır' },
                { value: 'served', label: 'Servis Edildi' },
                { value: 'payment_completed', label: 'Ödeme Tamamlandı' },
              ]"
            />
          </div>
        </div>

        <!-- Hızlı filtreler: selectbox'ın altında -->
        <div class="mt-3">
          <h3 class="font-semibold mb-2">Hızlı Filtreler</h3>
          <div class="space-y-2">
            <button
              @click="setFilter('new')"
              class="w-full text-left px-3 py-2 bg-yellow-50 rounded-lg"
            >
              Yeni Siparişler
            </button>
            <button
              @click="setFilter('preparing')"
              class="w-full text-left px-3 py-2 bg-indigo-50 rounded-lg"
            >
              Hazırlanıyor
            </button>
            <button
              @click="setFilter('ready')"
              class="w-full text-left px-3 py-2 bg-green-50 rounded-lg"
            >
              Hazır
            </button>
            <button
              @click="setFilter('served')"
              class="w-full text-left px-3 py-2 bg-gray-100 rounded-lg"
            >
              Servis Edildi
            </button>
            <button
              @click="setFilter('payment_completed')"
              class="w-full text-left px-3 py-2 bg-blue-50 rounded-lg"
            >
              Ödeme Tamamlandı
            </button>
          </div>

          <!-- Masa kapat: tableCode girerek ilgili oturumları kapat -->
          <div class="mt-4 p-3 border rounded-lg bg-white">
            <div class="text-sm font-medium mb-2">Masa Kapat</div>
            <div class="flex gap-2">
              <input
                v-model="tableCloseCode"
                placeholder="Masa kodu"
                class="flex-1 p-2 rounded-lg border shadow-sm focus:ring-2 focus:ring-brand-200"
              />
              <button
                @click="closeTable"
                class="px-3 py-2 bg-red-600 text-white rounded-lg disabled:opacity-50"
                :disabled="!tableCloseCode"
              >
                Kapat
              </button>
            </div>
            <div class="flex gap-2 mt-2">
              <button
                @click="fillFromSelected"
                class="px-3 py-2 border rounded-lg disabled:opacity-50"
                :disabled="!selected || !selected.table"
              >
                Seçili siparişin masasını doldur
              </button>
              <button
                @click="closeTableSelected"
                class="px-3 py-2 bg-red-50 text-red-700 border border-red-200 rounded-lg disabled:opacity-50"
                :disabled="!selected || !selected.table"
              >
                Seçili masayı kapat
              </button>
            </div>
            <div class="text-xs text-gray-500 mt-1">
              Bu işlem, belirtilen masaya ait aktif oturumları sonlandırır. Seçili bir sipariş
              varsa, masasını otomatik kullanabilirsiniz.
            </div>
          </div>
          <div class="mt-3 text-sm text-gray-500">Toplam: {{ filtered.length }}</div>
        </div>
      </div>
    </div>

    <div class="grid gap-3">
      <AdminOrderRow v-for="o in filtered" :key="o.id" :order="o" @open="openDetail" />
    </div>

    <OrderDetailDrawer
      :order="selected"
      @close="selected = null"
      @updated="onUpdated"
      @print="onPrint"
    />
  </div>
</template>

<script>
  import { ref, computed, onMounted } from "vue";
  import { useOrderStore } from "@/stores/orderStore";
  import AdminOrderRow from "@/components/AdminOrderRow.vue";
  import OrderDetailDrawer from "@/components/OrderDetailDrawer.vue";
  import BaseSelect from "@/components/BaseSelect.vue";
  import { fetchJson } from "@/utils/api";
  import { useUiStore } from "@/stores/uiStore";

  export default {
    name: "AdminOrdersView",
    components: { AdminOrderRow, OrderDetailDrawer, BaseSelect },
    setup() {
      const store = useOrderStore();
      const ui = useUiStore();
      const q = ref("");
      const filter = ref("all");
      const selected = ref(null);
      const tableCloseCode = ref("");

      const orders = computed(() => store.orders);
      const filtered = computed(() => {
        let list = orders.value;
        if (filter.value !== "all") list = list.filter((o) => o.status === filter.value);
        if (q.value)
          list = list.filter(
            (o) => String(o.id).includes(q.value) || (o.table && o.table.includes(q.value)),
          );
        return list;
      });

      function setFilter(f) {
        filter.value = f;
      }
      function openDetail(order) {
        selected.value = order;
      }
      function onUpdated() {
        /* optionally show toast */
      }
      function onPrint(order) {
        console.log("Print order", order);
      }

      async function closeTable() {
        if (!tableCloseCode.value) return;
        try {
          await fetchJson("/api/orders/close-table/" + encodeURIComponent(tableCloseCode.value), {
            method: "POST",
          });
          ui.toastSuccess("Masa kapatıldı");
          tableCloseCode.value = "";
          // Değişiklikleri listeye yansıt
          try {
            await store.loadOrders();
          } catch {
            /* ignore */
          }
        } catch (e) {
          // fetchJson already shows toast
        }
      }

      function fillFromSelected() {
        if (selected.value && selected.value.table) {
          tableCloseCode.value = selected.value.table;
        }
      }

      async function closeTableSelected() {
        if (!selected.value || !selected.value.table) return;
        try {
          await fetchJson("/api/orders/close-table/" + encodeURIComponent(selected.value.table), {
            method: "POST",
          });
          ui.toastSuccess("Seçili masadaki oturumlar kapatıldı");
          try {
            await store.loadOrders();
          } catch {
            /* ignore */
          }
        } catch {
          // toast shown by fetchJson
        }
      }

      onMounted(() => {
        if (store.loadOrders) store.loadOrders().catch(() => {});
      });

      return {
        q,
        filter,
        orders,
        filtered,
        selected,
        tableCloseCode,
        setFilter,
        openDetail,
        onUpdated,
        onPrint,
        closeTable,
        fillFromSelected,
        closeTableSelected,
      };
    },
  };
</script>

<style scoped>
  .grid {
    display: grid;
    gap: 12px;
  }
</style>
