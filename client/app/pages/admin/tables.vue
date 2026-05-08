<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.tables.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.tables.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
        <div class="relative w-full sm:w-64">
          <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.tables.searchPlaceholder')"
            class="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm"
          />
        </div>
        <button @click="openModal()" class="w-full sm:w-auto px-4 py-2.5 bg-brand-500 text-white font-semibold rounded-xl hover:bg-brand-600 transition-colors shadow-sm  flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          {{ $t('admin.tables.addTable') }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs uppercase tracking-wider">
              <th class="px-6 py-4 font-semibold">{{ $t('admin.tables.columns.code') }}</th>
              <th class="px-6 py-4 font-semibold">{{ $t('admin.tables.columns.capacity') }}</th>
              <th class="px-6 py-4 font-semibold">{{ $t('admin.tables.columns.qr') }}</th>
              <th class="px-6 py-4 font-semibold text-right">{{ $t('admin.tables.columns.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading" class="animate-pulse">
              <td colspan="4" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.common.loading') }}</td>
            </tr>
            <tr v-else-if="filteredTables.length === 0">
              <td colspan="4" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.tables.emptyState') }}</td>
            </tr>
            <tr v-for="table in filteredTables" :key="table.id" class="hover:bg-slate-50/80 transition-colors group">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-xl bg-brand-50 border border-brand-100 text-brand-600 flex items-center justify-center font-bold">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                    </svg>
                  </div>
                  <div class="font-bold text-slate-800 text-lg">{{ table.code }}</div>
                </div>
              </td>
              <td class="px-6 py-4">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-sm font-medium bg-slate-100 text-slate-700 border border-slate-200">
                  <svg class="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                  {{ table.capacity || 4 }}
                </span>
              </td>
              <td class="px-6 py-4">
                <div class="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button @click="downloadQR(table.code)" class="text-xs px-2.5 py-1.5 rounded-md font-medium bg-slate-100 hover:bg-slate-200 text-slate-700 transition-colors flex items-center gap-1">
                    <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                    {{ $t('admin.tables.downloadQR') }}
                  </button>
                  <button @click="printQR(table.code)" class="text-xs px-2.5 py-1.5 rounded-md font-medium bg-slate-100 hover:bg-slate-200 text-slate-700 transition-colors flex items-center gap-1">
                    <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                    </svg>
                    {{ $t('admin.tables.printQR') }}
                  </button>
                </div>
              </td>
              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <button @click="openModal(table)" class="p-2 text-slate-400 hover:text-brand-600 hover:bg-brand-50 rounded-lg transition-colors" :title="$t('admin.common.edit') || 'Edit'">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button @click="confirmDelete(table)" class="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors" :title="$t('admin.common.delete') || 'Delete'">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="closeModal"></div>
      <div class="bg-white rounded-2xl w-full max-w-md relative z-10  overflow-hidden animate-slide-up">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-slate-800">
            {{ isEditing ? $t('admin.tables.modal.editTitle') : $t('admin.tables.modal.addTitle') }}
          </h3>
          <button @click="closeModal" class="text-slate-400 hover:text-slate-600">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.tables.modal.code') }} *</label>
            <input v-model="form.code" type="text" placeholder="A-1" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all uppercase" required />
          </div>
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.tables.modal.capacity') }}</label>
            <input v-model.number="form.capacity" type="number" min="1" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
          </div>
        </div>
        <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 flex justify-end gap-3">
          <button @click="closeModal" class="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-200 rounded-xl transition-colors">
            {{ $t('admin.common.cancel') }}
          </button>
          <button @click="saveTable" class="px-4 py-2 text-sm font-semibold bg-brand-500 text-white rounded-xl hover:bg-brand-600 transition-colors disabled:opacity-50" :disabled="saving">
            {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
          </button>
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

useHead({
  title: () => `${t('admin.tables.title')} | Admin`
})

// State
const tables = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')

const isModalOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const form = ref({
  id: '',
  code: '',
  capacity: 4
})

// Derived
const filteredTables = computed(() => {
  if (!searchQuery.value) return tables.value
  const q = searchQuery.value.toLowerCase()
  return tables.value.filter((t: any) => 
    t.code.toLowerCase().includes(q)
  )
})

// Methods
async function loadTables() {
  loading.value = true
  try {
    const data = await fetchJson('/api/tables')
    tables.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('Failed to load tables', e)
    // If backend isn't ready, let's mock it for visual completion during development
    if (tables.value.length === 0) {
      tables.value = [
        { id: 1, code: 'A-1', capacity: 4 },
        { id: 2, code: 'A-2', capacity: 2 },
        { id: 3, code: 'B-1', capacity: 6 },
        { id: 4, code: 'T-1', capacity: 4 }
      ]
    }
  } finally {
    loading.value = false
  }
}

function openModal(table?: any) {
  if (table) {
    isEditing.value = true
    form.value = {
      id: table.id,
      code: table.code,
      capacity: table.capacity || 4
    }
  } else {
    isEditing.value = false
    form.value = {
      id: '',
      code: '',
      capacity: 4
    }
  }
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

async function saveTable() {
  if (!form.value.code) return

  saving.value = true
  try {
    const payload = {
      code: form.value.code.toUpperCase(),
      capacity: form.value.capacity
    }

    if (isEditing.value) {
      await fetchJson(`/api/tables/${form.value.id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
    } else {
      await fetchJson('/api/tables', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
    }
    
    await loadTables()
    closeModal()
  } catch (e: any) {
    const errorMessage = e?.message || e?.toString() || t('errors.serverError');
    const translated = t(errorMessage);
    uiStore.error(translated.includes('error.') ? errorMessage : translated);
  } finally {
    saving.value = false
  }
}

async function confirmDelete(table: any) {
  uiStore.confirm({
    title: t('admin.common.delete') || 'Sil',
    message: t('admin.tables.deleteConfirm') || 'Bu masayı silmek istediğinize emin misiniz?',
    isDanger: true,
    onConfirm: async () => {
      try {
        await fetchJson(`/api/tables/${table.id}`, { method: 'DELETE' })
        await loadTables()
        uiStore.success(t('admin.common.deleted') || 'Silindi.')
      } catch (e: any) {
        const errorMessage = e?.message || e?.toString() || t('errors.serverError');
        const translated = t(errorMessage);
        uiStore.error(translated.includes('error.') ? errorMessage : translated);
      }
    }
  })
}

function getTableUrl(code: string) {
  if (!import.meta.client) return ''
  const base = window.location.origin
  return `${base}/menu?table=${encodeURIComponent(code)}`
}

function downloadQR(code: string) {
  // Real implementation would generate a QR code image and download it.
  // For now, we simulate an alert or opening a new window with a QR code generator API.
  const url = getTableUrl(code)
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=${encodeURIComponent(url)}`
  
  // Download logic
  const a = document.createElement('a')
  a.href = qrApiUrl
  a.download = `QR-${code}.png`
  a.target = '_blank'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

function printQR(code: string) {
  const url = getTableUrl(code)
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=${encodeURIComponent(url)}`
  
  const printWindow = window.open('', '_blank')
  if (printWindow) {
    printWindow.document.write(`
      <html>
        <head>
          <title>${t('admin.tables.columns.code')}: ${code}</title>
          <style>
            body { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; font-family: sans-serif; margin: 0; }
            img { width: 300px; height: 300px; margin-bottom: 20px; }
            h1 { font-size: 48px; margin: 0; }
          </style>
        </head>
        <body>
          <img src="${qrApiUrl}" />
          <h1>${code}</h1>
          <script>
            window.onload = () => { window.print(); window.close(); }
          <\/script>
        </body>
      </html>
    `)
    printWindow.document.close()
  }
}

onMounted(() => {
  loadTables()
})
</script>
