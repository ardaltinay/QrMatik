<template>
  <div class="min-h-screen bg-slate-50 pb-32 font-sans selection:bg-black/10" :style="{ fontFamily: tenantConfig?.fontFamily || 'Inter, sans-serif' }">
    <!-- Dynamic Premium Header -->
    <header class="relative w-full overflow-hidden" :style="{ backgroundColor: tenantConfig?.primaryColor || '#0f172a' }">
      <!-- Decorative background elements -->
      <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 mix-blend-overlay"></div>
      <div class="absolute top-0 left-0 right-0 h-40 bg-gradient-to-b from-black/40 to-transparent z-0"></div>
      <div class="absolute -bottom-24 -right-24 w-96 h-96 bg-white/10 blur-[80px] rounded-full pointer-events-none"></div>
      <div class="absolute -top-24 -left-24 w-80 h-80 bg-black/20 blur-[60px] rounded-full pointer-events-none"></div>

      <div class="relative z-10 max-w-5xl mx-auto px-6 py-6 sm:py-10">
        <div class="flex flex-col sm:flex-row items-center sm:items-center gap-6 sm:gap-8">
          <!-- Logo -->
          <div class="shrink-0 relative group">
            <div class="absolute inset-0 bg-white/20 blur-xl rounded-full scale-110 opacity-0 group-hover:opacity-100 transition-opacity duration-700"></div>
            <div v-if="tenantConfig?.logoUrl" class="relative h-16 w-16 sm:h-20 sm:w-20 rounded-2xl overflow-hidden bg-white/10 p-2 shadow-xl backdrop-blur-md border border-white/20 ring-2 ring-white/5">
              <img :src="tenantConfig.logoUrl" class="h-full w-full object-contain drop-shadow-md" />
            </div>
            <div v-else class="relative flex items-center justify-center h-16 w-16 sm:h-20 sm:w-20 rounded-2xl bg-white/10 backdrop-blur-md border border-white/20 shadow-xl ring-2 ring-white/5 text-white font-black text-2xl uppercase">
              {{ (tenantConfig?.name || 'F').charAt(0) }}
            </div>
          </div>

          <!-- Text content -->
          <div class="text-center sm:text-left flex-grow">
            <h1 class="text-2xl sm:text-3xl font-black leading-tight text-white tracking-tight drop-shadow-sm">
              {{ tenantConfig?.welcomeMessage || $t('common.menu') }}
            </h1>
            <p class="mt-1 text-white/70 max-w-xl text-xs sm:text-sm font-medium line-clamp-1">
              {{ tenantConfig?.tagline || $t('menu.browseAndOrder') || 'Browse the menu and order easily.' }}
            </p>
          </div>

          <!-- Action Buttons (History & Stats) -->
          <div class="flex items-center gap-2 shrink-0">
            <!-- Table Info -->
            <div v-if="tableCode" class="flex flex-col items-center justify-center px-4 py-2 rounded-xl bg-white/10 backdrop-blur-md border border-white/20 shadow-lg">
              <span class="text-[8px] text-white/60 uppercase tracking-widest font-black">{{ $t('order.table') }}</span>
              <span class="text-lg font-black text-white leading-none">{{ tableCode }}</span>
            </div>
            
            <!-- My Orders Button -->
            <button 
              v-if="hasSession"
              @click="router.push('/order/history')"
              class="flex flex-col items-center justify-center px-4 py-2 rounded-xl bg-white text-slate-900 border border-white/20 shadow-lg hover:scale-105 active:scale-95 transition-all group"
            >
              <span class="text-[8px] text-slate-400 uppercase tracking-widest font-black">{{ $t('order.myOrders') }}</span>
              <svg class="w-4 h-4 text-brand-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Categories Sticky Bar -->
    <div class="sticky top-0 z-30 bg-white/80 backdrop-blur-xl border-b border-slate-200/50 shadow-sm transition-all duration-300">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 py-3">
        <div class="flex items-center gap-2 overflow-x-auto scrollbar-hide -mx-4 px-4 sm:mx-0 sm:px-0">
          <button
            v-for="cat in categories"
            :key="cat"
            @click="selectedCategory = cat"
            class="relative whitespace-nowrap px-6 py-2.5 rounded-full text-sm font-bold transition-all duration-300 overflow-hidden group"
            :class="selectedCategory === cat ? 'text-white shadow-lg' : 'bg-transparent text-slate-500 hover:bg-slate-100 hover:text-slate-900'"
          >
            <!-- Active state background with brand color -->
            <div 
              v-if="selectedCategory === cat" 
              class="absolute inset-0 transition-colors"
              :style="{ backgroundColor: tenantConfig?.primaryColor || '#0f172a' }"
            ></div>
            <!-- Text (relative to stay above background) -->
            <span class="relative z-10 tracking-wide">{{ $te(`menu.categories.${cat}`) ? $t(`menu.categories.${cat}`) : formatCategoryName(cat) }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <main class="max-w-5xl mx-auto px-6 py-8">
      
      <!-- Allergen Warning -->
      <div class="mb-6 p-4 rounded-2xl bg-amber-50 border border-amber-200/60 flex items-start gap-3 shadow-sm">
        <div class="text-amber-500 shrink-0 mt-0.5">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        </div>
        <p class="text-sm font-medium text-amber-800 leading-relaxed">{{ $t('menu.allergenWarning') }}</p>
      </div>

      <!-- Subcategories Filter -->
      <div v-if="subcategories.length > 0" class="flex flex-wrap gap-2 mb-8">
        <button
          v-for="sub in subcategories"
          :key="sub"
          @click="selectedSub = sub"
          class="px-4 py-1.5 rounded-lg text-xs font-bold uppercase tracking-wider transition-all"
          :class="selectedSub === sub ? 'text-white shadow-sm' : 'bg-white text-slate-500 border border-slate-200 hover:bg-slate-50'"
          :style="selectedSub === sub ? { backgroundColor: tenantConfig?.primaryColor || '#0f172a' } : {}"
        >
          {{ $te(`menu.subcategories.${sub}`) ? $t(`menu.subcategories.${sub}`) : formatCategoryName(sub) }}
        </button>
      </div>

      <!-- Menu Grid -->
      <div v-if="orderStore.loading && orderStore.menu.length === 0" class="flex flex-col items-center justify-center py-32">
        <div class="relative w-16 h-16">
          <div class="absolute inset-0 border-4 border-slate-100 rounded-full"></div>
          <div class="absolute inset-0 border-4 rounded-full border-t-transparent animate-spin" :style="{ borderColor: tenantConfig?.primaryColor || '#0f172a', borderTopColor: 'transparent' }"></div>
        </div>
      </div>
      
      <div v-else-if="filteredMenu.length === 0" class="text-center py-32 text-slate-400 font-medium italic">
        {{ $t('menu.emptyCategory') }}
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
        <MenuItemCard 
          v-for="item in filteredMenu" 
          :key="item.id" 
          :item="item" 
          @add="openNoteModal" 
        />
      </div>
    </main>

    <!-- Floating Cart Button (Mobile) -->
    <Transition
      enter-active-class="transition-all duration-500 ease-out"
      enter-from-class="translate-y-full opacity-0 scale-95"
      enter-to-class="translate-y-0 opacity-100 scale-100"
      leave-active-class="transition-all duration-300 ease-in"
      leave-from-class="translate-y-0 opacity-100 scale-100"
      leave-to-class="translate-y-full opacity-0 scale-95"
    >
      <div v-if="orderStore.cartCount > 0 && !isCartOpen" class="fixed bottom-6 left-4 right-4 z-40 sm:hidden">
        <button 
          @click="isCartOpen = true"
          class="w-full flex items-center justify-between text-white rounded-[2rem] p-4 font-black uppercase tracking-widest text-xs shadow-2xl shadow-slate-900/20 active:scale-95 transition-all border border-white/10 overflow-hidden relative"
          :style="{ backgroundColor: tenantConfig?.primaryColor || '#0f172a' }"
        >
          <div class="absolute inset-0 bg-gradient-to-r from-white/0 via-white/10 to-white/0 translate-x-[-100%] animate-[shimmer_2s_infinite]"></div>
          <div class="flex items-center gap-4 relative z-10">
            <div class="bg-white/20 w-10 h-10 rounded-2xl flex items-center justify-center font-black text-sm backdrop-blur-sm border border-white/20 shadow-inner">
              {{ orderStore.cartCount }}
            </div>
            <span class="tracking-[0.2em]">{{ $t('menu.cart') }}</span>
          </div>
          <div class="flex items-center gap-3 relative z-10 bg-black/20 pl-4 pr-3 py-2 rounded-xl backdrop-blur-md border border-white/10">
            <span class="text-sm tracking-tight">{{ formatPrice(orderStore.cartTotal) }}</span>
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </button>
      </div>
    </Transition>

    <!-- Floating Cart Button (Desktop) -->
    <Transition
      enter-active-class="transition-all duration-500 ease-out"
      enter-from-class="translate-x-full opacity-0 scale-90"
      enter-to-class="translate-x-0 opacity-100 scale-100"
      leave-active-class="transition-all duration-300 ease-in"
      leave-from-class="translate-x-0 opacity-100 scale-100"
      leave-to-class="translate-x-full opacity-0 scale-90"
    >
      <div v-if="orderStore.cartCount > 0 && !isCartOpen" class="hidden sm:block fixed bottom-10 right-10 z-40 group">
        <button 
          @click="isCartOpen = true"
          class="flex items-center gap-6 text-white rounded-full pl-6 pr-8 py-4 font-black uppercase tracking-[0.2em] text-xs shadow-2xl shadow-slate-900/20 group-hover:scale-105 group-hover:-translate-y-2 transition-all duration-300 border border-white/10 relative overflow-hidden"
          :style="{ backgroundColor: tenantConfig?.primaryColor || '#0f172a' }"
        >
          <div class="absolute inset-0 bg-gradient-to-r from-white/0 via-white/10 to-white/0 translate-x-[-100%] group-hover:animate-[shimmer_1.5s_infinite]"></div>
          <div class="relative z-10">
            <div class="relative">
              <svg class="w-7 h-7 transform group-hover:rotate-12 transition-transform duration-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                 <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              <span class="absolute -top-2 -right-2 bg-rose-500 text-white text-[10px] w-5 h-5 rounded-full flex items-center justify-center font-black shadow-lg border-2 border-white ring-2 ring-rose-500/20">
                {{ orderStore.cartCount }}
              </span>
            </div>
          </div>
          <span class="text-xl tracking-tighter relative z-10">{{ formatPrice(orderStore.cartTotal) }}</span>
        </button>
      </div>
    </Transition>

    <CartDrawer v-model:isOpen="isCartOpen" :tableCode="tableCode" @orderSuccess="onOrderSuccess" />

    <!-- Add Note Modal -->
    <Transition
      enter-active-class="transition-opacity duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-200"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="noteModal.open" class="fixed inset-0 z-50 flex items-end sm:items-center justify-center px-6 pb-6 sm:p-0">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="closeNoteModal"></div>
        <div class="relative w-full max-w-md bg-white rounded-[2.5rem] p-8 sm:p-10 shadow-2xl overflow-hidden transform transition-all border border-slate-100">
          <div class="absolute top-0 right-0 w-32 h-32 bg-brand-500/5 blur-[60px] rounded-full translate-x-1/2 -translate-y-1/2"></div>
          
          <div class="flex justify-between items-start mb-8 relative z-10">
            <div>
              <h3 class="text-xl font-black text-slate-900 mb-1 uppercase tracking-tight">{{ $t('menu.itemDetail') }}</h3>
              <p class="text-sm text-slate-400 font-bold uppercase tracking-wider">{{ getLocalizedName(noteModal.item) }}</p>
            </div>
            <button @click="closeNoteModal" class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 hover:text-slate-900 transition-all shadow-sm">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="mb-8 flex items-center justify-between p-5 bg-slate-50 rounded-2xl border border-slate-100 shadow-inner">
            <span class="text-xs font-black uppercase tracking-widest text-slate-400">{{ $t('menu.quantity') }}</span>
            <div class="flex items-center gap-4 bg-white p-1.5 rounded-xl border border-slate-100 shadow-sm">
              <button 
                @click="noteModal.qty = Math.max(1, noteModal.qty - 1)"
                class="w-9 h-9 flex items-center justify-center rounded-lg text-slate-400 hover:text-brand-600 hover:bg-brand-50 disabled:opacity-30 font-bold transition-all"
                :disabled="noteModal.qty <= 1"
              >-</button>
              <span class="font-black text-slate-900 w-6 text-center">{{ noteModal.qty }}</span>
              <button 
                @click="noteModal.qty++"
                class="w-9 h-9 flex items-center justify-center rounded-lg text-slate-400 hover:text-brand-600 hover:bg-brand-50 font-bold transition-all"
              >+</button>
            </div>
          </div>

          <div class="mb-10 relative z-10">
            <label class="block text-xs font-black uppercase tracking-widest text-slate-400 mb-3 ml-1">{{ $t('menu.specialRequest') }}</label>
            <textarea
              v-model="noteModal.note"
              rows="3"
              class="w-full bg-slate-50 border border-slate-100 rounded-2xl p-5 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none resize-none font-medium"
              :placeholder="$t('menu.requestPlaceholder')"
            ></textarea>
          </div>

          <button 
            @click="confirmAddWithNote"
            class="w-full py-5 rounded-[1.5rem] text-white font-black text-xs uppercase tracking-[0.2em] shadow-xl hover:opacity-90 active:scale-95 transition-all duration-300"
            :style="{ backgroundColor: tenantConfig?.accentColor || tenantConfig?.primaryColor || '#0f172a' }"
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

definePageMeta({
  layout: 'customer'
})

const route = useRoute()
const router = useRouter()

const { t, locale } = useI18n()
const orderStore = useOrderStore()
const { tenantConfig, detectTenant, loadTenantConfig } = useTenant()

const tableCode = ref<string>('')
const isCartOpen = ref(false)
const hasSession = ref(false)

// Init Table
onMounted(() => {
  if (import.meta.client) {
    hasSession.value = !!localStorage.getItem('qm_order_session')
  }
  // Read table code from query: ?t=A3 or ?table=A3
  const t = route.query.t || route.query.table
  if (t) {
    tableCode.value = String(t)
    try {
      localStorage.setItem('qm_table_code', tableCode.value)
    } catch {}
  } else {
    // try reading from localStorage
    try {
      const stored = localStorage.getItem('qm_table_code')
      if (stored) tableCode.value = stored
    } catch {}
  }
})

// Load tenant config and menu data
async function loadData() {
  const code = detectTenant()
  if (code) {
    await loadTenantConfig(code)
  }
  if (orderStore.menu.length === 0) {
    await orderStore.loadMenu()
  }
}

function formatCategoryName(cat: string) {
  if (!cat) return ''
  return cat.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(' ')
}

onMounted(() => {
  loadData()
})

// Categories
const categories = computed(() => {
  const set = new Set<string>()
  orderStore.menu.forEach(m => {
    if (m.category) set.add(m.category)
    else if ((m as any).primary) set.add((m as any).primary) // legacy fallback
  })
  return Array.from(set)
})

const selectedCategory = ref<string>('')

watch(categories, (cats) => {
  if (cats.length > 0 && (!selectedCategory.value || !cats.includes(selectedCategory.value))) {
    selectedCategory.value = cats[0] || ''
  }
}, { immediate: true })

const subcategories = computed(() => {
  const set = new Set<string>()
  orderStore.menu.forEach(m => {
    const cat = m.category || (m as any).primary
    if (cat === selectedCategory.value) {
      const sub = m.subcategory || (m as any).sub
      if (sub) set.add(sub)
    }
  })
  return Array.from(set)
})

const selectedSub = ref<string>('all')

watch(selectedCategory, () => {
  if (subcategories.value.length > 0) {
    selectedSub.value = subcategories.value[0]
  } else {
    selectedSub.value = ''
  }
}, { immediate: true })

// Filtered items
const filteredMenu = computed(() => {
  return orderStore.menu.filter(m => {
    const cat = m.category || (m as any).primary
    const sub = m.subcategory || (m as any).sub
    if (cat !== selectedCategory.value) return false
    if (subcategories.value.length > 0 && sub !== selectedSub.value) return false
    return true
  })
})

// Note Modal
const noteModal = ref({
  open: false,
  item: null as any,
  note: '',
  qty: 1
})

function openNoteModal(item: any) {
  noteModal.value = {
    open: true,
    item,
    note: '',
    qty: 1
  }
}

function closeNoteModal() {
  noteModal.value.open = false
  setTimeout(() => {
    noteModal.value.item = null
    noteModal.value.note = ''
    noteModal.value.qty = 1
  }, 200)
}

function confirmAddWithNote() {
  const it = noteModal.value.item
  if (it && it.id) {
    orderStore.addToCart(it.id, noteModal.value.qty, noteModal.value.note)
  }
  closeNoteModal()
}

function getLocalizedName(item: any) {
  if (!item) return ''
  if (locale.value === 'en' && item.nameEn) {
    return item.nameEn
  }
  return item.name
}

function getLocalizedItemPrice(item: any) {
  if (!item) return 0
  if (locale.value === 'en' && item.priceUsd != null && item.priceUsd > 0) {
    return item.priceUsd
  }
  return item.price
}

function formatPrice(p: number) {
  if (locale.value === 'en') {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(p)
  }
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(p)
}

function onOrderSuccess(order: any) {
  hasSession.value = true
  // Navigate to order tracking page
  if (order && order.id) {
    router.push(`/order/${order.id}`)
  }
}



useHead({
  title: () => `${tenantConfig.value?.name || t('common.menu')} | feasymenu`,
  meta: [
    { name: 'robots', content: 'noindex, nofollow' }
  ]
})
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
.pt-safe {
    padding-top: max(1rem, env(safe-area-inset-top));
}
@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}
</style>
