<template>
  <div class="p-4 md:p-8 max-w-7xl mx-auto space-y-10">
    <!-- Header Section -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 bg-white/50 backdrop-blur-xl p-8 rounded-[3rem] border border-white shadow-2xl shadow-slate-200/40">
      <div class="flex items-center gap-6">
        <div class="w-16 h-16 rounded-2xl bg-orange-500 text-white flex items-center justify-center shadow-lg shadow-orange-500/30">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v13m0-13V6a2 2 0 112 2h-2zm0 0V5.5A2.5 2.5 0 109.5 8H12zm-7 4h14M5 12a2 2 0 110-4h14a2 2 0 110 4M5 12v7a2 2 0 002 2h10a2 2 0 002-2v-7" /></svg>
        </div>
        <div>
          <h1 class="text-3xl font-black text-slate-900 tracking-tight leading-none mb-2">{{ $t('admin.loyalty.title') }}</h1>
          <p class="text-slate-500 font-medium text-sm">{{ $t('admin.loyalty.subtitle') }}</p>
        </div>
      </div>

      <div v-if="isPro" class="flex items-center gap-4 w-full lg:w-auto">
        <div class="flex items-center gap-3 bg-white px-5 py-3 rounded-2xl border border-slate-100 shadow-sm grow lg:grow-0">
           <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest">{{ campaign.active ? 'AKTİF' : 'PASİF' }}</span>
           <label class="relative inline-flex items-center cursor-pointer group">
             <input type="checkbox" v-model="campaign.active" class="sr-only peer">
             <div class="w-14 h-7 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[4px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-brand-600 shadow-inner group-hover:after:scale-110 transition-all"></div>
           </label>
        </div>
        
        <button 
          @click="saveSettings" 
          :disabled="saving"
          class="flex-1 lg:flex-none px-10 py-3.5 bg-brand-600 text-white font-black text-xs uppercase tracking-[0.2em] rounded-2xl hover:bg-brand-700 hover:-translate-y-0.5 active:translate-y-0 transition-all shadow-xl shadow-brand-500/30 flex items-center justify-center gap-3 disabled:opacity-50"
        >
          <div v-if="saving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
          {{ saving ? $t('admin.common.saving') : $t('admin.loyalty.save') }}
        </button>
      </div>
    </div>

    <div class="relative min-h-[600px]">
      <!-- PREMIUM OVERLAY -->
      <div v-if="!isProPlan" class="absolute inset-x-0 top-0 bottom-0 z-30 backdrop-blur-md bg-white/40 rounded-[2.5rem] flex items-center justify-center p-4 sm:p-6 text-center border-2 border-white/50">
        <div class="bg-white p-8 sm:p-12 rounded-[3rem] shadow-[0_32px_64px_-16px_rgba(0,0,0,0.2)] border border-slate-100 w-full max-w-lg animate-in fade-in zoom-in slide-in-from-bottom-8 duration-700 ease-out">
          <div class="w-24 h-24 bg-orange-50 text-orange-600 rounded-[2.5rem] flex items-center justify-center mx-auto mb-8 shadow-inner border border-orange-100 relative">
             <div class="absolute inset-0 rounded-[2.5rem] bg-orange-400 animate-ping opacity-10"></div>
             <svg class="w-12 h-12 relative z-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v13m0-13V6a2 2 0 112 2h-2zm0 0V5.5A2.5 2.5 0 109.5 8H12zm-7 4h14M5 12a2 2 0 110-4h14a2 2 0 110 4M5 12v7a2 2 0 002 2h10a2 2 0 002-2v-7" /></svg>
          </div>
          <h3 class="text-3xl font-black text-slate-900 mb-4 tracking-tight leading-none">{{ $t('admin.loyalty.premiumTitle') }}</h3>
          <p class="text-slate-500 font-medium text-lg leading-relaxed mb-10">{{ $t('admin.loyalty.premiumDesc') }}</p>
          <NuxtLink :to="localePath('/admin/upgrade')" class="w-full inline-flex items-center justify-center px-10 py-5 bg-brand-600 text-white font-black text-sm uppercase tracking-[0.2em] rounded-[1.5rem] hover:bg-brand-700 shadow-xl shadow-brand-500/30 active:scale-95 transition-all">
            {{ $t('admin.upgrade.button') }}
          </NuxtLink>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-10">
        <!-- Prize Configuration List -->
        <div class="lg:col-span-7 space-y-6" :class="currentPlan !== 'PRO' ? 'opacity-20 grayscale blur-[2px] pointer-events-none' : ''">
          <div class="bg-white rounded-[3rem] border border-slate-100 shadow-xl shadow-slate-200/50 p-10 overflow-hidden relative">
            <h3 class="text-xl font-black text-slate-900 mb-8 flex items-center gap-3">
              {{ $t('admin.loyalty.prizesTitle') }}
              <div class="w-1.5 h-1.5 rounded-full bg-brand-500"></div>
            </h3>

            <div class="space-y-4">
              <div v-for="(p, i) in campaign.prizes" :key="i" class="p-6 bg-slate-50/50 rounded-[2rem] border border-slate-100 group transition-all hover:bg-white hover:shadow-xl hover:shadow-slate-200/40 hover:border-brand-100">
                <div class="flex items-center gap-6">
                  <!-- Slice Preview & Color Picker -->
                  <div class="relative shrink-0">
                    <div class="w-16 h-16 rounded-2xl flex items-center justify-center font-black text-white text-xl shadow-lg transition-transform group-hover:scale-110" :style="{ backgroundColor: p.color }">
                      {{ i + 1 }}
                    </div>
                    <div class="absolute -right-2 -bottom-2 w-8 h-8 rounded-full border-4 border-white shadow-md overflow-hidden bg-white">
                       <input type="color" v-model="p.color" class="absolute inset-0 w-[200%] h-[200%] -translate-x-1/4 -translate-y-1/4 cursor-pointer scale-150 border-none p-0 outline-none">
                    </div>
                  </div>
                  
                  <div class="flex-grow">
                    <div class="flex items-center justify-between mb-4">
                      <span class="font-black text-slate-900 uppercase tracking-tight text-lg leading-none">
                        {{ p.label }}
                      </span>
                      <span class="px-3 py-1 bg-white text-slate-400 text-[10px] font-black rounded-lg border border-slate-100 uppercase tracking-widest shadow-sm">
                        {{ p.type }}
                      </span>
                    </div>
                    
                    <!-- Content Config -->
                    <div class="flex flex-col sm:flex-row items-start sm:items-center gap-4">
                      <div v-if="p.type === 'discount'" class="relative group/input w-full sm:w-auto">
                        <input 
                          type="number" 
                          v-model.number="p.discountPercent" 
                          class="w-full sm:w-28 bg-white border border-slate-200 rounded-xl pl-4 pr-10 py-2.5 text-base font-black text-slate-900 focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all shadow-inner" 
                        >
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 font-black text-lg">%</span>
                      </div>
                      <p class="text-xs font-bold text-slate-400 leading-tight">
                        {{ p.type === 'discount' ? 'Müşterinin bir sonraki siparişinde uygulanacak indirim oranı.' : 'Müşteriye hediye edilecek sürpriz ödül içeriği.' }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="mt-10 p-6 bg-brand-50 rounded-3xl border border-brand-100 flex items-start gap-4">
               <div class="w-10 h-10 rounded-xl bg-white text-brand-600 flex items-center justify-center shrink-0 shadow-sm border border-brand-100">
                  <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
               </div>
               <p class="text-xs font-bold text-brand-700 leading-relaxed">
                  <strong>PRO İPUCU:</strong> Her dilimin olasılığı (weight) varsayılan olarak eşittir. "Pas" dilimi hariç diğer tüm ödüller müşterileriniz için birer kazanma heyecanı yaratır!
               </p>
            </div>
          </div>
        </div>

        <!-- Live Wheel Preview Dashboard -->
        <div class="lg:col-span-5" :class="currentPlan !== 'PRO' ? 'opacity-20 grayscale blur-[2px] pointer-events-none' : ''">
          <div class="bg-slate-900 rounded-[3rem] p-10 sticky top-10 overflow-hidden shadow-2xl shadow-slate-900/40 border border-slate-800 transition-all hover:scale-[1.01] duration-500">
            <div class="absolute top-0 right-0 w-80 h-80 bg-brand-500/20 blur-[100px] rounded-full animate-pulse-slow"></div>
            <div class="absolute bottom-0 left-0 w-64 h-64 bg-indigo-500/10 blur-[80px] rounded-full"></div>

            <div class="relative z-10 flex flex-col items-center">
              <div class="w-full flex justify-between items-center mb-10">
                 <h3 class="text-xl font-black text-white tracking-tight">{{ $t('admin.loyalty.preview') }}</h3>
                 <div class="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-xl border border-white/10">
                    <div class="w-2 h-2 rounded-full bg-orange-500 animate-ping"></div>
                    <span class="text-[9px] font-black text-white uppercase tracking-widest">LIVE RENDER</span>
                 </div>
              </div>
              
              <!-- Physical Wheel Preview -->
              <div class="relative w-72 h-72 sm:w-80 sm:h-80 mx-auto mb-12">
                 <!-- Outer Ring -->
                 <div class="absolute inset-[-15px] rounded-full border-[8px] border-white/5 shadow-2xl"></div>
                 <div class="absolute inset-[-5px] rounded-full border-2 border-white/10"></div>
                 
                 <div class="relative w-full h-full rounded-full border-[10px] border-white shadow-[0_0_50px_rgba(249,115,22,0.3)] overflow-hidden transition-transform duration-700">
                    <svg viewBox="0 0 100 100" class="w-full h-full transform -rotate-90">
                      <defs>
                        <filter id="preview-shadow">
                          <feDropShadow dx="0.5" dy="0.5" stdDeviation="0.5" />
                        </filter>
                      </defs>
                      <g v-for="(p, i) in campaign.prizes" :key="i">
                        <path :d="getPieSlice(i, campaign.prizes.length)" :fill="p.color || '#334155'" />
                        <line x1="50" y1="50" :x2="getSliceLineX(i, campaign.prizes.length)" :y2="getSliceLineY(i, campaign.prizes.length)" stroke="rgba(255,255,255,0.15)" stroke-width="0.5" />
                      </g>
                      <!-- Center Pin Decor -->
                      <circle cx="50" cy="50" r="10" fill="white" class="shadow-2xl" />
                      <circle cx="50" cy="50" r="7" fill="#f8fafc" />
                      <text x="50" y="52" text-anchor="middle" font-size="2.5" font-weight="950" fill="#f97316" class="tracking-widest">FEASY</text>
                    </svg>
                 </div>
                 
                 <!-- Pointer -->
                 <div class="absolute -top-4 left-1/2 -translate-x-1/2 z-20">
                    <div class="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-2xl border-4 border-orange-500 scale-90">
                       <svg class="w-6 h-6 text-orange-600 transform rotate-180" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2L22 20H2L12 2Z"/></svg>
                    </div>
                 </div>
              </div>

              <!-- Quick Info Cards -->
              <div class="grid grid-cols-2 gap-4 w-full relative z-10">
                <div class="bg-white/5 rounded-[2rem] p-6 border border-white/10 backdrop-blur-md">
                   <p class="text-[9px] font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.loyalty.totalPrizes') }}</p>
                   <p class="text-2xl font-black text-white tracking-tighter">{{ campaign.prizes.length }} Dilim</p>
                </div>
                <div class="bg-white/5 rounded-[2rem] p-6 border border-white/10 backdrop-blur-md">
                   <p class="text-[9px] font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.loyalty.maxDiscount') }}</p>
                   <p class="text-2xl font-black text-brand-400 tracking-tighter">%{{ maxDiscount }} İndirim</p>
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

definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()
const localePath = useLocalePath()
const { isProPlan } = useTenant()

const currentPlan = computed(() => {
  const p = authStore.user?.tenant?.subscriptionPlan || authStore.tenantConfig?.plan || 'FREE'
  return String(p).toUpperCase()
})
const isPro = computed(() => currentPlan.value === 'PRO')

const campaign = ref({
  active: false,
  prizes: [] as any[]
})

const maxDiscount = computed(() => {
  if (!campaign.value.prizes || campaign.value.prizes.length === 0) return 0
  const values = campaign.value.prizes.map(p => Number(p.discountPercent || 0))
  return Math.max(...values)
})

const saving = ref(false)

onMounted(async () => {
  if (currentPlan.value !== 'PRO') {
    campaign.value.prizes = getDefaultPrizes()
    return
  }
  try {
    const data = await fetchJson('/api/admin/loyalty/campaign')
    if (data) {
      campaign.value.active = data.active
      const savedPrizes = data.prizesJson ? JSON.parse(data.prizesJson) : []
      campaign.value.prizes = mergeWithDefaults(savedPrizes)
    } else {
      campaign.value.prizes = getDefaultPrizes()
    }
  } catch (e) {
    campaign.value.prizes = getDefaultPrizes()
  }
})

function mergeWithDefaults(saved: any[]) {
  const defaults = getDefaultPrizes()
  return defaults.map(def => {
    const match = saved.find(s => s.type === def.type)
    return match ? { ...def, ...match, label: def.label } : def
  })
}

async function saveSettings() {
  if (!isPro.value) return
  saving.value = true
  try {
    await fetchJson('/api/admin/loyalty/campaign', {
      method: 'POST',
      body: JSON.stringify({
        active: campaign.value.active,
        prizesJson: JSON.stringify(campaign.value.prizes)
      })
    })
    uiStore.success(t('admin.loyalty.success') || 'Ayarlar kaydedildi')
  } catch (e) {
    uiStore.error(t('admin.loyalty.error') || 'Hata oluştu')
  } finally {
    saving.value = false
  }
}

function getDefaultPrizes() {
  return [
    { type: 'discount', label: t('admin.loyalty.prizes.percentageDiscount') || 'İndirim', discountPercent: 15, color: '#f97316', weight: 1 },
    { type: 'freeDrink', label: t('admin.loyalty.prizes.freeDrink') || 'Ücretsiz İçecek', discountPercent: 0, color: '#fbbf24', weight: 1 },
    { type: 'surpriseDessert', label: t('admin.loyalty.prizes.surpriseDessert') || 'Sürpriz Tatlı', discountPercent: 0, color: '#ec4899', weight: 1 },
    { type: 'pass', label: t('admin.loyalty.prizes.pass') || 'Pas', discountPercent: 0, color: '#94a3b8', weight: 1 }
  ]
}

// Wheel Render Helpers
function getPieSlice(index: number, total: number) {
  if (total === 0) return ''
  const sliceAngle = 360 / total
  const startAngle = index * sliceAngle
  const endAngle = (index + 1) * sliceAngle
  const start = polarToCartesian(50, 50, 50, startAngle)
  const end = polarToCartesian(50, 50, 50, endAngle)
  const largeArcFlag = sliceAngle <= 180 ? "0" : "1"
  return ["M", 50, 50, "L", start.x, start.y, "A", 50, 50, 0, largeArcFlag, 1, end.x, end.y, "Z"].join(" ")
}

function polarToCartesian(centerX: number, centerY: number, radius: number, angleInDegrees: number) {
  const angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0
  return {
    x: centerX + (radius * Math.cos(angleInRadians)),
    y: centerY + (radius * Math.sin(angleInRadians))
  }
}

function getSliceLineX(index: number, total: number) { return polarToCartesian(50, 50, 50, (index * 360) / total).x }
function getSliceLineY(index: number, total: number) { return polarToCartesian(50, 50, 50, (index * 360) / total).y }

useHead({ title: () => `${t('admin.loyalty.title')} | Admin` })
</script>

<style scoped>
@keyframes pulse-slow {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50% { opacity: 0.3; transform: scale(1.1); }
}
.animate-pulse-slow { animation: pulse-slow 5s infinite ease-in-out; }

@keyframes up {
  from { opacity: 0; transform: translateY(40px) scale(0.9); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.animate-in-up { animation: up 0.7s cubic-bezier(0.16, 1, 0.3, 1); }
</style>