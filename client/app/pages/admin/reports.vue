<template>
  <div class="p-4 md:p-8 max-w-7xl mx-auto space-y-10">
    <!-- Header Section -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight flex items-center gap-3">
          {{ $t('admin.reports.title') }}
        </h1>
        <p class="text-slate-500 font-medium mt-1">{{ $t('admin.reports.subtitle') }}</p>
        <span class="bg-emerald-100 text-emerald-600 text-[10px] font-black px-2 py-1 rounded-lg uppercase tracking-wider">{{ $t('admin.reports.analyticsLabel') || 'Analytics' }}</span>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full lg:w-auto">
        <div class="relative w-full sm:w-48 group">
          <select v-model="dateRange" class="w-full pl-5 pr-10 py-3.5 rounded-2xl bg-white border border-slate-200 focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none shadow-sm font-bold text-slate-700 appearance-none cursor-pointer">
            <option value="today">{{ $t('admin.reports.dateRange.today') }}</option>
            <option value="week">{{ $t('admin.reports.dateRange.week') }}</option>
            <option value="month">{{ $t('admin.reports.dateRange.month') }}</option>
          </select>
          <svg class="w-5 h-5 absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none group-hover:text-brand-500 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" /></svg>
        </div>
        
        <button 
          @click="exportReport" 
          :disabled="currentPlan === 'FREE'"
          class="flex-1 lg:flex-none px-8 py-3.5 bg-slate-900 text-white font-black text-xs rounded-2xl hover:bg-slate-800 hover:-translate-y-0.5 active:translate-y-0 transition-all shadow-xl shadow-slate-900/20 flex items-center justify-center gap-3 uppercase tracking-widest disabled:opacity-30 disabled:cursor-not-allowed"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" /></svg>
          {{ $t('admin.reports.export') }}
        </button>
      </div>
    </div>

    <div class="relative">
      <!-- PREMIUM OVERLAY -->
      <div v-if="currentPlan === 'FREE'" class="absolute inset-x-0 top-0 bottom-0 z-30 backdrop-blur-md bg-white/40 rounded-[2.5rem] flex items-center justify-center p-4 sm:p-6 text-center border-2 border-white/50">
        <div class="bg-white p-8 sm:p-12 rounded-[3rem] shadow-[0_32px_64px_-16px_rgba(0,0,0,0.2)] border border-slate-100 w-full max-w-lg animate-in fade-in zoom-in slide-in-from-bottom-8 duration-700 ease-out">
          <div class="w-24 h-24 bg-brand-50 text-brand-600 rounded-[2.5rem] flex items-center justify-center mx-auto mb-8 shadow-inner border border-brand-100 relative">
             <div class="absolute inset-0 rounded-[2.5rem] bg-brand-400 animate-ping opacity-10"></div>
             <svg class="w-12 h-12 relative z-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" /></svg>
          </div>
          <h3 class="text-3xl font-black text-slate-900 mb-4 tracking-tight leading-none">{{ $t('admin.reports.premiumTitle') }}</h3>
          <p class="text-slate-500 font-medium text-lg leading-relaxed mb-10">{{ $t('admin.reports.premiumDesc') }}</p>
          <NuxtLink :to="localePath('/admin/upgrade')" class="w-full inline-flex items-center justify-center px-10 py-5 bg-brand-600 text-white font-black text-sm uppercase tracking-[0.2em] rounded-[1.5rem] hover:bg-brand-700 hover:shadow-2xl hover:shadow-brand-500/30 active:scale-95 transition-all">
            {{ $t('admin.upgrade.button') }}
          </NuxtLink>
        </div>
      </div>

      <!-- MAIN DATA SECTION -->
      <div class="space-y-10" :class="currentPlan === 'FREE' ? 'opacity-30 pointer-events-none grayscale blur-[2px]' : ''">
        
        <!-- Summary Bento Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- Revenue -->
          <div class="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col relative overflow-hidden group hover:border-brand-200 transition-all duration-500">
            <div class="absolute -right-8 -top-8 w-32 h-32 bg-brand-50 rounded-full group-hover:scale-150 transition-transform duration-700 ease-out z-0 opacity-50"></div>
            <div class="relative z-10">
              <div class="flex items-center gap-4 mb-8">
                <div class="w-14 h-14 rounded-2xl bg-brand-100 text-brand-600 flex items-center justify-center shadow-inner border border-brand-200 group-hover:rotate-6 transition-transform">
                  <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                </div>
                <h3 class="font-black text-slate-400 text-[10px] uppercase tracking-[0.2em]">{{ $t('admin.reports.summary.revenue') }}</h3>
              </div>
              <div class="text-4xl font-black text-slate-900 tracking-tighter mb-4">{{ formatPrice(summary.revenue) }}</div>
              <div class="flex items-center gap-2">
                 <div class="px-2.5 py-1 rounded-lg text-xs font-black flex items-center gap-1" :class="summary.revenueGrowth >= 0 ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'">
                    <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="4" :class="summary.revenueGrowth < 0 ? 'rotate-180' : ''"><path stroke-linecap="round" stroke-linejoin="round" d="M5 15l7-7 7 7" /></svg>
                    {{ Math.abs(summary.revenueGrowth) }}%
                 </div>
                 <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">{{ $t('admin.reports.vsPrevious') }}</span>
              </div>
            </div>
          </div>

          <!-- Orders -->
          <div class="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col relative overflow-hidden group hover:border-emerald-200 transition-all duration-500">
            <div class="absolute -right-8 -top-8 w-32 h-32 bg-emerald-50 rounded-full group-hover:scale-150 transition-transform duration-700 ease-out z-0 opacity-50"></div>
            <div class="relative z-10">
              <div class="flex items-center gap-4 mb-8">
                <div class="w-14 h-14 rounded-2xl bg-emerald-100 text-emerald-600 flex items-center justify-center shadow-inner border border-emerald-200 group-hover:rotate-6 transition-transform">
                  <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                </div>
                <h3 class="font-black text-slate-400 text-[10px] uppercase tracking-[0.2em]">{{ $t('admin.reports.summary.orders') }}</h3>
              </div>
              <div class="text-4xl font-black text-slate-900 tracking-tighter mb-4">{{ summary.orders }}</div>
              <div class="flex items-center gap-2">
                 <div class="px-2.5 py-1 rounded-lg text-xs font-black flex items-center gap-1" :class="summary.ordersGrowth >= 0 ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'">
                    <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="4" :class="summary.ordersGrowth < 0 ? 'rotate-180' : ''"><path stroke-linecap="round" stroke-linejoin="round" d="M5 15l7-7 7 7" /></svg>
                    {{ Math.abs(summary.ordersGrowth) }}%
                 </div>
                 <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">{{ $t('admin.reports.vsPrevious') }}</span>
              </div>
            </div>
          </div>

          <!-- Cancelled -->
          <div class="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col relative overflow-hidden group hover:border-rose-200 transition-all duration-500">
            <div class="absolute -right-8 -top-8 w-32 h-32 bg-rose-50 rounded-full group-hover:scale-150 transition-transform duration-700 ease-out z-0 opacity-50"></div>
            <div class="relative z-10">
              <div class="flex items-center gap-4 mb-8">
                <div class="w-14 h-14 rounded-2xl bg-rose-100 text-rose-600 flex items-center justify-center shadow-inner border border-rose-200 group-hover:rotate-6 transition-transform">
                  <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                </div>
                <h3 class="font-black text-slate-400 text-[10px] uppercase tracking-[0.2em]">{{ $t('admin.reports.summary.cancelled') }}</h3>
              </div>
              <div class="text-4xl font-black text-slate-900 tracking-tighter mb-4">{{ summary.cancelled }}</div>
              <div class="flex items-center gap-2">
                 <div class="px-2.5 py-1 rounded-lg text-xs font-black flex items-center gap-1" :class="summary.cancelledGrowth <= 0 ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'">
                    <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="4" :class="summary.cancelledGrowth > 0 ? 'rotate-180' : ''"><path stroke-linecap="round" stroke-linejoin="round" d="M5 15l7-7 7 7" /></svg>
                    {{ Math.abs(summary.cancelledGrowth) }}%
                 </div>
                 <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">{{ $t('admin.reports.vsPrevious') }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Charts Grid -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          
          <!-- Modern Bar Chart Timeline -->
          <div class="bg-white p-10 rounded-[3rem] border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col relative overflow-hidden group">
            <h3 class="text-xl font-black text-slate-800 tracking-tight mb-10">{{ $t('admin.reports.charts.revenueTimeline') }}</h3>
            <div class="flex-1 flex items-end justify-between gap-3 h-64 mt-auto">
              <div v-for="(day, idx) in trendData" :key="idx" class="flex flex-col items-center flex-1 gap-3 group/bar relative">
                <!-- Data Point Tooltip -->
                <div class="opacity-0 group-hover/bar:opacity-100 transition-all duration-300 absolute -top-10 scale-90 group-hover/bar:scale-100 bg-slate-900 text-white text-[10px] font-black py-2 px-3 rounded-xl z-20 shadow-2xl pointer-events-none whitespace-nowrap">
                  {{ formatPrice(day.value) }}
                  <div class="absolute -bottom-1 left-1/2 -translate-x-1/2 w-2 h-2 bg-slate-900 rotate-45"></div>
                </div>
                
                <div class="w-full bg-brand-50 rounded-[1rem] group-hover/bar:bg-brand-500 transition-all duration-500 relative cursor-pointer" :style="{ height: `${Math.max(day.percentage, 5)}%` }">
                   <div class="absolute inset-0 bg-gradient-to-t from-black/5 to-transparent rounded-[1rem]"></div>
                </div>
                <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest">{{ day.label }}</span>
              </div>
            </div>
          </div>

          <!-- Top Products List -->
          <div class="bg-white p-10 rounded-[3rem] border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col">
            <h3 class="text-xl font-black text-slate-800 tracking-tight mb-8">{{ $t('admin.reports.charts.topProducts') }}</h3>
            <div class="space-y-6 overflow-y-auto max-h-80 pr-2 scrollbar-thin">
              <div v-for="(product, idx) in topProducts" :key="idx" class="flex items-center gap-5 group">
                <div class="w-10 h-10 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-center font-black text-slate-400 group-hover:bg-brand-500 group-hover:text-white transition-all shadow-sm">
                  {{ idx + 1 }}
                </div>
                <div class="w-14 h-14 rounded-2xl bg-slate-100 overflow-hidden shrink-0 shadow-sm">
                  <NuxtImg v-if="product.image" :src="product.image" format="webp" class="w-full h-full object-cover" loading="lazy" />
                  <div v-else class="w-full h-full flex items-center justify-center text-slate-300"><svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg></div>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="font-bold text-slate-900 truncate leading-tight">{{ product.name }}</div>
                  <div class="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-1">{{ product.category }}</div>
                </div>
                <div class="text-right">
                  <div class="font-black text-slate-900">{{ product.count }} {{ $t('admin.reports.countUnit') }}</div>
                  <div class="text-xs font-bold text-emerald-600">{{ formatPrice(product.revenue) }}</div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()
