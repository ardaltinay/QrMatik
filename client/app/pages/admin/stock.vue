<template>
  <div class="p-4 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 mb-10">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight flex items-center gap-3">
          {{ $t('admin.stock.title') }}
          <div class="flex h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.5)]"></div>
        </h1>
        <p class="text-slate-500 font-medium mt-1">{{ $t('admin.stock.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full lg:w-auto">
        <div class="relative flex-1 sm:min-w-[300px]">
          <svg class="w-5 h-5 absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.stock.searchPlaceholder')"
            class="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-white border border-slate-200 focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none shadow-sm font-medium"
          />
        </div>
      </div>
    </div>

    <!-- Quick Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-10">
      <div class="bg-white rounded-[2rem] p-6 border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center gap-5">
        <div class="w-14 h-14 rounded-2xl bg-brand-50 text-brand-600 flex items-center justify-center border border-brand-100">
          <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
        </div>
        <div>
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-0.5">{{ $t('admin.stock.stats.totalProducts') }}</p>
          <h3 class="text-3xl font-black text-slate-900 tracking-tight">{{ stockItems.length }}</h3>
        </div>
      </div>

      <div class="bg-white rounded-[2rem] p-6 border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center gap-5 relative overflow-hidden group">
        <div class="absolute -right-4 -bottom-4 w-20 h-20 bg-rose-50 rounded-full opacity-50 group-hover:scale-150 transition-transform duration-500"></div>
        <div class="w-14 h-14 rounded-2xl bg-rose-50 text-rose-500 flex items-center justify-center border border-rose-100 relative z-10">
          <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
        </div>
        <div class="relative z-10">
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-0.5">{{ $t('admin.stock.stats.outOfStock') }}</p>
          <h3 class="text-3xl font-black text-slate-900 tracking-tight">{{ stockItems.filter(i => i.stockQuantity <= 0).length }}</h3>
        </div>
      </div>

      <div class="md:col-span-2 bg-slate-900 rounded-[2rem] p-6 border border-slate-800 shadow-2xl flex items-center justify-between group overflow-hidden relative">
        <div class="absolute inset-0 bg-gradient-to-r from-brand-600/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
        <div class="flex items-center gap-5 relative z-10">
           <div class="w-14 h-14 rounded-2xl bg-white/10 text-white flex items-center justify-center backdrop-blur-md">
              <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" /></svg>
           </div>
           <div>
              <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-0.5">{{ $t('admin.stock.syncTitle') }}</p>
              <h3 class="text-lg font-bold text-white tracking-tight">{{ $t('admin.stock.syncSubtitle') }}</h3>
           </div>
        </div>
      </div>
    </div>

    <!-- Main Content Container with Premium Lock -->
    <div class="relative min-h-[400px]">
      <!-- PREMIUM OVERLAY -->
      <div v-if="!isProPlan" class="absolute inset-x-0 top-0 bottom-0 z-30 backdrop-blur-md bg-white/40 rounded-[2.5rem] flex items-center justify-center p-4 sm:p-6 text-center border-2 border-white/50">
        <div class="bg-white p-8 sm:p-12 rounded-[3rem] shadow-[0_32px_64px_-16px_rgba(0,0,0,0.2)] border border-slate-100 w-full max-w-lg animate-in fade-in zoom-in slide-in-from-bottom-8 duration-700 ease-out">
          <div class="w-20 h-20 bg-indigo-50 text-indigo-600 rounded-[2rem] flex items-center justify-center mx-auto mb-8 shadow-inner border border-indigo-100">
             <svg class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>
          </div>
          <h3 class="text-2xl font-black text-slate-900 mb-4 tracking-tighter uppercase">{{ $t('admin.upgrade.plans.PRO') }} {{ $t('admin.stock.premium.required') }}</h3>
          <p class="text-slate-500 font-medium mb-10 leading-relaxed">{{ $t('admin.stock.premium.subtitle') }}</p>
          <NuxtLink to="/admin/upgrade" class="inline-flex items-center gap-3 px-10 py-5 bg-brand-600 text-white font-black text-xs uppercase tracking-[0.2em] rounded-2xl hover:bg-brand-700 shadow-xl shadow-brand-500/40 transition-all hover:-translate-y-1">
            {{ $t('admin.upgrade.upgradeBtn') }}
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M13 7l5 5m0 0l-5 5m5-5H6" /></svg>
          </NuxtLink>
        </div>
      </div>

      <div class="bg-white rounded-[2.5rem] border border-slate-100 shadow-2xl shadow-slate-200/60 overflow-hidden" :class="!isProPlan ? 'opacity-20 pointer-events-none grayscale' : ''">
      <!-- Desktop View -->
      <div class="hidden md:block overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/50 border-b border-slate-100 text-slate-400 text-[10px] font-black uppercase tracking-[0.2em]">
              <th class="px-8 py-6">{{ $t('admin.stock.columns.product') }}</th>
              <th class="px-8 py-6">{{ $t('admin.stock.columns.category') }}</th>
              <th class="px-8 py-6">{{ $t('admin.stock.columns.currentStock') }}</th>
              <th class="px-8 py-6 text-right">{{ $t('admin.stock.columns.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="item in filteredItems" :key="item.id" class="hover:bg-slate-50/70 transition-all group">
              <td class="px-8 py-5">
                <div class="flex items-center gap-4">
                  <div class="w-12 h-12 rounded-xl bg-slate-100 overflow-hidden shrink-0">
                    <NuxtImg v-if="item.image" :src="item.image" class="w-full h-full object-cover" />
                    <div v-else class="w-full h-full flex items-center justify-center text-slate-300 bg-slate-50">
                       <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
                    </div>
                  </div>
                  <div>
                    <div class="font-bold text-slate-900">{{ item.name }}</div>
                    <div class="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-0.5">ID: #{{ item.id.toString().slice(-4) }}</div>
                  </div>
                </div>
              </td>
              <td class="px-8 py-5">
                <span class="px-3 py-1.5 rounded-xl text-[10px] font-black bg-slate-100 text-slate-500 uppercase tracking-widest border border-slate-200">
                  {{ item.category }}
                </span>
              </td>
              <td class="px-8 py-5">
                <div class="flex items-center gap-3">
                   <div class="text-xl font-black tracking-tight" :class="item.stockQuantity <= 0 ? 'text-rose-500' : 'text-slate-900'">
                      {{ item.stockQuantity }}
                   </div>
                   <div v-if="item.stockQuantity <= 5 && item.stockQuantity > 0" class="px-2 py-0.5 bg-amber-50 text-amber-600 text-[9px] font-black rounded uppercase tracking-tighter border border-amber-100">{{ $t('admin.stock.lowStock') }}</div>
                </div>
              </td>
              <td class="px-8 py-5 text-right">
                <div class="flex items-center justify-end gap-2">
                  <div class="flex bg-slate-100 p-1 rounded-xl border border-slate-200">
                    <button @click="adjustStock(item, -1)" class="w-8 h-8 flex items-center justify-center text-slate-500 hover:text-rose-500 hover:bg-white rounded-lg transition-all active:scale-90">-</button>
                    <button @click="adjustStock(item, 1)" class="w-8 h-8 flex items-center justify-center text-slate-500 hover:text-emerald-500 hover:bg-white rounded-lg transition-all active:scale-90">+</button>
                  </div>
                  <button @click="openModal(item)" class="p-3 bg-white text-slate-400 hover:text-brand-600 hover:bg-brand-50 border border-slate-100 hover:border-brand-200 rounded-2xl shadow-sm transition-all" :title="$t('admin.common.edit')">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile View -->
      <div class="md:hidden divide-y divide-slate-50">
        <div v-for="item in filteredItems" :key="item.id" class="p-5 active:bg-slate-50 transition-colors">
          <div class="flex items-center gap-4">
            <div class="w-16 h-16 rounded-2xl bg-slate-100 overflow-hidden shrink-0 shadow-sm border border-slate-200">
               <NuxtImg v-if="item.image" :src="item.image" class="w-full h-full object-cover" />
            </div>
            <div class="flex-1 min-w-0">
               <div class="flex justify-between items-start mb-1">
                  <h4 class="font-bold text-slate-900 truncate pr-2 leading-tight">{{ item.name }}</h4>
                  <span class="text-xl font-black" :class="item.stockQuantity <= 0 ? 'text-rose-500' : 'text-slate-900'">{{ item.stockQuantity }}</span>
               </div>
               <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-3">{{ item.category }}</p>
               <div class="flex gap-2">
                  <div class="flex-1 flex bg-slate-100 p-1 rounded-xl border border-slate-200">
                    <button @click="adjustStock(item, -1)" class="flex-1 py-2 flex items-center justify-center text-slate-500 font-bold">-</button>
                    <div class="w-[1px] bg-slate-200 my-1"></div>
                    <button @click="adjustStock(item, 1)" class="flex-1 py-2 flex items-center justify-center text-slate-500 font-bold">+</button>
                  </div>
                  <button @click="openModal(item)" class="px-4 py-2 bg-brand-50 text-brand-600 rounded-xl text-[10px] font-black uppercase tracking-widest border border-brand-100">
                    {{ $t('admin.stock.setBtn') }}
                  </button>
               </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="!loading && filteredItems.length === 0" class="py-32 px-8 text-center flex flex-col items-center justify-center">
        <div class="w-24 h-24 bg-slate-50 rounded-full flex items-center justify-center text-slate-200 mb-6 border border-slate-100">
          <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
        </div>
        <h3 class="text-xl font-black text-slate-900 mb-2">{{ $t('admin.stock.emptyState') }}</h3>
        <p class="text-slate-500 font-medium max-w-sm mb-8 text-sm">{{ $t('admin.stock.emptyStateSubtitle') }}</p>
      </div>
      </div>
    </div>

    <!-- MODERN MODAL: Update Stock -->
    <div v-if="isModalOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-md transition-opacity duration-300" @click="closeModal"></div>
      
      <div class="relative w-full max-w-sm bg-white rounded-[2.5rem] shadow-2xl overflow-hidden animate-modal-in border border-white/20">
        <div class="p-10 text-center">
           <div class="w-20 h-20 bg-brand-50 text-brand-600 rounded-[2rem] flex items-center justify-center mx-auto mb-6 border border-brand-100">
              <svg class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
           </div>
           
           <h3 class="text-2xl font-black text-slate-900 mb-2 tracking-tight">{{ selectedItem?.name }}</h3>
           <p class="text-slate-400 font-medium text-sm mb-8 italic">{{ $t('admin.stock.modal.updateSubtitle') }}</p>
           
           <div class="mb-10">
              <label class="block text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] mb-4">{{ $t('admin.stock.modal.quantityLabel') }}</label>
              <div class="flex items-center gap-4">
                 <button @click="tempQuantity = Math.max(0, tempQuantity - 5)" class="w-14 h-14 bg-slate-100 text-slate-900 rounded-2xl font-black text-xl hover:bg-slate-200 transition-all active:scale-90 shadow-inner">-5</button>
                 <input v-model.number="tempQuantity" type="number" min="0" class="flex-1 h-14 bg-white border border-slate-200 rounded-2xl text-center font-black text-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none" />
                 <button @click="tempQuantity += 5" class="w-14 h-14 bg-slate-100 text-slate-900 rounded-2xl font-black text-xl hover:bg-slate-200 transition-all active:scale-90 shadow-inner">+5</button>
              </div>
           </div>

           <div class="flex flex-col gap-3">
              <button @click="saveStock" class="w-full py-4.5 bg-brand-600 text-white font-black rounded-2xl hover:bg-brand-700 shadow-xl shadow-brand-500/30 transition-all uppercase tracking-widest text-sm" :disabled="saving">
                 {{ saving ? $t('admin.stock.modal.savingBtn') : $t('admin.stock.modal.saveBtn') }}
              </button>
              <button @click="closeModal" class="w-full py-4 text-slate-400 font-black hover:text-slate-600 transition-all uppercase tracking-widest text-xs">
                 {{ $t('admin.stock.modal.cancelBtn') }}
              </button>
           </div>
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
const { isProPlan } = useTenant()

const stockItems = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')
const isModalOpen = ref(false)
const selectedItem = ref<any>(null)
const tempQuantity = ref(0)
const saving = ref(false)

async function loadStock() {
  if (!isProPlan.value) {
    loading.value = false
    return
  }
  loading.value = true
  try {
    const data = await fetchJson('/api/menu/stock')
    // Filter items that have stock tracking enabled
    stockItems.value = (Array.isArray(data) ? data : []).filter(i => i.stockEnabled)
  } catch (e) {
    console.error('Failed to load stock', e)
  } finally {
    loading.value = false
  }
}

const filteredItems = computed(() => {
  if (!searchQuery.value) return stockItems.value
  const q = searchQuery.value.toLowerCase()
  return stockItems.value.filter(i => 
    i.name.toLowerCase().includes(q) || 
    i.category.toLowerCase().includes(q)
  )
})

function openModal(item: any) {
  selectedItem.value = item
  tempQuantity.value = item.stockQuantity || 0
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
  selectedItem.value = null
}

async function adjustStock(item: any, amount: number) {
  const newQty = Math.max(0, (item.stockQuantity || 0) + amount)
  try {
    const payload = { ...item, stockQuantity: newQty }
    await fetchJson(`/api/menu/${item.id}`, {
      method: 'PUT',
      body: JSON.stringify(payload)
    })
    item.stockQuantity = newQty
    uiStore.success(`${item.name} ${t('admin.stock.saveSuccess')}: ${newQty}`)
  } catch (e) {
    uiStore.error(t('admin.stock.updateError'))
  }
}

async function saveStock() {
  if (!selectedItem.value) return
  saving.value = true
  try {
    const payload = { ...selectedItem.value, stockQuantity: tempQuantity.value }
    await fetchJson(`/api/menu/${selectedItem.value.id}`, {
      method: 'PUT',
      body: JSON.stringify(payload)
    })
    const idx = stockItems.value.findIndex(i => i.id === selectedItem.value.id)
    if (idx !== -1) stockItems.value[idx].stockQuantity = tempQuantity.value
    uiStore.success(t('admin.stock.saveSuccess'))
    closeModal()
  } catch (e) {
    uiStore.error(t('admin.stock.genericError'))
  } finally {
    saving.value = false
  }
}

onMounted(loadStock)

useHead({
  title: () => `${t('admin.stock.title')} | Admin`
})
</script>

<style scoped>
@keyframes modal-in {
  from { transform: translateY(30px) scale(0.95); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}
.animate-modal-in { animation: modal-in 0.5s cubic-bezier(0.16, 1, 0.3, 1); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
