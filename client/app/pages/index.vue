<template>
  <div>
    <LandingHero />
    <LandingFeatures />
    <LandingHowItWorks />
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
        '@type': 'WebApplication',
        name: 'feasymenu',
        applicationCategory: 'BusinessApplication',
        operatingSystem: 'Web',
        url: 'https://feasymenu.com/',
        description: t('seo.homeDescription'),
        offers: {
          '@type': 'Offer',
          priceCurrency: 'TRY',
          price: '249',
          category: 'STANDARD',
        },
      }),
    },
  ],
})
</script>
