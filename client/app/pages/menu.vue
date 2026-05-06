<template>
  <div class="min-h-screen bg-[#FAF9F6] pb-32 font-sans" :style="{ fontFamily: tenantConfig?.fontFamily || 'Plus Jakarta Sans, sans-serif' }">
    <!-- Premium Concave Header -->
    <header class="relative w-full h-[360px] bg-slate-900 overflow-hidden">
      <!-- High-end Background Pattern or Gradient -->
      <div class="absolute inset-0 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 opacity-90"></div>
      
      <div class="container-custom relative z-10 h-full flex flex-col items-center justify-center pt-8">
        <!-- Logo -->
        <div class="flex justify-center mb-6">
          <div v-if="tenantConfig?.logoUrl" class="h-20 w-20 rounded-[2rem] overflow-hidden bg-white p-2 shadow-2xl">
            <img :src="tenantConfig.logoUrl" class="h-full w-full object-contain" />
          </div>
          <div v-else class="h-20 w-20 rounded-[2rem] bg-brand-400 flex items-center justify-center text-white font-black text-3xl shadow-2xl">
            {{ (tenantConfig?.name || 'F').charAt(0) }}
          </div>
        </div>

        <h1 class="text-3xl sm:text-4xl font-bold text-white mb-2 tracking-tight">
          {{ tenantConfig?.welcomeMessage || tenantConfig?.name || $t('common.menu') }}
        </h1>
        <p class="text-white/60 text-sm font-medium max-w-md mx-auto text-center px-6">
          {{ tenantConfig?.tagline || $t('menu.browseAndOrder') }}
        </p>

        <!-- Table Badge -->
        <div v-if="tableCode" class="mt-6 inline-flex items-center gap-2 bg-brand-400 text-white px-5 py-2 rounded-full text-[10px] font-bold uppercase tracking-[0.2em] shadow-lg">
          <span class="opacity-70">{{ $t('order.table') }}</span>
          <span>{{ tableCode }}</span>
        </div>
      </div>

      <!-- Concave Curve SVG -->
      <div class="absolute bottom-0 left-0 w-full leading-[0] transform rotate-180">
        <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-[80px]">
          <path d="M0 120L1440 120V0C1440 0 1140 120 720 120C300 120 0 0 0 0V120Z" fill="#FAF9F6"/>
        </svg>
      </div>
    </header>

    <!-- Categories Pill Bar (Floating) -->
    <div class="sticky top-0 z-30 bg-[#FAF9F6]/80 backdrop-blur-2xl py-6 border-b border-slate-100">
      <div class="container-custom">
        <div class="flex items-center gap-3 overflow-x-auto scrollbar-hide px-1">
          <button
            v-for="cat in categories"
            :key="cat"
            @click="selectedCategory = cat"
            class="whitespace-nowrap px-8 py-3.5 rounded-full text-xs font-bold uppercase tracking-widest transition-all duration-300"
            :class="selectedCategory === cat ? 'bg-slate-950 text-white shadow-xl scale-105' : 'bg-white text-slate-400 border border-slate-100 hover:bg-slate-50'"
          >
            {{ $te(`menu.categories.${cat}`) ? $t(`menu.categories.${cat}`) : formatCategoryName(cat) }}
          </button>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <main class="container-custom py-8">
      <!-- Allergen Warning -->
      <div class="mb-12 p-6 rounded-[2rem] bg-white border border-slate-100 flex items-start gap-4 shadow-sm">
        <div class="w-10 h-10 rounded-xl bg-brand-50 flex items-center justify-center shrink-0">
          <svg class="w-5 h-5 text-brand-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        </div>
        <p class="text-sm font-medium text-slate-500 leading-relaxed">{{ $t('menu.allergenWarning') }}</p>
      </div>

      <!-- Menu Grid -->
      <div v-if="orderStore.loading && orderStore.menu.length === 0" class="flex flex-col items-center justify-center py-32">
        <div class="w-12 h-12 border-4 border-brand-200 border-t-brand-400 rounded-full animate-spin"></div>
      </div>
      
      <div v-else-if="filteredMenu.length === 0" class="text-center py-32 text-slate-400 font-medium">
        {{ $t('menu.emptyCategory') }}
      </div>

      <div v-else class="grid grid-cols-1 xl:grid-cols-2 gap-6 md:gap-8">
        <MenuItemCard 
          v-for="item in filteredMenu" 
          :key="item.id" 
          :item="item" 
          @add="openNoteModal" 
        />
      </div>
    </main>

    <!-- Unified Floating Cart -->
    <Transition name="fade-up">
      <div v-if="orderStore.cartCount > 0 && !isCartOpen" class="fixed bottom-8 left-1/2 -translate-x-1/2 z-40 w-full max-w-md px-6">
        <button 
          @click="isCartOpen = true"
          class="w-full bg-slate-950 text-white rounded-[2rem] p-2 flex items-center justify-between shadow-2xl active:scale-95 transition-all group overflow-hidden relative"
        >
          <div class="absolute inset-0 bg-brand-400 opacity-0 group-hover:opacity-10 transition-opacity"></div>
          
          <div class="flex items-center gap-4 relative z-10">
            <div class="bg-brand-400 w-12 h-12 rounded-2xl flex items-center justify-center font-black text-xl shadow-lg rotate-3 group-hover:rotate-0 transition-transform">
              {{ orderStore.cartCount }}
            </div>
            <div class="flex flex-col items-start">
              <span class="text-[10px] font-black uppercase tracking-[0.2em] opacity-60 leading-none mb-1">{{ $t('menu.cart') }}</span>
              <span class="font-bold text-sm">{{ $t('menu.viewCart') }}</span>
            </div>
          </div>
          <div class="bg-white/10 px-6 py-3 rounded-2xl font-black text-lg mr-1 backdrop-blur-md border border-white/5 relative z-10">
            {{ formatPrice(orderStore.cartTotal) }}
          </div>
        </button>
      </div>
    </Transition>

    <CartDrawer v-model:isOpen="isCartOpen" :tableCode="tableCode" @orderSuccess="onOrderSuccess" />

    <!-- Add Note Modal (Styled for Organic Tech) -->
    <Transition name="fade">
      <div v-if="noteModal.open" class="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-6">
        <div class="absolute inset-0 bg-slate-950/20 backdrop-blur-md" @click="closeNoteModal"></div>
        <div class="relative w-full max-w-md bg-white rounded-[3rem] p-10 shadow-2xl border border-slate-100">
          <div class="flex justify-between items-start mb-8">
            <div>
              <h3 class="text-2xl font-bold text-slate-900 mb-1">{{ $t('menu.itemDetail') }}</h3>
              <p class="text-sm text-slate-400 font-bold uppercase tracking-wider">{{ getLocalizedName(noteModal.item) }}</p>
            </div>
          </div>

          <div class="mb-10 p-6 bg-slate-50 rounded-[2rem] flex items-center justify-between">
            <span class="text-xs font-bold uppercase tracking-widest text-slate-400">{{ $t('menu.quantity') }}</span>
            <div class="flex items-center gap-6">
              <button @click="noteModal.qty = Math.max(1, noteModal.qty - 1)" class="w-10 h-10 rounded-full bg-white shadow-sm flex items-center justify-center text-xl font-bold disabled:opacity-30" :disabled="noteModal.qty <= 1">-</button>
              <span class="text-xl font-bold w-4 text-center">{{ noteModal.qty }}</span>
              <button @click="noteModal.qty++" class="w-10 h-10 rounded-full bg-white shadow-sm flex items-center justify-center text-xl font-bold">+</button>
            </div>
          </div>

          <div class="mb-10">
            <label class="block text-xs font-bold uppercase tracking-widest text-slate-400 mb-4 ml-2">{{ $t('menu.specialRequest') }}</label>
            <textarea
              v-model="noteModal.note"
              rows="3"
              class="w-full bg-slate-50 rounded-[2rem] p-6 text-slate-900 border-none focus:ring-2 focus:ring-brand-400 transition-all outline-none resize-none font-medium"
              :placeholder="$t('menu.requestPlaceholder')"
            ></textarea>
          </div>

          <button 
            @click="confirmAddWithNote"
            class="w-full py-5 rounded-full bg-slate-950 text-white font-bold text-sm uppercase tracking-widest shadow-xl hover:bg-brand-950 active:scale-95 transition-all"
          >
            {{ $t('menu.addToCart') }} • {{ formatPrice(getLocalizedItemPrice(noteModal.item) * noteModal.qty) }}
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'
import { useTenant } from '~/composables/useTenant'

