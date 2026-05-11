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
      
      <div class="flex items-center gap-3 bg-emerald-50 px-6 py-3.5 rounded-2xl border border-emerald-100 shadow-sm shrink-0">
         <div class="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping"></div>
         <span class="text-[10px] font-black text-emerald-600 uppercase tracking-widest">{{ $t('admin.qr.generatorLabel') || 'QR GENERATOR' }}</span>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-10">
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
           <p class="text-white/40 text-[9px] font-black uppercase tracking-[0.2em] mb-2 relative z-10">QR İPUCU</p>
           <p class="text-white font-medium text-xs leading-relaxed relative z-10">Logonuzun QR kodun okunabilirliğini etkilememesi için merkeze yerleştirilmesini sağlıyoruz. Yüksek kontrastlı renkler kullanmanızı öneririz.</p>
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
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const uiStore = useUiStore()

useHead({ title: () => `${t('admin.qr.title')} | Admin` })

const url = ref('https://feasymenu.com/menu')
const color = ref('#0f172a')
const logo = ref('')

onMounted(() => {
  if (import.meta.client) url.value = window.location.origin + '/menu'
})

const qrApiUrl = computed(() => {
  const safeUrl = encodeURIComponent(url.value || 'https://feasymenu.com')
  const safeColor = color.value.replace('#', '') || '000000'
  return `https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=${safeUrl}&color=${safeColor}&bgcolor=ffffff`
})

function handleLogoError() {
  logo.value = ''
  uiStore.error('Logo yüklenemedi.')
}

async function getCombinedQRDataURL(): Promise<string> {
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
    qrImg.onerror = reject; qrImg.src = qrApiUrl.value
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
</script>

<style scoped>
@keyframes scale-in {
  from { transform: scale(0) rotate(-20deg); opacity: 0; }
  to { transform: scale(1) rotate(0); opacity: 1; }
}
.scale-in { animation: scale-in 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards 0.3s; transform: scale(0); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
