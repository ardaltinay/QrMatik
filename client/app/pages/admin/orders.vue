<template>
  <div class="p-4 md:p-8 max-w-[1600px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col font-sans">
    <!-- Header & Stats Bar -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 mb-8 shrink-0">
      <div class="flex items-center gap-4">
        <div class="w-12 h-12 bg-brand-500 rounded-2xl flex items-center justify-center shadow-lg shadow-brand-500/20">
          <svg class="w-7 h-7 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
        </div>
        <div>
          <h1 class="text-2xl font-black text-slate-900 tracking-tight">{{ $t('admin.orders.title') }}</h1>
          <div class="flex items-center gap-2 text-slate-500 text-sm font-medium mt-0.5">
             <span class="relative flex h-2 w-2">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
             </span>
             {{ $t('admin.orders.liveStatus') }}
          </div>
        </div>
      </div>

      <!-- Quick Stats -->
      <div class="flex items-center gap-3 w-full lg:w-auto overflow-x-auto pb-2 lg:pb-0 scrollbar-hide">
         <div class="bg-white px-5 py-3 rounded-2xl border border-slate-100 shadow-sm flex items-center gap-3 min-w-max">
            <div class="w-8 h-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center font-black text-xs">{{ newOrders.length }}</div>
            <span class="text-xs font-bold text-slate-500 uppercase tracking-widest">{{ $t('order.statuses.new') }}</span>
         </div>
         <div class="bg-white px-5 py-3 rounded-2xl border border-slate-100 shadow-sm flex items-center gap-3 min-w-max">
            <div class="w-8 h-8 rounded-lg bg-indigo-50 text-indigo-600 flex items-center justify-center font-black text-xs">{{ preparingOrders.length }}</div>
            <span class="text-xs font-bold text-slate-500 uppercase tracking-widest">{{ $t('order.statuses.preparing') }}</span>
         </div>
         <div class="relative w-full lg:w-64 min-w-[240px]">
           <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
             <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
           </svg>
           <input 
             v-model="searchQuery" 
             type="text" 
             :placeholder="$t('admin.orders.searchPlaceholder')"
             class="w-full pl-10 pr-4 py-3 rounded-2xl bg-white border border-slate-200 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none shadow-sm font-medium"
           />
         </div>
      </div>
    </div>

    <!-- Mobile Navigation Tabs (Only visible on Mobile) -->
    <div class="flex lg:hidden bg-white p-1 rounded-2xl border border-slate-100 mb-4 shadow-sm shrink-0">
        <button 
         v-for="tab in [{k:'NEW', l:'new'}, {k:'PREPARING', l:'preparing'}, {k:'READY', l:'ready'}, {k:'OTHER', l:'other'}]" 
         :key="tab.k"
         @click="activeMobileTab = tab.k"
         class="flex-1 py-2.5 rounded-xl text-[10px] font-black transition-all uppercase tracking-widest"
         :class="activeMobileTab === tab.k ? 'bg-brand-500 text-white shadow-lg shadow-brand-500/20' : 'text-slate-400'"
       >
         {{ $t(`admin.orders.tabs.${tab.l}`) }}
       </button>
    </div>

    <!-- Kanban Board -->
    <div class="flex-grow flex gap-6 overflow-x-auto pb-4 scrollbar-thin">
      
      <!-- New Column -->
      <div v-show="isColumnVisible('NEW')" class="flex-1 min-w-[280px] max-w-[300px] flex flex-col h-full shrink-0">
        <div class="flex items-center justify-between mb-4 px-2">
          <div class="flex items-center gap-3">
             <div class="w-2 h-8 bg-amber-400 rounded-full"></div>
             <h2 class="font-black text-slate-800 uppercase tracking-widest text-sm">{{ $t('order.statuses.new') }}</h2>
          </div>
          <span class="bg-white border border-slate-100 shadow-sm text-slate-600 text-[10px] font-black px-3 py-1 rounded-full">{{ newOrders.length }}</span>
        </div>
        
        <div class="flex-grow overflow-y-auto space-y-4 pr-2 scrollbar-thin pb-20">
          <div v-if="newOrders.length === 0" class="h-40 flex flex-col items-center justify-center bg-slate-50 rounded-[2rem] border-2 border-dashed border-slate-200 opacity-50">
             <svg class="w-10 h-10 text-slate-300 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
             <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">{{ $t('admin.orders.emptyState') }}</span>
          </div>

          <TransitionGroup name="list">
          <div 
            v-for="order in newOrders" 
            :key="order.id"
            class="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-xl shadow-slate-200/40 hover:shadow-2xl hover:shadow-brand-500/10 transition-all relative group animate-pulse-subtle"
          >
            <div class="flex justify-between items-start mb-4">
               <div>
                  <span class="text-[10px] font-black text-brand-500 uppercase tracking-[0.2em] block mb-1">{{ $t('admin.orders.newOrderLabel') }}</span>
                  <h3 class="text-xl font-black text-slate-900">#{{ orderCodeFromId(order.id) }}</h3>
               </div>
               <div class="flex flex-col items-end">
                  <div class="px-3 py-1 bg-slate-900 text-white text-[11px] font-black rounded-lg mb-1">{{ order.tableCode || '-' }}</div>
                  <span class="text-[10px] font-bold text-slate-400">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
               </div>
            </div>

            <!-- Items -->
            <div class="bg-slate-50/50 rounded-2xl p-4 mb-5 border border-slate-100">
               <div v-for="(it, i) in order.items || order.lines" :key="i" class="flex justify-between items-start gap-3 mb-2 last:mb-0">
                  <div class="flex gap-2">
                     <span class="w-6 h-6 rounded-md bg-white border border-slate-200 flex items-center justify-center text-[10px] font-black text-brand-600 shrink-0">{{ it.qty || it.quantity }}x</span>
                     <div>
                        <span class="text-sm font-bold text-slate-700 leading-tight block">{{ it.name || menuItemName(it.itemId) }}</span>
                        <span v-if="it.note" class="text-[10px] font-bold text-amber-600 bg-amber-50 px-1.5 py-0.5 rounded mt-1 inline-block">“{{ it.note }}”</span>
                     </div>
                  </div>
               </div>
            </div>

            <div class="flex items-center justify-between pt-2">
               <div class="text-lg font-black text-slate-900 tracking-tight">{{ formatPrice(order.total) }}</div>
               <div class="flex gap-2">
                  <button @click="updateStatus(order.id, 'canceled')" class="w-10 h-10 flex items-center justify-center bg-slate-50 text-slate-400 hover:text-rose-500 hover:bg-rose-50 rounded-xl transition-all border border-slate-100">
                     <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
                  </button>
                  <button 
                    @click="updateOrderStatus(order.id, 'PREPARING')"
                    class="flex-1 py-3 px-2 bg-brand-600 text-white text-[9px] font-black uppercase tracking-widest rounded-xl hover:bg-brand-700 transition-all shadow-lg shadow-brand-500/20 active:scale-95 whitespace-nowrap"
                 >
                    {{ $t('admin.orders.accept') }}
                 </button>
               </div>
            </div>
          </div>
          </TransitionGroup>
        </div>
      </div>

      <!-- Preparing Column -->
      <div v-show="isColumnVisible('PREPARING')" class="flex-1 min-w-[280px] max-w-[300px] flex flex-col h-full shrink-0">
        <div class="flex items-center justify-between mb-4 px-2">
          <div class="flex items-center gap-3">
             <div class="w-2 h-8 bg-indigo-400 rounded-full"></div>
             <h2 class="font-black text-slate-800 uppercase tracking-widest text-sm">{{ $t('order.statuses.preparing') }}</h2>
          </div>
          <span class="bg-white border border-slate-100 shadow-sm text-slate-600 text-[10px] font-black px-3 py-1 rounded-full">{{ preparingOrders.length }}</span>
        </div>
        <div class="flex-grow overflow-y-auto space-y-4 pr-2 scrollbar-thin pb-20">
           <TransitionGroup name="list">
           <div 
            v-for="order in preparingOrders" 
            :key="order.id"
            class="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-xl shadow-slate-200/40 transition-all relative group"
           >
            <div class="flex justify-between items-start mb-4">
               <div>
                  <span class="text-[10px] font-black text-brand-500 uppercase tracking-[0.2em] block mb-1">{{ $t('admin.orders.preparingLabel') }}</span>
                  <h3 class="text-xl font-black text-slate-900">#{{ orderCodeFromId(order.id) }}</h3>
               </div>
               <div class="flex flex-col items-end">
                  <div class="px-3 py-1 bg-slate-100 text-slate-900 text-[11px] font-black rounded-lg mb-1">{{ order.tableCode || '-' }}</div>
                  <span class="text-[10px] font-bold text-slate-400">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
               </div>
            </div>

            <div class="bg-indigo-50/30 rounded-2xl p-4 mb-5 border border-indigo-50">
               <div v-for="(it, i) in order.items || order.lines" :key="i" class="flex justify-between items-start gap-3 mb-2 last:mb-0">
                  <div class="flex gap-2">
                     <span class="w-6 h-6 rounded-md bg-white border border-indigo-100 flex items-center justify-center text-[10px] font-black text-indigo-600 shrink-0">{{ it.qty || it.quantity }}x</span>
                     <span class="text-sm font-bold text-slate-700 leading-tight block">{{ it.name || menuItemName(it.itemId) }}</span>
                  </div>
               </div>
            </div>

            <button @click="updateStatus(order.id, 'ready')" class="w-full py-3.5 bg-indigo-600 text-white font-black text-xs rounded-2xl hover:bg-indigo-700 hover:shadow-lg hover:shadow-indigo-500/20 transition-all uppercase tracking-widest flex items-center justify-center gap-2">
               <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
               {{ $t('admin.orders.readyBtn') }}
            </button>
           </div>
           </TransitionGroup>
        </div>
      </div>

      <!-- Ready Column -->
      <div v-show="isColumnVisible('READY')" class="flex-1 min-w-[280px] max-w-[300px] flex flex-col h-full shrink-0">
        <div class="flex items-center justify-between mb-4 px-2">
          <div class="flex items-center gap-3">
             <div class="w-2 h-8 bg-emerald-400 rounded-full"></div>
             <h2 class="font-black text-slate-800 uppercase tracking-widest text-sm">{{ $t('order.statuses.ready') }}</h2>
          </div>
          <span class="bg-white border border-slate-100 shadow-sm text-slate-600 text-[10px] font-black px-3 py-1 rounded-full">{{ readyOrders.length }}</span>
        </div>
        <div class="flex-grow overflow-y-auto space-y-4 pr-2 scrollbar-thin pb-20">
           <TransitionGroup name="list">
           <div 
            v-for="order in readyOrders" 
            :key="order.id"
            class="bg-white p-5 rounded-[2rem] border border-slate-100 shadow-xl shadow-slate-200/40 transition-all relative group border-t-4 border-t-emerald-400"
           >
            <div class="flex justify-between items-center mb-4">
               <div>
                  <span class="text-[10px] font-black text-brand-500 uppercase tracking-[0.2em] block mb-1">{{ $t('admin.orders.readyLabel') }}</span>
                  <h3 class="text-xl font-black text-slate-900">#{{ orderCodeFromId(order.id) }}</h3>
                  <span class="text-[10px] font-bold text-slate-400">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
               </div>
               <div class="px-4 py-2 bg-emerald-50 text-emerald-600 text-lg font-black rounded-2xl border border-emerald-100">{{ order.tableCode || '-' }}</div>
            </div>

            <button @click="updateStatus(order.id, 'served')" class="w-full py-3.5 bg-emerald-600 text-white font-black text-xs rounded-2xl hover:bg-emerald-700 hover:shadow-lg hover:shadow-emerald-500/20 transition-all uppercase tracking-widest flex items-center justify-center gap-2">
               <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M13 5l7 7-7 7M5 5l7 7-7 7" /></svg>
               {{ $t('admin.orders.serveBtn') }}
            </button>
           </div>
           </TransitionGroup>
        </div>
      </div>

      <!-- Others / Archive Column -->
      <div v-show="isColumnVisible('OTHER')" class="flex-1 min-w-[280px] max-w-[300px] flex flex-col h-full shrink-0 opacity-60 hover:opacity-100 transition-opacity">
        <div class="flex items-center justify-between mb-4 px-2">
          <div class="flex items-center gap-3">
             <div class="w-2 h-8 bg-slate-400 rounded-full"></div>
             <h2 class="font-black text-slate-500 uppercase tracking-widest text-sm">{{ $t('admin.orders.otherStatus') }}</h2>
          </div>
          <span class="bg-white border border-slate-100 shadow-sm text-slate-400 text-[10px] font-black px-3 py-1 rounded-full">{{ otherOrders.length }}</span>
        </div>
        <div class="flex-grow overflow-y-auto space-y-4 pr-2 scrollbar-thin pb-20">
           <div 
            v-for="order in otherOrders" 
            :key="order.id"
            class="bg-white/50 p-4 rounded-3xl border border-slate-100 shadow-sm transition-all"
           >
            <div class="flex justify-between items-center">
               <div class="flex items-center gap-2">
                  <h4 class="font-bold text-slate-900 text-sm">#{{ orderCodeFromId(order.id) }}</h4>
                  <span class="text-[10px] font-black px-2 py-0.5 rounded uppercase border" :class="getOtherStatusStyles(order.status)">
                     {{ statusLabel(order.status) }}
                  </span>
               </div>
               <div class="font-black text-slate-900 text-sm">{{ formatPrice(order.total) }}</div>
            </div>
            <div class="flex justify-between items-center mt-3">
               <span class="text-[10px] font-bold text-slate-400">Table: {{ order.tableCode || '-' }}</span>
               <button v-if="['served', 'bill_requested'].includes(order.status?.toLowerCase())" @click="updateStatus(order.id, 'payment_completed')" class="px-3 py-1.5 bg-slate-900 text-white font-black text-[9px] rounded-lg hover:bg-brand-600 transition-all uppercase tracking-widest">
                  {{ $t('admin.orders.completeBtn') }}
               </button>
            </div>
           </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'
