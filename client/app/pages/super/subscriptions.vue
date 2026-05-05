<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.super.subscriptions.title') }}</h1>
      <p class="text-slate-500 text-sm mt-1">{{ $t('admin.super.subscriptions.subtitle') }}</p>
    </div>

    <!-- Metrics -->
    <!-- Metrics -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div class="text-sm font-semibold text-slate-500 mb-2">{{ $t('admin.super.subscriptions.metrics.mrr') }}</div>
        <div class="text-3xl font-bold text-slate-900">₺{{ stats?.mrr?.toLocaleString('tr-TR') || 0 }}</div>
        <div class="text-xs text-slate-400 font-medium mt-2 flex items-center gap-1">
          Tahmini aylık gelir
        </div>
      </div>
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div class="text-sm font-semibold text-slate-500 mb-2">{{ $t('admin.super.subscriptions.metrics.active') }}</div>
        <div class="text-3xl font-bold text-slate-900">{{ stats?.activeTenants || 0 }}</div>
        <div class="text-xs text-slate-400 font-medium mt-2">Aktif işletme</div>
      </div>
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div class="text-sm font-semibold text-slate-500 mb-2">{{ $t('admin.super.subscriptions.metrics.trial') }}</div>
        <div class="text-3xl font-bold text-slate-900">{{ stats?.trialCount || 0 }}</div>
        <div class="text-xs text-slate-400 font-medium mt-2">Deneme süresinde</div>
      </div>
      <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div class="text-sm font-semibold text-slate-500 mb-2">Toplam İşletme</div>
        <div class="text-3xl font-bold text-slate-900">{{ stats?.totalTenants || 0 }}</div>
        <div class="text-xs text-slate-400 font-medium mt-2">Kayıtlı restoran sayısı</div>
      </div>
    </div>

    <!-- Subscriptions Table -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-sm font-semibold text-slate-600">
              <th class="p-4 px-6">{{ $t('admin.super.subscriptions.table.restaurant') }}</th>
              <th class="p-4 px-6">{{ $t('admin.super.subscriptions.table.plan') }}</th>
              <th class="p-4 px-6">Durum</th>
              <th class="p-4 px-6 text-right">Kayıt Tarihi</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading">
              <td colspan="4" class="p-12 text-center">
                <div class="inline-block w-8 h-8 border-4 border-slate-200 border-t-brand-500 rounded-full animate-spin"></div>
              </td>
            </tr>
            <tr v-else-if="tenants.length === 0">
              <td colspan="4" class="p-12 text-center text-slate-500">Henüz abonelik bulunmuyor.</td>
            </tr>
            <tr v-for="t in tenants" :key="t.id" v-else class="hover:bg-slate-50 transition-colors">
              <td class="p-4 px-6 font-bold text-slate-800">{{ t.name }}</td>
              <td class="p-4 px-6">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold"
                  :class="{
                    'bg-slate-100 text-slate-700': String(t.plan).toLowerCase() === 'free',
                    'bg-brand-100 text-brand-700': String(t.plan).toLowerCase() === 'pro',
                    'bg-emerald-100 text-emerald-700': String(t.plan).toLowerCase() === 'enterprise'
                  }">
                  {{ String(t.plan || 'free').toUpperCase() }}
                </span>
              </td>
              <td class="p-4 px-6">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold"
                  :class="t.active ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'">
                  <span class="w-1.5 h-1.5 rounded-full" :class="t.active ? 'bg-emerald-500' : 'bg-rose-500'"></span>
                  {{ t.active ? 'Aktif' : 'Askıda' }}
                </span>
              </td>
              <td class="p-4 px-6 text-slate-500 text-sm text-right">{{ t.createdAt ? new Date(t.createdAt).toLocaleDateString('tr-TR') : '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const loading = ref(true)
const stats = ref<any>(null)
const tenants = ref<any[]>([])

useHead({
  title: () => `${t('admin.super.subscriptions.title')} | Super Admin | feasymenu`
})

async function fetchData() {
  loading.value = true
  try {
    const { fetchJson } = useApi()
    const [statsData, tenantsData] = await Promise.all([
      fetchJson('/api/tenants/dashboard-stats'),
      fetchJson('/api/tenants')
    ])
    stats.value = statsData
    tenants.value = tenantsData || []
  } catch (e) {
    console.error('Failed to fetch subscription data:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>
