<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto space-y-8">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.reports.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.reports.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
        <select v-model="dateRange" class="w-full sm:w-48 px-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm font-medium text-slate-700">
          <option value="today">{{ $t('admin.reports.dateRange.today') }}</option>
          <option value="week">{{ $t('admin.reports.dateRange.week') }}</option>
          <option value="month">{{ $t('admin.reports.dateRange.month') }}</option>
        </select>
        <button 
          @click="exportReport" 
          :disabled="currentPlan === 'FREE'"
          class="w-full sm:w-auto px-4 py-2.5 bg-slate-800 text-white font-semibold rounded-xl hover:bg-slate-900 transition-colors shadow-sm flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          {{ $t('admin.reports.export') }}
        </button>
      </div>
    </div>

    <div class="relative">
      <!-- Premium Overlay for FREE Users -->
      <div v-if="currentPlan === 'FREE'" class="absolute inset-0 z-50 backdrop-blur-[2px] bg-white/30 rounded-2xl flex items-center justify-center p-6 text-center">
        <div class="bg-white p-8 rounded-3xl shadow-xl border border-slate-200 max-w-md animate-in fade-in zoom-in duration-300">
          <div class="w-16 h-16 bg-brand-100 text-brand-600 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-2">{{ $t('admin.reports.premiumTitle') }}</h3>
          <p class="text-slate-500 mb-8">{{ $t('admin.reports.premiumDesc') }}</p>
          <NuxtLink :to="localePath('/admin/upgrade')" class="inline-flex items-center justify-center px-8 py-3 bg-brand-500 text-white font-bold rounded-xl hover:bg-brand-600 transition-all shadow-lg shadow-brand-500/25">
            {{ $t('admin.upgrade.button') }}
          </NuxtLink>
        </div>
      </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Revenue -->
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col relative overflow-hidden group hover:border-brand-300 transition-colors">
        <div class="absolute -right-6 -top-6 w-24 h-24 bg-brand-50 rounded-full group-hover:scale-150 transition-transform duration-500 z-0"></div>
        <div class="relative z-10">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-10 h-10 rounded-xl bg-brand-100 text-brand-600 flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="font-semibold text-slate-600">{{ $t('admin.reports.summary.revenue') }}</h3>
          </div>
          <div class="text-3xl font-bold text-slate-900">{{ formatPrice(summary.revenue) }}</div>
          <div class="mt-2 text-sm font-medium" :class="summary.revenueGrowth >= 0 ? 'text-emerald-600' : 'text-rose-600'">
            {{ summary.revenueGrowth >= 0 ? '+' : '' }}{{ summary.revenueGrowth }}% 
            <span class="text-slate-400 font-normal ml-1">{{ $t('admin.reports.vsPrevious') }}</span>
          </div>
        </div>
      </div>

      <!-- Completed Orders -->
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col relative overflow-hidden group hover:border-emerald-300 transition-colors">
        <div class="absolute -right-6 -top-6 w-24 h-24 bg-emerald-50 rounded-full group-hover:scale-150 transition-transform duration-500 z-0"></div>
        <div class="relative z-10">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-600 flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="font-semibold text-slate-600">{{ $t('admin.reports.summary.orders') }}</h3>
          </div>
          <div class="text-3xl font-bold text-slate-900">{{ summary.orders }}</div>
          <div class="mt-2 text-sm font-medium" :class="summary.ordersGrowth >= 0 ? 'text-emerald-600' : 'text-rose-600'">
            {{ summary.ordersGrowth >= 0 ? '+' : '' }}{{ summary.ordersGrowth }}% 
            <span class="text-slate-400 font-normal ml-1">{{ $t('admin.reports.vsPrevious') }}</span>
          </div>
        </div>
      </div>

      <!-- Cancelled Orders -->
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col relative overflow-hidden group hover:border-rose-300 transition-colors">
        <div class="absolute -right-6 -top-6 w-24 h-24 bg-rose-50 rounded-full group-hover:scale-150 transition-transform duration-500 z-0"></div>
        <div class="relative z-10">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-10 h-10 rounded-xl bg-rose-100 text-rose-600 flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="font-semibold text-slate-600">{{ $t('admin.reports.summary.cancelled') }}</h3>
          </div>
          <div class="text-3xl font-bold text-slate-900">{{ summary.cancelled }}</div>
          <div class="mt-2 text-sm font-medium" :class="summary.cancelledGrowth <= 0 ? 'text-emerald-600' : 'text-rose-600'">
            {{ summary.cancelledGrowth > 0 ? '+' : '' }}{{ summary.cancelledGrowth }}% 
            <span class="text-slate-400 font-normal ml-1">{{ $t('admin.reports.vsPrevious') }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts & Lists -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      
      <!-- Revenue Trend (Mock Bar Chart) -->
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
        <h3 class="font-bold text-slate-800 mb-6">{{ $t('admin.reports.charts.revenueTimeline') }}</h3>
        <div class="flex-1 flex items-end justify-between gap-2 h-48 mt-auto">
          <div v-for="(day, idx) in trendData" :key="idx" class="flex flex-col items-center flex-1 gap-2 group">
            <!-- Tooltip simulation -->
            <div class="opacity-0 group-hover:opacity-100 transition-opacity bg-slate-800 text-white text-[10px] font-bold py-1 px-2 rounded absolute -mt-8 pointer-events-none whitespace-nowrap z-10">
              {{ formatPrice(day.value) }}
            </div>
            <div class="w-full bg-brand-100 rounded-t-sm group-hover:bg-brand-500 transition-colors relative" :style="{ height: `${day.percentage}%` }"></div>
            <span class="text-[10px] font-medium text-slate-400 uppercase tracking-wider">{{ day.label }}</span>
          </div>
        </div>
      </div>

      <!-- Top Selling Products -->
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col">
        <h3 class="font-bold text-slate-800 mb-6">{{ $t('admin.reports.charts.topProducts') }}</h3>
        <div class="flex-1 overflow-y-auto">
          <div class="space-y-4">
            <div v-for="(product, idx) in topProducts" :key="idx" class="flex items-center gap-4">
              <div class="w-8 font-bold text-slate-300 text-lg">#{{ idx + 1 }}</div>
              <div class="w-10 h-10 rounded-lg bg-slate-100 overflow-hidden shrink-0">
                <NuxtImg v-if="product.image" :src="product.image" format="webp" class="w-full h-full object-cover" loading="lazy" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="font-semibold text-slate-800 truncate">{{ product.name }}</div>
                <div class="text-xs text-slate-500">{{ product.category }}</div>
              </div>
              <div class="text-right shrink-0">
                <div class="font-bold text-slate-800">{{ product.count }} {{ $t('admin.reports.countUnit') }}</div>
                <div class="text-xs text-brand-600 font-semibold">{{ formatPrice(product.revenue) }}</div>
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
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()
const localePath = useLocalePath()
const currentPlan = computed(() => authStore.user?.tenant?.subscriptionPlan || 'FREE')

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
      if (data.summary) {
        summary.value = data.summary
      }
      if (data.topProducts) {
        topProducts.value = data.topProducts
      }
      // Trend calculation (if backend provides it, otherwise calculate here or show placeholder)
      if (data.trend && data.trend.length > 0) {
        trendData.value = data.trend
      } else {
        // Fallback or placeholder for trend if not implemented in backend yet
        trendData.value = [
          { label: 'Pzt', value: 0, percentage: 10 },
          { label: 'Sal', value: 0, percentage: 10 },
          { label: 'Çar', value: 0, percentage: 10 },
          { label: 'Per', value: 0, percentage: 10 },
          { label: 'Cum', value: 0, percentage: 10 },
          { label: 'Cmt', value: 0, percentage: 10 },
          { label: 'Paz', value: 0, percentage: 10 },
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
  // Simulate export
  uiStore.success(t('admin.reports.exportSuccess'))
}

// Watch dateRange and update
watch(dateRange, () => {
  loadReport()
})

onMounted(() => {
  if (currentPlan.value !== 'FREE') {
    loadReport()
  }
})
</script>
