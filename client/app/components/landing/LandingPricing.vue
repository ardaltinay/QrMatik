<template>
  <section id="pricing" class="py-24 relative overflow-hidden bg-[#f3f5f0]">
    <div class="absolute inset-0 pointer-events-none">
      <div class="absolute left-[-12%] top-12 h-72 w-72 rounded-full bg-brand-300/25 blur-3xl"></div>
      <div class="absolute right-[-8%] bottom-0 h-80 w-80 rounded-full bg-slate-900/10 blur-3xl"></div>
    </div>
    <div class="container-custom">
      <div class="text-center mb-24">
        <h2 class="text-4xl sm:text-5xl font-black text-slate-900 mb-6 tracking-tight">
          {{ $t('landing.pricing.title') }}
        </h2>
        <p class="text-lg text-slate-600 max-w-xl mx-auto font-medium mb-12">
          {{ $t('landing.pricing.description') }}
        </p>

        <!-- Billing Toggle -->
        <div class="flex items-center justify-center gap-6">
          <span class="text-sm font-bold transition-colors" :class="billingCycle === 'monthly' ? 'text-slate-900' : 'text-slate-400'">{{ $t('landing.pricing.monthly') }}</span>
          <button 
            @click="billingCycle = billingCycle === 'monthly' ? 'yearly' : 'monthly'"
            class="relative w-16 h-8 rounded-full bg-slate-200/90 border border-slate-300/60 transition-colors p-1 shadow-inner"
          >
            <div 
              class="absolute top-1 left-1 w-6 h-6 rounded-full bg-brand-400 transition-transform duration-300 shadow-sm"
              :style="{ transform: billingCycle === 'yearly' ? 'translateX(32px)' : 'translateX(0)' }"
            ></div>
          </button>
          <span class="text-sm font-bold transition-colors" :class="billingCycle === 'yearly' ? 'text-slate-900' : 'text-slate-400'">{{ $t('landing.pricing.yearly') }}</span>
        </div>
      </div>

      <div class="grid gap-6 md:gap-8 grid-cols-1 md:grid-cols-3 max-w-6xl mx-auto items-stretch">
        <div v-for="plan in plans" :key="plan.nameKey"
          class="panel-glass rounded-[2.5rem] md:rounded-[3rem] p-6 sm:p-8 md:p-10 border flex flex-col transition-all duration-500 hover:shadow-2xl"
          :class="plan.popular
            ? 'md:-translate-y-3 md:scale-[1.02] border-brand-300/80 ring-2 ring-brand-400/90 ring-offset-4 shadow-2xl shadow-brand-400/20 bg-white'
            : 'border-slate-100/80 hover:shadow-brand-400/10'"
        >
          <div class="mb-6 md:mb-8 relative">
            <div v-if="plan.popular" class="absolute -top-10 -right-2 sm:-right-4 bg-brand-500 text-white text-[10px] font-black uppercase tracking-widest px-3 py-1 rounded-full shadow-lg shadow-brand-500/30">
              {{ $t('landing.pricing.mostPopular') }}
            </div>
            <h3 class="text-lg md:text-xl font-bold text-slate-900 mb-1">{{ plan.isDynamic ? plan.nameKey : $t(plan.nameKey) }}</h3>
            <p v-if="plan.popular" class="text-xs font-bold uppercase tracking-[0.16em] text-brand-700 mb-3">
              {{ $t('landing.pricing.bestValue') }}
            </p>
            <div class="flex items-baseline gap-1 mb-3 md:mb-4">
              <span class="text-3xl md:text-4xl font-bold text-slate-900">
                {{ billingCycle === 'yearly' ? plan.priceYearly : plan.priceMonthly }}
              </span>
              <span v-if="plan.nameKey !== 'landing.pricing.freeName'" class="text-sm font-bold text-slate-400">
                / {{ billingCycle === 'yearly' ? $t('common.year') : $t('common.month') }}
              </span>
            </div>
            <p class="text-sm text-slate-400 font-medium leading-relaxed">{{ $t(plan.descKey) }}</p>
          </div>

          <div class="h-px bg-slate-50 mb-6 md:mb-8"></div>

          <ul class="space-y-3 md:space-y-4 mb-8 md:mb-12 flex-1">
            <li v-for="f in plan.featureKeys" :key="f" class="flex items-start gap-3 text-sm">
              <svg class="w-5 h-5 text-brand-400 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
              </svg>
              <span class="font-medium text-slate-600">{{ plan.isDynamic ? f : $t(f) }}</span>
            </li>
          </ul>

          <NuxtLink :to="localePath('/signup/tenant')"
            class="block w-full text-center py-3.5 md:py-4 rounded-full font-bold text-sm md:text-base transition-all duration-300"
            :class="plan.popular 
              ? 'bg-brand-400 text-white hover:bg-brand-500 shadow-lg shadow-brand-400/20' 
              : 'bg-slate-100 text-slate-900 hover:bg-slate-950 hover:text-white'">
            {{ plan.popular ? $t('landing.pricing.choosePlan') : $t('landing.pricing.getStarted') }}
          </NuxtLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const { fetchJson } = useApi()