const localePath = useLocalePath()

const currentPlan = computed(() => {
  const p = authStore.user?.tenant?.subscriptionPlan || authStore.tenantConfig?.plan || 'FREE'
  return String(p).toUpperCase()
})

useHead({
  title: () => `${t('admin.reports.title')} | Admin`
})

// State
const dateRange = ref('week')
const loadingData = ref(false)
const summary = ref({
  revenue: 0,
  revenueGrowth: 0.0,
  orders: 0,
  ordersGrowth: 0.0,
  cancelled: 0,
  cancelledGrowth: 0.0
})

const trendData = ref<any[]>([])
const topProducts = ref<any[]>([])

// Methods
async function loadReport() {
  try {
    loadingData.value = true
    const data = await fetchJson(`/api/reports?range=${dateRange.value}`)
    if (data) {
      if (data.summary) summary.value = data.summary
      if (data.topProducts) topProducts.value = data.topProducts
      
      if (data.trend && data.trend.length > 0) {
        trendData.value = data.trend
      } else {
        // Mock trend if no data
        trendData.value = [
          { label: 'Pzt', value: 0, percentage: 10 },
          { label: 'Sal', value: 0, percentage: 15 },
          { label: 'Çar', value: 0, percentage: 25 },
          { label: 'Per', value: 0, percentage: 20 },
          { label: 'Cum', value: 0, percentage: 40 },
          { label: 'Cmt', value: 0, percentage: 60 },
          { label: 'Paz', value: 0, percentage: 30 },
        ]
      }
    }
  } catch (e) {
    console.error('Failed to load report:', e)
  } finally {
    loadingData.value = false
  }
}

function formatPrice(p: number) {
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(p)
}

function exportReport() {
  if (currentPlan.value === 'FREE') return
  uiStore.success(t('admin.reports.exportSuccess'))
}

watch(dateRange, loadReport)

onMounted(() => {
  if (currentPlan.value !== 'FREE') {
    loadReport()
  }
})
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar { width: 4px; }
.scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
.scrollbar-thin::-webkit-scrollbar-thumb { background-color: #e2e8f0; border-radius: 20px; }

@keyframes modal-in {
  from { transform: translateY(40px) scale(0.9); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}
.animate-in { animation: modal-in 0.7s cubic-bezier(0.16, 1, 0.3, 1); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