definePageMeta({ layout: 'customer' })

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()
const orderStore = useOrderStore()
const { tenantConfig, detectTenant, loadTenantConfig } = useTenant()

const tableCode = ref<string>('')
const isCartOpen = ref(false)
const hasSession = ref(false)

onMounted(() => {
  if (import.meta.client) {
    hasSession.value = !!localStorage.getItem('qm_order_session')
  }
  const tParam = route.query.t || route.query.table
  if (tParam) {
    tableCode.value = String(tParam)
    localStorage.setItem('qm_table_code', tableCode.value)
  } else {
    const stored = localStorage.getItem('qm_table_code')
    if (stored) tableCode.value = stored
  }
  loadData()
})

async function loadData() {
  const code = detectTenant()
  if (code) await loadTenantConfig(code)
  if (orderStore.menu.length === 0) await orderStore.loadMenu()
}

function formatCategoryName(cat: string) {
  if (!cat) return ''
  return cat.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(' ')
}

const categories = computed(() => {
  const set = new Set<string>()
  orderStore.menu.forEach(m => {
    if (m.category) set.add(m.category)
  })
  return Array.from(set)
})

const selectedCategory = ref<string>('')
watch(categories, (cats) => {
  if (cats.length > 0 && (!selectedCategory.value || !cats.includes(selectedCategory.value))) {
    selectedCategory.value = cats[0] || ''
  }
}, { immediate: true })

