<template>
  <div class="p-4 md:p-8 max-w-7xl mx-auto space-y-10">
    <!-- Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 bg-white/50 backdrop-blur-xl p-8 rounded-[3rem] border border-white shadow-2xl shadow-slate-200/40">
      <div class="flex items-center gap-6">
        <div class="w-16 h-16 rounded-2xl bg-slate-900 text-white flex items-center justify-center shadow-lg shadow-slate-900/30">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" /></svg>
        </div>
        <div>
          <h1 class="text-3xl font-black text-slate-900 tracking-tight leading-none mb-2">{{ $t('admin.qr.title') }}</h1>
          <p class="text-slate-500 font-medium text-sm">{{ $t('admin.qr.subtitle') }}</p>
        </div>
      </div>
      
      <!-- Tab Switcher -->
      <div class="flex p-1.5 bg-slate-100 rounded-2xl w-full lg:w-auto">
        <button 
          @click="activeTab = 'general'"
          class="flex-1 lg:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all"
          :class="activeTab === 'general' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-400 hover:text-slate-600'"
        >
          {{ $t('admin.qr.generalTab') }}
        </button>
        <button 
          @click="activeTab = 'tables'"
          class="flex-1 lg:flex-none px-6 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all"
          :class="activeTab === 'tables' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-400 hover:text-slate-600'"
        >
          {{ $t('admin.qr.tablesTab') }}
        </button>
      </div>
    </div>

    <!-- GENERAL QR TAB -->
    <div v-if="activeTab === 'general'" class="grid grid-cols-1 lg:grid-cols-12 gap-10">
      <!-- Settings Panel -->
      <div class="lg:col-span-5 xl:col-span-4 space-y-6">
        <div class="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 space-y-8">
           <h3 class="text-lg font-black text-slate-900 uppercase tracking-widest flex items-center gap-3">
              <span class="w-1.5 h-1.5 rounded-full bg-indigo-500"></span>
              {{ $t('admin.qr.settings') }}
           </h3>
          
          <!-- URL Input -->
          <div>
            <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.qr.url') }}</label>
            <input 
              v-model="url" 
              type="text" 
              class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-bold text-slate-700 shadow-inner" 
            />
          </div>

          <!-- Color Input -->
          <div>
            <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.qr.color') }}</label>
            <div class="flex items-center gap-4">
              <div class="relative w-14 h-14 rounded-2xl overflow-hidden shadow-lg border-4 border-white cursor-pointer group">
                <input v-model="color" type="color" class="absolute inset-[-50%] w-[200%] h-[200%] cursor-pointer border-none bg-transparent">
              </div>
              <input 
                v-model="color" 
                type="text" 
                class="flex-1 px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-black uppercase text-slate-700" 
              />
            </div>
          </div>

          <!-- Logo Input -->
          <div>
            <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.qr.logo') }}</label>
            <div class="flex items-center gap-4">
               <div class="w-14 h-14 rounded-2xl bg-slate-50 border-2 border-dashed border-slate-200 flex items-center justify-center overflow-hidden shrink-0">
                  <img v-if="logo" :src="logo" class="max-w-full max-h-full object-contain p-1" />
                  <svg v-else class="w-6 h-6 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
               </div>
               <input 
                 v-model="logo" 
                 type="text" 
                 placeholder="Logo URL..."
                 class="flex-1 px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-bold text-slate-700 shadow-inner" 
               />
            </div>
          </div>
        </div>

        <div class="p-6 bg-slate-900 rounded-[2rem] shadow-2xl relative overflow-hidden group">
           <div class="absolute -right-10 -bottom-10 w-32 h-32 bg-white/5 rounded-full group-hover:scale-150 transition-transform duration-700"></div>
           <p class="text-white/40 text-[9px] font-black uppercase tracking-[0.2em] mb-2 relative z-10">{{ $t('admin.qr.tipTitle') }}</p>
           <p class="text-white font-medium text-xs leading-relaxed relative z-10">{{ $t('admin.qr.tipDesc') }}</p>
        </div>
      </div>

      <!-- Preview Panel -->
      <div class="lg:col-span-7 xl:col-span-8 flex flex-col items-center justify-center p-12 bg-slate-50 rounded-[3rem] border-2 border-dashed border-slate-200 relative overflow-hidden min-h-[600px]">
        <div class="absolute inset-0 opacity-[0.03] pointer-events-none">
           <svg width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
             <defs><pattern id="qr-grid" width="40" height="40" patternUnits="userSpaceOnUse"><rect width="40" height="40" fill="none" stroke="currentColor" stroke-width="1"/></pattern></defs>
             <rect width="100%" height="100%" fill="url(#qr-grid)"/>
           </svg>
        </div>

        <h3 class="font-black text-slate-300 mb-12 uppercase tracking-[0.4em] text-xs relative z-10">{{ $t('admin.qr.preview') }}</h3>
        
        <div id="qr-preview-container" class="relative group/qr">
          <div class="absolute -inset-10 bg-white/40 blur-3xl rounded-full scale-0 group-hover/qr:scale-100 transition-transform duration-1000"></div>
          
          <div class="relative bg-white p-8 rounded-[3rem] shadow-[0_64px_128px_-32px_rgba(0,0,0,0.15)] border border-white transition-all duration-700 hover:scale-105 hover:rotate-1">
            <img :src="qrApiUrl" alt="QR Code" class="w-64 h-64 sm:w-80 sm:h-80 object-contain relative z-10" crossorigin="anonymous" />
            
            <!-- Logo Overlay -->
            <div v-if="logo" class="absolute inset-0 flex items-center justify-center pointer-events-none z-20">
              <div class="bg-white p-2 rounded-2xl shadow-2xl border border-slate-100 scale-in">
                <img :src="logo" class="w-12 h-12 sm:w-20 sm:h-20 object-contain rounded-xl" crossorigin="anonymous" @error="handleLogoError" />
              </div>
            </div>
          </div>
        </div>

        <div class="flex flex-col sm:flex-row items-center gap-4 mt-16 w-full max-w-md relative z-10">
          <button @click="downloadQR" class="flex-1 w-full px-8 py-4.5 bg-white text-slate-900 font-black text-[10px] uppercase tracking-widest rounded-2xl hover:bg-slate-50 transition-all shadow-xl shadow-slate-200/40 border border-slate-100 active:scale-95 flex items-center justify-center gap-3">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" /></svg>
            {{ $t('admin.qr.download') }}
          </button>
          
          <button @click="printQR" class="flex-1 w-full px-8 py-4.5 bg-brand-600 text-white font-black text-[10px] uppercase tracking-widest rounded-2xl hover:bg-brand-700 transition-all shadow-2xl shadow-brand-500/30 active:scale-95 flex items-center justify-center gap-3">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" /></svg>
            {{ $t('admin.qr.print') }}
          </button>
        </div>
      </div>
    </div>

    <!-- TABLES QR TAB -->
    <div v-else class="space-y-8">
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 class="text-xl font-black text-slate-900 tracking-tight">{{ $t('admin.qr.tablesTabTitle') || 'Masa Sipariş Kodları' }}</h2>
          <p class="text-sm font-medium text-slate-500">{{ $t('admin.qr.tablesTabSubtitle') || 'Her masaya özel sipariş linkleri oluşturun.' }}</p>
        </div>
        <button 
          @click="printAllTableQRs"
          class="w-full sm:w-auto px-8 py-4 bg-slate-900 text-white font-black text-[10px] uppercase tracking-widest rounded-2xl hover:bg-slate-800 transition-all shadow-xl active:scale-95 flex items-center justify-center gap-3"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" /></svg>
          {{ $t('admin.qr.printAll') }}
        </button>
      </div>

      <div v-if="loadingTables" class="py-20 flex flex-col items-center justify-center bg-white rounded-[2.5rem] border border-slate-100 shadow-xl">
        <div class="w-12 h-12 border-4 border-slate-100 border-t-indigo-500 rounded-full animate-spin mb-4"></div>
        <p class="text-xs font-black text-slate-400 uppercase tracking-widest italic">{{ $t('admin.qr.loading') }}</p>
      </div>

      <div v-else-if="tables.length === 0" class="py-20 text-center bg-white rounded-[2.5rem] border border-slate-100 shadow-xl">
        <p class="text-slate-400 font-bold tracking-tight">{{ $t('admin.qr.emptyTables') }}</p>
        <NuxtLink :to="localePath('/admin/tables')" class="mt-4 inline-flex text-indigo-600 font-black text-xs uppercase tracking-widest border-b-2 border-indigo-100 hover:border-indigo-500 transition-all">{{ $t('admin.qr.goToTables') }}</NuxtLink>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        <div v-for="table in tables" :key="table.id" class="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-lg shadow-slate-200/40 hover:shadow-2xl hover:shadow-indigo-500/10 transition-all group overflow-hidden relative">
           <div class="absolute -right-10 -bottom-10 w-24 h-24 bg-slate-50 rounded-full group-hover:scale-150 transition-transform duration-700 opacity-50"></div>
           <div class="flex items-center gap-6 relative z-10">
              <div class="w-20 h-20 bg-slate-50 rounded-2xl flex items-center justify-center p-2 border border-slate-100 group-hover:bg-white transition-colors duration-500">
                <img :src="getTableQRUrl(table.code)" class="w-full h-full object-contain" />
              </div>
              <div class="flex-1">
                <span class="text-[9px] font-black text-slate-300 uppercase tracking-[0.2em] mb-1 block">{{ $t('admin.qr.tableCodeLabel') }}</span>
                <h4 class="text-2xl font-black text-slate-900 tracking-tighter mb-3">{{ table.code }}</h4>
                <div class="flex gap-2">
                  <button @click="downloadTableQR(table.code)" class="w-10 h-10 bg-slate-100 text-slate-600 rounded-xl flex items-center justify-center hover:bg-slate-200 transition-all active:scale-90" title="İndir">
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" /></svg>
                  </button>
                  <button @click="printTableQR(table.code)" class="w-10 h-10 bg-indigo-50 text-indigo-600 rounded-xl flex items-center justify-center hover:bg-indigo-100 transition-all active:scale-90" title="Yazdır">
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" /></svg>
                  </button>
                </div>
              </div>
           </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const uiStore = useUiStore()
