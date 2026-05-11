<template>
  <div class="min-h-screen bg-slate-50 pt-8 pb-24 font-sans selection:bg-brand-500/10">
    <div class="max-w-3xl mx-auto px-6">
      
      <!-- Top header with back button -->
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
        
        <NuxtLink 
          to="/order/history" 
          class="flex items-center gap-2 text-brand-600 hover:bg-brand-50 transition-all px-5 py-2.5 rounded-2xl border border-brand-100 bg-brand-50/30 font-bold text-xs uppercase tracking-widest"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <span>{{ $t('order.myOrders') || 'Tüm Siparişlerim' }}</span>
        </NuxtLink>
      </div>

      <div v-if="order" class="space-y-8">
        
        <!-- Info Card -->
        <div class="bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-2xl shadow-slate-200/50 relative overflow-hidden">
          <div class="absolute top-0 right-0 w-48 h-48 bg-brand-500/5 blur-[100px] rounded-full translate-x-1/2 -translate-y-1/2"></div>
          
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-6 relative z-10">
            <div>
              <div class="flex flex-wrap items-end gap-4">
                <span class="text-4xl font-black text-slate-900 tracking-tighter">#{{ orderCodeFromId(order.id) }}</span>
                <div class="mb-2 px-3 py-1 rounded-lg text-[10px] font-black uppercase tracking-widest border" :class="getStatusBadgeClass(order.status)">
                  {{ statusLabel(order.status) }}
                </div>
                <span class="text-xs text-slate-400 font-bold uppercase tracking-wider mb-2">{{ formatDateTz(order.createdAt || order.createdTime) }}</span>
              </div>
            </div>
            
            <div class="bg-slate-50 px-6 py-3 rounded-2xl border border-slate-100">
              <p class="text-[10px] text-slate-400 font-bold uppercase tracking-widest mb-1">{{ $t('order.table') }}</p>
              <p class="font-black text-brand-600 text-lg tracking-tight">{{ (typeof order.table === 'object' ? order.table?.code : order.table) || order.tableCode || $t('common.guest') }}</p>
            </div>
          </div>

          <!-- Stepper -->
          <div class="mt-10 pt-10 border-t border-slate-50">
            <div class="relative">
              <div class="absolute top-1/2 left-0 w-full h-1.5 bg-slate-100 -translate-y-1/2 rounded-full hidden sm:block"></div>
              <div class="absolute top-1/2 left-0 h-1.5 bg-brand-600 -translate-y-1/2 rounded-full hidden sm:block transition-all duration-700" :style="{ width: progressWidth }"></div>
              
              <ul class="relative flex flex-col sm:flex-row sm:justify-between gap-8 sm:gap-0">
                <li v-for="(s, idx) in steps" :key="s.value" class="flex items-center gap-5 sm:flex-col sm:gap-4 group">
                  <div 
                    class="w-12 h-12 rounded-2xl flex items-center justify-center shrink-0 border-4 transition-all duration-500 relative z-10"
                    :class="[
                      idx < currentIdx ? 'bg-brand-600 border-brand-50 text-white shadow-xl shadow-brand-600/20' : 
                      idx === currentIdx ? 'bg-brand-600 border-brand-50 text-white shadow-xl shadow-brand-600/20 ring-8 ring-brand-500/5' : 
                      'bg-white border-slate-100 text-slate-300'
                    ]"
                  >
                    <svg v-if="idx < currentIdx" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                    <span v-else class="font-black text-sm">{{ idx + 1 }}</span>
                  </div>
                  <div class="sm:text-center">
                    <p class="text-xs font-black uppercase tracking-widest" :class="idx <= currentIdx ? 'text-slate-900' : 'text-slate-300'">{{ s.label }}</p>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Alerts & Actions -->
        <TransitionGroup name="fade">
          <div v-if="canCancel" key="cancel-action" class="bg-amber-50 border border-amber-100 rounded-2xl p-5 flex flex-col sm:flex-row items-center justify-between gap-4 mb-6 shadow-lg shadow-amber-500/5">
            <div class="flex gap-4">
              <div class="w-10 h-10 rounded-xl bg-amber-500 flex items-center justify-center text-white shrink-0 shadow-md">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <p class="text-sm text-amber-900 font-bold leading-tight">{{ $t('order.cancelTimeTitle') || 'Vazgeçme Hakkı' }}</p>
                <p class="text-xs text-amber-600 font-medium">{{ $t('order.cancelTimeDesc') || 'Siparişi iptal etmek için son' }} {{ cancelTimeLeft }}</p>
              </div>
            </div>
            <button 
              @click="cancelOrder"
              :disabled="isCanceling"
              class="w-full sm:w-auto px-6 py-2.5 bg-white border border-amber-200 text-amber-600 font-black text-[10px] uppercase tracking-widest rounded-xl hover:bg-amber-500 hover:text-white hover:border-amber-500 transition-all active:scale-95 shadow-sm"
            >
              {{ isCanceling ? '...' : $t('order.cancelBtn') || 'İPTAL ET' }}
            </button>
          </div>

          <div v-if="isExpired" key="expired" class="bg-rose-50 border border-rose-100 rounded-2xl p-5 flex gap-4">
            <div class="w-10 h-10 rounded-xl bg-rose-500 flex items-center justify-center text-white shrink-0 shadow-lg shadow-rose-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <p class="text-sm text-rose-600 font-bold leading-relaxed">{{ $t('order.expiredAlert') }}</p>
          </div>

          <div v-else-if="order.status?.toLowerCase() === 'served'" key="served" class="bg-emerald-50 border border-emerald-100 rounded-[2rem] p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 shadow-xl shadow-emerald-500/5">
            <div class="flex gap-4">
              <div class="w-12 h-12 rounded-2xl bg-emerald-500 flex items-center justify-center text-white shrink-0 shadow-lg shadow-emerald-500/20">
                <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <div>
                <h4 class="text-emerald-900 font-black uppercase tracking-tight">{{ $t('order.servedAlert.title') }}</h4>
                <p class="text-sm text-emerald-600/80 font-medium">{{ $t('order.servedAlert.desc') }}</p>
              </div>
            </div>
            <button @click="requestBill" :disabled="isRequestingBill" class="shrink-0 bg-emerald-600 hover:bg-emerald-500 text-white font-black text-xs uppercase tracking-[0.2em] py-3.5 px-8 rounded-2xl transition-all shadow-xl shadow-emerald-600/20 disabled:opacity-70 flex items-center gap-3 active:scale-95">
              <span v-if="!isRequestingBill">{{ $t('order.requestBill') }}</span>
              <svg v-else class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </button>
          </div>
          
          <div v-else-if="order.status?.toLowerCase() === 'bill_requested'" key="bill_requested" class="bg-brand-50 border border-brand-100 rounded-2xl p-5 flex gap-4">
            <div class="w-10 h-10 rounded-xl bg-brand-600 flex items-center justify-center text-white shrink-0 shadow-lg shadow-brand-600/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <p class="text-sm text-brand-700 font-bold leading-relaxed">{{ $t('order.billRequested') }}</p>
          </div>

          <div v-else-if="order.status?.toLowerCase() === 'canceled'" key="canceled" class="bg-rose-50 border border-rose-100 rounded-2xl p-5 flex gap-4">
            <div class="w-10 h-10 rounded-xl bg-rose-500 flex items-center justify-center text-white shrink-0 shadow-lg shadow-rose-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
            <p class="text-sm text-rose-600 font-black leading-relaxed">{{ $t('order.canceledAlert') }}</p>
          </div>

          <div v-else-if="order.status?.toLowerCase() === 'expired'" key="status-expired" class="bg-slate-100 border border-slate-200 rounded-2xl p-5 flex gap-4">
            <div class="w-10 h-10 rounded-xl bg-slate-500 flex items-center justify-center text-white shrink-0 shadow-lg shadow-slate-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <p class="text-sm text-slate-600 font-black leading-relaxed">{{ $t('order.expiredAlert') }}</p>
          </div>
        </TransitionGroup>

        <!-- Gamification Banner (Spin the Wheel / Loyalty) -->
        <div v-if="order?.status && ['payment_completed', 'served', 'bill_requested'].includes(order.status.toLowerCase()) && !hasSpunWheel && isLoyaltyActive" 
             class="bg-gradient-to-r from-brand-400 to-brand-600 rounded-[2.5rem] p-8 sm:p-10 text-white shadow-2xl shadow-brand-500/30 relative overflow-hidden group cursor-pointer mb-10"
             @click="openLoyaltyModal">
          <!-- Decorative Background -->
          <div class="absolute -right-10 -top-10 w-40 h-40 bg-white/10 blur-2xl rounded-full group-hover:scale-150 transition-transform duration-700"></div>
          
          <div class="relative z-10 flex flex-col sm:flex-row items-center gap-6 text-center sm:text-left">
            <div class="w-16 h-16 rounded-full bg-white/20 flex items-center justify-center shrink-0 border border-white/30 backdrop-blur-md">
              <svg class="w-8 h-8 text-white animate-spin-slow" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="flex-grow">
              <h3 class="text-xl font-black mb-1 tracking-tight text-white">{{ $t('loyalty.wheel.title') }}</h3>
              <p class="text-white/80 font-medium text-sm">{{ $t('loyalty.wheel.desc') }}</p>
            </div>
            <button class="shrink-0 bg-white text-brand-600 font-black text-xs uppercase tracking-widest px-6 py-3 rounded-xl shadow-lg hover:scale-105 transition-all">
              {{ $t('loyalty.wheel.spinBtn') }}
            </button>
          </div>
        </div>

        <!-- Order Items -->
        <div class="bg-white rounded-[2.5rem] p-8 sm:p-10 border border-slate-100 shadow-xl shadow-slate-200/50">
          <h3 class="text-xl font-black text-slate-900 mb-8 tracking-tight">{{ $t('order.contents') }}</h3>
          
          <div class="space-y-5 mb-10">
            <div v-for="(it, idx) in order.items" :key="idx" class="flex items-center justify-between p-5 bg-slate-50/50 rounded-3xl border border-slate-50 hover:bg-white hover:shadow-lg hover:shadow-slate-200/50 transition-all duration-500">
              <div class="flex items-center gap-5">
                <div class="w-12 h-12 rounded-2xl bg-white border border-slate-100 flex items-center justify-center font-black text-brand-600 shrink-0 shadow-sm">
                  x{{ it.qty || it.quantity }}
                </div>
                <div>
                  <h4 class="text-slate-900 font-bold tracking-tight text-lg">{{ it.name || menuItemName(it.itemId) }}</h4>
                  <div v-if="it.note" class="text-[10px] text-slate-500 mt-2 inline-flex items-center gap-2 bg-white px-2 py-1 rounded-lg border border-slate-100 font-bold uppercase tracking-wider">
                    <svg class="w-3 h-3 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                    {{ it.note }}
                  </div>
                </div>
              </div>
              <div class="text-slate-900 font-black text-lg shrink-0 pl-6">
                {{ formatMoney((it.price || 0) * (it.qty || it.quantity)) }}
              </div>
            </div>
          </div>

          <div class="flex items-center justify-between pt-8 border-t border-slate-100">
            <span class="text-[10px] text-slate-400 font-black uppercase tracking-[0.2em]">{{ $t('menu.total') }}</span>
            <span class="text-4xl font-black text-slate-900 tracking-tighter">{{ formatMoney(order.total) }}</span>
          </div>
        </div>

        <!-- Share & QR -->
        <div class="bg-white rounded-[2.5rem] p-8 sm:p-10 border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col sm:flex-row gap-10 items-center">
          <div class="w-44 h-44 shrink-0 bg-slate-50 p-4 rounded-3xl border border-slate-100 shadow-inner">
            <img :src="qrUrl" alt="QR Code" class="w-full h-full object-contain" />
          </div>
          
          <div class="flex-grow w-full text-center sm:text-left">
            <h4 class="text-slate-900 font-black mb-3 uppercase tracking-tight text-lg">{{ $t('order.liveTracking') }}</h4>
            <p class="text-sm text-slate-400 font-mono mb-6 break-all bg-slate-50 p-4 rounded-2xl border border-slate-100 select-all leading-relaxed">{{ shareLink }}</p>
            
            <div class="flex flex-wrap gap-4 justify-center sm:justify-start">
              <button @click="copyLink" class="bg-white hover:bg-slate-50 text-slate-700 font-black text-[10px] uppercase tracking-[0.2em] py-4 px-8 rounded-2xl transition-all border border-slate-100 flex items-center gap-3 shadow-sm active:scale-95">
                <svg class="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                </svg>
                <span>{{ copied ? $t('order.copied') : $t('order.copy') }}</span>
              </button>
            </div>
          </div>
        </div>
        
      </div>

      <!-- Loading / Not Found -->
      <div v-else-if="isLoading" class="flex justify-center py-32">
        <div class="relative w-16 h-16">
          <div class="absolute inset-0 border-4 border-slate-100 rounded-full"></div>
          <div class="absolute inset-0 border-4 border-brand-600 rounded-full border-t-transparent animate-spin"></div>
        </div>
      </div>
      <div v-else class="text-center py-24 bg-white rounded-[3rem] border border-slate-100 shadow-2xl shadow-slate-200/50">
        <div class="w-24 h-24 bg-slate-50 rounded-[2rem] flex items-center justify-center mx-auto mb-8 text-slate-200 shadow-inner">
          <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
        <h2 class="text-2xl font-black text-slate-900 mb-3 uppercase tracking-tight">{{ $t('order.notFound.title') }}</h2>
        <p class="text-slate-400 font-medium mb-10 max-w-xs mx-auto">{{ $t('order.notFound.desc') }}</p>
        <NuxtLink to="/menu" class="inline-flex bg-brand-600 text-white font-black text-xs uppercase tracking-[0.2em] px-10 py-4 rounded-2xl hover:bg-brand-500 transition-all shadow-xl shadow-brand-600/20 active:scale-95">
          {{ $t('order.notFound.back') }}
        </NuxtLink>
      </div>

    </div>

    <!-- Loyalty Modal -->
    <LoyaltyWheelModal 
      v-if="isLoyaltyModalOpen" 
      :is-open="isLoyaltyModalOpen" 
      :order-id="id"
      @close="isLoyaltyModalOpen = false" 
      @spun="onWheelSpun"
    />

  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'

