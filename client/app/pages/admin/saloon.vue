<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8 shrink-0">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.saloon.title') || 'Salon Paneli' }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.saloon.subtitle') || 'Servis edilecek hazır siparişler' }}</p>
      </div>

      <div class="w-full sm:w-72">
        <div class="relative">
          <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.orders.searchPlaceholder')"
            class="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm"
          />
        </div>
      </div>
    </div>

    <!-- Ready Orders List -->
    <div class="flex-grow overflow-y-auto pb-4 scrollbar-thin">
      <div v-if="readyOrders.length === 0" class="text-center py-20 text-slate-400 font-medium">
        {{ $t('admin.orders.emptyState') }}
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="order in filteredReadyOrders" 
          :key="order.id"
          class="bg-white rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden flex flex-col"
        >
          <div class="absolute top-0 left-0 w-1.5 h-full bg-emerald-500"></div>
          
          <div class="p-5 border-b border-slate-50 flex justify-between items-start">
            <div>
              <span class="font-black text-slate-900 text-xl tracking-tight">#{{ orderCodeFromId(order.id) }}</span>
              <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-1">
                {{ timeAgo(order.createdAt || order.createdTime) }}
              </p>
            </div>
            <div class="bg-emerald-50 px-3 py-1 rounded-lg border border-emerald-100">
              <span class="text-emerald-700 font-bold text-sm tracking-tight">{{ order.table || order.tableCode || '-' }}</span>
            </div>
          </div>

          <div class="p-5 flex-grow space-y-4">
            <!-- Status Indicators -->
            <div class="flex gap-2">
              <div class="flex-1 flex flex-col gap-1">
                <span class="text-[9px] font-black text-slate-400 uppercase tracking-widest">{{ $t('admin.kitchen.title') }}</span>
                <div :class="getStatusBadgeClass(order.kitchenStatus)" class="px-2 py-1.5 rounded-lg border text-[10px] font-bold text-center">
                  {{ $t(`order.statuses.${(order.kitchenStatus || 'new').toLowerCase()}`) }}
                </div>
              </div>
              <div class="flex-1 flex flex-col gap-1">
                <span class="text-[9px] font-black text-slate-400 uppercase tracking-widest">{{ $t('admin.bar.title') }}</span>
                <div :class="getStatusBadgeClass(order.barStatus)" class="px-2 py-1.5 rounded-lg border text-[10px] font-bold text-center">
                  {{ $t(`order.statuses.${(order.barStatus || 'new').toLowerCase()}`) }}
                </div>
              </div>
            </div>

            <div class="h-px bg-slate-100 my-2"></div>

            <div v-for="(it, i) in (order.items || order.lines)" :key="i" class="flex items-start gap-3">
              <div class="w-8 h-8 rounded-lg bg-slate-50 border border-slate-100 flex items-center justify-center font-bold text-slate-700 shrink-0 text-sm">
                {{ it.qty || it.quantity }}x
              </div>
              <div>
                <p class="font-bold text-slate-800">{{ it.name || menuItemName(it.itemId) }}</p>
                <p v-if="it.note" class="text-[10px] text-amber-600 font-medium mt-0.5">• {{ it.note }}</p>
              </div>
            </div>
          </div>

          <div class="p-5 bg-slate-50 border-t border-slate-100 mt-auto">
            <button 
              @click="serveOrder(order.id)" 
              :disabled="!isFullyReady(order)"
              class="w-full py-3 font-black text-xs uppercase tracking-[0.2em] rounded-xl transition-all flex items-center justify-center gap-2"
              :class="[
                isFullyReady(order) 
                  ? 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-lg shadow-emerald-600/20 active:scale-95' 
                  : 'bg-slate-200 text-slate-400 cursor-not-allowed'
              ]"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
              </svg>
              {{ $t('admin.saloon.serveBtn') || 'SERVİS ET' }}
            </button>
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

definePageMeta({
  layout: 'admin',
})

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

  // WebSocket for real-time READY notifications
  connect(() => {
    const tenantCode = authStore.user?.tenantCode || authStore.tenantCode
    if (tenantCode) {
      unsub = subscribe(`/topic/orders/${tenantCode}`, (rawOrder: any) => {
        // Normalize the order data
        const updatedOrder = { ...rawOrder }
        if (typeof updatedOrder.items === 'string') {
          try { updatedOrder.items = JSON.parse(updatedOrder.items) } catch (e) {}
        }
        if (typeof updatedOrder.lines === 'string') {
          try { updatedOrder.lines = JSON.parse(updatedOrder.lines) } catch (e) {}
        }

        const isNewReady = updatedOrder.status === 'ready' && !orderStore.orders.some(o => o.id === updatedOrder.id && o.status === 'ready')
        if (isNewReady) {
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

const readyOrders = computed(() => {
  const activeStatuses = ['new', 'preparing', 'ready']
  return orderStore.orders.filter(o => activeStatuses.includes((o.status || '').toLowerCase()))
})

const filteredReadyOrders = computed(() => {
  if (!searchQuery.value) return readyOrders.value
  const q = searchQuery.value.toLowerCase()
  return readyOrders.value.filter(o => {
    const code = String(orderCodeFromId(o.id)).toLowerCase()
    const table = String(o.table || o.tableCode || '').toLowerCase()
    return code.includes(q) || table.includes(q)
  })
})

async function serveOrder(id: string | number) {
  try {
    await orderStore.updateOrderStatus(id, 'served')
    uiStore.success(t('admin.saloon.serveSuccess') || 'Sipariş servis edildi.')
  } catch (e: any) {
    uiStore.error(e?.message || t('admin.orders.updateError'))
  }
}

function isFullyReady(order: any) {
  const kReady = (order.kitchenStatus || '').toLowerCase() === 'ready'
  const bReady = (order.barStatus || '').toLowerCase() === 'ready'
  return kReady && bReady
}

function getStatusBadgeClass(status?: string) {
  const s = (status || 'new').toLowerCase()
  if (s === 'ready') return 'bg-emerald-50 text-emerald-700 border-emerald-100'
  if (s === 'preparing') return 'bg-indigo-50 text-indigo-700 border-indigo-100'
  return 'bg-amber-50 text-amber-700 border-amber-100'
}

function menuItemName(id: number) {
  const i = orderStore.menuItemById(id)
  return i ? i.name : t('order.productFallback')
}

function timeAgo(dateString: string) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    const currentLocale = locale.value === 'tr' ? tr : enUS
    return formatDistanceToNow(date, { addSuffix: true, locale: currentLocale })
  } catch {
    return ''
  }
}

useHead({
  title: () => `${t('admin.saloon.title') || 'Salon'} | Admin`
})
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}
.scrollbar-thin {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}
</style>