const { fetchJson } = useApi()
const localePath = useLocalePath()

useHead({ title: () => `${t('admin.qr.title')} | Admin` })

const activeTab = ref('general')
const url = ref('https://feasymenu.com/menu')
const color = ref('#1a1c18') // Default brand charcoal
const logo = ref('')

const tables = ref<any[]>([])
const loadingTables = ref(false)

onMounted(async () => {
  if (import.meta.client) url.value = window.location.origin + '/menu'
  loadTables()
})

async function loadTables() {
  loadingTables.value = true
  try {
    const data = await fetchJson('/api/tables')
    tables.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('Failed to load tables', e)
  } finally { loadingTables.value = false }
}

const qrApiUrl = computed(() => {
  const safeUrl = encodeURIComponent(url.value || 'https://feasymenu.com')
  const safeColor = color.value.replace('#', '') || '000000'
  return `https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=${safeUrl}&color=${safeColor}&bgcolor=ffffff`
})

function getTableQRUrl(code: string) {
  const tableUrl = `${window.location.origin}/menu?table=${encodeURIComponent(code)}`
  const safeColor = color.value.replace('#', '') || '000000'
  return `https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=${encodeURIComponent(tableUrl)}&color=${safeColor}&bgcolor=ffffff`
}

function handleLogoError() {
  logo.value = ''
  uiStore.error('Logo yüklenemedi.')
}