definePageMeta({
  layout: 'customer'
})

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const orderStore = useOrderStore()
const { formatMoney, formatDateTz, orderCodeFromId, statusLabel } = useFormat()
const { fetchJson } = useApi()
const { connect, subscribe, disconnect } = useSocket()
const uiStore = useUiStore()

const id = String(route.params.id)
const loadedOrder = ref<any>(null)
const isLoading = ref(true)
const isRequestingBill = ref(false)
const isCanceling = ref(false)
const now = ref(Date.now())

// Loyalty State
const isLoyaltyModalOpen = ref(false)
const hasSpunWheel = ref(false)
const isLoyaltyActive = ref(false)

let timer: any = null

const orderFromStore = computed(() => orderStore.orders.find(o => String(o.id) === id))
const order = computed(() => orderFromStore.value || loadedOrder.value)

const canCancel = computed(() => {
  if (!order.value || order.value.status?.toLowerCase() !== 'new') return false
  
  const createdAt = order.value.createdTime || order.value.createdAt
  if (!createdAt) return false
  
  const createdDate = new Date(createdAt).getTime()
  const diffMinutes = (now.value - createdDate) / 1000 / 60
  
  return diffMinutes < 2
})

const cancelTimeLeft = computed(() => {
  if (!order.value) return ''
  const createdAt = new Date(order.value.createdTime || order.value.createdAt).getTime()
  const secondsLeft = Math.max(0, 120 - Math.floor((now.value - createdAt) / 1000))
  const m = Math.floor(secondsLeft / 60)
  const s = secondsLeft % 60
  return `${m}:${s.toString().padStart(2, '0')}`
})

