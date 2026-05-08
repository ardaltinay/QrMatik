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
    <div class="flex-grow overflow-y-auto pb-8 scrollbar-thin">
      <div v-if="filteredTableGroups.length === 0" class="text-center py-20 text-slate-400 font-medium">
        {{ $t('admin.cashier.noActiveTables') }}
      </div>
      
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        <div 
          v-for="group in filteredTableGroups" 
          :key="group.table"
          class="bg-white rounded-2xl border flex flex-col overflow-hidden transition-all duration-200"
          :class="[
            group.hasBillRequest ? 'border-rose-500 shadow-lg shadow-rose-500/10' : 'border-slate-200 shadow-sm hover:shadow-md'
          ]"
        >
          <!-- Bill Requested Indicator -->
          <div v-if="group.hasBillRequest" class="bg-rose-500 text-white text-[10px] font-bold uppercase tracking-widest py-1.5 text-center animate-pulse">
            {{ $t('order.statuses.bill_requested') }}
          </div>

          <!-- Table Header -->
          <div class="p-4 flex justify-between items-center border-b border-slate-50 bg-slate-50/50">
            <div class="flex items-center gap-2">
              <span class="font-bold text-slate-900 text-lg tracking-tight">{{ group.table }}</span>
            </div>
            <div class="text-[10px] font-bold text-brand-600 bg-brand-50 px-2 py-1 rounded-lg">
              {{ group.orders.length }} {{ $t('admin.reports.summary.orders') || 'Orders' }}
            </div>
          </div>
 
          <!-- Items List (Compact) -->
          <div class="p-4 flex-grow overflow-y-auto max-h-[280px] space-y-4">
            <div v-for="(order, oIdx) in group.orders" :key="order.id" class="relative">
              <div v-if="oIdx > 0" class="border-t border-dashed border-slate-100 my-3"></div>
              
              <div class="flex justify-between items-center mb-2">
                <span class="text-[10px] font-medium text-slate-400 uppercase">#{{ orderCodeFromId(order.id) }}</span>
                <span class="text-[9px] font-bold px-1.5 py-0.5 rounded border border-current" :class="getStatusBadgeClass(order.status)">
                  {{ statusLabel(order.status) }}
                </span>
              </div>

              <div class="space-y-1">
                <div v-for="(it, i) in (order.items || order.lines)" :key="i" class="flex justify-between items-start text-sm">
                  <span class="text-slate-600 line-clamp-1 flex-grow">
                    <span class="font-bold text-slate-900 pr-1">{{ it.qty || it.quantity }}x</span>
                    {{ it.name || menuItemName(it.itemId) }}
                  </span>
                  <span class="font-semibold text-slate-800 shrink-0 ml-2">{{ formatPrice((it.price || 0) * (it.qty || it.quantity)) }}</span>
                </div>
              </div>
            </div>
          </div>
 
          <!-- Footer -->
          <div class="p-4 bg-slate-50 border-t border-slate-100 mt-auto">
            <div class="flex justify-between items-center mb-3">
              <span class="text-xs font-semibold text-slate-500">{{ $t('admin.cashier.tableTotal') }}</span>
              <span class="text-xl font-bold text-slate-900 tracking-tight">{{ formatPrice(group.total) }}</span>
            </div>
            <button 
              @click="checkoutTable(group)" 
              class="w-full py-2.5 font-bold text-xs uppercase tracking-widest rounded-xl transition-all flex items-center justify-center gap-2 active:scale-[0.98]"
              :class="[
                group.hasBillRequest 
                  ? 'bg-rose-500 hover:bg-rose-600 text-white shadow-md shadow-rose-500/20' 
                  : 'bg-slate-800 hover:bg-slate-900 text-white shadow-sm'
              ]"
              :disabled="checkingOut === group.table"
            >
              <svg v-if="checkingOut === group.table" class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
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
const authStore = useAuthStore()
const { orderCodeFromId, statusLabel } = useFormat()
const uiStore = useUiStore()

const searchQuery = ref('')
const checkingOut = ref<string | null>(null)
let pollInterval: any = null

function getStatusBadgeClass(status?: string) {
  const s = (status || '').toLowerCase()
  switch (s) {
    case 'bill_requested': return 'bg-rose-100 text-rose-700'
    case 'served': return 'bg-emerald-100 text-emerald-700'
    case 'ready': return 'bg-blue-100 text-blue-700'
    case 'preparing': return 'bg-amber-100 text-amber-700'
    default: return 'bg-slate-100 text-slate-500'
  }
}

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
  // Cashier sees all orders that are not terminal (completed, canceled, expired)
  const terminalStatuses = ['payment_completed', 'canceled', 'expired']
  const activeOrders = orderStore.orders.filter(o => !terminalStatuses.includes((o.status || '').toLowerCase()))

  const groups: Record<string, { table: string, orders: any[], total: number, hasBillRequest: boolean }> = {}

  for (const order of activeOrders) {
    const tableId = order.tableCode || order.table || 'Unknown'
    if (!groups[tableId]) {
      groups[tableId] = {
        table: tableId,
        orders: [],
        total: 0,
        hasBillRequest: false
      }
    }
    groups[tableId].orders.push(order)
    groups[tableId].total += order.total || 0
    if ((order.status || '').toLowerCase() === 'bill_requested') {
      groups[tableId].hasBillRequest = true
    }
  }

  // Convert to array and sort
  return Object.values(groups).sort((a, b) => {
    // 1. Prioritize tables with bill requests
    if (a.hasBillRequest && !b.hasBillRequest) return -1
    if (!a.hasBillRequest && b.hasBillRequest) return 1
    // 2. Then sort by table name
    return a.table.localeCompare(b.table)
  })
})

const filteredTableGroups = computed(() => {
  if (!searchQuery.value) return tableGroups.value
  const q = searchQuery.value.toLowerCase()
  return tableGroups.value.filter(g => g.table.toLowerCase().includes(q))
})

async function checkoutTable(group: any) {
  uiStore.confirm({
    title: t('admin.cashier.checkoutBtn') || 'Ödeme Al',
    message: t('admin.cashier.checkoutConfirm') || 'Tüm siparişler ödendi olarak işaretlenecek. Emin misiniz?',
    confirmText: t('admin.cashier.checkoutBtn'),
    onConfirm: async () => {
      checkingOut.value = group.table
      try {
        // Update all orders in this group to payment_completed
        const promises = group.orders.map((o: any) => orderStore.updateOrderStatus(o.id, 'payment_completed'))
        await Promise.all(promises)
        uiStore.success(t('admin.cashier.checkoutSuccess') || 'Ödeme tamamlandı.')
      } catch (e: any) {
        const errorMessage = e?.message || e?.toString() || t('admin.orders.updateError') || 'An error occurred.';
        uiStore.error(errorMessage);
      } finally {
        checkingOut.value = null
      }
    }
  })
}

useHead({
  title: () => `${t('admin.cashier.title')} | Admin`
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
