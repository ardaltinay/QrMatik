<template>
  <div class="p-4 md:p-8 max-w-[1800px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col space-y-8">
    <!-- Service Hub Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 shrink-0 bg-white/50 backdrop-blur-xl p-8 rounded-[3rem] border border-white shadow-2xl shadow-slate-200/40">
      <div class="flex items-center gap-6">
        <div class="w-16 h-16 rounded-[1.5rem] bg-emerald-600 text-white flex items-center justify-center shadow-lg shadow-emerald-500/30">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" /></svg>
        </div>
        <div>
          <h1 class="text-3xl font-black text-slate-900 tracking-tight leading-none mb-2">{{ $t('admin.saloon.title') || 'Salon Paneli' }}</h1>
          <p class="text-slate-500 font-medium text-sm">{{ $t('admin.saloon.subtitle') || 'Servis edilecek hazır siparişler' }}</p>
        </div>
      </div>

      <div class="flex items-center gap-4 w-full lg:w-auto">
        <div class="relative flex-1 lg:min-w-[400px] group">
          <svg class="w-5 h-5 absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-emerald-500 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.orders.searchPlaceholder')"
            class="w-full pl-14 pr-6 py-4 rounded-2xl bg-white border border-slate-200 focus:ring-8 focus:ring-emerald-500/5 focus:border-emerald-500 transition-all outline-none shadow-sm font-black text-slate-700"
          />
        </div>
        
        <!-- Live Status -->
        <div class="hidden lg:flex items-center gap-3 bg-emerald-50 px-6 py-4 rounded-2xl border border-emerald-100 shrink-0">
           <div class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></div>
           <span class="text-[10px] font-black text-emerald-600 uppercase tracking-widest">{{ $t('admin.common.status.systemActive') }}</span>
        </div>
      </div>
    </div>

    <!-- Ready Orders Matrix -->
    <div class="flex-grow overflow-y-auto pb-10 scrollbar-hide">
      <div v-if="filteredReadyOrders.length === 0" class="h-full flex flex-col items-center justify-center space-y-6 opacity-30">
         <div class="w-32 h-32 bg-slate-100 rounded-[3rem] flex items-center justify-center text-slate-300 border border-slate-200">
            <svg class="w-16 h-16" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
         </div>
         <p class="text-xl font-black text-slate-900 uppercase tracking-widest">ŞU AN SERVİS BEKLEYEN YOK</p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-8">
        <TransitionGroup name="list">
          <div 
            v-for="order in filteredReadyOrders" 
            :key="order.id"
            class="bg-white rounded-[2.5rem] border-2 flex flex-col overflow-hidden transition-all duration-500 hover:-translate-y-2 group/card"
            :class="[
              isFullyReady(order) 
                ? 'border-emerald-500 shadow-[0_32px_64px_-16px_rgba(16,185,129,0.2)] bg-emerald-50/10' 
                : 'border-white shadow-[0_20px_40px_-10px_rgba(0,0,0,0.05)] bg-white'
            ]"
          >
            <!-- Fully Ready Glow/Badge -->
            <div v-if="isFullyReady(order)" class="bg-emerald-500 text-white text-[10px] font-black uppercase tracking-[0.3em] py-3 text-center shadow-lg animate-pulse">
              SERVİS İÇİN HAZIR
            </div>

            <!-- Table & Order Info -->
            <div class="p-8 border-b border-slate-50 flex justify-between items-center bg-white/50">
              <div class="flex items-center gap-4">
                 <div class="w-14 h-14 rounded-2xl bg-slate-900 text-white flex items-center justify-center font-black text-2xl tracking-tighter shadow-xl">
                    {{ order.table || order.tableCode || '-' }}
                 </div>
                 <div>
                    <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest block mb-0.5">SİPARİŞ</span>
                    <span class="text-xl font-black text-slate-900 tracking-tight">#{{ orderCodeFromId(order.id) }}</span>
                 </div>
              </div>
              <div class="text-right shrink-0">
                <span class="text-[9px] font-black text-slate-400 uppercase tracking-widest block leading-none mb-1">ZAMAN</span>
                <span class="text-[10px] font-bold text-slate-900 italic">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
              </div>
            </div>

            <!-- Preparation Status Chips -->
            <div class="px-8 py-4 flex gap-3 bg-slate-50/50">
               <div class="flex-1 flex flex-col gap-1.5">
                  <span class="text-[9px] font-black text-slate-400 uppercase tracking-widest px-1">MUTFAK</span>
                  <div :class="getStatusBadgeClass(order.kitchenStatus)" class="px-3 py-2 rounded-xl border text-[10px] font-black text-center uppercase tracking-widest shadow-sm">
                    {{ $t(`order.statuses.${(order.kitchenStatus || 'new').toLowerCase()}`) }}
                  </div>
               </div>
               <div class="flex-1 flex flex-col gap-1.5">
                  <span class="text-[9px] font-black text-slate-400 uppercase tracking-widest px-1">BAR</span>
                  <div :class="getStatusBadgeClass(order.barStatus)" class="px-3 py-2 rounded-xl border text-[10px] font-black text-center uppercase tracking-widest shadow-sm">
                    {{ $t(`order.statuses.${(order.barStatus || 'new').toLowerCase()}`) }}
                  </div>
               </div>
            </div>

            <!-- Items Detailed List -->
            <div class="p-8 flex-grow space-y-4 max-h-[300px] overflow-y-auto scrollbar-thin">
              <div v-for="(it, i) in (order.items || order.lines)" :key="i" class="flex items-start gap-4 group/item">
                <div class="w-10 h-10 rounded-xl bg-white border border-slate-100 flex items-center justify-center font-black text-slate-900 shrink-0 shadow-sm transition-transform group-hover/item:scale-110">
                  {{ it.qty || it.quantity }}x
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-black text-slate-800 text-lg leading-tight uppercase truncate">{{ it.name || menuItemName(it.itemId) }}</p>
                  <p v-if="it.note" class="text-[11px] text-orange-600 font-bold mt-1 bg-orange-50 px-2 py-0.5 rounded-lg inline-block border border-orange-100">⚠️ {{ it.note }}</p>
                </div>
              </div>
            </div>

            <!-- Serve Action Footer -->
            <div class="p-8 bg-white border-t border-slate-50 mt-auto">
              <button 
                @click="serveOrder(order.id)" 
                :disabled="!isFullyReady(order)"
                class="w-full py-5 font-black text-xs uppercase tracking-[0.2em] rounded-[1.5rem] transition-all flex items-center justify-center gap-3 active:scale-95 shadow-2xl"
                :class="[
                  isFullyReady(order) 
                    ? 'bg-emerald-600 hover:bg-emerald-700 text-white shadow-emerald-500/30' 
                    : 'bg-slate-100 text-slate-300 cursor-not-allowed border border-slate-200 shadow-none'
                ]"
              >
                <svg v-if="isFullyReady(order)" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                {{ $t('admin.saloon.serveBtn') || 'SERVİS ET' }}
              </button>
            </div>
          </div>
        </TransitionGroup>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'
