<template>
  <div class="menu">
    <div class="mb-4 flex items-center justify-between">
      <h1 class="text-2xl font-semibold">Menü</h1>
      <button
        @click="onRefresh"
        :disabled="isRefreshing"
        class="rounded-md border px-3 py-1.5 text-sm hover:bg-gray-50 disabled:opacity-60"
        title="Menüyü sunucudan yeniden yükle"
      >
        {{ isRefreshing ? "Yükleniyor..." : "Yenile" }}
      </button>
    </div>

    <!-- filtreler (üstte ve sağa hizalı) -->
    <div class="mb-3 flex flex-wrap justify-start gap-2">
      <button
        v-for="p in primaries"
        :key="p"
        @click="primary = p"
        :class="[
          'rounded-full px-3 py-1',
          primary === p ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700',
        ]"
      >
        {{ tPrimary(p) }}
      </button>
    </div>

    <div class="mb-4 flex flex-wrap justify-start gap-2">
      <button
        @click="sub = 'all'"
        :class="[
          'rounded px-2 py-1',
          sub === 'all' ? 'bg-indigo-50 text-indigo-700' : 'bg-gray-100 text-gray-700',
        ]"
      >
        Tümü
      </button>
      <button
        v-for="s in subsForPrimary"
        :key="s"
        @click="sub = s"
        :class="[
          'rounded px-2 py-1',
          sub === s ? 'bg-indigo-50 text-indigo-700' : 'bg-gray-100 text-gray-700',
        ]"
      >
        {{ tSub(s) }}
      </button>
    </div>

    <div class="grid">
      <MenuItemCard v-for="item in filtered" :key="item.id" :item="item" @add="openAddNote" />
    </div>
    <CartDrawer :mobileOpen="mobileOpen" @update:mobileOpen="(v) => (mobileOpen = v)" />

    <!-- mobile sticky mini cart bar -->
    <div
      v-if="store.cart.length && !mobileOpen"
      class="fixed bottom-4 left-4 right-4 z-50 md:hidden"
    >
      <button
        @click="mobileOpen = true"
        class="flex w-full items-center justify-between rounded-full bg-indigo-600 px-4 py-3 text-white shadow-lg"
      >
        <div class="font-medium">Sepet ({{ store.cart.length }})</div>
        <div class="text-sm">Öğe: {{ totalItems }}</div>
      </button>
    </div>
    <!-- Add-to-cart note modal -->
    <div
      v-if="noteModal.open"
      class="fixed inset-0 z-50 flex items-end justify-center md:items-center"
    >
      <div class="absolute inset-0 bg-black/40" @click="closeNoteModal"></div>
      <div
        class="relative w-full rounded-t-xl bg-white p-4 shadow-xl md:mx-auto md:max-w-md md:rounded-xl md:p-6"
      >
        <div class="text-lg font-semibold">Not ekle</div>
        <div class="mt-1 text-sm text-gray-500">
          İsteğe bağlı: bu ürün için mutfak/bar'a not bırakabilirsiniz.
        </div>
        <div class="mt-3 flex items-center gap-3">
          <label class="text-sm text-gray-700">Adet</label>
          <div class="flex items-center gap-1">
            <button
              @click="noteModal.qty = Math.max(1, Number(noteModal.qty || 1) - 1)"
              class="h-7 w-7 rounded border text-sm text-gray-600 hover:bg-gray-50"
              :disabled="(noteModal.qty || 1) <= 1"
            >
              -
            </button>
            <input
              v-model.number="noteModal.qty"
              type="number"
              min="1"
              class="h-7 w-16 rounded border px-1 text-center text-sm focus:outline-none focus:ring focus:ring-indigo-200"
            />
            <button
              @click="noteModal.qty = Math.min(999, Number(noteModal.qty || 1) + 1)"
              class="h-7 w-7 rounded border text-sm text-gray-600 hover:bg-gray-50"
            >
              +
            </button>
          </div>
        </div>
        <div class="mt-3">
          <textarea
            v-model="noteModal.note"
            rows="3"
            class="w-full rounded-md border p-2 focus:outline-none focus:ring focus:ring-indigo-200"
            placeholder="Örn. soğansız, acılı olsun..."
          ></textarea>
        </div>
        <div class="mt-4 flex justify-end gap-2">
          <button @click="closeNoteModal" class="rounded-md border px-3 py-1.5">Vazgeç</button>
          <button
            @click="confirmAddWithNote"
            class="rounded-md bg-indigo-600 px-3 py-1.5 text-white"
          >
            Sepete ekle
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { ref, computed, onMounted, watch } from "vue";
  import { useOrderStore } from "@/stores/orderStore";
  import { primaryLabel, subLabel } from "@/utils/format";
  import MenuItemCard from "@/components/MenuItemCard.vue";
  import CartDrawer from "@/components/CartDrawer.vue";

  export default {
    name: "MenuView",
    components: { MenuItemCard, CartDrawer },
    setup() {
      const store = useOrderStore();
      const mobileOpen = ref(false);
      if (!store.menuLoaded) {
        void store.loadMenu();
      }
      const isRefreshing = ref(false);
      async function onRefresh() {
        try {
          isRefreshing.value = true;
          await store.refreshMenu();
        } finally {
          isRefreshing.value = false;
        }
      }

      const totalItems = computed(() => store.cart.reduce((s, i) => s + i.qty, 0));

      const primaries = computed(() => {
        const set = new Set(store.menu.map((m) => m.primary));
        return Array.from(set);
      });

      const primary = ref(primaries.value[0] || "food");
      const sub = ref("all");

      const subsForPrimary = computed(() => {
        const list = store.menu.filter((m) => m.primary === primary.value).map((m) => m.sub);
        return Array.from(new Set(list));
      });

      const filtered = computed(() =>
        store.menu.filter(
          (m) => m.primary === primary.value && (sub.value === "all" || m.sub === sub.value),
        ),
      );

      // simple modal state for add-to-cart note
      const noteModal = ref({ open: false, item: null, note: "", qty: 1 });

      function openAddNote(item) {
        noteModal.value = { open: true, item, note: "", qty: 1 };
      }
      function closeNoteModal() {
        noteModal.value.open = false;
        noteModal.value.item = null;
        noteModal.value.note = "";
        noteModal.value.qty = 1;
      }
      function confirmAddWithNote() {
        const it = noteModal.value.item;
        if (it && it.id) {
          const qty = Math.max(1, Number(noteModal.value.qty || 1));
          store.addToCart(it.id, noteModal.value.note || "", qty);
        }
        closeNoteModal();
      }

      // read optional table code from URL (e.g. ?table=Table-01 or ?t=Table-01) and persist
      onMounted(() => {
        try {
          const params = new URLSearchParams(window.location.search);
          const table = params.get("table") || params.get("t");
          if (table) {
            try {
              localStorage.setItem("qm_table_code", table);
              const tenant = localStorage.getItem("qm_tenant");
              if (tenant) localStorage.setItem("qm_table_tenant", tenant);
            } catch (e) {
              /* ignore */
            }
          }
        } catch (e) {
          /* ignore */
        }
      });

      // when menu loads, ensure selected primary is valid
      watch(
        primaries,
        (list) => {
          if (Array.isArray(list) && list.length && !list.includes(primary.value)) {
            primary.value = list[0];
          }
        },
        { immediate: true },
      );

      // Etiketler utils'ten
      function tPrimary(p) {
        return primaryLabel(p);
      }
      function tSub(s) {
        return subLabel(s);
      }

      return {
        store,
        mobileOpen,
        isRefreshing,
        totalItems,
        primaries,
        primary,
        sub,
        subsForPrimary,
        filtered,
        tPrimary,
        tSub,
        noteModal,
        openAddNote,
        closeNoteModal,
        confirmAddWithNote,
        onRefresh,
      };
    },
  };
</script>

<style scoped>
  .grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
</style>
