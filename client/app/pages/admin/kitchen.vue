<template>
  <div class="p-4 md:p-8 max-w-[1800px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col space-y-6">
    <!-- Operations Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 shrink-0 bg-white/50 backdrop-blur-xl p-6 rounded-[2.5rem] border border-white shadow-xl shadow-slate-200/40">
      <div class="flex items-center gap-5">
        <div class="w-16 h-16 rounded-2xl bg-orange-500 text-white flex items-center justify-center shadow-lg shadow-orange-500/30">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>
        </div>
        <div>
          <h1 class="text-3xl font-black text-slate-900 tracking-tight leading-none mb-2">{{ $t('admin.kitchen.title') }}</h1>
          <p class="text-slate-500 font-medium text-sm">{{ $t('admin.kitchen.subtitle') }}</p>
        </div>
      </div>

      <div class="flex items-center gap-3 w-full lg:w-auto">
        <div class="relative flex-1 lg:min-w-[400px] group">
          <svg class="w-5 h-5 absolute left-5 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-orange-500 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.orders.searchPlaceholder')"
            class="w-full pl-14 pr-6 py-4 rounded-2xl bg-white border border-slate-200 focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500 transition-all outline-none shadow-sm font-bold text-slate-700"
          />
        </div>
        
        <!-- Live Indicator -->
        <div class="hidden lg:flex items-center gap-3 bg-emerald-50 px-6 py-3.5 rounded-2xl border border-emerald-100 shadow-sm shrink-0">
           <div class="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping"></div>
           <span class="text-[10px] font-black text-emerald-600 uppercase tracking-widest">{{ $t('admin.common.status.liveSync') }}</span>
        </div>
      </div>
    </div>

    <!-- Operations Kanban Board -->
    <div class="flex-grow overflow-x-auto pb-6 flex gap-8 items-start scrollbar-hide">
      
      <!-- COLUMN: NEW -->
      <div class="w-full min-w-[320px] max-w-[450px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-5 px-4 bg-white/70 backdrop-blur-md py-4 rounded-2xl border border-white shadow-sm">
          <h2 class="font-black text-slate-900 flex items-center gap-3 tracking-tight">
            <div class="w-3 h-3 rounded-full bg-orange-500 shadow-[0_0_10px_rgba(249,115,22,0.4)]"></div>
            {{ $t('order.statuses.new') }}
          </h2>
          <span class="bg-orange-500 text-white text-[10px] font-black px-3 py-1.5 rounded-xl shadow-lg shadow-orange-500/20">{{ newOrders.length }}</span>
        </div>
        
        <div class="flex-grow overflow-y-auto space-y-4 pr-2 scrollbar-thin">
          <TransitionGroup name="list">
            <div 
              v-for="order in newOrders" 
              :key="order.id"
              class="bg-white p-6 rounded-[2rem] shadow-xl shadow-slate-200/40 border border-slate-100 hover:border-orange-300 transition-all relative overflow-hidden group/card animate-pulse-subtle"
            >
              <div class="absolute top-0 left-0 w-2 h-full bg-orange-500"></div>
              
              <div class="flex justify-between items-start mb-4">
                <div>
                   <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest block mb-1">SIPARİŞ NO</span>
                   <span class="text-2xl font-black text-slate-900 tracking-tighter">#{{ orderCodeFromId(order.id) }}</span>
                </div>
                <div class="text-right">
                   <div class="px-3 py-1.5 bg-slate-900 text-white text-[10px] font-black rounded-xl tracking-widest uppercase mb-1 shadow-lg shadow-slate-900/10">
                      {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
                   </div>
                   <span class="text-[10px] font-bold text-slate-400 uppercase">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
                </div>
              </div>
              
              <div class="space-y-3 mb-6 bg-slate-50/50 p-4 rounded-2xl border border-slate-100 shadow-inner">
                <div v-for="(it, i) in filterItems(order.items || order.lines)" :key="i" class="flex items-start gap-3">
                  <div class="bg-orange-500 text-white w-7 h-7 rounded-lg flex items-center justify-center font-black text-sm shrink-0 shadow-md shadow-orange-500/20">
                    {{ it.qty || it.quantity }}x
                  </div>
                  <div class="flex-1">
                    <p class="font-black text-slate-900 text-lg leading-tight uppercase tracking-tight">{{ it.name || menuItemName(it.itemId) }}</p>
                    <p v-if="it.note" class="text-xs font-bold text-orange-600 bg-orange-50 px-2 py-1 rounded-md mt-1.5 inline-block border border-orange-100">
                      ⚠️ {{ it.note }}
                    </p>
                  </div>
                </div>
              </div>

              <button @click="updateStatus(order.id, 'preparing')" class="w-full py-4.5 bg-orange-500 text-white font-black text-sm uppercase tracking-[0.2em] rounded-2xl shadow-xl shadow-orange-500/30 hover:bg-orange-600 active:scale-[0.98] transition-all">
                {{ $t('admin.orders.accept') }}
              </button>
            </div>
          </TransitionGroup>

          <div v-if="newOrders.length === 0" class="py-20 text-center space-y-4 bg-slate-50/50 rounded-[2.5rem] border border-dashed border-slate-200">
             <div class="w-16 h-16 bg-white rounded-2xl flex items-center justify-center mx-auto text-slate-200 shadow-sm">
                <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
             </div>
             <p class="text-slate-400 font-bold uppercase tracking-widest text-xs">Yeni sipariş bekleniyor</p>
          </div>
        </div>
      </div>

      <!-- COLUMN: PREPARING -->
      <div class="w-full min-w-[320px] max-w-[450px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-5 px-4 bg-white/70 backdrop-blur-md py-4 rounded-2xl border border-white shadow-sm">
          <h2 class="font-black text-slate-900 flex items-center gap-3 tracking-tight">
            <div class="w-3 h-3 rounded-full bg-indigo-500 shadow-[0_0_10px_rgba(99,102,241,0.4)]"></div>
            {{ $t('order.statuses.preparing') }}
          </h2>
          <span class="bg-indigo-500 text-white text-[10px] font-black px-3 py-1.5 rounded-xl shadow-lg shadow-indigo-500/20">{{ preparingOrders.length }}</span>
        </div>

        <div class="flex-grow overflow-y-auto space-y-4 pr-2 scrollbar-thin">
          <TransitionGroup name="list">
            <div 
              v-for="order in preparingOrders" 
              :key="order.id"
              class="bg-white p-6 rounded-[2rem] shadow-xl shadow-slate-200/40 border border-slate-100 hover:border-indigo-300 transition-all relative overflow-hidden group/card"
            >
              <div class="absolute top-0 left-0 w-2 h-full bg-indigo-500"></div>
              
              <div class="flex justify-between items-start mb-4">
                <div>
                   <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest block mb-1">SIPARİŞ NO</span>
                   <span class="text-2xl font-black text-slate-900 tracking-tighter">#{{ orderCodeFromId(order.id) }}</span>
                </div>
                <div class="text-right">
                   <div class="px-3 py-1.5 bg-indigo-600 text-white text-[10px] font-black rounded-xl tracking-widest uppercase mb-1 shadow-lg shadow-indigo-500/20">
                      {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
                   </div>
                   <span class="text-[10px] font-bold text-slate-400 uppercase">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
                </div>
              </div>
              
              <div class="space-y-3 mb-6 bg-slate-50/50 p-4 rounded-2xl border border-slate-100 shadow-inner">
                <div v-for="(it, i) in filterItems(order.items || order.lines)" :key="i" class="flex items-start gap-3">
                  <div class="bg-indigo-500 text-white w-7 h-7 rounded-lg flex items-center justify-center font-black text-sm shrink-0 shadow-md shadow-indigo-500/20">
                    {{ it.qty || it.quantity }}x
                  </div>
                  <div class="flex-1">
                    <p class="font-black text-slate-900 text-lg leading-tight uppercase tracking-tight">{{ it.name || menuItemName(it.itemId) }}</p>
                    <p v-if="it.note" class="text-xs font-bold text-orange-600 bg-orange-50 px-2 py-1 rounded-md mt-1.5 inline-block border border-orange-100">
                      ⚠️ {{ it.note }}
                    </p>
                  </div>
                </div>
              </div>

              <button @click="updateStatus(order.id, 'ready')" class="w-full py-4.5 bg-indigo-600 text-white font-black text-sm uppercase tracking-[0.2em] rounded-2xl shadow-xl shadow-indigo-500/30 hover:bg-indigo-700 active:scale-[0.98] transition-all">
                {{ $t('admin.orders.readyBtn') }}
              </button>
            </div>
          </TransitionGroup>
        </div>
      </div>

      <!-- COLUMN: READY -->
      <div class="w-full min-w-[320px] max-w-[450px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-5 px-4 bg-white/70 backdrop-blur-md py-4 rounded-2xl border border-white shadow-sm">
          <h2 class="font-black text-slate-900 flex items-center gap-3 tracking-tight">
            <div class="w-3 h-3 rounded-full bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.4)]"></div>
            {{ $t('order.statuses.ready') }}
          </h2>
          <span class="bg-emerald-500 text-white text-[10px] font-black px-3 py-1.5 rounded-xl shadow-lg shadow-emerald-500/20">{{ readyOrders.length }}</span>
        </div>

        <div class="flex-grow overflow-y-auto space-y-4 pr-2 scrollbar-thin opacity-60 hover:opacity-100 transition-opacity">
          <TransitionGroup name="list">
            <div 
              v-for="order in readyOrders" 
              :key="order.id"
              class="bg-white p-6 rounded-[2.5rem] shadow-sm border border-slate-100 relative overflow-hidden"
            >
              <div class="absolute top-0 left-0 w-2 h-full bg-emerald-500"></div>
              <div class="flex justify-between items-center">
                 <div>
                    <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest block mb-1">BİTEN SİPARİŞ</span>
                    <span class="text-xl font-black text-slate-400 tracking-tighter strike">#{{ orderCodeFromId(order.id) }}</span>
                 </div>
                 <div class="px-4 py-2 bg-slate-100 text-slate-500 text-xs font-black rounded-xl">
                    {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
                 </div>
              </div>
            </div>
          </TransitionGroup>
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

definePageMeta({ layout: 'admin' })

const { t, locale } = useI18n()
const orderStore = useOrderStore()
const { orderCodeFromId } = useFormat()
const uiStore = useUiStore()

const { connect, subscribe } = useSocket()
const authStore = useAuthStore()
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

        const isNew = !orderStore.orders.some(o => o.id === updatedOrder.id)
        if (isNew) {
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

function getCategory(itemId: number) { return orderStore.menuItemById(itemId)?.category || '' }

function filterItems(items: any[]) {
  if (!items) return []
  return items.filter(it => {
    const cat = (getCategory(it.itemId) || '').toLowerCase()
    return !['drink', 'drinks', 'içecek', 'içecekler'].includes(cat)
  })
}

const filteredOrders = computed(() => {
  let list = orderStore.orders.filter(o => {
    const s = (o.status || '').toLowerCase()
    if (['payment_completed', 'canceled', 'expired'].includes(s)) return false
    return filterItems(o.items || o.lines || []).length > 0
  })

  if (!searchQuery.value) return list
  const q = searchQuery.value.toLowerCase()
  return list.filter(o => 
    String(orderCodeFromId(o.id)).toLowerCase().includes(q) || 
    String(o.tableCode || o.table || '').toLowerCase().includes(q)
  )
})

const newOrders = computed(() => filteredOrders.value.filter(o => (o.kitchenStatus || '').toLowerCase() === 'new'))
const preparingOrders = computed(() => filteredOrders.value.filter(o => (o.kitchenStatus || '').toLowerCase() === 'preparing'))
const readyOrders = computed(() => filteredOrders.value.filter(o => (o.kitchenStatus || '').toLowerCase() === 'ready'))

function menuItemName(id: number) { return orderStore.menuItemById(id)?.name || t('order.productFallback') }

function timeAgo(dateString: string) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return ''
    return formatDistanceToNow(date, { addSuffix: true, locale: locale.value === 'tr' ? tr : enUS })
  } catch { return '' }
}

async function updateStatus(id: string | number, status: string) {
  try { await orderStore.updateOrderStatus(id, status, 'KITCHEN') }
  catch (e: any) { uiStore.error(e?.message || 'Hata oluştu') }
}

useHead({ title: () => `${t('admin.kitchen.title')} | Admin` })
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar { display: none; }
.scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }

.scrollbar-thin::-webkit-scrollbar { width: 4px; }
.scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
.scrollbar-thin::-webkit-scrollbar-thumb { background-color: #e2e8f0; border-radius: 20px; }

@keyframes pulse-subtle {
  0%, 100% { transform: scale(1); box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1); }
  50% { transform: scale(1.01); box-shadow: 0 20px 40px -10px rgba(249, 115, 22, 0.2); border-color: rgba(249, 115, 22, 0.3); }
}
.animate-pulse-subtle { animation: pulse-subtle 3s infinite ease-in-out; }

.list-enter-active, .list-leave-active { transition: all 0.5s ease; }
.list-enter-from { opacity: 0; transform: translateX(-30px); }
.list-leave-to { opacity: 0; transform: translateX(30px); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