async function cancelOrder() {
  if (!order.value || !canCancel.value || isCanceling.value) return

  uiStore.confirm({
    title: t('order.cancelBtn') || 'İptal Et',
    message: t('order.cancelConfirm') || 'Siparişi iptal etmek istediğinize emin misiniz?',
    isDanger: true,
    onConfirm: async () => {
      isCanceling.value = true
      try {
        const tableCode = localStorage.getItem('qm_table_code')
        const sessionKey = tableCode ? `qm_session_${tableCode}` : 'qm_order_session'
        const sid = localStorage.getItem(sessionKey) || localStorage.getItem('qm_order_session') || ''

        await fetchJson(`/api/orders/${encodeURIComponent(id)}/cancel`, {
          method: 'POST',
          body: JSON.stringify({ sessionId: sid })
        })
        
        // Refresh data
        await loadOrderData()
        uiStore.success(t('order.canceledSuccess') || 'Siparişiniz iptal edildi.')
      } catch (e) {
        uiStore.error(t('order.cancelError') || 'İptal edilemedi. Mutfak hazırlığa başlamış olabilir.')
      } finally {
        isCanceling.value = false
      }
    }
  })
}

// Stepper Logic
const steps = computed(() => [
  { value: 'new', label: t('order.statuses.new') },
  { value: 'preparing', label: t('order.statuses.preparing') },
  { value: 'ready', label: t('order.statuses.ready') },
  { value: 'served', label: t('order.statuses.served') },
])

