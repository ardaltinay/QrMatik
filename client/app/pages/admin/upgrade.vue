<template>
  <div class="p-6 md:p-8 max-w-5xl mx-auto">
    <!-- Header -->
    <div class="mb-10">
      <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.upgrade.title') }}</h1>
      <p class="text-slate-500 text-sm mt-1">{{ $t('admin.upgrade.subtitle') }}</p>
    </div>

    <!-- Current Plan Status -->
    <div class="bg-white border border-slate-200 rounded-2xl p-5 mb-8 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-4">
        <div class="w-10 h-10 rounded-xl flex items-center justify-center"
          :class="currentPlan === 'FREE' ? 'bg-slate-100' : 'bg-brand-50'"
        >
          <svg class="w-5 h-5" :class="currentPlan === 'FREE' ? 'text-slate-400' : 'text-brand-600'" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
        </div>
        <div>
          <p class="text-xs text-slate-400 font-medium uppercase tracking-wider mb-0.5">{{ $t('admin.upgrade.currentPlan') }}</p>
          <p class="text-base font-bold text-slate-900 uppercase tracking-tight">{{ $t(`admin.upgrade.plans.${currentPlan}`) }}</p>
        </div>
      </div>
      <div v-if="currentPlan === 'FREE'" class="flex items-center gap-2 bg-amber-50 border border-amber-100 text-amber-600 px-3 py-1.5 rounded-lg text-xs font-semibold">
        <span class="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse inline-block"></span>
        {{ $t('admin.upgrade.limitedFeatures') }}
      </div>
      <div v-else class="flex items-center gap-2 bg-emerald-50 border border-emerald-100 text-emerald-600 px-3 py-1.5 rounded-lg text-xs font-semibold">
        <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 inline-block"></span>
        {{ $t('admin.common.active') }}
      </div>
    </div>

    <!-- Billing Toggle -->
    <div class="flex items-center justify-center gap-4 mb-10">
      <span class="text-sm font-semibold transition-colors" :class="billingCycle === 'monthly' ? 'text-slate-900' : 'text-slate-400'">{{ $t('landing.pricing.monthly') }}</span>
      <button 
        @click="billingCycle = billingCycle === 'monthly' ? 'yearly' : 'monthly'"
        class="relative w-12 h-6 rounded-full bg-slate-200 transition-colors"
        :class="billingCycle === 'yearly' ? 'bg-brand-600' : 'bg-slate-200'"
      >
        <div 
          class="absolute top-1 left-1 w-4 h-4 rounded-full bg-white transition-transform duration-300 shadow"
          :style="{ transform: billingCycle === 'yearly' ? 'translateX(24px)' : 'translateX(0)' }"
        ></div>
      </button>
      <div class="flex items-center gap-2">
        <span class="text-sm font-semibold transition-colors" :class="billingCycle === 'yearly' ? 'text-slate-900' : 'text-slate-400'">{{ $t('landing.pricing.yearly') }}</span>
        <span class="px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-600 text-[10px] font-bold uppercase tracking-wider">
          {{ $t('landing.pricing.twoMonthsFree') }}
        </span>
      </div>
    </div>

    <!-- Pricing Grid -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div v-for="plan in plans" :key="plan.name"
        class="bg-white rounded-2xl p-6 border relative overflow-hidden flex flex-col transition-all duration-300"
        :class="[
          plan.popular 
            ? 'border-brand-500 shadow-xl shadow-brand-500/10 ring-1 ring-brand-500/20 md:-translate-y-3' 
            : 'border-slate-200 shadow-sm',
          plan.isCurrent ? 'opacity-75' : ''
        ]"
      >
        <div v-if="plan.popular" class="absolute top-0 inset-x-0 h-0.5 bg-gradient-to-r from-brand-400 to-brand-600"></div>
        <div v-if="plan.popular" class="absolute top-4 right-4 bg-brand-600 text-white text-[9px] font-black uppercase tracking-widest px-2.5 py-1 rounded-full">
          {{ $t('admin.upgrade.popular') }}
        </div>
        <div v-if="plan.isCurrent" class="absolute top-4 right-4 bg-emerald-50 text-emerald-600 text-[9px] font-black uppercase tracking-widest px-2.5 py-1 rounded-full border border-emerald-100">
          {{ $t('admin.upgrade.currentPlan') }}
        </div>

        <div class="mb-5">
          <h3 class="text-sm font-bold text-slate-400 uppercase tracking-widest mb-3">{{ plan.name }}</h3>
          <div class="flex items-baseline gap-1">
            <span class="text-3xl font-black text-slate-900">
              {{ billingCycle === 'yearly' ? plan.priceYearly : plan.priceMonthly }}
            </span>
            <span v-if="plan.name !== 'Ücretsiz'" class="text-slate-400 text-xs font-medium">
              / {{ billingCycle === 'yearly' ? $t('common.year') : $t('common.month') }}
            </span>
          </div>
        </div>

        <ul class="space-y-3 flex-1 mb-6">
          <li v-for="feature in plan.features" :key="feature" class="flex items-start gap-2.5 text-sm text-slate-600">
            <svg class="w-4 h-4 text-emerald-500 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
            {{ feature }}
          </li>
        </ul>

        <button 
          @click="handleUpgrade(plan)"
          :disabled="plan.isCurrent || upgrading === plan.name"
          class="w-full py-3 font-bold text-sm rounded-xl transition-all duration-200"
          :class="[
            plan.isCurrent 
              ? 'bg-slate-100 text-slate-400 cursor-default' 
              : (plan.popular 
                  ? 'bg-brand-600 text-white hover:bg-brand-500 shadow-md shadow-brand-600/20 hover:shadow-lg hover:shadow-brand-600/25' 
                  : 'bg-slate-900 text-white hover:bg-slate-700')
          ]"
        >
          <span v-if="upgrading === plan.name" class="flex items-center justify-center gap-2">
            <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path></svg>
            {{ $t('common.loading') }}
          </span>
          <span v-else-if="plan.isCurrent">{{ $t('admin.upgrade.currentPlan') }}</span>
          <span v-else>{{ $t('admin.upgrade.upgradeBtn') }}</span>
        </button>
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