import { formatDistanceToNow } from 'date-fns'
import { tr, enUS } from 'date-fns/locale'
import { useSocket } from '~/composables/useSocket'
import { useAuthStore } from '~/stores/auth'

definePageMeta({
  layout: 'admin',
})

const { t, locale } = useI18n()
const orderStore = useOrderStore()
const { formatMoney, orderCodeFromId, statusLabel } = useFormat()
const uiStore = useUiStore()

const { connect, subscribe } = useSocket()
const authStore = useAuthStore()
const searchQuery = ref('')
const activeMobileTab = ref('NEW')

let unsub: (() => void) | null = null

onMounted(async () => {
  await orderStore.loadMenu() 
  await orderStore.loadOrders()
  
  connect(() => {
    const tenantCode = authStore.user?.tenantCode || authStore.tenantCode
    if (tenantCode) {
      unsub = subscribe(`/topic/orders/${tenantCode}`, (rawOrder: any) => {
        const updatedOrder = { ...rawOrder }
        if (typeof updatedOrder.items === 'string') {
          try { updatedOrder.items = JSON.parse(updatedOrder.items) } catch (e) {}
        }
        if (typeof updatedOrder.lines === 'string') {
          try { updatedOrder.lines = JSON.parse(updatedOrder.lines) } catch (e) {}
        }
        if (updatedOrder.lines && !updatedOrder.items) {
          updatedOrder.items = updatedOrder.lines
        }
        
        const isNew = !orderStore.orders.some(o => o.id === updatedOrder.id)
        if (isNew) {
          const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3')
          audio.play().catch(() => {})
        }

        const index = orderStore.orders.findIndex(o => o.id === updatedOrder.id)
        if (index !== -1) {
          orderStore.orders[index] = updatedOrder
        } else {
          orderStore.orders.unshift(updatedOrder)
        }
      })
    }
  })
})

