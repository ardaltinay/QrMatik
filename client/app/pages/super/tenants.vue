<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.super.tenants.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.super.tenants.subtitle') }}</p>
      </div>
      <div class="w-full sm:w-72 relative">
        <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input 
          v-model="searchQuery" 
          type="text" 
          :placeholder="$t('admin.super.tenants.searchPlaceholder')"
          class="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm"
        />
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-sm font-semibold text-slate-600">
              <th class="p-4 px-6">{{ $t('admin.super.tenants.table.name') }}</th>
              <th class="p-4 px-6">{{ $t('admin.super.tenants.table.owner') }}</th>
              <th class="p-4 px-6">{{ $t('admin.super.tenants.table.plan') }}</th>
              <th class="p-4 px-6">{{ $t('admin.super.tenants.table.status') }}</th>
              <th class="p-4 px-6">{{ $t('admin.super.tenants.table.joined') }}</th>
              <th class="p-4 px-6 text-right">{{ $t('admin.super.tenants.table.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <!-- Loading State -->
            <tr v-if="loading">
              <td colspan="6" class="p-12 text-center">
                <div class="inline-block w-8 h-8 border-4 border-slate-200 border-t-brand-500 rounded-full animate-spin"></div>
                <p class="mt-2 text-slate-500 text-sm font-medium">{{ $t('admin.common.loading') }}</p>
              </td>
            </tr>

            <!-- Empty State -->
            <tr v-else-if="filteredTenants.length === 0">
              <td colspan="6" class="p-12 text-center">
                <div class="inline-flex items-center justify-center w-12 h-12 rounded-full bg-slate-50 text-slate-400 mb-3">
                  <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                </div>
                <p class="text-slate-500 font-medium">{{ $t('admin.super.tenants.noData') || 'No tenants found' }}</p>
              </td>
            </tr>

            <!-- Data State -->
            <tr v-for="t in filteredTenants" :key="t.id" v-else class="hover:bg-slate-50 transition-colors">
              <td class="p-4 px-6 font-bold text-slate-800">{{ t.name }}</td>
              <td class="p-4 px-6 text-slate-600">{{ t.ownerEmail }}</td>
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
                  :class="t.status === 'active' || t.status === undefined ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'">
                  <span class="w-1.5 h-1.5 rounded-full" :class="t.status === 'active' || t.status === undefined ? 'bg-emerald-500' : 'bg-rose-500'"></span>
                  {{ $t(`admin.super.tenants.status.${t.status || 'active'}`) }}
                </span>
              </td>
              <td class="p-4 px-6 text-slate-500 text-sm">{{ t.createdAt ? new Date(t.createdAt).toLocaleDateString('tr-TR') : '-' }}</td>
              <td class="p-4 px-6 text-right space-x-2">
                <button @click="viewTenant(t)" class="text-brand-600 hover:text-brand-700 text-sm font-medium">{{ $t('admin.super.tenants.actions.view') }}</button>
                <button @click="toggleStatus(t)" class="text-sm font-medium" :class="t.active || t.active === undefined ? 'text-rose-600 hover:text-rose-700' : 'text-emerald-600 hover:text-emerald-700'">
                  {{ t.active || t.active === undefined ? $t('admin.super.tenants.actions.suspend') : $t('admin.super.tenants.actions.activate') }}
                </button>
              </td>
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

useHead({
  title: () => `${t('admin.super.tenants.title')} | Super Admin`
})

const searchQuery = ref('')
const loading = ref(true)
const tenants = ref<any[]>([])

async function fetchTenants() {
  loading.value = true
  try {
    const { fetchJson } = useApi()
    const data = await fetchJson('/api/tenants')
    tenants.value = data || []
  } catch (e) {
    console.error('Failed to fetch tenants:', e)
    useUiStore().error(t('admin.common.error'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTenants()
})

const filteredTenants = computed(() => {
  if (!searchQuery.value) return tenants.value
  const q = searchQuery.value.toLowerCase()
  return tenants.value.filter(t => t.name.toLowerCase().includes(q) || t.ownerEmail.toLowerCase().includes(q))
})

function viewTenant(tenant: any) {
  // Open the tenant's public menu page in a new tab
  window.open(`https://${tenant.code}.feasymenu.com`, '_blank')
}

async function toggleStatus(tenant: any) {
  try {
    const { fetchJson } = useApi()
    const updated = await fetchJson(`/api/tenants/${tenant.id}/toggle-active`, {
      method: 'POST'
    })
    if (updated) {
      tenant.active = updated.active
      useUiStore().success(t(tenant.active ? 'admin.common.active' : 'admin.common.suspended'))
    }
  } catch (e) {
    console.error('Failed to toggle tenant status:', e)
    useUiStore().error(t('admin.common.error'))
  }
}
</script>
