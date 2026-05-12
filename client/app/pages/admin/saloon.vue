<template>
  <div class="p-4 md:p-8 max-w-[1800px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col space-y-8">
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 shrink-0 bg-white/80 backdrop-blur-2xl p-8 rounded-[3rem] border border-white shadow-2xl shadow-slate-200/40">
      <div class="flex items-center gap-6">
        <div class="w-16 h-16 rounded-[1.5rem] bg-slate-900 text-white flex items-center justify-center shadow-2xl shadow-slate-900/20">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" /></svg>
        </div>
        <div>
          <h1 class="text-3xl font-black text-slate-900 tracking-tight leading-none mb-2 uppercase">{{ $t('admin.saloon.title') }}</h1>
          <div class="flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            <p class="text-slate-500 font-bold text-xs uppercase tracking-widest">{{ $t('admin.saloon.subtitle') }}</p>
          </div>
        </div>
      </div>
      <div class="flex items-center gap-4 w-full lg:w-auto">
        <div class="relative flex-1 lg:min-w-[400px] group">
          <svg class="w-5 h-5 absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-slate-900 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.orders.searchPlaceholder')"
            class="w-full pl-14 pr-6 py-4 rounded-[1.5rem] bg-slate-50 border border-slate-100 focus:ring-8 focus:ring-slate-900/5 focus:border-slate-900 transition-all outline-none font-bold text-slate-700 shadow-inner"
          />
        </div>
      </div>
    </div>
    <div class="flex-grow overflow-y-auto pb-10 scrollbar-hide">
      <div v-if="filteredReadyOrders.length === 0" class="h-full flex flex-col items-center justify-center space-y-6 py-20 bg-slate-50 rounded-[3rem] border border-dashed border-slate-200/60">
         <div class="w-24 h-24 bg-white rounded-[2rem] flex items-center justify-center text-slate-200 border border-slate-100 shadow-inner">
            <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M5 13l4 4L19 7" /></svg>
         </div>
         <p class="text-xs font-black text-slate-400 uppercase tracking-[0.3em]">{{ $t('admin.saloon.emptyState') }}</p>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-8">
        <TransitionGroup name="list">
          <div 
            v-for="order in filteredReadyOrders" 
            :key="order.id"
            class="bg-white rounded-[3rem] border flex flex-col overflow-hidden transition-all duration-500 hover:-translate-y-2 group/card shadow-[0_20px_50px_rgba(0,0,0,0.04)]"
            :class="isFullyReady(order) ? 'border-emerald-500 shadow-[0_32px_64px_-16px_rgba(16,185,129,0.1)]' : 'border-slate-100'"
          >
            <div v-if="isFullyReady(order)" class="bg-emerald-500 text-white text-[9px] font-black uppercase tracking-[0.4em] py-3 text-center shadow-lg animate-pulse-subtle">
              {{ $t('admin.products.readyLabel') }}
            </div>
            <div class="p-8 space-y-6">
              <div class="flex justify-between items-start gap-4">
                <div class="flex-1 min-w-0">
                  <span class="text-[9px] font-black text-slate-400 uppercase tracking-[0.2em] block mb-2">{{ $t('admin.products.orderNo') }}</span>
                  <span class="text-3xl font-black text-slate-900 tracking-tighter leading-none block">#{{ orderCodeFromId(order.id) }}</span>
                </div>
                <div class="shrink-0 text-right">
                  <span class="text-[9px] font-black text-slate-400 uppercase tracking-[0.2em] block mb-2">{{ $t('admin.products.tableLabel') }}</span>
                  <div class="inline-flex px-6 py-3 bg-slate-900 text-white text-xl font-black rounded-2xl tracking-tighter shadow-2xl shadow-slate-900/20">
                    {{ order.table || order.tableCode || '-' }}
                  </div>
                </div>
              </div>
              <div class="bg-slate-50 px-4 py-2 rounded-xl border border-slate-100 flex items-center justify-between">
                <div class="flex items-center gap-2">
                   <svg class="w-3 h-3 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                   <span class="text-[10px] font-bold text-slate-500 uppercase italic leading-none">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
                </div>
                <span class="text-[9px] font-black text-slate-300 uppercase tracking-[0.1em]">{{ $t('admin.products.timeLabel') }}</span>
              </div>
            </div>
            <div class="px-8 py-6 grid grid-cols-2 gap-6 bg-slate-50/50 border-y border-slate-50">
               <div class="flex flex-col gap-2.5">
                  <span class="text-[8px] font-black text-slate-400 uppercase tracking-widest px-1">{{ $t('admin.products.kitchenOrder') }}</span>
                  <div :class="getStatusBadgeClass(order.kitchenStatus)" class="px-3 py-3 rounded-xl border text-[10px] font-black text-center uppercase tracking-widest shadow-sm">
                    {{ $t('order.statuses.' + (order.kitchenStatus || 'new').toLowerCase()) }}
                  </div>
               </div>
               <div class="flex flex-col gap-2.5">
                  <span class="text-[8px] font-black text-slate-400 uppercase tracking-widest px-1">{{ $t('admin.products.barOrder') }}</span>
                  <div :class="getStatusBadgeClass(order.barStatus)" class="px-3 py-3 rounded-xl border text-[10px] font-black text-center uppercase tracking-widest shadow-sm">
                    {{ $t('order.statuses.' + (order.barStatus || 'new').toLowerCase()) }}
                  </div>
               </div>
            </div>
            <div class="p-8 flex-grow space-y-5 max-h-[320px] overflow-y-auto scrollbar-thin">
              <div v-for="(it, i) in (order.items || order.lines)" :key="i" class="flex items-center gap-5 group/item">
                <div class="w-10 h-10 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-center font-black text-slate-900 shrink-0 text-sm shadow-sm transition-transform group-hover/item:scale-110">
                  {{ it.qty || it.quantity }}x
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-black text-slate-800 text-lg leading-tight uppercase truncate group-hover/item:text-slate-900">{{ it.name || menuItemName(it.itemId) }}</p>
                  <p v-if="it.note" class="text-[10px] text-amber-600 font-bold mt-1 bg-amber-50 px-2 py-0.5 rounded-lg inline-block border border-amber-100">{{ it.note }}</p>
                </div>
              </div>
            </div>
            <div class="p-8 bg-white mt-auto">
              <button 
                @click="serveOrder(order.id)" 
                :disabled="!isFullyReady(order)"
                class="w-full py-5 font-black text-xs uppercase tracking-[0.3em] rounded-[1.5rem] transition-all flex items-center justify-center gap-3 active:scale-95 shadow-2xl"
                :class="isFullyReady(order) ? 'bg-slate-900 hover:bg-slate-800 text-white shadow-slate-900/20' : 'bg-slate-50 text-slate-300 cursor-not-allowed border border-slate-100 shadow-none'"
              >
                <svg v-if="isFullyReady(order)" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                {{ $t('admin.saloon.serveBtn') }}
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
      unsub = subscribe('/topic/orders/' + tenantCode, (rawOrder: any) => {
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
  if (s === 'preparing') return 'bg-slate-900 text-white border-slate-900'
  return 'bg-slate-50 text-slate-400 border-slate-100'
}

function menuItemName(id: number) { return orderStore.menuItemById(id)?.name || t('order.productFallback') }

function timeAgo(dateString: string) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return formatDistanceToNow(date, { addSuffix: true, locale: locale.value === 'tr' ? tr : enUS })
  } catch { return '' }
}

useHead({ title: () => t('admin.saloon.title') + ' | Admin' })
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar { display: none; }
.scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }

.scrollbar-thin::-webkit-scrollbar { width: 4px; }
.scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
.scrollbar-thin::-webkit-scrollbar-thumb { background-color: #e2e8f0; border-radius: 20px; }

.list-enter-active, .list-leave-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.list-enter-from { opacity: 0; transform: translateY(30px); }
.list-leave-to { opacity: 0; transform: translateX(50px); }

@keyframes pulse-subtle {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}
.animate-pulse-subtle { animation: pulse-subtle 2s infinite ease-in-out; }
</style>
