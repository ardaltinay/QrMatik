<template>
  <div class="min-h-screen bg-slate-50 pt-8 pb-24 font-sans selection:bg-brand-500/10">
    <div class="max-w-3xl mx-auto px-6">
      
      <!-- Header -->
      <div class="flex items-center justify-between mb-10">
        <button 
          @click="router.push('/menu')" 
          class="flex items-center gap-2 text-slate-500 hover:text-brand-600 transition-all bg-white px-5 py-2.5 rounded-2xl border border-slate-100 shadow-sm font-bold text-xs uppercase tracking-widest"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          <span>{{ $t('common.menu') }}</span>
        </button>
        <h1 class="text-xl font-black text-slate-900 uppercase tracking-tight">{{ $t('order.myOrders') || 'Siparişlerim' }}</h1>
      </div>

      <div v-if="loading" class="flex justify-center py-32">
        <div class="relative w-16 h-16">
          <div class="absolute inset-0 border-4 border-slate-100 rounded-full"></div>
          <div class="absolute inset-0 border-4 border-brand-600 rounded-full border-t-transparent animate-spin"></div>
        </div>
      </div>

      <div v-else-if="orders.length === 0" class="text-center py-24 bg-white rounded-[3rem] border border-slate-100 shadow-2xl shadow-slate-200/50">
        <div class="w-24 h-24 bg-slate-50 rounded-[2rem] flex items-center justify-center mx-auto mb-8 text-slate-200 shadow-inner">
          <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
          </svg>
        </div>
        <h2 class="text-2xl font-black text-slate-900 mb-3 uppercase tracking-tight">{{ $t('order.noOrders') || 'Henüz Siparişiniz Yok' }}</h2>
        <p class="text-slate-400 font-medium mb-10 max-w-xs mx-auto">{{ $t('order.noOrdersDesc') || 'Menüye göz atıp ilk siparişinizi verebilirsiniz.' }}</p>
        <NuxtLink to="/menu" class="inline-flex bg-brand-600 text-white font-black text-xs uppercase tracking-[0.2em] px-10 py-4 rounded-2xl hover:bg-brand-500 transition-all shadow-xl shadow-brand-600/20 active:scale-95">
          {{ $t('common.menu') }}
        </NuxtLink>
      </div>

      <div v-else class="space-y-6">
        <div 
          v-for="order in orders" 
          :key="order.id" 
          @click="router.push(`/order/${order.id}`)"
          class="bg-white rounded-[2rem] p-6 border border-slate-100 shadow-xl shadow-slate-200/40 hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 cursor-pointer group relative overflow-hidden"
        >
          <div class="absolute top-0 left-0 w-1.5 h-full transition-colors" :class="getStatusColor(order.status)"></div>
          
          <div class="flex justify-between items-start mb-4">
            <div>
              <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest block mb-1">#{{ orderCodeFromId(order.id) }}</span>
              <h3 class="text-lg font-black text-slate-900 tracking-tight">{{ formatDateTz(order.createdAt || order.createdTime) }}</h3>
            </div>
            <div class="px-3 py-1 rounded-lg text-[10px] font-black uppercase tracking-widest border" :class="getStatusBadgeClass(order.status)">
              {{ statusLabel(order.status) }}
            </div>
          </div>

          <div class="flex items-center justify-between pt-4 border-t border-slate-50">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center text-slate-400">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-sm font-bold text-slate-600">{{ order.lines?.length || order.items?.length || 0 }} {{ $t('menu.quantity') }}</span>
            </div>
            <div class="text-xl font-black text-slate-900 tracking-tight">
              {{ formatMoney(order.total) }}
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'

definePageMeta({
  layout: 'customer'
})

const { t } = useI18n()
const router = useRouter()
const orderStore = useOrderStore()
const { formatMoney, formatDateTz, orderCodeFromId, statusLabel } = useFormat()

const orders = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  const sid = localStorage.getItem('qm_order_session')
  if (sid) {
    orders.value = await orderStore.loadSessionOrders(sid)
  }
  loading.value = false
})

function getStatusColor(status: string) {
  const s = String(status || '').toLowerCase()
  switch (s) {
    case 'new': return 'bg-amber-500'
    case 'preparing': return 'bg-indigo-500'
    case 'ready': return 'bg-emerald-500'
    case 'served': return 'bg-blue-500'
    case 'payment_completed': return 'bg-slate-800'
    case 'canceled': return 'bg-rose-500'
    default: return 'bg-slate-300'
  }
}

function getStatusBadgeClass(status: string) {
  const s = String(status || '').toLowerCase()
  switch (s) {
    case 'new': return 'bg-amber-50 text-amber-600 border-amber-100'
    case 'preparing': return 'bg-indigo-50 text-indigo-600 border-indigo-100'
    case 'ready': return 'bg-emerald-50 text-emerald-600 border-emerald-100'
    case 'served': return 'bg-blue-50 text-blue-600 border-blue-100'
    case 'payment_completed': return 'bg-slate-50 text-slate-600 border-slate-200'
    case 'canceled': return 'bg-rose-50 text-rose-600 border-rose-100'
    default: return 'bg-slate-50 text-slate-400 border-slate-100'
  }
}

useHead({
  title: () => `${t('order.myOrders') || 'Siparişlerim'} | feasymenu`,
  meta: [
    { name: 'robots', content: 'noindex, nofollow' }
  ]
})
</script>
