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
                { value: 'canceled', label: 'İptal Edildi' },
              ]"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Hızlı filtreler (mobil + küçük tablet: alt alta) -->
    <div class="mb-3 md:hidden">
      <h3 class="font-semibold mb-2">Hızlı Filtreler</h3>
      <div class="grid grid-cols-1 gap-2">
        <button @click="setFilter('new')" class="w-full px-3 py-2 rounded-lg text-left"
          :class="filter==='new' ? 'bg-yellow-600 text-white' : 'bg-yellow-50 text-yellow-800 hover:bg-yellow-100'">
          Yeni
        </button>
        <button @click="setFilter('preparing')" class="w-full px-3 py-2 rounded-lg text-left"
          :class="filter==='preparing' ? 'bg-indigo-600 text-white' : 'bg-indigo-50 text-indigo-800 hover:bg-indigo-100'">
          Hazırlanıyor
        </button>
        <button @click="setFilter('ready')" class="w-full px-3 py-2 rounded-lg text-left"
          :class="filter==='ready' ? 'bg-green-600 text-white' : 'bg-green-50 text-green-800 hover:bg-green-100'">
          Hazır
        </button>
        <button @click="setFilter('served')" class="w-full px-3 py-2 rounded-lg text-left"
          :class="filter==='served' ? 'bg-gray-700 text-white' : 'bg-gray-100 text-gray-800 hover:bg-gray-200'">
          Servis Edildi
        </button>
        <button @click="setFilter('payment_completed')" class="w-full px-3 py-2 rounded-lg text-left"
          :class="filter==='payment_completed' ? 'bg-blue-600 text-white' : 'bg-blue-50 text-blue-800 hover:bg-blue-100'">
          Ödeme Tamamlandı
        </button>
        <button @click="setFilter('canceled')" class="w-full px-3 py-2 rounded-lg text-left"
          :class="filter==='canceled' ? 'bg-red-600 text-white' : 'bg-red-50 text-red-700 hover:bg-red-100'">
          İptal Edildi
        </button>
      </div>
      <div class="mt-2 text-sm text-gray-500">Toplam: {{ filtered.length }}</div>
    </div>

    <!-- Hızlı filtreler (tablet/web: yatay chip bar, sayfayı kaplar) -->
    <div class="mb-3 hidden md:block">
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-sm font-medium mr-2">Hızlı Filtreler:</span>
        <button
          @click="setFilter('new')"
          :class="'px-3 py-1.5 rounded-full text-sm border ' + (filter==='new' ? 'bg-yellow-600 text-white border-yellow-600' : 'bg-yellow-50 text-yellow-800 border-yellow-200 hover:bg-yellow-100')"
        >
          Yeni
        </button>
        <button
          @click="setFilter('preparing')"
          :class="'px-3 py-1.5 rounded-full text-sm border ' + (filter==='preparing' ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-indigo-50 text-indigo-800 border-indigo-200 hover:bg-indigo-100')"
        >
          Hazırlanıyor
        </button>
        <button
          @click="setFilter('ready')"
          :class="'px-3 py-1.5 rounded-full text-sm border ' + (filter==='ready' ? 'bg-green-600 text-white border-green-600' : 'bg-green-50 text-green-800 border-green-200 hover:bg-green-100')"
        >
          Hazır
        </button>
        <button
          @click="setFilter('served')"
          :class="'px-3 py-1.5 rounded-full text-sm border ' + (filter==='served' ? 'bg-gray-700 text-white border-gray-700' : 'bg-gray-100 text-gray-800 border-gray-200 hover:bg-gray-200')"
        >
          Servis Edildi
        </button>
        <button
          @click="setFilter('payment_completed')"
          :class="'px-3 py-1.5 rounded-full text-sm border ' + (filter==='payment_completed' ? 'bg-blue-600 text-white border-blue-600' : 'bg-blue-50 text-blue-800 border-blue-200 hover:bg-blue-100')"
        >
          Ödeme Tamamlandı
        </button>
        <button
          @click="setFilter('canceled')"
          :class="'px-3 py-1.5 rounded-full text-sm border ' + (filter==='canceled' ? 'bg-red-600 text-white border-red-600' : 'bg-red-50 text-red-700 border-red-200 hover:bg-red-100')"
        >
          İptal Edildi
        </button>

        <div class="ml-auto text-sm text-gray-500">Toplam: {{ filtered.length }}</div>
      </div>
    </div>

    <div class="grid gap-3">
      <AdminOrderRow v-for="o in paged" :key="o.id" :order="o" @open="openDetail" />
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="mt-4 flex items-center justify-between">
      <div class="text-sm text-gray-600">Toplam {{ filtered.length }} kayıt · Sayfa {{ page }} / {{ totalPages }}</div>
      <div class="flex items-center gap-2">
        <button
          class="px-3 py-1 rounded border"
          :disabled="page === 1"
          @click="prevPage"
        >Önceki</button>
        <div class="flex gap-1">
          <button
            v-for="p in pageList"
            :key="p"
            @click="goPage(p)"
            :class="'px-3 py-1 rounded border ' + (p === page ? 'bg-brand-500 text-white border-brand-500' : 'bg-white text-gray-700')"
          >{{ p }}</button>
        </div>
        <button
          class="px-3 py-1 rounded border"
          :disabled="page === totalPages"
          @click="nextPage"
        >Sonraki</button>
      </div>
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
  import { ref, computed, onMounted, watch } from "vue";
  import { useOrderStore } from "@/stores/orderStore";
  import AdminOrderRow from "@/components/AdminOrderRow.vue";
  import OrderDetailDrawer from "@/components/OrderDetailDrawer.vue";
  import BaseSelect from "@/components/BaseSelect.vue";


  export default {
    name: "AdminOrdersView",
    components: { AdminOrderRow, OrderDetailDrawer, BaseSelect },
    setup() {
  const store = useOrderStore();
      const q = ref("");
      const filter = ref("all");
      const selected = ref(null);


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

      // pagination
      const page = ref(1);
      const pageSize = ref(10);
      const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize.value)));
      const paged = computed(() => {
        const start = (page.value - 1) * pageSize.value;
        return filtered.value.slice(start, start + pageSize.value);
      });
      const pageList = computed(() => {
        const t = totalPages.value;
        // show up to 5 pages centered on current
        const span = 5;
        let from = Math.max(1, page.value - Math.floor(span / 2));
        let to = Math.min(t, from + span - 1);
        from = Math.max(1, to - span + 1);
        const arr = [];
        for (let i = from; i <= to; i++) arr.push(i);
        return arr;
      });
      function goPage(p) {
        if (p < 1 || p > totalPages.value) return;
        page.value = p;
      }
      function prevPage() { if (page.value > 1) page.value--; }
      function nextPage() { if (page.value < totalPages.value) page.value++; }

      // reset to page 1 when filters/search change
      watch([q, filter], () => { page.value = 1; });

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



      onMounted(() => {
        if (store.loadOrders) store.loadOrders().catch(() => {});
      });

      return {
        q,
        filter,
        orders,
  filtered,
  paged,
  page,
  totalPages,
  pageList,
  goPage,
  prevPage,
  nextPage,
        selected,
        setFilter,
        openDetail,
        onUpdated,
        onPrint,
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
