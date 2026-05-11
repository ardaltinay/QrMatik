<template>
  <div class="p-6 max-w-6xl mx-auto">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight">{{ $t('admin.loyalty.title') }}</h1>
        <p class="text-slate-500 font-medium">{{ $t('admin.loyalty.subtitle') }}</p>
      </div>
      <div v-if="isPro" class="flex items-center gap-3">
        <label class="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" v-model="campaign.active" class="sr-only peer">
          <div class="w-14 h-7 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[4px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-brand-600"></div>
          <span class="ml-3 text-sm font-bold text-slate-700">{{ campaign.active ? $t('admin.loyalty.active') : $t('admin.loyalty.inactive') }}</span>
        </label>
        <button 
          @click="saveSettings" 
          :disabled="saving"
          class="bg-brand-600 hover:bg-brand-700 disabled:opacity-50 text-white font-black px-8 py-3 rounded-2xl transition-all shadow-lg shadow-brand-600/20 active:scale-95 flex items-center gap-2"
        >
          <span v-if="saving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
          {{ saving ? $t('admin.common.saving') : $t('admin.loyalty.save') }}
        </button>
      </div>
    </div>

    <div class="relative">
      <!-- Premium Overlay for FREE/STANDARD Users -->
      <div v-if="currentPlan !== 'PRO'" class="absolute inset-0 z-50 backdrop-blur-[2px] bg-white/30 rounded-2xl flex items-center justify-center p-6 text-center">
        <div class="bg-white p-8 rounded-3xl shadow-xl border border-slate-200 max-w-md animate-in fade-in zoom-in duration-300">
          <div class="w-16 h-16 bg-brand-100 text-brand-600 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v13m0-13V6a2 2 0 112 2h-2zm0 0V5.5A2.5 2.5 0 109.5 8H12zm-7 4h14M5 12a2 2 0 110-4h14a2 2 0 110 4M5 12v7a2 2 0 002 2h10a2 2 0 002-2v-7" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-2">{{ $t('admin.loyalty.premiumTitle') }}</h3>
          <p class="text-slate-500 mb-8">{{ $t('admin.loyalty.premiumDesc') }}</p>
          <NuxtLink :to="localePath('/admin/upgrade')" class="inline-flex items-center justify-center px-8 py-3 bg-brand-500 text-white font-bold rounded-xl hover:bg-brand-600 transition-all shadow-lg shadow-brand-500/25">
            {{ $t('admin.upgrade.button') }}
          </NuxtLink>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
      <!-- Prize List -->
      <div class="lg:col-span-7 space-y-6">
        <div class="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/50 p-8">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-black text-slate-900">{{ $t('admin.loyalty.prizesTitle') }}</h3>
          </div>

          <div class="space-y-4">
            <div v-for="(p, i) in campaign.prizes" :key="i" class="p-5 bg-slate-50 rounded-2xl border border-slate-100 group transition-all hover:border-brand-200">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-xl flex items-center justify-center shrink-0 shadow-sm" :style="{ backgroundColor: p.color }">
                  <span class="text-white font-black text-sm">{{ i + 1 }}</span>
                </div>
                
                <div class="flex-grow">
                  <div class="flex items-center justify-between mb-2">
                    <span class="font-bold text-slate-900">
                      {{ p.type === 'discount' && p.discountPercent > 0 ? `%${p.discountPercent} ${p.label}` : p.label }}
                    </span>
                    <input type="color" v-model="p.color" class="w-8 h-8 rounded-lg cursor-pointer border-none bg-transparent">
                  </div>
                  
                  <!-- Discount Percentage Input - Only for the first prize -->
                  <div v-if="p.type === 'discount'" class="flex items-center gap-3">
                    <div class="relative flex-grow max-w-[150px]">
                      <input 
                        type="number" 
                        v-model.number="p.discountPercent" 
                        class="w-full bg-white border border-slate-200 rounded-xl px-4 py-2 text-sm font-bold focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" 
                        placeholder="%"
                      >
                      <span class="absolute right-3 top-2 text-slate-400 font-bold text-sm">%</span>
                    </div>
                    <span class="text-xs text-slate-500 font-medium">{{ $t('admin.loyalty.discountHint') || 'İndirim oranını belirleyin' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Preview -->
      <div class="lg:col-span-5">
        <div class="bg-slate-900 rounded-[2.5rem] p-8 sticky top-6 overflow-hidden">
          <div class="absolute top-0 right-0 w-64 h-64 bg-brand-500/10 blur-[100px] rounded-full"></div>
          <h3 class="text-xl font-black text-white mb-8 relative z-10">{{ $t('admin.loyalty.preview') }}</h3>
          
          <div class="relative w-64 h-64 mx-auto mb-8">
            <svg viewBox="0 0 100 100" class="w-full h-full transform -rotate-90">
              <path 
                v-for="(p, i) in campaign.prizes" 
                :key="i" 
                :d="getPieSlice(i, campaign.prizes.length)" 
                :fill="p.color || '#ddd'" 
              />
              <circle cx="50" cy="50" r="5" fill="white" />
            </svg>
            <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-12 h-12 bg-white rounded-full shadow-2xl flex items-center justify-center border-4 border-slate-900">
               <div class="w-2 h-2 bg-brand-600 rounded-full animate-ping"></div>
            </div>
            <div class="absolute -top-2 left-1/2 -translate-x-1/2 z-20">
              <div class="w-6 h-6 bg-white rotate-45 rounded-sm shadow-lg"></div>
            </div>
          </div>

          <div class="bg-white/5 rounded-3xl p-6 border border-white/10 backdrop-blur-sm relative z-10">
            <h4 class="text-white font-bold mb-4 text-sm uppercase tracking-widest opacity-60">{{ $t('admin.loyalty.summary') }}</h4>
            <div class="space-y-3">
              <div class="flex justify-between text-sm">
                <span class="text-slate-400">{{ $t('admin.loyalty.totalPrizes') }}:</span>
                <span class="text-white font-bold">{{ campaign.prizes.length }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-slate-400">{{ $t('admin.loyalty.maxDiscount') }}:</span>
                <span class="text-brand-400 font-bold">%{{ maxDiscount }}</span>
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
definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()

const currentPlan = computed(() => authStore.user?.tenant?.subscriptionPlan || 'FREE')
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
  try {
    const data = await fetchJson('/api/loyalty/campaign')
    if (data) {
      campaign.value.active = data.active
      const savedPrizes = data.prizesJson ? JSON.parse(data.prizesJson) : []
      // Merge with default 4 types to ensure structure is always correct
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
  saving.value = true
  try {
    await fetchJson('/api/loyalty/campaign', {
      method: 'POST',
      body: JSON.stringify({
        active: campaign.value.active,
        prizesJson: JSON.stringify(campaign.value.prizes)
      })
    })
    uiStore.success(t('admin.loyalty.success'))
  } catch (e) {
    uiStore.error(t('admin.loyalty.error'))
  } finally {
    saving.value = false
  }
}

function addPrize() {
  // Disabled as requested - fixed 4 fields
}

function removePrize(index: number) {
  // Disabled as requested - fixed 4 fields
}

function getDefaultPrizes() {
  return [
    { type: 'discount', label: t('admin.loyalty.prizes.percentageDiscount') || 'İndirim', discountPercent: 15, color: '#f97316', weight: 1 },
    { type: 'freeDrink', label: t('admin.loyalty.prizes.freeDrink'), discountPercent: 0, color: '#fbbf24', weight: 1 },
    { type: 'surpriseDessert', label: t('admin.loyalty.prizes.surpriseDessert'), discountPercent: 0, color: '#ec4899', weight: 1 },
    { type: 'pass', label: t('admin.loyalty.prizes.pass'), discountPercent: 0, color: '#94a3b8', weight: 1 }
  ]
}

function getPieSlice(index: number, total: number) {
  if (total === 0) return ''
  const sliceAngle = 360 / total
  const startAngle = index * sliceAngle
  const endAngle = (index + 1) * sliceAngle
  const start = polarToCartesian(50, 50, 45, startAngle)
  const end = polarToCartesian(50, 50, 45, endAngle)
  const largeArcFlag = sliceAngle <= 180 ? "0" : "1"
  return ["M", 50, 50, "L", start.x, start.y, "A", 45, 45, 0, largeArcFlag, 1, end.x, end.y, "Z"].join(" ")
}

function polarToCartesian(centerX: number, centerY: number, radius: number, angleInDegrees: number) {
  const angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0
  return {
    x: centerX + (radius * Math.cos(angleInRadians)),
    y: centerY + (radius * Math.sin(angleInRadians))
  }
}

const localePath = useLocalePath()
</script>