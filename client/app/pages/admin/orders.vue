<template>
  <div class="p-6 md:p-8 max-w-[1600px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8 shrink-0">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.orders.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.orders.subtitle') }}</p>
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

    <!-- Kanban Board -->
    <div class="flex-grow overflow-x-auto pb-4 scrollbar-thin flex gap-6">
      
      <!-- New Column -->
      <div class="w-[320px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-3 px-1">
          <h2 class="font-bold text-slate-700 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-amber-500"></span>
            {{ $t('order.statuses.new') }}
          </h2>
          <span class="bg-amber-100 text-amber-700 text-xs font-bold px-2 py-1 rounded-lg">{{ newOrders.length }}</span>
        </div>
        <div class="bg-slate-100 rounded-2xl p-3 flex-grow overflow-y-auto space-y-3 shadow-inner">
          <div v-if="newOrders.length === 0" class="text-center py-10 text-slate-400 text-sm font-medium">
            {{ $t('admin.orders.emptyState') }}
          </div>
          <div 
            v-for="order in newOrders" 
            :key="order.id"
            class="bg-white p-4 rounded-xl shadow-sm border border-slate-200/60 hover:shadow-md transition-shadow relative overflow-hidden group"
          >
            <div class="absolute top-0 left-0 w-1 h-full bg-amber-500"></div>
            <div class="flex justify-between items-start mb-2">
              <span class="font-bold text-slate-800">#{{ orderCodeFromId(order.id) }}</span>
              <span class="text-xs font-semibold text-slate-500">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
            </div>
            <div class="flex items-center gap-2 mb-3">
              <span class="bg-slate-100 text-slate-600 text-xs font-bold px-2 py-1 rounded-md border border-slate-200">
                {{ $t('order.table') }}: {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
              </span>
            </div>
            
            <div class="text-sm text-slate-600 mb-4 line-clamp-3 bg-slate-50 p-2 rounded-lg border border-slate-100">
              <span v-for="(it, i) in order.items || order.lines" :key="i" class="block truncate">
                <span class="font-semibold">{{ it.qty || it.quantity }}x</span> {{ it.name || menuItemName(it.itemId) }}
                <span v-if="it.note" class="text-xs text-amber-600 block mt-0.5">• {{ it.note }}</span>
              </span>
            </div>

            <div class="flex items-center justify-between mt-auto pt-2 border-t border-slate-100">
              <div class="font-bold text-slate-800">{{ formatPrice(order.total) }}</div>
              <div class="flex gap-2">
                <button @click="updateStatus(order.id, 'canceled')" class="p-1.5 text-slate-400 hover:text-rose-500 hover:bg-rose-50 rounded-lg transition-colors" :title="$t('admin.orders.cancelBtn')">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
                <button @click="updateStatus(order.id, 'preparing')" class="px-3 py-1.5 bg-amber-50 text-amber-600 font-bold text-xs rounded-lg hover:bg-amber-100 transition-colors border border-amber-200">
                  {{ $t('admin.orders.accept') }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Preparing Column -->
      <div class="w-[320px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-3 px-1">
          <h2 class="font-bold text-slate-700 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-indigo-500"></span>
            {{ $t('order.statuses.preparing') }}
          </h2>
          <span class="bg-indigo-100 text-indigo-700 text-xs font-bold px-2 py-1 rounded-lg">{{ preparingOrders.length }}</span>
        </div>
        <div class="bg-slate-100 rounded-2xl p-3 flex-grow overflow-y-auto space-y-3 shadow-inner">
          <div v-if="preparingOrders.length === 0" class="text-center py-10 text-slate-400 text-sm font-medium">
            {{ $t('admin.orders.emptyState') }}
          </div>
          <div 
            v-for="order in preparingOrders" 
            :key="order.id"
            class="bg-white p-4 rounded-xl shadow-sm border border-slate-200/60 hover:shadow-md transition-shadow relative overflow-hidden"
          >
            <div class="absolute top-0 left-0 w-1 h-full bg-indigo-500"></div>
            <div class="flex justify-between items-start mb-2">
              <span class="font-bold text-slate-800">#{{ orderCodeFromId(order.id) }}</span>
              <span class="text-xs font-semibold text-slate-500">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
            </div>
            <div class="flex items-center gap-2 mb-3">
              <span class="bg-slate-100 text-slate-600 text-xs font-bold px-2 py-1 rounded-md border border-slate-200">
                {{ $t('order.table') }}: {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
              </span>
            </div>
            
            <div class="text-sm text-slate-600 mb-4 line-clamp-2 bg-slate-50 p-2 rounded-lg border border-slate-100">
              <span v-for="(it, i) in order.items || order.lines" :key="i" class="block truncate">
                <span class="font-semibold">{{ it.qty || it.quantity }}x</span> {{ it.name || menuItemName(it.itemId) }}
              </span>
            </div>

            <div class="flex items-center justify-between mt-auto pt-2 border-t border-slate-100">
              <div class="font-bold text-slate-800">{{ formatPrice(order.total) }}</div>
              <button @click="updateStatus(order.id, 'ready')" class="px-3 py-1.5 bg-indigo-50 text-indigo-600 font-bold text-xs rounded-lg hover:bg-indigo-100 transition-colors border border-indigo-200">
                {{ $t('admin.orders.readyBtn') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Ready Column -->
      <div class="w-[320px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-3 px-1">
          <h2 class="font-bold text-slate-700 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
            {{ $t('order.statuses.ready') }}
          </h2>
          <span class="bg-emerald-100 text-emerald-700 text-xs font-bold px-2 py-1 rounded-lg">{{ readyOrders.length }}</span>
        </div>
        <div class="bg-slate-100 rounded-2xl p-3 flex-grow overflow-y-auto space-y-3 shadow-inner">
          <div v-if="readyOrders.length === 0" class="text-center py-10 text-slate-400 text-sm font-medium">
            {{ $t('admin.orders.emptyState') }}
          </div>
          <div 
            v-for="order in readyOrders" 
            :key="order.id"
            class="bg-white p-4 rounded-xl shadow-sm border border-slate-200/60 hover:shadow-md transition-shadow relative overflow-hidden"
          >
            <div class="absolute top-0 left-0 w-1 h-full bg-emerald-500"></div>
            <div class="flex justify-between items-start mb-2">
              <span class="font-bold text-slate-800">#{{ orderCodeFromId(order.id) }}</span>
              <span class="text-xs font-semibold text-slate-500">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
            </div>
            <div class="flex items-center gap-2 mb-3">
              <span class="bg-slate-100 text-slate-600 text-xs font-bold px-2 py-1 rounded-md border border-slate-200">
                {{ $t('order.table') }}: {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
              </span>
            </div>

            <div class="flex items-center justify-between mt-auto pt-2 border-t border-slate-100">
              <div class="font-bold text-slate-800">{{ formatPrice(order.total) }}</div>
              <button @click="updateStatus(order.id, 'served')" class="px-3 py-1.5 bg-emerald-50 text-emerald-600 font-bold text-xs rounded-lg hover:bg-emerald-100 transition-colors border border-emerald-200">
                {{ $t('admin.orders.serveBtn') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Served / Completed Column -->
      <div class="w-[320px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-3 px-1">
          <h2 class="font-bold text-slate-700 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-slate-500"></span>
            {{ $t('admin.orders.otherStatus') }}
          </h2>
          <span class="bg-slate-200 text-slate-700 text-xs font-bold px-2 py-1 rounded-lg">{{ otherOrders.length }}</span>
        </div>
        <div class="bg-slate-100 rounded-2xl p-3 flex-grow overflow-y-auto space-y-3 shadow-inner opacity-80 hover:opacity-100 transition-opacity">
          <div v-if="otherOrders.length === 0" class="text-center py-10 text-slate-400 text-sm font-medium">
            {{ $t('admin.orders.emptyState') }}
          </div>
          <div 
            v-for="order in otherOrders" 
            :key="order.id"
            class="bg-white p-4 rounded-xl shadow-sm border border-slate-200/60 relative overflow-hidden"
          >
            <div class="absolute top-0 left-0 w-1 h-full" :class="getOtherStatusColor(order.status)"></div>
            <div class="flex justify-between items-start mb-2">
              <span class="font-bold text-slate-800">#{{ orderCodeFromId(order.id) }}</span>
              <span class="text-xs font-bold" :class="getOtherStatusTextColor(order.status)">{{ statusLabel(order.status) }}</span>
            </div>
            <div class="flex items-center gap-2 mb-3">
              <span class="bg-slate-100 text-slate-600 text-xs font-bold px-2 py-1 rounded-md border border-slate-200">
                {{ $t('order.table') }}: {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
              </span>
            </div>

            <div class="flex items-center justify-between mt-auto pt-2 border-t border-slate-100">
              <div class="font-bold text-slate-800">{{ formatPrice(order.total) }}</div>
              <button v-if="['served', 'bill_requested'].includes(order.status?.toLowerCase())" @click="updateStatus(order.id, 'payment_completed')" class="px-3 py-1.5 bg-brand-50 text-brand-600 font-bold text-xs rounded-lg hover:bg-brand-100 transition-colors border border-brand-200">
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

let unsub: (() => void) | null = null

onMounted(async () => {
  await orderStore.loadMenu() // Need menu for item names
  await orderStore.loadOrders()
  
  // WebSocket Connection
  connect(() => {
    const tenantCode = authStore.user?.tenantCode || authStore.tenantCode
    if (tenantCode) {
      unsub = subscribe(`/topic/orders/${tenantCode}`, (rawOrder: any) => {
        // Normalize the order data
        const updatedOrder = { ...rawOrder }
        
        // Ensure items/lines is an array (sometimes it might come as a string)
        if (typeof updatedOrder.items === 'string') {
          try { updatedOrder.items = JSON.parse(updatedOrder.items) } catch (e) {}
        }
        if (typeof updatedOrder.lines === 'string') {
          try { updatedOrder.lines = JSON.parse(updatedOrder.lines) } catch (e) {}
        }

        // Backend DTO uses 'lines', but template might expect 'items'
        if (updatedOrder.lines && !updatedOrder.items) {
          updatedOrder.items = updatedOrder.lines
        }
        
        // Play notification sound for NEW orders
        const isNew = !orderStore.orders.some(o => o.id === updatedOrder.id)
        if (isNew) {
          const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3')
          audio.play().catch(() => { /* ignore autoplay blocks */ })
        }

        // Update order in store
        const index = orderStore.orders.findIndex(o => o.id === updatedOrder.id)
        if (index !== -1) {
          orderStore.orders[index] = updatedOrder
        } else {
          // If it's a new order, add it to the beginning of the list
          orderStore.orders.unshift(updatedOrder)
        }
      })
    }
  })
})

onBeforeUnmount(() => {
  if (unsub) unsub()
})

// Filter by search
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
const otherOrders = computed(() => filteredOrders.value.filter(o => !['new', 'preparing', 'ready'].includes(o.status?.toLowerCase())).slice(0, 50)) // limit others

function menuItemName(id: number) {
  const i = orderStore.menuItemById(id)
  return i ? i.name : t('order.productFallback')
}

function formatPrice(p: number) {
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(p)
}

function timeAgo(dateString: string) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return ''
    const currentLocale = locale.value === 'tr' ? tr : enUS
    const distance = formatDistanceToNow(date, { addSuffix: true, locale: currentLocale })
    return distance // Using the robust date-fns library
  } catch {
    return ''
  }
}

async function updateStatus(id: string | number, status: string) {
  try {
    await orderStore.updateOrderStatus(id, status)
  } catch (e) {
    const errorMessage = e?.message || e?.toString() || t('admin.orders.updateError');
    uiStore.error(errorMessage);
  }
}

function getOtherStatusColor(status: string) {
  const s = status?.toLowerCase()
  if (s === 'served') return 'bg-blue-500'
  if (s === 'payment_completed') return 'bg-slate-800'
  if (s === 'canceled') return 'bg-rose-500'
  return 'bg-slate-400'
}

function getOtherStatusTextColor(status: string) {
  const s = status?.toLowerCase()
  if (s === 'served') return 'text-blue-600'
  if (s === 'payment_completed') return 'text-slate-600'
  if (s === 'canceled') return 'text-rose-600'
  return 'text-slate-500'
}

useHead({
  title: () => `${t('admin.orders.title')} | Admin`
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
