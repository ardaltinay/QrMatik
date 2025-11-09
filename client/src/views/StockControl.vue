<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h2 class="text-xl font-semibold">Stok Kontrolü</h2>
      <button
        v-if="!isPro"
        @click="goUpgrade"
        class="rounded bg-brand-500 px-3 py-2 text-white shadow hover:shadow-md"
      >
        Pro'ya Yükselt
      </button>
    </div>

    <div v-if="!isPro" class="rounded border border-amber-200 bg-amber-50 p-4 text-amber-800">
      Bu özellik yalnızca Pro plan için kullanılabilir. Planınızı yükselterek ürün stoklarını
      yönetebilir, tükenen ürünleri menüde otomatik gizleyebilirsiniz.
    </div>

    <div v-else>
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div class="text-sm text-gray-600">Toplam ürün: {{ items.length }}</div>
        <div class="flex items-center gap-2">
          <button
            @click="load"
            :disabled="loading"
            class="rounded border px-3 py-1 text-sm hover:bg-gray-50 disabled:opacity-50"
          >
            Yenile
          </button>
        </div>
      </div>

      <!-- Desktop/tablet: tablo -->
      <div class="overflow-x-auto hidden md:block">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-3 py-2 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                Ürün
              </th>
              <th class="px-3 py-2 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                Kategori
              </th>
              <th class="px-3 py-2 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                Stok Aktif
              </th>
              <th class="px-3 py-2 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                Miktar
              </th>
              <th class="px-3 py-2"></th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100 bg-white">
            <tr v-for="it in items" :key="it.id" class="hover:bg-gray-50">
              <td class="px-3 py-2">
                <div class="flex items-center gap-3">
                  <img
                    v-if="it.image"
                    :src="it.image"
                    class="h-10 w-10 rounded object-cover"
                    alt=""
                  />
                  <div>
                    <div class="font-medium">{{ it.name }}</div>
                    <div class="text-xs text-gray-500">{{ it.subcategory || '' }}</div>
                  </div>
                </div>
              </td>
              <td class="px-3 py-2 text-sm text-gray-600">{{ it.category }}</td>
              <td class="px-3 py-2">
                <label class="inline-flex items-center gap-2 text-sm">
                  <input type="checkbox" v-model="it.stockEnabled" @change="queueSave(it)" />
                  <span class="text-gray-700">Aktif</span>
                </label>
              </td>
              <td class="px-3 py-2">
                <input
                  type="number"
                  min="0"
                  :disabled="!it.stockEnabled"
                  v-model.number="it.stockQuantity"
                  @change="queueSave(it)"
                  class="w-24 rounded border p-1 disabled:opacity-50"
                />
              </td>
              <td class="px-3 py-2 text-right text-sm">
                <span v-if="saving[it.id]" class="text-gray-400">Kaydediliyor…</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobil: kart listesi -->
      <div class="md:hidden space-y-3">
        <div
          v-for="it in items"
          :key="it.id"
          class="rounded-lg border bg-white p-3 shadow-sm"
        >
          <div class="flex items-center gap-3">
            <img v-if="it.image" :src="it.image" class="h-12 w-12 rounded object-cover" alt="" />
            <div>
              <div class="font-medium leading-tight">{{ it.name }}</div>
              <div class="text-xs text-gray-500">
                {{ it.category }}<span v-if="it.subcategory"> • {{ it.subcategory }}</span>
              </div>
            </div>
          </div>
          <div class="mt-3 flex items-center justify-between">
            <label class="inline-flex items-center gap-2 text-sm">
              <input type="checkbox" v-model="it.stockEnabled" @change="queueSave(it)" />
              <span class="text-gray-700">Stok aktif</span>
            </label>
            <div class="flex items-center gap-2">
              <input
                type="number"
                min="0"
                :disabled="!it.stockEnabled"
                v-model.number="it.stockQuantity"
                @change="queueSave(it)"
                class="w-24 rounded border p-1 text-sm disabled:opacity-50"
              />
            </div>
          </div>
          <div class="mt-1 text-right text-xs text-gray-400" v-if="saving[it.id]">Kaydediliyor…</div>
        </div>
      </div>

      <div v-if="!loading && items.length === 0" class="p-6 text-center text-sm text-gray-500">
        Henüz menü ürünü yok. Önce Menü Yönetimi'nden ürün ekleyin.
      </div>
    </div>
  </div>
</template>

<script>
  import { ref, onMounted, computed } from "vue";
  import { fetchJson } from "@/utils/api";
  import { useUiStore } from "@/stores/uiStore";
  import { useRouter } from "vue-router";

  export default {
    name: "StockControl",
    setup() {
      const items = ref([]);
      const loading = ref(false);
      const saving = ref({});
      const ui = useUiStore();
      const router = useRouter();

      const isPro = computed(() => {
        try {
          const raw = localStorage.getItem("qm_tenant_cfg");
          if (!raw) return false;
          const cfg = JSON.parse(raw);
          const plan = String(cfg?.plan || "").toUpperCase();
          return plan === "PRO";
        } catch {
          return false;
        }
      });

      async function load() {
        if (!isPro.value) return;
        loading.value = true;
        try {
          const data = await fetchJson("/api/stock/items");
          items.value = Array.isArray(data) ? data : [];
        } catch (e) {
          // fetchJson shows toast; if 402, show upgrade CTA
        } finally {
          loading.value = false;
        }
      }

      function goUpgrade() {
        router.push("/admin/upgrade");
      }

      let debounceTimers = {};
      function queueSave(it) {
        const id = it.id;
        if (debounceTimers[id]) clearTimeout(debounceTimers[id]);
        debounceTimers[id] = setTimeout(() => save(it), 400);
      }

      async function save(it) {
        saving.value = Object.assign({}, saving.value, { [it.id]: true });
        try {
          const body = {
            stockEnabled: !!it.stockEnabled,
            stockQuantity:
              it.stockEnabled && typeof it.stockQuantity === "number"
                ? Math.max(0, Math.floor(it.stockQuantity))
                : null,
          };
          const updated = await fetchJson(`/api/stock/items/${it.id}`, {
            method: "PUT",
            body: JSON.stringify(body),
          });
          // Replace local item with server-confirmed values
          const idx = items.value.findIndex((x) => x.id === it.id);
          if (idx >= 0) items.value[idx] = Object.assign({}, items.value[idx], updated);
          ui.toastSuccess("Kaydedildi");
        } catch (e) {
          // error toast already shown
        } finally {
          const s = Object.assign({}, saving.value);
          delete s[it.id];
          saving.value = s;
        }
      }

      onMounted(load);

      return { items, loading, saving, isPro, load, queueSave, goUpgrade };
    },
  };
</script>
