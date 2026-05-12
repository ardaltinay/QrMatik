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

useHead({
  script: [
    {
      type: 'application/ld+json',
      innerHTML: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'Organization',
        name: 'feasymenu',
        url: 'https://feasymenu.com',
        logo: 'https://feasymenu.com/favicon.svg',
        contactPoint: {
          '@type': 'ContactPoint',
          email: 'support@feasymenu.com',
          contactType: 'customer support',
          availableLanguage: ['Turkish', 'English']
        },
        sameAs: [
          'https://twitter.com/feasymenu',
          'https://linkedin.com/company/feasymenu'
        ]
      })
    }
  ]
})
</script>