import { formatDistanceToNow } from 'date-fns'
import { tr, enUS } from 'date-fns/locale'
import { useAuthStore } from '~/stores/auth'
import { useSocket } from '~/composables/useSocket'

definePageMeta({ layout: 'admin' })

const { t, locale } = useI18n()
const orderStore = useOrderStore()
const { orderCodeFromId } = useFormat()
const uiStore = useUiStore()
const authStore = useAuthStore()
const { connect, subscribe } = useSocket()

const searchQuery = ref('')
let unsub: (() => void) | null = null

onMounted(async () => {
  await orderStore.loadMenu()
  await orderStore.loadOrders()

  connect(() => {
    const tenantCode = authStore.user?.tenantCode || authStore.tenantCode
    if (tenantCode) {
      unsub = subscribe(`/topic/orders/${tenantCode}`, (rawOrder: any) => {
        const updatedOrder = { ...rawOrder }
        if (typeof updatedOrder.items === 'string') { try { updatedOrder.items = JSON.parse(updatedOrder.items) } catch (e) {} }
        if (typeof updatedOrder.lines === 'string') { try { updatedOrder.lines = JSON.parse(updatedOrder.lines) } catch (e) {} }

        const isNewReady = updatedOrder.status === 'ready' && !orderStore.orders.some(o => o.id === updatedOrder.id && o.status === 'ready')
        if (isNewReady) {
          const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3')
          audio.play().catch(() => {})
        }

        const index = orderStore.orders.findIndex(o => o.id === updatedOrder.id)
        if (index !== -1) orderStore.orders[index] = updatedOrder
        else orderStore.orders.unshift(updatedOrder)
      })
    }
  })
})

onBeforeUnmount(() => { if (unsub) unsub() })

const readyOrders = computed(() => {
  const activeStatuses = ['new', 'preparing', 'ready']
  return orderStore.orders.filter(o => activeStatuses.includes((o.status || '').toLowerCase()))
})

const filteredReadyOrders = computed(() => {
  if (!searchQuery.value) return readyOrders.value
  const q = searchQuery.value.toLowerCase()
  return readyOrders.value.filter(o => 
    String(orderCodeFromId(o.id)).toLowerCase().includes(q) || 
    String(o.table || o.tableCode || '').toLowerCase().includes(q)
  )
})

async function serveOrder(id: string | number) {
  try {
    await orderStore.updateOrderStatus(id, 'served')
    uiStore.success(t('admin.saloon.serveSuccess') || 'Sipariş servis edildi.')
  } catch (e: any) { uiStore.error(e?.message || 'Hata oluştu') }
}

function isFullyReady(order: any) {
  const kReady = (order.kitchenStatus || '').toLowerCase() === 'ready'
  const bReady = (order.barStatus || '').toLowerCase() === 'ready'
  return kReady && bReady
}

function getStatusBadgeClass(status?: string) {
  const s = (status || 'new').toLowerCase()
  if (s === 'ready') return 'bg-emerald-50 text-emerald-600 border-emerald-100'
  if (s === 'preparing') return 'bg-indigo-50 text-indigo-600 border-indigo-100'
  return 'bg-amber-50 text-amber-600 border-amber-100'
}

function menuItemName(id: number) { return orderStore.menuItemById(id)?.name || t('order.productFallback') }

function timeAgo(dateString: string) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return formatDistanceToNow(date, { addSuffix: true, locale: locale.value === 'tr' ? tr : enUS })
  } catch { return '' }
}

useHead({ title: () => `${t('admin.saloon.title') || 'Salon'} | Admin` })
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar { display: none; }
.scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }

.scrollbar-thin::-webkit-scrollbar { width: 4px; }
.scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
.scrollbar-thin::-webkit-scrollbar-thumb { background-color: #e2e8f0; border-radius: 20px; }

.list-enter-active, .list-leave-active { transition: all 0.5s ease; }
.list-enter-from { opacity: 0; transform: translateY(30px); }
.list-leave-to { opacity: 0; transform: translateX(50px); }
</style>
