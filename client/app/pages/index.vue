<template>
  <div class="landing-shell">
    <div class="pointer-events-none absolute inset-0 mesh-overlay opacity-40"></div>
    <LandingHero />
    <div aria-hidden="true" class="relative -mt-14 mb-6 h-24 overflow-hidden">
      <div class="absolute inset-x-0 top-0 h-full bg-gradient-to-b from-transparent via-white/70 to-[#f3f5f0]"></div>
      <div class="absolute left-1/3 top-2 h-24 w-24 rounded-full bg-brand-300/20 blur-2xl"></div>
      <div class="absolute right-1/4 bottom-0 h-20 w-20 rounded-full bg-slate-900/10 blur-2xl"></div>
    </div>
    <LandingHowItWorks />
    <LandingFeaturesBento />
    <LandingPricing />
    <!-- Solutions Section for Internal Linking -->
    <section class="relative py-28">
      <div class="pointer-events-none absolute inset-0">
        <div class="absolute left-[-10%] top-8 h-72 w-72 rounded-full bg-brand-300/30 blur-3xl"></div>
        <div class="absolute right-[-5%] bottom-0 h-80 w-80 rounded-full bg-slate-900/10 blur-3xl"></div>
      </div>
      <div class="max-w-7xl mx-auto px-6 relative z-10">
        <div class="text-center mb-20">
          <h2 class="section-heading mb-6">{{ $t('common.solutions') }}</h2>
          <p class="section-subheading max-w-2xl mx-auto">{{ $t('common.solutionsDesc') }}</p>
        </div>
        <div class="grid md:grid-cols-3 gap-8">
          <NuxtLink to="/qr-menu" class="group panel-glass rounded-[2.5rem] p-10 hover:-translate-y-2 transition-all duration-300">
            <div class="w-16 h-16 rounded-2xl bg-brand-50 text-brand-600 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform shadow-sm border border-brand-100">
              <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" /></svg>
            </div>
            <h3 class="text-2xl font-black text-slate-900 mb-3">{{ $t('common.qrMenuCard') }}</h3>
            <p class="text-slate-600 font-medium mb-6">{{ $t('common.qrMenuCardDesc') }}</p>
            <p class="mb-6 inline-flex items-center gap-2 rounded-full border border-brand-100 bg-brand-50/70 px-3 py-1 text-xs font-bold text-brand-700">
              <span class="h-1.5 w-1.5 rounded-full bg-brand-500"></span>
              {{ $t('common.qrMenuOutcome') }}
            </p>
            <span class="inline-flex items-center text-brand-600 font-bold group-hover:gap-3 transition-all gap-2">{{ $t('common.discover') }} <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg></span>
          </NuxtLink>
          <NuxtLink to="/dijital-menu" class="group panel-glass rounded-[2.5rem] p-10 hover:-translate-y-2 transition-all duration-300">
            <div class="w-16 h-16 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform shadow-sm border border-emerald-100">
              <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" /></svg>
            </div>
            <h3 class="text-2xl font-black text-slate-900 mb-3">{{ $t('common.digitalMenuCard') }}</h3>
            <p class="text-slate-600 font-medium mb-6">{{ $t('common.digitalMenuCardDesc') }}</p>
            <p class="mb-6 inline-flex items-center gap-2 rounded-full border border-emerald-100 bg-emerald-50/70 px-3 py-1 text-xs font-bold text-emerald-700">
              <span class="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
              {{ $t('common.digitalMenuOutcome') }}
            </p>
            <span class="inline-flex items-center text-emerald-600 font-bold group-hover:gap-3 transition-all gap-2">{{ $t('common.discover') }} <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg></span>
          </NuxtLink>
          <NuxtLink to="/restoran-bar-menu" class="group panel-glass rounded-[2.5rem] p-10 hover:-translate-y-2 transition-all duration-300">
            <div class="w-16 h-16 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform shadow-sm border border-rose-100">
              <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            <h3 class="text-2xl font-black text-slate-900 mb-3">{{ $t('common.restaurantBarCard') }}</h3>
            <p class="text-slate-600 font-medium mb-6">{{ $t('common.restaurantBarCardDesc') }}</p>
            <p class="mb-6 inline-flex items-center gap-2 rounded-full border border-rose-100 bg-rose-50/70 px-3 py-1 text-xs font-bold text-rose-700">
              <span class="h-1.5 w-1.5 rounded-full bg-rose-500"></span>
              {{ $t('common.restaurantBarOutcome') }}
            </p>
            <span class="inline-flex items-center text-rose-600 font-bold group-hover:gap-3 transition-all gap-2">{{ $t('common.discover') }} <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg></span>
          </NuxtLink>
        </div>
      </div>
    </section>
    <LandingFaq />
    <LandingCta />
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()

useSeoMeta({
  title: () => t('seo.homeTitle'),
  description: () => t('seo.homeDescription'),
  ogTitle: () => t('seo.homeTitle'),
  ogDescription: () => t('seo.ogDescription'),
  ogType: 'website',
  ogSiteName: 'feasymenu',
  twitterCard: 'summary_large_image',
})

useHead({
  script: [
    {
      type: 'application/ld+json',
      innerHTML: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'SoftwareApplication',
        name: 'feasymenu',
        operatingSystem: 'Any',
        applicationCategory: 'BusinessApplication',
        offers: {
          '@type': 'AggregateOffer',
          offerCount: '3',
          lowPrice: '0',
          highPrice: '299',
          priceCurrency: 'TRY'
        },
        description: t('seo.homeDescription'),
        aggregateRating: {
          '@type': 'AggregateRating',
          ratingValue: '4.9',
          ratingCount: '124'
        }
      }),
    },
  ],
})
</script>
