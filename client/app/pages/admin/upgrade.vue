<template>
  <div class="p-4 md:p-8 max-w-6xl mx-auto space-y-12">
    <!-- Premium Header -->
    <div class="text-center space-y-4">
      <div class="inline-flex items-center gap-2 px-4 py-2 bg-brand-50 text-brand-600 rounded-full text-[10px] font-black uppercase tracking-[0.2em] shadow-sm border border-brand-100">
         <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
         Premium Üyeliğe Geç
      </div>
      <h1 class="text-4xl md:text-5xl font-black text-slate-900 tracking-tighter leading-none">{{ $t('admin.upgrade.title') }}</h1>
      <p class="text-slate-500 font-medium text-lg max-w-2xl mx-auto leading-relaxed">{{ $t('admin.upgrade.subtitle') }}</p>
    </div>

    <!-- Current Plan Bento -->
    <div class="bg-white/50 backdrop-blur-xl border border-white shadow-2xl shadow-slate-200/50 rounded-[2.5rem] p-8 flex flex-col md:flex-row items-center justify-between gap-8 relative overflow-hidden group">
      <div class="absolute -right-20 -top-20 w-64 h-64 bg-brand-500/5 blur-[100px] rounded-full group-hover:scale-150 transition-transform duration-1000"></div>
      
      <div class="flex items-center gap-6 relative z-10">
        <div class="w-20 h-20 rounded-[2rem] flex items-center justify-center shadow-inner border border-white"
          :class="currentPlan === 'FREE' ? 'bg-slate-100' : 'bg-brand-500 text-white'"
        >
          <svg v-if="currentPlan === 'FREE'" class="w-8 h-8 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
          <svg v-else class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" /></svg>
        </div>
        <div>
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.upgrade.currentPlan') }}</p>
          <h3 class="text-2xl font-black text-slate-900 uppercase tracking-tighter">{{ $t(`admin.upgrade.plans.${currentPlan}`) }}</h3>
        </div>
      </div>

      <div class="relative z-10">
        <div v-if="currentPlan === 'FREE'" class="flex items-center gap-3 bg-amber-500/10 border border-amber-200 text-amber-700 px-6 py-3 rounded-2xl text-xs font-black uppercase tracking-widest">
          <span class="w-2.5 h-2.5 rounded-full bg-amber-500 animate-ping"></span>
          {{ $t('admin.upgrade.limitedFeatures') }}
        </div>
        <div v-else class="flex items-center gap-3 bg-emerald-500/10 border border-emerald-200 text-emerald-700 px-6 py-3 rounded-2xl text-xs font-black uppercase tracking-widest">
          <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
          {{ $t('admin.common.active') }}
        </div>
      </div>
    </div>

    <!-- Pricing Selector -->
    <div class="flex flex-col items-center gap-12">
      <div class="inline-flex items-center p-2 bg-slate-100 rounded-[2rem] border border-slate-200/50 shadow-inner group">
        <button 
          @click="billingCycle = 'monthly'"
          class="px-10 py-4 rounded-[1.5rem] text-sm font-black uppercase tracking-widest transition-all duration-500"
          :class="billingCycle === 'monthly' ? 'bg-white text-slate-900 shadow-xl' : 'text-slate-400 hover:text-slate-600'"
        >
          {{ $t('landing.pricing.monthly') }}
        </button>
        <button 
          @click="billingCycle = 'yearly'"
          class="relative px-10 py-4 rounded-[1.5rem] text-sm font-black uppercase tracking-widest transition-all duration-500 overflow-hidden"
          :class="billingCycle === 'yearly' ? 'bg-white text-slate-900 shadow-xl' : 'text-slate-400 hover:text-slate-600'"
        >
          {{ $t('landing.pricing.yearly') }}
          <div class="absolute -top-1 -right-1" v-if="billingCycle !== 'yearly'">
             <span class="flex h-3 w-3"><span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span><span class="relative inline-flex rounded-full h-3 w-3 bg-emerald-500"></span></span>
          </div>
        </button>
      </div>

      <!-- Pricing Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 w-full">
        <div v-for="plan in plans" :key="plan.name"
          class="bg-white rounded-[3rem] p-10 border-2 flex flex-col transition-all duration-700 group/card relative"
          :class="[
            plan.popular 
              ? 'border-brand-500 shadow-[0_64px_128px_-24px_rgba(79,70,229,0.15)] md:-translate-y-6' 
              : 'border-slate-100 shadow-[0_32px_64px_-24px_rgba(0,0,0,0.08)] hover:shadow-[0_64px_128px_-24px_rgba(0,0,0,0.1)] hover:-translate-y-2',
            plan.isCurrent ? 'opacity-90 grayscale-[0.5]' : ''
          ]"
        >
          <!-- Premium Tags -->
          <div v-if="plan.popular" class="absolute -top-5 left-1/2 -translate-x-1/2 bg-brand-600 text-white text-[10px] font-black uppercase tracking-[0.3em] px-6 py-2.5 rounded-full shadow-xl shadow-brand-500/40 z-20">
            {{ $t('admin.upgrade.popular') }}
          </div>
          <div v-if="plan.isCurrent" class="absolute -top-5 left-1/2 -translate-x-1/2 bg-emerald-500 text-white text-[10px] font-black uppercase tracking-[0.3em] px-6 py-2.5 rounded-full shadow-xl shadow-emerald-500/40 z-20 border-2 border-white">
            {{ $t('admin.upgrade.currentPlan') }}
          </div>

          <!-- Price Header -->
          <div class="mb-10 text-center">
            <h3 class="text-sm font-black text-slate-400 uppercase tracking-[0.3em] mb-6">{{ plan.name }}</h3>
            <div class="flex flex-col items-center">
              <span class="text-6xl font-black text-slate-900 tracking-tighter mb-2">
                {{ billingCycle === 'yearly' ? plan.priceYearly : plan.priceMonthly }}
              </span>
              <span v-if="plan.name !== 'Ücretsiz'" class="text-slate-400 text-xs font-bold uppercase tracking-widest">
                {{ billingCycle === 'yearly' ? $t('common.year') : $t('common.month') }}
              </span>
            </div>
          </div>

          <!-- Features List -->
          <ul class="space-y-4 flex-1 mb-10">
            <li v-for="feature in plan.features" :key="feature" class="flex items-start gap-4 text-sm font-bold text-slate-600 group/item">
              <div class="w-6 h-6 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center shrink-0 border border-emerald-100 group-hover/card:scale-110 transition-transform">
                 <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
              </div>
              <span class="leading-relaxed">{{ feature }}</span>
            </li>
          </ul>

          <!-- Action Button -->
          <button 
            @click="handleUpgrade(plan)"
            :disabled="plan.isCurrent || upgrading === plan.name"
            class="w-full py-5 font-black text-xs uppercase tracking-[0.2em] rounded-[1.5rem] transition-all duration-500 relative overflow-hidden"
            :class="[
              plan.isCurrent 
                ? 'bg-slate-100 text-slate-400 cursor-not-allowed shadow-inner' 
                : (plan.popular 
                    ? 'bg-brand-600 text-white hover:bg-brand-700 shadow-2xl shadow-brand-500/40 hover:-translate-y-1' 
                    : 'bg-slate-900 text-white hover:bg-black shadow-xl shadow-slate-900/30 hover:-translate-y-1')
            ]"
          >
            <div v-if="upgrading === plan.name" class="flex items-center justify-center gap-3">
              <svg class="animate-spin w-5 h-5 text-white" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path></svg>
              {{ $t('common.loading') }}
            </div>
            <span v-else-if="plan.isCurrent">{{ $t('admin.upgrade.currentPlan') }}</span>
            <span v-else>{{ $t('admin.upgrade.upgradeBtn') }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const { fetchJson } = useApi()

const billingCycle = ref<'monthly' | 'yearly'>('monthly')
const plans = ref<any[]>([])
const loading = ref(true)
const upgrading = ref<string | null>(null)
const currentPlan = ref<string>('FREE')
const checkoutTenantCode = ref<string>('')

async function loadPricing() {
  try {
    loading.value = true
    const [checkoutData, pricingData] = await Promise.all([
      fetchJson('/api/billing/checkout/init').catch(() => null),
      fetchJson('/api/public/pricing').catch(err => {
        console.error('Pricing API error:', err)
        return null
      })
    ])

    if (checkoutData) {
      currentPlan.value = checkoutData.currentPlan || 'FREE'
      checkoutTenantCode.value = checkoutData.tenantCode || ''
    }

    // Match backend data or use homepage fallback
    const tiers = pricingData?.tiers || [
      { name: 'Ücretsiz', monthly: 0, yearly: 0 },
      { name: 'Standart', monthly: 15, yearly: 150 },
      { name: 'Pro', monthly: 30, yearly: 300 }
    ]

    const isUSD = pricingData ? pricingData.currency === 'USD' : true // Default to USD for fallback
    const symbol = isUSD ? '$' : '₺'
    const locale = isUSD ? 'en-US' : 'tr-TR'
    
    plans.value = tiers.map((tier: any) => {
      const name = tier.name.toLowerCase()
      let nameKey = 'admin.upgrade.plans.FREE'
      let features: string[] = []

      if (name.includes('standart') || name.includes('standard')) {
        nameKey = 'admin.upgrade.plans.STANDARD'
        features = [
          t('admin.upgrade.digitalMenu'),
          t('admin.upgrade.user10'),
          t('admin.upgrade.qrOrdering'),
          t('admin.upgrade.analytics')
        ]
      } else if (name.includes('pro')) {
        nameKey = 'admin.upgrade.plans.PRO'
        features = [
          t('admin.upgrade.proEverything'),
          t('admin.upgrade.stock'),
          t('admin.upgrade.kds'),
          t('admin.upgrade.loyalty'),
          t('admin.upgrade.support')
        ]
      } else {
        features = [
          t('admin.upgrade.user1'),
          t('admin.upgrade.basicQr'),
          t('admin.upgrade.limitedFeatures')
        ]
      }

      return {
        name: t(nameKey),
        rawName: tier.name,
        priceMonthly: symbol + (tier.monthly || 0).toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 2 }),
        priceYearly: symbol + (tier.yearly || (tier.monthly * 10) || 0).toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 2 }),
        variantMonthly: tier.variantMonthly,
        variantYearly: tier.variantYearly,
        popular: name.includes('standart') || name.includes('standard'),
        features: features,
        isCurrent: currentPlan.value.toUpperCase() === (name.includes('standart') ? 'STANDARD' : name.toUpperCase())
      }
    })
  } catch (e) {
    console.error('Pricing load error', e)
  } finally { loading.value = false }
}

async function handleUpgrade(plan: any) {
  if (plan.rawName === 'Ücretsiz' || plan.isCurrent) return
  upgrading.value = plan.name
  try {
    const variantId = billingCycle.value === 'yearly' ? plan.variantYearly : plan.variantMonthly
    if (!variantId) {
       console.error('No variant ID found for plan', plan)
       return
    }
    const checkoutUrl = `https://feasymenu.lemonsqueezy.com/checkout/buy/${variantId}?checkout[custom][tenant_code]=${checkoutTenantCode.value}`
    window.open(checkoutUrl, '_blank')
  } catch (e) {
    console.error('Upgrade failed', e)
  } finally { upgrading.value = null }
}

onMounted(loadPricing)
useHead({ title: () => `${t('admin.upgrade.title')} | Admin` })
</script>

<style scoped>
.shadow-2xl {
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
}
@keyframes pulse-slow {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}
.animate-pulse-slow { animation: pulse-slow 3s infinite ease-in-out; }
</style>
