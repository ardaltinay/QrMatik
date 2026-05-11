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

    <div v-if="!isPro" class="bg-amber-50 border border-amber-200 rounded-3xl p-8 text-center">
      <div class="w-16 h-16 bg-amber-100 text-amber-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
        <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
      </div>
      <h3 class="text-xl font-bold text-slate-900 mb-2">{{ $t('admin.loyalty.proOnly') }}</h3>
      <p class="text-slate-600 mb-6 max-w-md mx-auto">{{ $t('admin.loyalty.upgradeMsg') }}</p>
      <NuxtLink :to="localePath('/admin/upgrade')" class="inline-flex bg-brand-600 text-white font-black px-8 py-3 rounded-xl hover:bg-brand-700 transition-all">
        {{ $t('admin.loyalty.upgradeBtn') }}
      </NuxtLink>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-12 gap-8">
      <!-- Prize List -->
      <div class="lg:col-span-7 space-y-6">
        <div class="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/50 p-8">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-black text-slate-900">{{ $t('admin.loyalty.prizesTitle') }}</h3>
            <button @click="addPrize" class="text-brand-600 hover:text-brand-700 font-bold text-sm flex items-center gap-2">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              {{ $t('admin.loyalty.addPrize') }}
            </button>
          </div>

          <div class="space-y-3">
            <div v-for="(p, i) in campaign.prizes" :key="i" class="flex items-center gap-4 p-4 bg-slate-50 rounded-2xl border border-slate-100 group">
              <div class="w-10 h-10 rounded-xl flex items-center justify-center shrink-0 shadow-sm" :style="{ backgroundColor: p.color }">
                <span class="text-white font-black text-xs">{{ i + 1 }}</span>
              </div>
              <div class="flex-grow grid grid-cols-12 gap-4">
                <div class="col-span-7">
                  <input v-model="p.label" class="w-full bg-white border border-slate-200 rounded-xl px-4 py-2 text-sm font-bold focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" :placeholder="$t('loyalty.label')">
                </div>
                <div class="col-span-5">
                  <div class="relative">
                    <input type="number" v-model="p.discountPercent" class="w-full bg-white border border-slate-200 rounded-xl px-4 py-2 text-sm font-bold focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" placeholder="%">
                    <span class="absolute right-3 top-2 text-slate-400 font-bold text-sm">%</span>
                  </div>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <input type="color" v-model="p.color" class="w-8 h-8 rounded-lg cursor-pointer border-none bg-transparent">
                <button @click="removePrize(i)" class="p-2 text-slate-300 hover:text-rose-500 transition-colors">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
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
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()

const isPro = computed(() => {
  const plan = String(authStore.user?.tenant?.subscriptionPlan || 'FREE').toUpperCase()
  return plan !== 'FREE'
})

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
      campaign.value.prizes = data.prizesJson ? JSON.parse(data.prizesJson) : getDefaultPrizes()
    } else {
      campaign.value.prizes = getDefaultPrizes()
    }
  } catch (e) {
    campaign.value.prizes = getDefaultPrizes()
  }
})

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
    uiStore.success(t('loyalty.success'))
  } catch (e) {
    uiStore.error(t('loyalty.error'))
  } finally {
    saving.value = false
  }
}

function addPrize() {
  campaign.value.prizes.push({ label: t('loyalty.newPrize'), discountPercent: 10, color: '#6366f1', weight: 1 })
}

function removePrize(index: number) {
  campaign.value.prizes.splice(index, 1)
}

function getDefaultPrizes() {
  return [
    { label: t('loyalty.prizes.discount15'), discountPercent: 15, color: '#f97316', weight: 1 },
    { label: t('loyalty.prizes.freeDrink'), discountPercent: 0, color: '#fbbf24', weight: 1 },
    { label: t('loyalty.prizes.discount10'), discountPercent: 10, color: '#8b5cf6', weight: 1 },
    { label: t('loyalty.prizes.surpriseDessert'), discountPercent: 0, color: '#ec4899', weight: 1 },
    { label: t('loyalty.prizes.discount20'), discountPercent: 20, color: '#10b981', weight: 1 },
    { label: t('loyalty.prizes.pass'), discountPercent: 0, color: '#94a3b8', weight: 1 }
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