async function getCombinedQRDataURL(customUrl?: string): Promise<string> {
  return new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    if (!ctx) return reject('No canvas context')
    const qrImg = new Image()
    qrImg.crossOrigin = 'anonymous'
    qrImg.onload = () => {
      canvas.width = qrImg.width; canvas.height = qrImg.height
      ctx.fillStyle = '#ffffff'; ctx.fillRect(0, 0, canvas.width, canvas.height)
      ctx.drawImage(qrImg, 0, 0)
      if (logo.value) {
        const logoImg = new Image(); logoImg.crossOrigin = 'anonymous'
        logoImg.onload = () => {
          const logoSize = canvas.width * 0.22, cx = (canvas.width - logoSize) / 2, cy = (canvas.height - logoSize) / 2
          ctx.fillStyle = '#ffffff'; const padding = 15; ctx.beginPath()
          ctx.roundRect(cx - padding, cy - padding, logoSize + padding*2, logoSize + padding*2, 30); ctx.fill()
          ctx.drawImage(logoImg, cx, cy, logoSize, logoSize); resolve(canvas.toDataURL('image/png'))
        }
        logoImg.onerror = () => resolve(canvas.toDataURL('image/png'))
        logoImg.src = logo.value
      } else resolve(canvas.toDataURL('image/png'))
    }
    qrImg.onerror = reject; 
    qrImg.src = customUrl ? customUrl : qrApiUrl.value
  })
}

