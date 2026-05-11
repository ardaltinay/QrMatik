<template>
  <div class="p-4 md:p-8 max-w-[1800px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col space-y-8">
    <!-- Financial Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 shrink-0 bg-white/50 backdrop-blur-xl p-8 rounded-[3rem] border border-white shadow-2xl shadow-slate-200/40">
      <div class="flex items-center gap-6">
        <div class="w-20 h-20 rounded-[2rem] bg-slate-900 text-white flex items-center justify-center shadow-2xl shadow-slate-900/20">
           <svg class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
        </div>
        <div>
          <h1 class="text-4xl font-black text-slate-900 tracking-tighter leading-none mb-2">{{ $t('admin.cashier.title') }}</h1>
          <p class="text-slate-500 font-medium text-lg">{{ $t('admin.cashier.subtitle') }}</p>
        </div>
      </div>

      <div class="flex items-center gap-4 w-full lg:w-auto">
        <div class="relative flex-1 lg:min-w-[400px] group">
          <svg class="w-6 h-6 absolute left-5 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-slate-900 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.tables.searchPlaceholder') || 'Search...'"
            class="w-full pl-16 pr-6 py-5 rounded-[2rem] bg-white border border-slate-100 focus:ring-8 focus:ring-slate-900/5 focus:border-slate-900 transition-all outline-none shadow-sm font-black text-slate-900 text-lg placeholder:text-slate-300"
          />
        </div>
      </div>
    </div>

    <!-- Active Tables Grid -->
    <div class="flex-grow overflow-y-auto pb-10 scrollbar-hide">
      <div v-if="filteredTableGroups.length === 0" class="h-full flex flex-col items-center justify-center space-y-6 opacity-40 grayscale">
         <div class="w-32 h-32 bg-slate-100 rounded-[3rem] flex items-center justify-center text-slate-300 border border-slate-200">
            <svg class="w-16 h-16" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" /></svg>
         </div>
         <p class="text-2xl font-black text-slate-900 uppercase tracking-widest">{{ $t('admin.cashier.noActiveTables') }}</p>
      </div>
      
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
        <TransitionGroup name="list">
          <div 
            v-for="group in filteredTableGroups" 
            :key="group.table"
            class="bg-white rounded-[3rem] border-2 flex flex-col overflow-hidden transition-all duration-500 hover:-translate-y-2 group/card shadow-[0_32px_64px_-24px_rgba(0,0,0,0.1)]"
            :class="[
              group.hasBillRequest 
                ? 'border-rose-500 animate-pulse-border bg-rose-50/10' 
                : 'border-white hover:border-slate-200'
            ]"
          >
            <!-- High Alert Badge -->
            <div v-if="group.hasBillRequest" class="bg-rose-500 text-white text-[10px] font-black uppercase tracking-[0.3em] py-3 text-center shadow-lg">
              {{ $t('order.statuses.bill_requested') }}
            </div>

            <!-- Table Header -->
            <div class="p-8 flex justify-between items-center border-b border-slate-50 bg-slate-50/30">
              <div class="flex items-center gap-3">
                 <div class="w-12 h-12 rounded-2xl bg-white shadow-md flex items-center justify-center text-slate-900 font-black text-xl border border-slate-100">
                    {{ group.table }}
                 </div>
                 <div class="flex flex-col">
                    <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest">MASA</span>
                    <span class="text-xs font-bold text-slate-600">{{ group.orders.length }} Sipariş</span>
                 </div>
              </div>
              <div class="w-3 h-3 rounded-full" :class="group.hasBillRequest ? 'bg-rose-500 animate-ping' : 'bg-emerald-500'"></div>
            </div>
    
            <!-- Items Detail -->
            <div class="p-8 flex-grow overflow-y-auto max-h-[300px] space-y-6 scrollbar-thin">
              <div v-for="(order, oIdx) in group.orders" :key="order.id">
                <div v-if="oIdx > 0" class="h-px bg-slate-100 mb-6"></div>
                
                <div class="flex justify-between items-center mb-3">
                  <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest">#{{ orderCodeFromId(order.id) }}</span>
                  <span class="px-2 py-1 rounded-lg text-[9px] font-black uppercase tracking-widest border" :class="getStatusBadgeClass(order.status)">
                    {{ statusLabel(order.status) }}
                  </span>
                </div>

                <div class="space-y-2">
                  <div v-for="(it, i) in (order.items || order.lines)" :key="i" class="flex justify-between items-start text-sm group/item">
                    <div class="flex-grow min-w-0 pr-4">
                       <span class="font-black text-slate-900 pr-1 text-base">{{ it.qty || it.quantity }}x</span>
                       <span class="text-slate-600 font-bold group-hover/item:text-slate-900 transition-colors">{{ it.name || menuItemName(it.itemId) }}</span>
                    </div>
                    <span class="font-black text-slate-900 shrink-0">{{ formatPrice((it.price || 0) * (it.qty || it.quantity)) }}</span>
                  </div>
                </div>
              </div>
            </div>
    
            <!-- Action Footer -->
            <div class="p-8 bg-slate-50 border-t border-slate-100 mt-auto">
              <div class="flex justify-between items-center mb-6">
                <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest">{{ $t('admin.cashier.tableTotal') }}</span>
                <span class="text-3xl font-black text-slate-900 tracking-tighter">{{ formatPrice(group.total) }}</span>
              </div>
              <button 
                @click="checkoutTable(group)" 
                class="w-full py-5 font-black text-xs uppercase tracking-[0.2em] rounded-[1.5rem] transition-all flex items-center justify-center gap-3 active:scale-[0.98] shadow-2xl"
                :class="[
                  group.hasBillRequest 
                    ? 'bg-rose-500 hover:bg-rose-600 text-white shadow-rose-500/30' 
                    : 'bg-slate-900 hover:bg-black text-white shadow-slate-900/30'
                ]"
                :disabled="checkingOut === group.table"
              >
                <svg v-if="checkingOut === group.table" class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span v-else>{{ $t('admin.cashier.payBtn') }}</span>
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
import { useAuthStore } from '~/stores/auth'
import { useSocket } from '~/composables/useSocket'

definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const orderStore = useOrderStore()
const authStore = useAuthStore()
const { orderCodeFromId, statusLabel } = useFormat()
const uiStore = useUiStore()

const searchQuery = ref('')
const checkingOut = ref<string | null>(null)

function getStatusBadgeClass(status?: string) {
  const s = (status || '').toLowerCase()
  switch (s) {
    case 'bill_requested': return 'bg-rose-50 text-rose-600 border-rose-100'
    case 'served': return 'bg-emerald-50 text-emerald-600 border-emerald-100'
    case 'ready': return 'bg-blue-50 text-blue-600 border-blue-100'
    case 'preparing': return 'bg-amber-50 text-amber-600 border-amber-100'
    default: return 'bg-slate-50 text-slate-500 border-slate-100'
  }
}

onMounted(async () => {
  await orderStore.loadMenu()
  await orderStore.loadOrders()
  
  const { connect, subscribe } = useSocket()
  connect(() => {
    const tenantCode = authStore.user?.tenantCode || authStore.tenantCode
    if (tenantCode) {
      subscribe(`/topic/orders/${tenantCode}`, (updatedOrder: any) => {
        if (updatedOrder.status === 'bill_requested') {
          const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3')
          audio.play().catch(() => {})
          uiStore.info(`${updatedOrder.tableCode || updatedOrder.table} masası hesap istedi!`)
        }
        const index = orderStore.orders.findIndex(o => o.id === updatedOrder.id)
        if (index !== -1) orderStore.orders[index] = updatedOrder
        else orderStore.orders.unshift(updatedOrder)
      })
    }
  })
})

function menuItemName(id: number) { return orderStore.menuItemById(id)?.name || t('order.productFallback') }

function formatPrice(p: number) {
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(p)
}

const tableGroups = computed(() => {
  const terminalStatuses = ['payment_completed', 'canceled', 'expired']
  const activeOrders = orderStore.orders.filter(o => !terminalStatuses.includes((o.status || '').toLowerCase()))
  const groups: Record<string, any> = {}

  for (const order of activeOrders) {
    const tableId = order.tableCode || order.table || 'Unknown'
    if (!groups[tableId]) {
      groups[tableId] = { table: tableId, orders: [], total: 0, hasBillRequest: false }
    }
    groups[tableId].orders.push(order)
    groups[tableId].total += order.total || 0
    if ((order.status || '').toLowerCase() === 'bill_requested') groups[tableId].hasBillRequest = true
  }

  return Object.values(groups).sort((a, b) => {
    if (a.hasBillRequest && !b.hasBillRequest) return -1
    if (!a.hasBillRequest && b.hasBillRequest) return 1
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
    title: t('admin.cashier.checkoutBtn'),
    message: t('admin.cashier.checkoutConfirm'),
    confirmText: t('admin.cashier.checkoutBtn'),
    onConfirm: async () => {
      checkingOut.value = group.table
      try {
        const promises = group.orders.map((o: any) => orderStore.updateOrderStatus(o.id, 'payment_completed'))
        await Promise.all(promises)
        uiStore.success(t('admin.cashier.checkoutSuccess'))
      } catch (e: any) { uiStore.error(e?.message || 'Hata oluştu') }
      finally { checkingOut.value = null }
    }
  })
}

useHead({ title: () => `${t('admin.cashier.title')} | Admin` })
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar { display: none; }
.scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }

.scrollbar-thin::-webkit-scrollbar { width: 4px; }
.scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
.scrollbar-thin::-webkit-scrollbar-thumb { background-color: #e2e8f0; border-radius: 20px; }

@keyframes pulse-border {
  0%, 100% { border-color: rgba(244, 63, 94, 1); box-shadow: 0 0 0 0 rgba(244, 63, 94, 0.4); }
  50% { border-color: rgba(244, 63, 94, 0.4); box-shadow: 0 0 40px 10px rgba(244, 63, 94, 0.1); }
}
.animate-pulse-border { animation: pulse-border 2s infinite ease-in-out; }

.list-enter-active, .list-leave-active { transition: all 0.5s ease; }
.list-enter-from { opacity: 0; transform: scale(0.9); }
.list-leave-to { opacity: 0; transform: scale(1.1); }
</style>