const billingCycle = ref<'monthly' | 'yearly'>('monthly')
const plans = ref<any[]>([])
const loading = ref(true)
const upgrading = ref<string | null>(null)
const currentPlan = ref<string>('FREE')
const checkoutTenantCode = ref<string>('')
const checkoutStoreId = ref<string>('')

const currentPlanEmoji = undefined // unused, kept as placeholder

async function loadPricing() {
  try {
    loading.value = true
    
    // Load checkout info (includes current plan) and pricing data in parallel
    const [checkoutData, data] = await Promise.all([
      fetchJson('/api/billing/checkout/init'),
      fetchJson('/api/public/pricing')
    ])

    if (checkoutData) {
      currentPlan.value = checkoutData.currentPlan || 'FREE'
      checkoutTenantCode.value = checkoutData.tenantCode || ''
      checkoutStoreId.value = checkoutData.storeId || ''
    }

    if (data && data.tiers) {
      const isUSD = data.currency === 'USD'
      const symbol = isUSD ? '$' : (data.currency === 'TRY' ? '₺' : data.currency)
      const locale = isUSD ? 'en-US' : 'tr-TR'
      
      plans.value = data.tiers.map((tier: any) => ({
        name: tier.name,
        priceMonthly: symbol + tier.monthly.toLocaleString(locale, { minimumFractionDigits: 2 }),
        priceYearly: symbol + (tier.yearly || (tier.monthly * 10)).toLocaleString(locale, { minimumFractionDigits: 2 }),
        variantMonthly: tier.variantMonthly,
        variantYearly: tier.variantYearly,
        popular: tier.name === 'Standart',
        features: tier.features,
        isCurrent: currentPlan.value === 'FREE'
          ? tier.name === 'Ücretsiz'
          : currentPlan.value.toUpperCase() === tier.name.toUpperCase()
      }))
    }
  } catch (e) {
    console.error('Failed to load pricing', e)
  } finally {
    loading.value = false
  }
}

async function handleUpgrade(plan: any) {
  if (plan.name === 'Ücretsiz' || plan.isCurrent) return
  
  try {
    upgrading.value = plan.name
    const variantId = billingCycle.value === 'yearly' ? plan.variantYearly : plan.variantMonthly
    const checkoutUrl = `https://feasymenu.lemonsqueezy.com/checkout/buy/${variantId}?checkout[custom][tenant_code]=${checkoutTenantCode.value}`
    window.open(checkoutUrl, '_blank')
  } catch (e) {
    console.error('Upgrade failed', e)
  } finally {
    upgrading.value = null
  }
}

onMounted(() => {
  loadPricing()
})

useHead({
  title: () => `${t('admin.upgrade.title')} | Admin | feasymenu`
})
</script>