const currentIdx = computed(() => {
  if (!order.value) return 0
  const st = String(order.value.status).toLowerCase()
  const idx = steps.value.findIndex(s => s.value === st)
  if (idx >= 0) return idx
  if (st === 'payment_completed' || st === 'bill_requested') return steps.value.length - 1
  if (st === 'canceled' || st === 'expired') return -1 // -1 means none of the normal steps are active
  return 0
})

const progressWidth = computed(() => {
  if (currentIdx.value < 0) return '0%'
  const percent = (currentIdx.value / (steps.value.length - 1)) * 100
  return `${percent}%`
})

const isExpired = computed(() => {
  if (order.value?.status?.toLowerCase() === 'expired') return true
  if (!import.meta.client) return false
  try {
    const exp = localStorage.getItem('qm_order_session_expires')
    if (!exp) return false
    const ts = new Date(exp).getTime()
    return !isNaN(ts) && Date.now() > ts
  } catch {
    return false
  }
})

function getStatusBadgeClass(status: string) {
  const s = String(status || '').toLowerCase()
  switch (s) {
    case 'new': return 'bg-amber-50 text-amber-600 border-amber-100'
    case 'preparing': return 'bg-indigo-50 text-indigo-600 border-indigo-100'
    case 'ready': return 'bg-emerald-50 text-emerald-600 border-emerald-100'
    case 'served': return 'bg-blue-50 text-blue-600 border-blue-100'
    case 'payment_completed': return 'bg-slate-50 text-slate-600 border-slate-200'
    case 'canceled': return 'bg-rose-50 text-rose-600 border-rose-100'
    case 'expired': return 'bg-slate-50 text-slate-400 border-slate-100'
    default: return 'bg-slate-50 text-slate-400 border-slate-100'
  }
}

