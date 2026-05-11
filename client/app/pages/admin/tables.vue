<template>
  <div class="p-4 md:p-8 max-w-7xl mx-auto space-y-10">
    <!-- Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight flex items-center gap-3">
          {{ $t('admin.tables.title') }}
          <span class="bg-indigo-100 text-indigo-600 text-[10px] font-black px-2 py-1 rounded-lg uppercase tracking-wider">Plan</span>
        </h1>
        <p class="text-slate-500 font-medium mt-1">{{ $t('admin.tables.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full lg:w-auto">
        <div class="relative flex-1 sm:min-w-[300px]">
          <svg class="w-5 h-5 absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.tables.searchPlaceholder')"
            class="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-white border border-slate-200 focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none shadow-sm font-bold text-slate-700"
          />
        </div>
        <button @click="openModal()" class="w-full sm:w-auto px-6 py-3.5 bg-brand-600 text-white font-black rounded-2xl hover:bg-brand-700 hover:-translate-y-0.5 active:translate-y-0 transition-all shadow-xl shadow-brand-500/25 flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          {{ $t('admin.tables.addTable') }}
        </button>
      </div>
    </div>

    <!-- Quick Stats Bento -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center gap-6 group overflow-hidden relative">
        <div class="absolute -right-6 -bottom-6 w-24 h-24 bg-brand-50 rounded-full group-hover:scale-150 transition-transform duration-500 opacity-50"></div>
        <div class="w-16 h-16 rounded-2xl bg-brand-50 text-brand-600 flex items-center justify-center border border-brand-100 relative z-10 shadow-inner">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" /></svg>
        </div>
        <div class="relative z-10">
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Toplam Masa</p>
          <h3 class="text-4xl font-black text-slate-900 tracking-tighter">{{ tables.length }}</h3>
        </div>
      </div>

      <div class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center gap-6 group overflow-hidden relative">
        <div class="absolute -right-6 -bottom-6 w-24 h-24 bg-emerald-50 rounded-full group-hover:scale-150 transition-transform duration-500 opacity-50"></div>
        <div class="w-16 h-16 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center border border-emerald-100 relative z-10 shadow-inner">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
        </div>
        <div class="relative z-10">
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Toplam Kapasite</p>
          <h3 class="text-4xl font-black text-slate-900 tracking-tighter">{{ tables.reduce((acc, t) => acc + (t.capacity || 4), 0) }} <span class="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Kişi</span></h3>
        </div>
      </div>

      <div class="bg-slate-900 p-8 rounded-[2rem] shadow-2xl flex items-center gap-6 group overflow-hidden relative">
        <div class="absolute inset-0 bg-gradient-to-r from-brand-600/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
        <div class="w-16 h-16 rounded-2xl bg-white/10 text-white flex items-center justify-center backdrop-blur-md relative z-10">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 11c0 3.517-1.009 6.799-2.753 9.571m-3.44-2.04l.054-.09A10.003 10.003 0 0012 20a10.003 10.003 0 006.203-2.138l.054.09a10.3 10.3 0 011.723 5.523H4.02a10.3 10.3 0 011.723-5.523zM12 11a3.999 3.999 0 01-4-4 4 4 0 018 0 3.999 3.999 0 01-4 4z" /></svg>
        </div>
        <div class="relative z-10">
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Anlık Durum</p>
          <h3 class="text-xl font-bold text-white tracking-tight leading-tight">Masalarınızı anlık olarak yönetin.</h3>
        </div>
      </div>
    </div>

    <!-- Tables Grid -->
    <div v-if="filteredTables.length === 0" class="py-32 text-center bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40">
       <div class="w-24 h-24 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-6 text-slate-200 border border-slate-100 shadow-inner">
          <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 6h16M4 10h16M4 14h16M4 18h16" /></svg>
       </div>
       <h3 class="text-2xl font-black text-slate-900 mb-2 tracking-tight">{{ $t('admin.tables.emptyState') }}</h3>
       <p class="text-slate-500 font-medium text-sm">Masaları oluşturup QR kodlarını hemen indirebilirsiniz.</p>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
      <TransitionGroup name="list">
        <div 
          v-for="table in filteredTables" 
          :key="table.id"
          class="bg-white rounded-[2.5rem] border border-slate-100 shadow-[0_20px_40px_-15px_rgba(0,0,0,0.08)] p-8 hover:shadow-[0_40px_80px_-20px_rgba(0,0,0,0.12)] hover:-translate-y-2 transition-all duration-500 group relative overflow-hidden"
        >
          <!-- Table Pattern -->
          <div class="absolute inset-0 opacity-0 group-hover:opacity-[0.03] transition-opacity pointer-events-none">
             <svg width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
               <defs><pattern id="grid" width="20" height="20" patternUnits="userSpaceOnUse"><rect width="20" height="20" fill="none" stroke="currentColor" stroke-width="1"/></pattern></defs>
               <rect width="100%" height="100%" fill="url(#grid)"/>
             </svg>
          </div>

          <div class="relative z-10 flex flex-col h-full">
            <div class="flex justify-between items-start mb-8">
               <div class="w-16 h-16 rounded-[1.5rem] bg-brand-50 text-brand-600 flex items-center justify-center shadow-inner border border-brand-100 font-black text-2xl tracking-tighter">
                  {{ table.code }}
               </div>
               <div class="flex flex-col items-end">
                  <span class="text-[10px] font-black text-slate-300 uppercase tracking-widest mb-1">KAPASİTE</span>
                  <div class="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-50 border border-slate-100 text-sm font-black text-slate-700">
                    <svg class="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
                    {{ table.capacity || 4 }}
                  </div>
               </div>
            </div>

            <!-- QR Preview Simulation -->
            <div class="aspect-square w-full rounded-[2rem] bg-slate-50 border-2 border-dashed border-slate-200 mb-8 flex items-center justify-center group-hover:border-brand-200 group-hover:bg-brand-50/30 transition-all cursor-pointer relative overflow-hidden" @click="downloadQR(table.code)">
               <svg class="w-20 h-20 text-slate-200 group-hover:text-brand-300 group-hover:scale-110 transition-all duration-700" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" /></svg>
               <div class="absolute inset-0 bg-brand-600 text-white flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <svg class="w-10 h-10 mb-2 animate-bounce" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" /></svg>
                  <span class="text-[10px] font-black uppercase tracking-widest">{{ $t('admin.tables.downloadQR') }}</span>
               </div>
            </div>

            <!-- Actions Footer -->
            <div class="flex items-center gap-3 pt-6 border-t border-slate-100 mt-auto">
               <button @click="printQR(table.code)" class="flex-1 py-3.5 bg-slate-900 text-white text-[10px] font-black uppercase tracking-widest rounded-2xl hover:bg-black transition-all shadow-lg shadow-slate-900/20 active:scale-95">
                  {{ $t('admin.tables.printQR') }}
               </button>
               <div class="flex items-center gap-2">
                  <button @click="openModal(table)" class="p-3.5 bg-white text-slate-400 hover:text-brand-600 hover:bg-brand-50 border border-slate-100 hover:border-brand-200 rounded-2xl shadow-sm transition-all" title="Edit">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                  </button>
                  <button @click="confirmDelete(table)" class="p-3.5 bg-white text-slate-400 hover:text-rose-600 hover:bg-rose-50 border border-slate-100 hover:border-rose-200 rounded-2xl shadow-sm transition-all" title="Delete">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                  </button>
               </div>
            </div>
          </div>
        </div>
      </TransitionGroup>
    </div>

    <!-- MODERN MODAL: Add/Edit Table -->
    <div v-if="isModalOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-md transition-opacity duration-300" @click="closeModal"></div>
      
      <div class="relative w-full max-w-sm bg-white rounded-[2.5rem] shadow-2xl overflow-hidden animate-modal-in border border-white/20">
        <div class="p-10">
           <div class="w-20 h-20 bg-brand-50 text-brand-600 rounded-[2rem] flex items-center justify-center mx-auto mb-8 border border-brand-100 shadow-inner">
              <svg class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" /></svg>
           </div>
           
           <div class="text-center mb-10">
             <h3 class="text-2xl font-black text-slate-900 tracking-tight">
               {{ isEditing ? $t('admin.tables.modal.editTitle') : $t('admin.tables.modal.addTitle') }}
             </h3>
             <p class="text-slate-400 font-medium text-sm mt-1 italic">Masa bilgilerini giriniz.</p>
           </div>
           
           <div class="space-y-6 mb-10">
              <div>
                <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.tables.modal.code') }} *</label>
                <input v-model="form.code" type="text" placeholder="A-1" class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-black uppercase text-xl text-center" required />
              </div>
              <div>
                <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.tables.modal.capacity') }}</label>
                <div class="flex items-center gap-4">
                   <button @click="form.capacity = Math.max(1, form.capacity - 1)" class="w-12 h-12 bg-slate-100 rounded-xl font-black text-xl hover:bg-slate-200 transition-all active:scale-90">-</button>
                   <input v-model.number="form.capacity" type="number" min="1" class="flex-1 h-12 bg-white border border-slate-100 rounded-xl text-center font-black text-xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none" />
                   <button @click="form.capacity++" class="w-12 h-12 bg-slate-100 rounded-xl font-black text-xl hover:bg-slate-200 transition-all active:scale-90">+</button>
                </div>
              </div>
           </div>

           <div class="flex flex-col gap-3">
              <button @click="saveTable" class="w-full py-4.5 bg-brand-600 text-white font-black text-sm uppercase tracking-[0.2em] rounded-2xl shadow-2xl shadow-brand-500/30 hover:bg-brand-700 active:scale-[0.98] transition-all disabled:opacity-50" :disabled="saving">
                 {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
              </button>
              <button @click="closeModal" class="w-full py-3 text-slate-400 font-black hover:text-slate-600 transition-all uppercase tracking-widest text-xs">
                 {{ $t('admin.common.cancel') }}
              </button>
           </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()

useHead({ title: () => `${t('admin.tables.title')} | Admin` })

const tables = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')
const isModalOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const form = ref({ id: '', code: '', capacity: 4 })

async function loadTables() {
  loading.value = true
  try {
    const data = await fetchJson('/api/tables')
    tables.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('Failed to load tables', e)
    if (tables.value.length === 0) {
      tables.value = [
        { id: 1, code: 'A-1', capacity: 4 },
        { id: 2, code: 'A-2', capacity: 2 },
        { id: 3, code: 'B-1', capacity: 6 },
        { id: 4, code: 'T-1', capacity: 4 }
      ]
    }
  } finally { loading.value = false }
}

const filteredTables = computed(() => {
  if (!searchQuery.value) return tables.value
  const q = searchQuery.value.toLowerCase()
  return tables.value.filter((t: any) => t.code.toLowerCase().includes(q))
})

function openModal(table?: any) {
  if (table) {
    isEditing.value = true
    form.value = { id: table.id, code: table.code, capacity: table.capacity || 4 }
  } else {
    isEditing.value = false
    form.value = { id: '', code: '', capacity: 4 }
  }
  isModalOpen.value = true
}

function closeModal() { isModalOpen.value = false }

async function saveTable() {
  if (!form.value.code) return
  saving.value = true
  try {
    const payload = { code: form.value.code.toUpperCase(), capacity: form.value.capacity }
    if (isEditing.value) await fetchJson(`/api/tables/${form.value.id}`, { method: 'PUT', body: JSON.stringify(payload) })
    else await fetchJson('/api/tables', { method: 'POST', body: JSON.stringify(payload) })
    await loadTables()
    closeModal()
    uiStore.success('Başarıyla kaydedildi')
  } catch (e: any) {
    uiStore.error(e?.message || 'Hata oluştu')
  } finally { saving.value = false }
}

async function confirmDelete(table: any) {
  uiStore.confirm({
    title: t('admin.common.delete'),
    message: t('admin.tables.deleteConfirm'),
    isDanger: true,
    onConfirm: async () => {
      try {
        await fetchJson(`/api/tables/${table.id}`, { method: 'DELETE' })
        await loadTables()
        uiStore.success(t('admin.common.deleted'))
      } catch (e: any) { uiStore.error('Hata oluştu') }
    }
  })
}

function getTableUrl(code: string) {
  if (!import.meta.client) return ''
  return `${window.location.origin}/menu?table=${encodeURIComponent(code)}`
}

function downloadQR(code: string) {
  const url = getTableUrl(code)
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=${encodeURIComponent(url)}`
  const a = document.createElement('a')
  a.href = qrApiUrl
  a.download = `QR-${code}.png`
  a.target = '_blank'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  uiStore.success(`${code} masası QR kodu indiriliyor...`)
}

function printQR(code: string) {
  const url = getTableUrl(code)
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=${encodeURIComponent(url)}`
  const printWindow = window.open('', '_blank')
  if (printWindow) {
    printWindow.document.write(`
      <html>
        <head>
          <title>${code}</title>
          <style>
            body { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; font-family: sans-serif; margin: 0; }
            img { width: 500px; height: 500px; margin-bottom: 20px; }
            h1 { font-size: 80px; margin: 0; font-weight: 900; letter-spacing: -2px; }
            p { font-size: 24px; color: #666; margin-top: 10px; }
          </style>
        </head>
        <body>
          <img src="${qrApiUrl}" />
          <h1>${code}</h1>
          <p>FeasyMenu ile kolayca sipariş verin</p>
          <script>window.onload = () => { window.print(); setTimeout(() => window.close(), 100); }<\/script>
        </body>
      </html>
    `)
    printWindow.document.close()
  }
}

onMounted(loadTables)
</script>

<style scoped>
@keyframes modal-in {
  from { transform: translateY(30px) scale(0.95); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}
.animate-modal-in { animation: modal-in 0.5s cubic-bezier(0.16, 1, 0.3, 1); }

.list-enter-active, .list-leave-active { transition: all 0.5s ease; }
.list-enter-from { opacity: 0; transform: translateY(20px); }
.list-leave-to { opacity: 0; transform: translateY(-20px); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
