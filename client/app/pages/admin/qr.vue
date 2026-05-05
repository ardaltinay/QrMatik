<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.qr.title') }}</h1>
      <p class="text-slate-500 text-sm mt-1">{{ $t('admin.qr.subtitle') }}</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
      
      <!-- Settings Panel -->
      <div class="lg:col-span-5 xl:col-span-4 space-y-6 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm self-start">
        <h3 class="font-bold text-slate-800 text-lg border-b border-slate-100 pb-3">{{ $t('admin.qr.settings') }}</h3>
        
        <!-- URL Input -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.qr.url') }}</label>
          <input 
            v-model="url" 
            type="text" 
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" 
          />
        </div>

        <!-- Color Input -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.qr.color') }}</label>
          <div class="flex items-center gap-3">
            <input 
              v-model="color" 
              type="color" 
              class="w-10 h-10 rounded cursor-pointer border-0 p-0" 
            />
            <input 
              v-model="color" 
              type="text" 
              class="flex-1 px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all uppercase" 
            />
          </div>
        </div>

        <!-- Logo Input -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.qr.logo') }}</label>
          <input 
            v-model="logo" 
            type="text" 
            placeholder="https://..."
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" 
          />
        </div>
      </div>

      <!-- Preview Panel -->
      <div class="lg:col-span-7 xl:col-span-8 flex flex-col items-center justify-center p-8 bg-slate-50 rounded-2xl border border-slate-200 border-dashed min-h-[500px]">
        <h3 class="font-bold text-slate-400 mb-8 uppercase tracking-widest text-sm">{{ $t('admin.qr.preview') }}</h3>
        
        <div id="qr-preview-container" class="relative bg-white p-4 rounded-3xl  shadow-slate-200/50">
          <img :src="qrApiUrl" alt="QR Code" class="w-64 h-64 sm:w-80 sm:h-80 object-contain" crossorigin="anonymous" />
          
          <!-- Logo Overlay -->
          <div v-if="logo" class="absolute inset-0 flex items-center justify-center pointer-events-none">
            <div class="bg-white p-1 rounded-lg shadow-sm">
              <img :src="logo" class="w-12 h-12 sm:w-16 sm:h-16 object-contain rounded-md" crossorigin="anonymous" @error="handleLogoError" />
            </div>
          </div>
        </div>

        <div class="flex items-center gap-4 mt-10">
          <button @click="downloadQR" class="px-6 py-3 bg-white border border-slate-200 text-slate-700 font-semibold rounded-xl hover:bg-slate-50 hover:border-slate-300 transition-all shadow-sm flex items-center gap-2">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            {{ $t('admin.qr.download') }}
          </button>
          
          <button @click="printQR" class="px-6 py-3 bg-brand-500 text-white font-semibold rounded-xl hover:bg-brand-600 transition-all shadow-sm  flex items-center gap-2">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
            </svg>
            {{ $t('admin.qr.print') }}
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
const uiStore = useUiStore()

useHead({
  title: () => `${t('admin.qr.title')} | Admin | feasymenu`
})

// State
const url = ref('https://feasymenu.com/menu')
const color = ref('#0f172a') // slate-900
const logo = ref('')

onMounted(() => {
  if (import.meta.client) {
    url.value = window.location.origin + '/menu'
  }
})

// Derived API URL
const qrApiUrl = computed(() => {
  const safeUrl = encodeURIComponent(url.value || 'https://feasymenu.com')
  const safeColor = color.value.replace('#', '') || '000000'
  return `https://api.qrserver.com/v1/create-qr-code/?size=600x600&data=${safeUrl}&color=${safeColor}&bgcolor=ffffff`
})

function handleLogoError() {
  logo.value = ''
  const errorMessage = t('errors.serverError') || 'Logo load error.';
  uiStore.error(errorMessage);
}

async function getCombinedQRDataURL(): Promise<string> {
  return new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    if (!ctx) return reject('No canvas context')

    const qrImg = new Image()
    qrImg.crossOrigin = 'anonymous'
    qrImg.onload = () => {
      canvas.width = qrImg.width
      canvas.height = qrImg.height
      ctx.fillStyle = '#ffffff'
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      ctx.drawImage(qrImg, 0, 0)

      if (logo.value) {
        const logoImg = new Image()
        logoImg.crossOrigin = 'anonymous'
        logoImg.onload = () => {
          // Draw logo in center with white background box
          const logoSize = canvas.width * 0.2
          const cx = (canvas.width - logoSize) / 2
          const cy = (canvas.height - logoSize) / 2
          
          // White box
          ctx.fillStyle = '#ffffff'
          const padding = 10
          ctx.beginPath()
          ctx.roundRect(cx - padding, cy - padding, logoSize + padding*2, logoSize + padding*2, 16)
          ctx.fill()

          // Logo
          ctx.drawImage(logoImg, cx, cy, logoSize, logoSize)
          resolve(canvas.toDataURL('image/png'))
        }
        logoImg.onerror = () => {
          resolve(canvas.toDataURL('image/png')) // fallback without logo
        }
        logoImg.src = logo.value
      } else {
        resolve(canvas.toDataURL('image/png'))
      }
    }
    qrImg.onerror = reject
    qrImg.src = qrApiUrl.value
  })
}

async function downloadQR() {
  try {
    const dataUrl = await getCombinedQRDataURL()
    const a = document.createElement('a')
    a.href = dataUrl
    a.download = `feasymenu-custom-qr.png`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
  } catch (e) {
    console.error('Error generating composite QR', e)
    // Fallback to direct download
    const a = document.createElement('a')
    a.href = qrApiUrl.value
    a.download = `feasymenu-qr.png`
    a.target = '_blank'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
  }
}

async function printQR() {
  try {
    const dataUrl = await getCombinedQRDataURL()
    const printWindow = window.open('', '_blank')
    if (printWindow) {
      printWindow.document.write(`
        <html>
          <head>
            <title>${t('admin.qr.title')}</title>
            <style>
              body { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; font-family: sans-serif; margin: 0; }
              img { width: 400px; height: 400px; margin-bottom: 20px; }
            </style>
          </head>
          <body>
            <img src="${dataUrl}" />
            <script>
              window.onload = () => { window.print(); window.close(); }
            <\/script>
          </body>
        </html>
      `)
      printWindow.document.close()
    }
  } catch (e) {
    console.error('Error printing composite QR', e)
  }
}
</script>
