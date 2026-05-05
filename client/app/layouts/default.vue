<template>
  <div class="flex min-h-screen flex-col">
    <AppHeader />
    <main class="flex-1">
      <slot />
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const config = useRuntimeConfig()
const siteUrl = 'https://feasymenu.com'

useHead({
  link: [
    {
      rel: 'canonical',
      href: () => `${siteUrl}${route.path}`
    }
  ],
  script: [
    {
      type: 'application/ld+json',
      innerHTML: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'Organization',
        name: 'feasymenu',
        url: 'https://feasymenu.com',
        logo: 'https://feasymenu.com/favicon.svg',
        sameAs: [
          'https://twitter.com/feasymenu',
          'https://linkedin.com/company/feasymenu'
        ]
      })
    },
    {
      type: 'application/ld+json',
      innerHTML: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'SoftwareApplication',
        name: 'feasymenu',
        operatingSystem: 'Any',
        applicationCategory: 'BusinessApplication',
        offers: {
          '@type': 'Offer',
          price: '0',
          priceCurrency: 'TRY'
        }
      })
    }
  ]
})
</script>