// Share Logic
const shareLink = ref('')
const qrUrl = ref('')
const copied = ref(false)

onMounted(() => {
  let sid = localStorage.getItem('qm_order_session') || ''
  const querySid = route.query.sid
  if (querySid) sid = String(querySid)

  const url = new URL(window.location.href)
  url.search = ''
  if (sid) url.searchParams.set('sid', sid)
  
  shareLink.value = url.toString()
  qrUrl.value = `/api/qr/image?text=${encodeURIComponent(shareLink.value)}&size=300`
})

function copyLink() {
  if (!import.meta.client) return
  navigator.clipboard.writeText(shareLink.value).then(() => {
    copied.value = true
    setTimeout(() => copied.value = false, 2500)
  })
}

// Load Order data
async function loadOrderData() {
  isLoading.value = true
  try {
    const tableCode = localStorage.getItem('qm_table_code')
    const sessionKey = tableCode ? `qm_session_${tableCode}` : 'qm_order_session'
    const sid = localStorage.getItem(sessionKey) || localStorage.getItem('qm_order_session') || ''
    
    const currentTable = localStorage.getItem('qm_table_code') || ''
    
    // We send sessionId via header for security (prevents IDOR)
    const data = await fetchJson(`/api/orders/${encodeURIComponent(id)}`, {
      headers: {
        'X-Session-Id': sid
      }
    })

    if (data) {
      // Security: Even if backend returned data, ensure it belongs to the current table session
      // If the customer scanned A2 but is trying to view A1's order, block it.
      if (currentTable && data.tableCode && data.tableCode !== currentTable) {
        console.error('Security Breach: Table mismatch detected!')
        router.push('/menu')
        return
      }

      loadedOrder.value = {
        ...data,
        items: Array.isArray(data.lines) ? data.lines : [],
      }
    }
  } catch (e: any) {
    if (e?.status === 403 || e?.status === 404) {
      router.push('/menu')
    }
  } finally {
    isLoading.value = false
  }
}

