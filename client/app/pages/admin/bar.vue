<template>
  <div class="p-6 md:p-8 max-w-[1600px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8 shrink-0">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.bar.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.bar.subtitle') }}</p>
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
                {{ $t('order.table') }}: {{ order.tableCode || order.table || '-' }}
              </span>
            </div>
            
            <div class="text-sm text-slate-600 mb-4 bg-slate-50 p-2 rounded-lg border border-slate-100">
              <span v-for="(it, i) in filterItems(order.items || order.lines)" :key="i" class="block">
                <span class="font-semibold text-slate-800">{{ it.qty || it.quantity }}x</span> {{ it.name || menuItemName(it.itemId) }}
                <span v-if="it.note" class="text-xs text-amber-600 block mt-0.5 font-medium">• {{ it.note }}</span>
              </span>
            </div>

            <div class="flex items-center justify-end mt-auto pt-2 border-t border-slate-100">
              <div class="flex gap-2">
                <button @click="updateStatus(order.id, 'preparing')" class="px-4 py-2 bg-amber-50 text-amber-600 font-bold text-xs rounded-lg hover:bg-amber-100 transition-colors border border-amber-200 w-full text-center">
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
                {{ $t('order.table') }}: {{ order.tableCode || order.table || '-' }}
              </span>
            </div>
            
            <div class="text-sm text-slate-600 mb-4 bg-slate-50 p-2 rounded-lg border border-slate-100">
              <span v-for="(it, i) in filterItems(order.items || order.lines)" :key="i" class="block">
                <span class="font-semibold text-slate-800">{{ it.qty || it.quantity }}x</span> {{ it.name || menuItemName(it.itemId) }}
                <span v-if="it.note" class="text-xs text-amber-600 block mt-0.5 font-medium">• {{ it.note }}</span>
              </span>
            </div>

            <div class="flex items-center justify-end mt-auto pt-2 border-t border-slate-100">
              <button @click="updateStatus(order.id, 'ready')" class="px-4 py-2 bg-indigo-50 text-indigo-600 font-bold text-xs rounded-lg hover:bg-indigo-100 transition-colors border border-indigo-200 w-full text-center">
                {{ $t('admin.orders.readyBtn') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Ready Column (Completed in Bar) -->
      <div class="w-[320px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-3 px-1">
          <h2 class="font-bold text-slate-700 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
            {{ $t('order.statuses.ready') }}
          </h2>
          <span class="bg-emerald-100 text-emerald-700 text-xs font-bold px-2 py-1 rounded-lg">{{ readyOrders.length }}</span>
        </div>
        <div class="bg-slate-100 rounded-2xl p-3 flex-grow overflow-y-auto space-y-3 shadow-inner opacity-70">
          <div v-if="readyOrders.length === 0" class="text-center py-10 text-slate-400 text-sm font-medium">
            {{ $t('admin.orders.emptyState') }}
          </div>
          <div 
            v-for="order in readyOrders" 
            :key="order.id"
            class="bg-white p-4 rounded-xl shadow-sm border border-slate-200/60 relative overflow-hidden"
          >
            <div class="absolute top-0 left-0 w-1 h-full bg-emerald-500"></div>
            <div class="flex justify-between items-start mb-2">
              <span class="font-bold text-slate-800">#{{ orderCodeFromId(order.id) }}</span>
              <span class="text-xs font-semibold text-slate-400 italic">{{ $t('order.statuses.ready') }}</span>
            </div>
            
            <div class="text-xs text-slate-500 mt-2">
              {{ $t('order.table') }}: {{ order.tableCode || order.table || '-' }}
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
const { orderCodeFromId } = useFormat()
const uiStore = useUiStore()

const { connect, subscribe } = useSocket()
const authStore = useAuthStore()
const searchQuery = ref('')
let unsub: (() => void) | null = null

onMounted(async () => {
  await orderStore.loadMenu() // Need menu for item categories
  await orderStore.loadOrders()
  
  // WebSocket Connection
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
          // If it's a new order, add it to the beginning
          orderStore.orders.unshift(updatedOrder)
        }
      })
    }
  })
})

onBeforeUnmount(() => {
  if (unsub) unsub()
})

function getCategory(itemId: number) {
  const i = orderStore.menuItemById(itemId)
  return i ? i.category : ''
}

function filterItems(items: any[]) {
  if (!items) return []
  return items.filter(it => {
    const cat = (getCategory(it.itemId) || '').toLowerCase()
    // Sadece içecek olduğu bilinenleri göster
    return cat === 'drink' || cat === 'drinks' || cat === 'içecek' || cat === 'içecekler'
  })
}

// Filter by search & bar category
const filteredOrders = computed(() => {
  let list = orderStore.orders.filter(o => {
    const items = o.items || o.lines || []
    return filterItems(items).length > 0
  })

  if (!searchQuery.value) return list
  const q = searchQuery.value.toLowerCase()
  return list.filter(o => {
    const idMatch = String(orderCodeFromId(o.id)).toLowerCase().includes(q)
    const tableMatch = String(o.tableCode || o.table || '').toLowerCase().includes(q)
    return idMatch || tableMatch
  })
})

const newOrders = computed(() => filteredOrders.value.filter(o => (o.status || '').toLowerCase() === 'new'))
const preparingOrders = computed(() => filteredOrders.value.filter(o => (o.status || '').toLowerCase() === 'preparing'))
const readyOrders = computed(() => filteredOrders.value.filter(o => (o.status || '').toLowerCase() === 'ready'))

function menuItemName(id: number) {
  const i = orderStore.menuItemById(id)
  return i ? i.name : t('order.productFallback')
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
  } catch (e) {
    const errorMessage = e?.message || e?.toString() || t('admin.orders.updateError');
    uiStore.error(errorMessage);
  }
}

useHead({
  title: () => `${t('admin.bar.title')} | Admin | feasymenu`
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