async function downloadQR() {
  try {
    const dataUrl = await getCombinedQRDataURL()
    const a = document.createElement('a'); a.href = dataUrl; a.download = `feasymenu-custom-qr.png`
    document.body.appendChild(a); a.click(); document.body.removeChild(a)
    uiStore.success('QR Kod indirildi.')
  } catch (e) { uiStore.error('Hata oluştu.') }
}

async function printQR() {
  try {
    const dataUrl = await getCombinedQRDataURL()
    const printWindow = window.open('', '_blank')
    if (printWindow) {
      printWindow.document.write(`<html><head><title>QR Print</title><style>body { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; font-family: sans-serif; margin: 0; } img { width: 500px; height: 500px; }</style></head><body><img src="${dataUrl}" /><script>window.onload = () => { window.print(); setTimeout(() => window.close(), 100); }<\/script></body></html>`)
      printWindow.document.close()
    }
  } catch (e) { uiStore.error('Hata oluştu.') }
}

async function downloadTableQR(code: string) {
  try {
    const dataUrl = await getCombinedQRDataURL(getTableQRUrl(code))
    const a = document.createElement('a'); a.href = dataUrl; a.download = `QR-${code}.png`
    document.body.appendChild(a); a.click(); document.body.removeChild(a)
  } catch (e) { uiStore.error('İndirme başarısız.') }
}

async function printTableQR(code: string) {
  try {
    const dataUrl = await getCombinedQRDataURL(getTableQRUrl(code))
    const printWindow = window.open('', '_blank')
    if (printWindow) {
      printWindow.document.write(`
        <html>
          <head>
            <title>${code}</title>
            <style>
              body { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; font-family: 'Plus Jakarta Sans', sans-serif; margin: 0; background: #f8fafc; }
              .qr-card { background: white; padding: 40px; border-radius: 40px; box-shadow: 0 20px 50px rgba(0,0,0,0.1); text-align: center; }
              img { width: 400px; height: 400px; margin-bottom: 20px; }
              h1 { font-size: 72px; margin: 0; font-weight: 900; color: #0f172a; letter-spacing: -2px; }
              p { font-size: 20px; color: #64748b; margin-top: 10px; font-weight: 600; }
              .brand { margin-top: 30px; font-size: 14px; color: #94a684; font-weight: 900; letter-spacing: 2px; }
            </style>
          </head>
          <body>
            <div class="qr-card">
              <img src="${dataUrl}" />
              <h1>${code}</h1>
              <p>${t('admin.qr.scanForOrder')}</p>
              <div class="brand">FEASYMENU</div>
            </div>
            <script>window.onload = () => { window.print(); setTimeout(() => window.close(), 100); }<\/script>
          </body>
        </html>
      `)
      printWindow.document.close()
    }
  } catch (e) { uiStore.error('Yazdırma başarısız.') }
}

async function printAllTableQRs() {
  try {
    const printWindow = window.open('', '_blank')
    if (!printWindow) return
    
    let html = `
      <html>
        <head>
          <title>${t('admin.qr.allTablesPrintTitle')}</title>
          <style>
            body { font-family: 'Plus Jakarta Sans', sans-serif; margin: 0; padding: 20px; background: white; }
            .grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 40px; }
            .qr-item { border: 1px solid #eee; padding: 30px; border-radius: 30px; text-align: center; page-break-inside: avoid; }
            img { width: 250px; height: 250px; }
            h2 { font-size: 48px; margin: 10px 0; font-weight: 900; color: #0f172a; }
            p { font-size: 14px; color: #64748b; margin: 0; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; }
          </style>
        </head>
        <body>
          <div class="grid">
    `
    
    for (const table of tables.value) {
      const dataUrl = await getCombinedQRDataURL(getTableQRUrl(table.code))
      html += `
        <div class="qr-item">
          <p>${t('admin.qr.tableLabel')}</p>
          <h2>${table.code}</h2>
          <img src="${dataUrl}" />
          <p style="margin-top: 15px; color: #94a684;">feasymenu.com</p>
        </div>
      `
    }
    
    html += `
          </div>
          <script>window.onload = () => { window.print(); setTimeout(() => window.close(), 100); }<\/script>
        </body>
      </html>
    `
    printWindow.document.write(html)
    printWindow.document.close()
  } catch (e) { uiStore.error('Toplu yazdırma başarısız.') }
}
</script>

<style scoped>
@keyframes scale-in {
  from { transform: scale(0) rotate(-20deg); opacity: 0; }
  to { transform: scale(1) rotate(0); opacity: 1; }
}
.scale-in { animation: scale-in 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards 0.3s; transform: scale(0); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