const localePath = useLocalePath()

const billingCycle = ref<'monthly' | 'yearly'>('monthly')
const plans = ref<any[]>([])
const loading = ref(true)

async function loadPricing() {
  try {
    loading.value = true
    const data = await fetchJson('/api/public/pricing')
    if (data && data.tiers && data.tiers.length > 0) {
      const isUSD = data.currency === 'USD'
      const symbol = isUSD ? '$' : (data.currency === 'TRY' ? '₺' : data.currency)
      const locale = isUSD ? 'en-US' : 'tr-TR'
      
      plans.value = data.tiers.map((tier: any) => {
        const name = (tier.name || '').toLowerCase();
        let featureKeys: string[] = [];
        let descKey = '';
        let nameKey = '';

        // Flexible matching for names
        if (name.includes('ücretsiz') || name.includes('free')) {
          nameKey = 'landing.pricing.freeName';
          descKey = 'landing.pricing.freeDesc';
          featureKeys = [
            'landing.pricing.features.tables20',
            'landing.pricing.features.products100',
            'landing.pricing.features.qrMenuBasic',
            'landing.pricing.features.kitchenBarBoards'
          ];
        } else if (name.includes('standart') || name.includes('standard')) {
          nameKey = 'landing.pricing.standardName';
          descKey = 'landing.pricing.standardDesc';
          featureKeys = [
            'landing.pricing.features.tables50',
            'landing.pricing.features.products500',
            'landing.pricing.features.kitchenBarBoards',
            'landing.pricing.features.branding',
            'landing.pricing.features.reports',
            'landing.pricing.features.geofencing',
            'landing.pricing.features.prioritySupport'
          ];
        } else if (name.includes('pro')) {
          nameKey = 'landing.pricing.proName';
          descKey = 'landing.pricing.proDesc';
          featureKeys = [
            'landing.pricing.features.productsUnlimited',
            'landing.pricing.features.tablesUnlimited',
            'landing.pricing.features.reportsAdvanced',
            'landing.pricing.features.unlimitedUsers',
            'landing.pricing.features.kitchenBarCashierBoards',
            'landing.pricing.features.reports',
            'landing.pricing.features.stockControl',
            'landing.pricing.features.loyaltySystem',
            'landing.pricing.features.prioritySupport'
          ];
        }

        return {
          nameKey,
          descKey,
          priceMonthly: symbol + (tier.monthly || 0).toLocaleString(locale, { minimumFractionDigits: 0 }),
          priceYearly: symbol + (tier.yearly || (tier.monthly * 10) || 0).toLocaleString(locale, { minimumFractionDigits: 0 }),
          popular: name.includes('standart') || name.includes('standard'),
          featureKeys,
          isDynamic: false
        };
      })
    }

    // If still empty after API check, trigger fallback
    if (!plans.value || plans.value.length === 0) {
      throw new Error('No pricing tiers found');
    }
  } catch (e) {
    console.error('Failed to load pricing', e)
    // Fallback to static if API fails or is empty
    plans.value = [
      {
        nameKey: 'landing.pricing.freeName', descKey: 'landing.pricing.freeDesc',
        priceMonthly: '₺0', priceYearly: '₺0', popular: false,
        featureKeys: [
          'landing.pricing.features.tables20',
          'landing.pricing.features.products100',
          'landing.pricing.features.qrMenuBasic',
          'landing.pricing.features.kitchenBarBoards'
        ],
      },
      {
        nameKey: 'landing.pricing.standardName', descKey: 'landing.pricing.standardDesc',
        priceMonthly: '$15', priceYearly: '$150', popular: true,
        featureKeys: [
          'landing.pricing.features.tables50',
          'landing.pricing.features.products500',
          'landing.pricing.features.kitchenBarBoards',
          'landing.pricing.features.branding',
          'landing.pricing.features.reports',
          'landing.pricing.features.geofencing',
          'landing.pricing.features.prioritySupport'
        ],
      },
      {
        nameKey: 'landing.pricing.proName', descKey: 'landing.pricing.proDesc',
        priceMonthly: '$30', priceYearly: '$300', popular: false,
        featureKeys: [
          'landing.pricing.features.productsUnlimited',
          'landing.pricing.features.tablesUnlimited',
          'landing.pricing.features.reportsAdvanced',
          'landing.pricing.features.unlimitedUsers',
          'landing.pricing.features.kitchenBarCashierBoards',
          'landing.pricing.features.reports',
          'landing.pricing.features.stockControl',
          'landing.pricing.features.loyaltySystem',
          'landing.pricing.features.prioritySupport'
        ],
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