onBeforeUnmount(() => {
  if (unsub) unsub()
})

const filteredOrders = computed(() => {
  if (!searchQuery.value) return orderStore.orders
  const q = searchQuery.value.toLowerCase()
  return orderStore.orders.filter(o => {
    const idMatch = String(orderCodeFromId(o.id)).toLowerCase().includes(q)
    const tableMatch = String(o.tableCode || o.table || '').toLowerCase().includes(q)
    return idMatch || tableMatch
  })
})

const newOrders = computed(() => filteredOrders.value.filter(o => o.status?.toLowerCase() === 'new'))
const preparingOrders = computed(() => filteredOrders.value.filter(o => o.status?.toLowerCase() === 'preparing'))
const readyOrders = computed(() => filteredOrders.value.filter(o => o.status?.toLowerCase() === 'ready'))
const otherOrders = computed(() => filteredOrders.value.filter(o => !['new', 'preparing', 'ready'].includes(o.status?.toLowerCase())).slice(0, 30))

function isColumnVisible(type: string) {
  if (import.meta.client && window.innerWidth < 1024) {
     return activeMobileTab.value === type
  }
  return true
}

function menuItemName(id: number) {
  const i = orderStore.menuItemById(id)
  return i ? i.name : t('order.productFallback')
}

function formatPrice(p: number) {
  return new Intl.NumberFormat(locale.value === 'en' ? 'en-US' : 'tr-TR', { 
    style: 'currency', currency: locale.value === 'en' ? 'USD' : 'TRY' 
  }).format(p)
}

