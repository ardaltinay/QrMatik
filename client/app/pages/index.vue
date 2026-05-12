<template>
  <div>
    <LandingHero />
    <LandingStats />
    <LandingHowItWorks />
    <LandingFeaturesBento />
    <LandingPricing />
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