const filteredMenu = computed(() => {
  return orderStore.menu.filter(m => m.category === selectedCategory.value)
})

const noteModal = ref({ open: false, item: null as any, note: '', qty: 1 })
function openNoteModal(item: any) { noteModal.value = { open: true, item, note: '', qty: 1 } }
function closeNoteModal() { noteModal.value.open = false }
function confirmAddWithNote() {
  if (noteModal.value.item?.id) orderStore.addToCart(noteModal.value.item.id, noteModal.value.qty, noteModal.value.note)
  closeNoteModal()
}

function getLocalizedName(item: any) {
  return (locale.value === 'en' && item?.nameEn) ? item.nameEn : item?.name || ''
}
function getLocalizedItemPrice(item: any) {
  return (locale.value === 'en' && item?.priceUsd > 0) ? item.priceUsd : item?.price || 0
}
function formatPrice(p: number) {
  return new Intl.NumberFormat(locale.value === 'en' ? 'en-US' : 'tr-TR', { 
    style: 'currency', currency: locale.value === 'en' ? 'USD' : 'TRY' 
  }).format(p)
}
function onOrderSuccess(order: any) {
  hasSession.value = true
  if (order?.id) router.push(`/order/${order.id}`)
}

useHead({
  title: () => `${tenantConfig.value?.name || t('common.menu')} | feasymenu`,
  meta: [{ name: 'robots', content: 'noindex, nofollow' }]
})
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar { display: none; }
.scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }

.fade-up-enter-active, .fade-up-leave-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.fade-up-enter-from, .fade-up-leave-to { opacity: 0; transform: translate(-50%, 20px); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
