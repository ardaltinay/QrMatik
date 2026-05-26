<template>
  <div>
    <LandingHero />
    <LandingStats />
    <LandingHowItWorks />
    <LandingFeaturesBento />
    <LandingPricing />
    <!-- Solutions Section for Internal Linking -->
    <section class="py-24 bg-slate-50">
      <div class="max-w-7xl mx-auto px-6">
        <div class="text-center mb-20">
          <h2 class="text-4xl md:text-5xl font-black text-slate-900 mb-6">{{ $t('common.solutions') }}</h2>
          <p class="text-lg text-slate-500 max-w-2xl mx-auto font-medium">{{ $t('common.solutionsDesc') }}</p>
        </div>
        <div class="grid md:grid-cols-3 gap-8">
          <NuxtLink to="/qr-menu" class="group bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-lg hover:shadow-xl hover:-translate-y-2 transition-all duration-300">
            <div class="w-16 h-16 rounded-2xl bg-brand-50 text-brand-600 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
              <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" /></svg>
            </div>
            <h3 class="text-2xl font-black text-slate-900 mb-3">{{ $t('common.qrMenuCard') }}</h3>
            <p class="text-slate-500 font-medium mb-6">{{ $t('common.qrMenuCardDesc') }}</p>
            <span class="inline-flex items-center text-brand-600 font-bold group-hover:gap-3 transition-all gap-2">{{ $t('common.discover') }} <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg></span>
          </NuxtLink>
          <NuxtLink to="/dijital-menu" class="group bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-lg hover:shadow-xl hover:-translate-y-2 transition-all duration-300">
            <div class="w-16 h-16 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
              <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" /></svg>
            </div>
            <h3 class="text-2xl font-black text-slate-900 mb-3">{{ $t('common.digitalMenuCard') }}</h3>
            <p class="text-slate-500 font-medium mb-6">{{ $t('common.digitalMenuCardDesc') }}</p>
            <span class="inline-flex items-center text-emerald-600 font-bold group-hover:gap-3 transition-all gap-2">{{ $t('common.discover') }} <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" /></svg></span>
          </NuxtLink>
          <NuxtLink to="/restoran-bar-menu" class="group bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-lg hover:shadow-xl hover:-translate-y-2 transition-all duration-300">
            <div class="w-16 h-16 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
              <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            <h3 class="text-2xl font-black text-slate-900 mb-3">{{ $t('common.restaurantBarCard') }}</h3>
            <p class="text-slate-500 font-medium mb-6">{{ $t('common.restaurantBarCardDesc') }}</p>
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
