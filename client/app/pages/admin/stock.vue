<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.stock.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.stock.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
        <div class="relative w-full sm:w-64">
          <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.stock.searchPlaceholder')"
            :disabled="currentPlan !== 'PRO'"
            class="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm disabled:opacity-50 disabled:bg-slate-50"
          />
        </div>
      </div>
    </div>

    <div class="relative">
      <!-- Premium Overlay for FREE/STANDARD Users -->
      <div v-if="currentPlan !== 'PRO'" class="absolute inset-0 z-50 backdrop-blur-[2px] bg-white/30 rounded-2xl flex items-center justify-center p-6 text-center">
        <div class="bg-white p-8 rounded-3xl shadow-xl border border-slate-200 max-w-md animate-in fade-in zoom-in duration-300">
          <div class="w-16 h-16 bg-brand-100 text-brand-600 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-2">{{ $t('admin.stock.premiumTitle') }}</h3>
          <p class="text-slate-500 mb-8">{{ $t('admin.stock.premiumDesc') }}</p>
          <NuxtLink :to="localePath('/admin/upgrade')" class="inline-flex items-center justify-center px-8 py-3 bg-brand-500 text-white font-bold rounded-xl hover:bg-brand-600 transition-all shadow-lg shadow-brand-500/25">
            {{ $t('admin.upgrade.button') }}
          </NuxtLink>
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-left border-collapse">
            <thead>
              <tr class="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs uppercase tracking-wider">
                <th class="px-6 py-4 font-semibold w-16"></th>
                <th class="px-6 py-4 font-semibold">{{ $t('admin.stock.columns.name') }}</th>
                <th class="px-6 py-4 font-semibold">{{ $t('admin.stock.columns.category') }}</th>
                <th class="px-6 py-4 font-semibold">{{ $t('admin.stock.columns.currentStock') }}</th>
                <th class="px-6 py-4 font-semibold text-right">{{ $t('admin.stock.columns.actions') }}</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              <tr v-if="loading" class="animate-pulse">
                <td colspan="5" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.common.loading') }}</td>
              </tr>
              <tr v-else-if="filteredStock.length === 0">
                <td colspan="5" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.stock.emptyState') }}</td>
              </tr>
              <tr v-for="item in filteredStock" :key="item.id" class="hover:bg-slate-50/80 transition-colors">
                <td class="px-6 py-4">
                  <div v-if="item.image" class="w-10 h-10 rounded-lg bg-slate-100 overflow-hidden border border-slate-200 shrink-0">
                    <NuxtImg :src="item.image" :alt="item.name" format="webp" class="w-full h-full object-cover" loading="lazy" />
                  </div>
                  <div v-else class="w-10 h-10 rounded-lg bg-slate-100 flex items-center justify-center text-slate-400 border border-slate-200 shrink-0">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <div class="font-bold text-slate-800">{{ item.name }}</div>
                </td>
                <td class="px-6 py-4">
                  <span class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
                    {{ $t(`menu.categories.${item.category}`) || item.category }}
                  </span>
                </td>
                <td class="px-6 py-4">
                  <span class="inline-flex items-center justify-center px-4 py-1 rounded-full text-sm font-bold border min-w-[3rem]" 
                    :class="item.stockQuantity && item.stockQuantity > 5 ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : (item.stockQuantity && item.stockQuantity > 0 ? 'bg-amber-50 text-amber-700 border-amber-200' : 'bg-rose-50 text-rose-700 border-rose-200')">
                    {{ item.stockQuantity || 0 }}
                  </span>
                </td>
                <td class="px-6 py-4 text-right">
                  <div class="flex items-center justify-end gap-2">
                    <div class="flex items-center bg-slate-50 border border-slate-200 rounded-lg overflow-hidden focus-within:ring-2 focus-within:ring-brand-500/20 focus-within:border-brand-500 transition-all w-32">
                      <button @click="quickUpdate(item, -1)" class="w-8 h-10 flex items-center justify-center text-slate-500 hover:bg-slate-200 hover:text-slate-800 transition-colors" :disabled="updating === item.id">
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M20 12H4" />
                        </svg>
                      </button>
                      <input 
                        v-model.number="item._tempStock" 
                        type="number" 
                        min="0"
                        class="w-full h-10 bg-transparent text-center font-bold text-slate-800 outline-none p-0 border-0 focus:ring-0 appearance-none" 
                        :disabled="updating === item.id"
                        @change="saveStock(item)"
                      />
                      <button @click="quickUpdate(item, 1)" class="w-8 h-10 flex items-center justify-center text-slate-500 hover:bg-slate-200 hover:text-slate-800 transition-colors" :disabled="updating === item.id">
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                        </svg>
                      </button>
                    </div>
                    
                    <button @click="saveStock(item)" class="h-10 px-4 flex items-center justify-center text-sm font-bold rounded-lg transition-colors" 
                      :class="item.stockQuantity !== item._tempStock ? 'bg-brand-500 text-white hover:bg-brand-600 shadow-sm ' : 'bg-slate-100 text-slate-400 cursor-default'"
                      :disabled="updating === item.id || item.stockQuantity === item._tempStock">
                      <svg v-if="updating === item.id" class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                      </svg>
                      <span v-else>{{ $t('admin.common.save') }}</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()
const localePath = useLocalePath()
const currentPlan = computed(() => authStore.user?.tenant?.subscriptionPlan || 'FREE')

useHead({
  title: () => `${t('admin.stock.title')} | Admin`
})

// State
const stockItems = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')
const updating = ref<number | string | null>(null)

// Derived
const filteredStock = computed(() => {
  let list = stockItems.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter((m: any) => m.name.toLowerCase().includes(q))
  }
  // Sort by stock quantity (low to high) to prioritize critical items
  return list.sort((a, b) => (a.stockQuantity || 0) - (b.stockQuantity || 0))
})

// Methods
async function loadStock() {
  loading.value = true
  try {
    const data = await fetchJson('/api/menu')
    if (Array.isArray(data)) {
      // Filter only items with stock tracking enabled
      stockItems.value = data
        .filter(m => m.stockEnabled === true || m.stockEnabled === 1)
        .map(m => ({ ...m, _tempStock: m.stockQuantity || 0 }))
    } else {
      stockItems.value = []
    }
  } catch (e) {
    console.error('Failed to load menu for stock', e)
  } finally {
    loading.value = false
  }
}

function quickUpdate(item: any, delta: number) {
  let current = parseInt(item._tempStock, 10) || 0
  let next = current + delta
  if (next < 0) next = 0
  item._tempStock = next
}

async function saveStock(item: any) {
  if (item.stockQuantity === item._tempStock) return
  
  updating.value = item.id
  try {
    // We send PUT /api/menu/:id
    const payload = {
      ...item,
      stockQuantity: item._tempStock
    }
    delete payload._tempStock

    await fetchJson(`/api/menu/${item.id}`, {
      method: 'PUT',
      body: JSON.stringify(payload)
    })
    
    // Update local state to reflect successful save
    item.stockQuantity = item._tempStock
    uiStore.success(t('admin.stock.updateSuccess'))
    
  } catch (e: any) {
    const errorMessage = e?.message || e?.toString() || t('errors.serverError');
    uiStore.error(errorMessage);
    // Revert
    item._tempStock = item.stockQuantity || 0
  } finally {
    updating.value = null
  }
}

onMounted(() => {
  loadStock()
})
</script>

<style scoped>
/* Remove arrows from number input */
input[type=number]::-webkit-inner-spin-button, 
input[type=number]::-webkit-outer-spin-button { 
  -webkit-appearance: none; 
  margin: 0; 
}
</style>