// Bill Request
async function requestBill() {
  if (!order.value || isRequestingBill.value) return
  isRequestingBill.value = true
  try {
    const tableCode = localStorage.getItem('qm_table_code')
    const sessionKey = tableCode ? `qm_session_${tableCode}` : 'qm_order_session'
    const sid = localStorage.getItem(sessionKey) || localStorage.getItem('qm_order_session') || ''

    await fetchJson(`/api/orders/${encodeURIComponent(order.value.id)}/request-bill`, {
      method: 'POST',
      body: JSON.stringify({ sessionId: sid })
    })
    
    if (loadedOrder.value) {
      loadedOrder.value.status = 'bill_requested'
    }
    // Also update in pinia if there
    const idx = orderStore.orders.findIndex(o => String(o.id) === id)
    if (idx >= 0) {
      orderStore.orders[idx].status = 'bill_requested'
    }
  } catch (e) {
    // Error is handled implicitly or silently. A toast will be nice, but we rely on api.
  } finally {
    isRequestingBill.value = false
  }
}

function menuItemName(itemId: number) {
  const item = orderStore.menuItemById(itemId)
  return item ? item.name : t('common.product')
}

function openLoyaltyModal() {
  isLoyaltyModalOpen.value = true
}

function onWheelSpun() {
  hasSpunWheel.value = true
  if (import.meta.client) {
    localStorage.setItem(`qm_wheel_spun_${id}`, 'true')
  }
}

let unsub: (() => void) | null = null

onMounted(() => {
  timer = setInterval(() => {
    now.value = Date.now()
  }, 1000)

  if (!orderFromStore.value) {
    loadOrderData()
  } else {
    isLoading.value = false
  }

  // Load hasSpun status from localStorage
  if (import.meta.client) {
    hasSpunWheel.value = localStorage.getItem(`qm_wheel_spun_${id}`) === 'true'
  }
  
  // Check if loyalty is active
  try {
    fetchJson('/api/loyalty/campaign').then(campaign => {
      isLoyaltyActive.value = campaign?.active || false
    })
  } catch {
    isLoyaltyActive.value = false
  }
  
  if (orderStore.menu.length === 0) {
    orderStore.loadMenu()
  }

  // WebSocket Connection
  connect(() => {
    // We need the session ID to subscribe
    const tableCode = localStorage.getItem('qm_table_code')
    const sessionKey = tableCode ? `qm_session_${tableCode}` : 'qm_order_session'
    const sid = localStorage.getItem(sessionKey) || localStorage.getItem('qm_order_session') || route.query.sid
    
    if (sid) {
      unsub = subscribe(`/topic/session/${sid}`, (updatedOrder: any) => {
        if (String(updatedOrder.id) === id) {
          loadedOrder.value = {
            ...updatedOrder,
            items: Array.isArray(updatedOrder.lines) ? updatedOrder.lines : [],
          }
          // Also update in pinia if there
          const idx = orderStore.orders.findIndex(o => String(o.id) === id)
          if (idx >= 0) {
            orderStore.orders[idx] = loadedOrder.value
          }
        }
      })
    }
  })
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
  if (unsub) unsub()
})

useHead({
  title: () => `${t('order.orderDetail')} #${orderCodeFromId(id)}`,
  meta: [
    { name: 'robots', content: 'noindex, nofollow' }
  ]
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