function timeAgo(dateString: string) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return ''
    const currentLocale = locale.value === 'tr' ? tr : enUS
    return formatDistanceToNow(date, { addSuffix: true, locale: currentLocale })
  } catch {
    return ''
  }
}

async function updateStatus(id: string | number, status: string) {
  try {
    await orderStore.updateOrderStatus(id, status)
  } catch (e: any) {
    uiStore.error(e?.message || 'Update failed')
  }
}

function getOtherStatusStyles(status: string) {
  const s = status?.toLowerCase()
  if (s === 'served') return 'bg-blue-50 text-blue-600 border-blue-100'
  if (s === 'payment_completed') return 'bg-slate-50 text-slate-600 border-slate-200'
  if (s === 'canceled') return 'bg-rose-50 text-rose-600 border-rose-100'
  return 'bg-slate-50 text-slate-400 border-slate-100'
}

useHead({
  title: () => `${t('admin.orders.title')} | Admin`
})
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar {
  height: 4px;
  width: 4px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background-color: #e2e8f0;
  border-radius: 20px;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

@keyframes pulse-subtle {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.01); border-color: rgba(99, 102, 241, 0.2); }
}
.animate-pulse-subtle {
  animation: pulse-subtle 3s infinite ease-in-out;
}

/* List Transitions */
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
