<template>
  <div class="p-4 md:p-8 max-w-[1800px] mx-auto h-[calc(100vh-64px)] sm:h-screen overflow-hidden flex flex-col space-y-8">
    <!-- Operations Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 shrink-0 bg-white/80 backdrop-blur-2xl p-8 rounded-[3rem] border border-white shadow-2xl shadow-slate-200/40">
      <div class="flex items-center gap-6">
        <div class="w-16 h-16 rounded-[1.5rem] bg-slate-900 text-white flex items-center justify-center shadow-2xl shadow-slate-900/20">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>
        </div>
        <div>
          <h1 class="text-3xl font-black text-slate-900 tracking-tight leading-none mb-2 uppercase">{{ $t('admin.kitchen.title') }}</h1>
          <div class="flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            <p class="text-slate-500 font-bold text-xs uppercase tracking-widest">{{ $t('admin.kitchen.subtitle') }}</p>
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

    <!-- Kanban Board -->
    <div class="flex-grow overflow-x-auto pb-10 flex gap-8 items-start scrollbar-hide">
      
      <!-- COLUMN: NEW -->
      <div class="w-full min-w-[360px] max-w-[480px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-6 px-6 bg-slate-900 py-5 rounded-[2rem] shadow-xl shadow-slate-900/10">
          <h2 class="font-black text-white flex items-center gap-3 tracking-[0.1em] text-[10px] uppercase">
            <span class="w-2 h-2 rounded-full bg-amber-400"></span>
            {{ $t('admin.products.newOrderLabel') }}
          </h2>
          <span class="bg-white/10 text-white text-[10px] font-black px-3 py-1.5 rounded-xl backdrop-blur-md">{{ newOrders.length }}</span>
        </div>
        
        <div class="flex-grow overflow-y-auto space-y-6 pr-2 scrollbar-thin">
          <TransitionGroup name="list">
            <div 
              v-for="order in newOrders" 
              :key="order.id"
              class="bg-white p-8 rounded-[3rem] shadow-[0_20px_50px_rgba(0,0,0,0.04)] border border-slate-100 hover:border-slate-300 transition-all relative group/card overflow-hidden"
            >
              <div class="absolute top-0 left-0 w-2 h-full bg-amber-400"></div>
              
              <div class="space-y-6">
                <div class="flex justify-between items-start gap-4">
                  <div class="flex-1 min-w-0">
                    <span class="text-[9px] font-black text-slate-400 uppercase tracking-[0.2em] block mb-2">{{ $t('admin.products.orderNo') }}</span>
                    <span class="text-3xl font-black text-slate-900 tracking-tighter leading-none block">#{{ orderCodeFromId(order.id) }}</span>
                  </div>
                  <div class="shrink-0 text-right">
                    <span class="text-[9px] font-black text-slate-400 uppercase tracking-[0.2em] block mb-2">{{ $t('admin.products.tableLabel') }}</span>
                    <div class="inline-flex px-5 py-2.5 bg-slate-900 text-white text-base font-black rounded-2xl tracking-tighter shadow-2xl shadow-slate-900/20">
                      {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
                    </div>
                  </div>
                </div>

                <div class="bg-slate-50 px-4 py-2 rounded-xl border border-slate-100 flex items-center gap-2">
                   <svg class="w-3 h-3 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                   <span class="text-[10px] font-bold text-slate-500 uppercase italic leading-none">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
                </div>
                
                <div class="space-y-4 bg-slate-50/50 p-6 rounded-[2.5rem] border border-slate-100/50 shadow-inner">
                  <div v-for="(it, i) in filterItems(order.items || order.lines)" :key="i" class="flex items-start gap-4">
                    <div class="bg-slate-900 text-white w-9 h-9 rounded-xl flex items-center justify-center font-black text-sm shrink-0 shadow-lg">
                      {{ it.qty || it.quantity }}x
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="font-black text-slate-900 text-lg leading-tight uppercase tracking-tight truncate">{{ it.name || menuItemName(it.itemId) }}</p>
                      <p v-if="it.note" class="text-[11px] font-bold text-amber-600 mt-2 bg-amber-50 px-3 py-1 rounded-lg inline-block border border-amber-100">
                        {{ it.note }}
                      </p>
                    </div>
                  </div>
                </div>

                <button @click="updateStatus(order.id, 'preparing')" class="w-full py-5 bg-slate-900 text-white font-black text-xs uppercase tracking-[0.2em] rounded-[1.5rem] hover:bg-slate-800 active:scale-95 transition-all shadow-2xl shadow-slate-900/10">
                  {{ $t('admin.orders.accept') }}
                </button>
              </div>
            </div>
          </TransitionGroup>

          <div v-if="newOrders.length === 0" class="py-24 text-center space-y-6 bg-slate-50 rounded-[3rem] border border-dashed border-slate-200/60">
             <div class="w-20 h-20 bg-white rounded-[1.5rem] flex items-center justify-center mx-auto text-slate-200 shadow-inner border border-slate-100">
                <svg class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
             </div>
             <p class="text-slate-400 font-black uppercase tracking-[0.2em] text-[10px]">Beklemede</p>
          </div>
        </div>
      </div>

      <!-- COLUMN: PREPARING -->
      <div class="w-full min-w-[360px] max-w-[480px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-6 px-6 bg-white py-5 rounded-[2rem] border border-slate-100 shadow-sm">
          <h2 class="font-black text-slate-900 flex items-center gap-3 tracking-[0.1em] text-[10px] uppercase">
            <span class="w-2 h-2 rounded-full bg-slate-900 animate-pulse"></span>
            {{ $t('admin.products.preparingLabel') }}
          </h2>
          <span class="bg-slate-100 text-slate-900 text-[10px] font-black px-3 py-1.5 rounded-xl">{{ preparingOrders.length }}</span>
        </div>

        <div class="flex-grow overflow-y-auto space-y-6 pr-2 scrollbar-thin">
          <TransitionGroup name="list">
            <div 
              v-for="order in preparingOrders" 
              :key="order.id"
              class="bg-white p-8 rounded-[3rem] shadow-[0_20px_50px_rgba(0,0,0,0.04)] border border-slate-100 hover:border-slate-300 transition-all relative group/card overflow-hidden"
            >
              <div class="absolute top-0 left-0 w-2 h-full bg-slate-900 opacity-20"></div>
              
              <div class="space-y-6">
                <div class="flex justify-between items-start gap-4">
                  <div class="flex-1 min-w-0">
                    <span class="text-[9px] font-black text-slate-400 uppercase tracking-[0.2em] block mb-2">{{ $t('admin.products.preparingLabel') }}</span>
                    <span class="text-3xl font-black text-slate-900 tracking-tighter leading-none block">#{{ orderCodeFromId(order.id) }}</span>
                  </div>
                  <div class="shrink-0 text-right">
                    <span class="text-[9px] font-black text-slate-400 uppercase tracking-[0.2em] block mb-2">{{ $t('admin.products.tableLabel') }}</span>
                    <div class="inline-flex px-5 py-2.5 bg-slate-900 text-white text-base font-black rounded-2xl tracking-tighter shadow-2xl">
                      {{ order.tableCode || (typeof order.table === 'object' ? order.table?.code : order.table) || '-' }}
                    </div>
                  </div>
                </div>

                <div class="bg-slate-50 px-4 py-2 rounded-xl border border-slate-100 flex items-center gap-2">
                   <svg class="w-3 h-3 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                   <span class="text-[10px] font-bold text-slate-500 uppercase italic leading-none">{{ timeAgo(order.createdAt || order.createdTime) }}</span>
                </div>
                
                <div class="space-y-4 mb-8 bg-slate-50/50 p-6 rounded-[2.5rem] border border-slate-100/50 shadow-inner">
                  <div v-for="(it, i) in filterItems(order.items || order.lines)" :key="i" class="flex items-start gap-4">
                    <div class="bg-slate-200 text-slate-600 w-9 h-9 rounded-xl flex items-center justify-center font-black text-sm shrink-0">
                      {{ it.qty || it.quantity }}x
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="font-black text-slate-900 text-lg leading-tight uppercase tracking-tight truncate">{{ it.name || menuItemName(it.itemId) }}</p>
                      <p v-if="it.note" class="text-[11px] font-bold text-amber-600 mt-2 bg-amber-50 px-3 py-1 rounded-lg inline-block border border-amber-100">
                        {{ it.note }}
                      </p>
                    </div>
                  </div>
                </div>

                <button @click="updateStatus(order.id, 'ready')" class="w-full py-5 bg-emerald-600 text-white font-black text-xs uppercase tracking-[0.2em] rounded-[1.5rem] hover:bg-emerald-700 active:scale-95 transition-all shadow-2xl shadow-emerald-500/20">
                  {{ $t('admin.orders.readyBtn') }}
                </button>
              </div>
            </div>
          </TransitionGroup>
        </div>
      </div>

      <!-- COLUMN: READY -->
      <div class="w-full min-w-[360px] max-w-[480px] shrink-0 flex flex-col h-full max-h-full">
        <div class="flex items-center justify-between mb-6 px-6 bg-slate-50 py-5 rounded-[2rem] border border-slate-100">
          <h2 class="font-black text-slate-400 flex items-center gap-3 tracking-[0.1em] text-[10px] uppercase">
            <span class="w-2 h-2 rounded-full bg-slate-300"></span>
            {{ $t('admin.products.readyLabel') }}
          </h2>
          <span class="bg-white text-slate-400 text-[10px] font-black px-3 py-1.5 rounded-xl border border-slate-100">{{ readyOrders.length }}</span>
        </div>

        <div class="flex-grow overflow-y-auto space-y-6 pr-2 scrollbar-thin opacity-60 hover:opacity-100 transition-opacity duration-500">
          <TransitionGroup name="list">
            <div 
              v-for="order in readyOrders" 
              :key="order.id"
              class="bg-white p-8 rounded-[3rem] shadow-sm border border-slate-100 relative overflow-hidden"
            >
              <div class="absolute top-0 left-0 w-2 h-full bg-emerald-500/20"></div>
              <div class="flex justify-between items-center">
                 <div>
                    <span class="text-[9px] font-black text-slate-300 uppercase tracking-[0.2em] block mb-1">{{ $t('admin.products.readyLabel') }}</span>
                    <span class="text-2xl font-black text-slate-300 tracking-tighter line-through decoration-slate-200">#{{ orderCodeFromId(order.id) }}</span>
                 </div>
                 <div class="px-5 py-3 bg-slate-50 text-slate-400 text-xs font-black rounded-[1.25rem] border border-slate-100 uppercase tracking-tighter">
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

.list-enter-active, .list-leave-active { transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.list-enter-from { opacity: 0; transform: translateY(30px) scale(0.95); }
.list-leave-to { opacity: 0; transform: translateX(30px); }
</style>
