<template>
  <section id="pricing" class="relative py-24">
    <div class="mx-auto max-w-7xl px-6">
      <div class="text-center mb-16">
        <h2 class="text-4xl sm:text-5xl font-black text-slate-900 mb-6">
          {{ $t('landing.pricing.title') }} 
          <span class="text-brand-600 italic">{{ $t('landing.pricing.titleHighlight') }}</span>
        </h2>
        <p class="text-slate-500 max-w-xl mx-auto font-medium mb-12">{{ $t('landing.pricing.description') }}</p>

        <!-- Billing Toggle -->
        <div class="flex items-center justify-center gap-4 mb-8">
          <span class="text-sm font-bold transition-colors" :class="billingCycle === 'monthly' ? 'text-slate-900' : 'text-slate-400'">{{ $t('landing.pricing.monthly') }}</span>
          <button 
            @click="billingCycle = billingCycle === 'monthly' ? 'yearly' : 'monthly'"
            class="relative w-14 h-7 rounded-full bg-slate-100 border border-slate-200 transition-colors p-1"
          >
            <div 
              class="absolute top-1 left-1 w-5 h-5 rounded-full bg-brand-600 transition-transform duration-300 shadow-lg shadow-brand-600/20"
              :style="{ transform: billingCycle === 'yearly' ? 'translateX(28px)' : 'translateX(0)' }"
            ></div>
          </button>
          <div class="flex items-center gap-2">
            <span class="text-sm font-bold transition-colors" :class="billingCycle === 'yearly' ? 'text-slate-900' : 'text-slate-400'">{{ $t('landing.pricing.yearly') }}</span>
            <span class="px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-600 text-[10px] font-black uppercase tracking-wider">
              {{ $t('landing.pricing.twoMonthsFree') }}
            </span>
          </div>
        </div>
      </div>

      <div class="grid gap-8 md:grid-cols-3 max-w-6xl mx-auto items-end">
        <div v-for="plan in plans" :key="plan.nameKey"
          class="relative flex flex-col transition-all duration-500 hover:-translate-y-2"
          :class="[
            plan.popular 
              ? 'bg-slate-900 p-8 shadow-2xl shadow-slate-900/20 z-10 scale-105 rounded-[2.5rem] border border-slate-800' 
              : 'bg-white p-8 rounded-3xl border border-slate-100 shadow-xl shadow-slate-200/40'
          ]">
          
          <div v-if="plan.popular" class="absolute -top-4 left-1/2 -translate-x-1/2">
            <div class="bg-brand-500 text-white text-[10px] font-black uppercase tracking-[0.2em] px-5 py-1.5 rounded-full shadow-xl shadow-brand-500/20">
              {{ $t('landing.pricing.popular') }}
            </div>
          </div>

          <div class="mb-8">
            <h3 class="text-xl font-black mb-2 uppercase tracking-tight" :class="plan.popular ? 'text-white' : 'text-slate-900'">{{ plan.isDynamic ? plan.nameKey : $t(plan.nameKey) }}</h3>
            <p v-if="plan.descKey" class="text-sm font-medium leading-relaxed" :class="plan.popular ? 'text-slate-400' : 'text-slate-500'">{{ $t(plan.descKey) }}</p>
          </div>

          <div class="mb-10">
            <div class="flex items-baseline gap-1">
              <span class="text-5xl font-black transition-all duration-300" :class="plan.popular ? 'text-white' : 'text-slate-900'">
                {{ billingCycle === 'yearly' ? plan.priceYearly : plan.priceMonthly }}
              </span>
              <span v-if="plan.nameKey !== 'Ücretsiz' && plan.nameKey !== 'landing.pricing.freeName'" 
                class="text-sm font-bold uppercase tracking-widest transition-colors" 
                :class="plan.popular ? 'text-slate-500' : 'text-slate-300'">
                / {{ billingCycle === 'yearly' ? $t('common.year') : $t('common.month') }}
              </span>
            </div>
          </div>

          <ul class="space-y-4 mb-10 flex-1">
            <li v-for="f in plan.featureKeys" :key="f" class="flex items-start gap-3 text-sm group">
              <div class="w-5 h-5 rounded-full flex items-center justify-center shrink-0 mt-0.5 transition-colors" :class="plan.popular ? 'bg-white/10' : 'bg-brand-50'">
                <svg class="w-3 h-3" :class="plan.popular ? 'text-brand-400' : 'text-brand-600'" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="4">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <span class="font-medium transition-colors" :class="plan.popular ? 'text-slate-300' : 'text-slate-600'">{{ plan.isDynamic ? f : $t(f) }}</span>
            </li>
          </ul>

          <NuxtLink to="/signup/tenant"
            class="block w-full text-center py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all duration-300"
            :class="plan.popular 
              ? 'bg-white text-slate-900 hover:bg-brand-50 shadow-xl' 
              : 'bg-slate-50 text-slate-700 hover:bg-brand-600 hover:text-white border border-slate-100 hover:border-brand-600 shadow-sm'">
            {{ plan.popular ? $t('landing.pricing.choosePlan') : $t('landing.pricing.getStarted') }}
          </NuxtLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const { fetchJson } = useApi()

const billingCycle = ref<'monthly' | 'yearly'>('monthly')
const plans = ref<any[]>([])
const loading = ref(true)

async function loadPricing() {
  try {
    loading.value = true
    const data = await fetchJson('/api/public/pricing')
    if (data && data.tiers) {
      const isUSD = data.currency === 'USD'
      const symbol = isUSD ? '$' : (data.currency === 'TRY' ? '₺' : data.currency)
      const locale = isUSD ? 'en-US' : 'tr-TR'
      
      plans.value = data.tiers.map((tier: any) => ({
        nameKey: tier.name,
        descKey: '',
        priceMonthly: symbol + tier.monthly.toLocaleString(locale, { minimumFractionDigits: 2 }),
        priceYearly: symbol + (tier.yearly || (tier.monthly * 10)).toLocaleString(locale, { minimumFractionDigits: 2 }),
        popular: tier.name === 'Standart',
        featureKeys: tier.features,
        isDynamic: true
      }))
    }
  } catch (e) {
    console.error('Failed to load pricing', e)
    // Fallback to static if API fails
    plans.value = [
      {
        nameKey: 'landing.pricing.freeName', descKey: 'landing.pricing.freeDesc',
        priceMonthly: '$0', priceYearly: '$0', popular: false,
        featureKeys: ['landing.pricing.feature50Items', 'landing.pricing.feature10Tables', 'landing.pricing.featureQrCode', 'landing.pricing.featureDigitalMenu', 'landing.pricing.featureOrderTracking', 'landing.pricing.featureAdminPanel'],
      },
      {
        nameKey: 'landing.pricing.standardName', descKey: 'landing.pricing.standardDesc',
        priceMonthly: '$14.99', priceYearly: '$149.99', popular: true,
        featureKeys: ['landing.pricing.feature500Items', 'landing.pricing.feature50Tables', 'landing.pricing.featureKitchenBar', 'landing.pricing.featureLogo', 'landing.pricing.featureReports', 'landing.pricing.featurePrioritySupport'],
      },
      {
        nameKey: 'landing.pricing.proName', descKey: 'landing.pricing.proDesc',
        priceMonthly: '$29.99', priceYearly: '$299.99', popular: false,
        featureKeys: ['landing.pricing.featureUnlimitedItems', 'landing.pricing.featureUnlimitedTables', 'landing.pricing.featureStock', 'landing.pricing.featureCustomDomain', 'landing.pricing.featureAdvancedReports', 'landing.pricing.featureAllStandard'],
      },
    ]
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPricing()
})
</script>
