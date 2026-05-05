<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8 shrink-0">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.cashier.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.cashier.subtitle') }}</p>
      </div>

      <div class="w-full sm:w-72">
        <div class="relative">
          <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.tables.searchPlaceholder') || 'Search...'"
            class="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm"
          />
        </div>
      </div>
    </div>

    <!-- Tables Grid -->
    <div class="flex-grow overflow-y-auto pb-4 scrollbar-thin">
      <div v-if="filteredTableGroups.length === 0" class="text-center py-20 text-slate-400 font-medium">
        {{ $t('admin.cashier.noActiveTables') }}
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div 
          v-for="group in filteredTableGroups" 
          :key="group.table"
          class="bg-white rounded-2xl border flex flex-col overflow-hidden transition-all duration-500"
          :class="[
            group.hasBillRequest ? 'border-rose-500 shadow-xl shadow-rose-500/10 ring-2 ring-rose-500 ring-offset-2 animate-pulse-subtle' : 'border-slate-200 shadow-sm hover:shadow-md'
          ]"
        >
          <!-- Bill Requested Badge -->
          <div v-if="group.hasBillRequest" class="bg-rose-500 text-white text-[10px] font-black uppercase tracking-[0.2em] py-1.5 text-center">
            {{ $t('order.statuses.bill_requested') }}
          </div>
          <!-- Table Header -->
          <div class="bg-brand-50 border-b border-brand-100 p-4 flex justify-between items-center">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-white text-brand-600 flex items-center justify-center font-bold shadow-sm">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                </svg>
              </div>
              <span class="font-bold text-brand-900 text-lg">{{ group.table }}</span>
            </div>
            <div class="text-xs font-semibold text-brand-600 bg-brand-100 px-2 py-1 rounded-md">
              {{ group.orders.length }} {{ $t('admin.reports.summary.orders') || 'Orders' }}
            </div>
          </div>

          <!-- Items List -->
          <div class="p-4 flex-grow overflow-y-auto max-h-64 bg-slate-50/50">
            <div class="space-y-3">
              <div v-for="order in group.orders" :key="order.id" class="text-sm">
                <div class="flex justify-between items-center mb-1">
                  <span class="text-xs font-semibold text-slate-400">#{{ orderCodeFromId(order.id) }} - {{ statusLabel(order.status) }}</span>
                  <span class="font-bold text-slate-700">{{ formatPrice(order.total) }}</span>
                </div>
                <div v-for="(it, i) in (order.items || order.lines)" :key="i" class="flex justify-between text-slate-600 mb-0.5">
                  <span class="truncate pr-2">
                    <span class="font-semibold text-slate-800">{{ it.qty || it.quantity }}x</span> 
                    {{ it.name || menuItemName(it.itemId) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Checkout Footer -->
          <div class="p-4 border-t border-slate-100 bg-white mt-auto">
            <div class="flex justify-between items-center mb-4">
              <span class="text-sm font-semibold text-slate-500">{{ $t('admin.cashier.tableTotal') }}</span>
              <span class="text-xl font-bold text-slate-900">{{ formatPrice(group.total) }}</span>
            </div>
            <button 
              @click="checkoutTable(group)" 
              class="w-full py-2.5 bg-brand-500 text-white font-bold rounded-xl hover:bg-brand-600 transition-colors shadow-sm  disabled:opacity-50"
              :disabled="checkingOut === group.table"
            >
              <svg v-if="checkingOut === group.table" class="w-5 h-5 mx-auto animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span v-else>{{ $t('admin.cashier.payBtn') }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'

definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const orderStore = useOrderStore()
const { orderCodeFromId, statusLabel } = useFormat()
const uiStore = useUiStore()

const searchQuery = ref('')
const checkingOut = ref<string | null>(null)
let pollInterval: any = null

onMounted(async () => {
  await orderStore.loadMenu()
  await orderStore.loadOrders()
  
  // WebSocket Connection
  const { connect, subscribe } = useSocket()
  connect(() => {
    const tenantCode = authStore.user?.tenantCode || authStore.tenantCode
    if (tenantCode) {
      subscribe(`/topic/orders/${tenantCode}`, (updatedOrder: any) => {
        if (updatedOrder.status === 'bill_requested') {
          // Play notification sound
          const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3')
          audio.play().catch(() => {})
          uiStore.info(`${updatedOrder.tableCode || updatedOrder.table} masası hesap istedi!`)
        }
        
        // Update order in store
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
  if (pollInterval) clearInterval(pollInterval)
})

function menuItemName(id: number) {
  const i = orderStore.menuItemById(id)
  return i ? i.name : t('order.productFallback')
}

function formatPrice(p: number) {
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(p)
}

// Group active orders by table
const tableGroups = computed(() => {
  // Cashier only sees orders with BILL_REQUESTED status
  const activeOrders = orderStore.orders.filter(o => o.status === 'bill_requested')

  const groups: Record<string, { table: string, orders: any[], total: number }> = {}

  for (const order of activeOrders) {
    const tableId = order.tableCode || order.table || 'Unknown'
    if (!groups[tableId]) {
      groups[tableId] = {
        table: tableId,
        orders: [],
        total: 0
      }
    }
    groups[tableId].orders.push(order)
    groups[tableId].total += order.total || 0
    if (order.status === 'bill_requested') {
      (groups[tableId] as any).hasBillRequest = true
    }
  }

  // Convert to array and sort by table name
  return Object.values(groups).sort((a, b) => {
    // Prioritize tables with bill requests
    if ((a as any).hasBillRequest && !(b as any).hasBillRequest) return -1
    if (!(a as any).hasBillRequest && (b as any).hasBillRequest) return 1
    return a.table.localeCompare(b.table)
  })
})

const filteredTableGroups = computed(() => {
  if (!searchQuery.value) return tableGroups.value
  const q = searchQuery.value.toLowerCase()
  return tableGroups.value.filter(g => g.table.toLowerCase().includes(q))
})

async function checkoutTable(group: any) {
  if (!confirm(t('admin.cashier.checkoutConfirm'))) return

  checkingOut.value = group.table
  try {
    // Update all orders in this group to payment_completed
    const promises = group.orders.map((o: any) => orderStore.updateOrderStatus(o.id, 'payment_completed'))
    await Promise.all(promises)
    // Optional: add a toast success message here
  } catch (e: any) {
    const errorMessage = e?.message || e?.toString() || t('admin.orders.updateError') || 'An error occurred.';
    uiStore.error(errorMessage);
  } finally {
    checkingOut.value = null
  }
}

useHead({
  title: () => `${t('admin.cashier.title')} | Admin | feasymenu`
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
@keyframes pulse-subtle {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.85; transform: scale(1.01); }
}
.animate-pulse-subtle {
  animation: pulse-subtle 2s infinite ease-in-out;
}
</style>
