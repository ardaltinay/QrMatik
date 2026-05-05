<script setup lang="ts">
const head = useLocaleHead({
  addDirAttribute: true,
  identifierAttribute: 'id',
  addSeoAttributes: true
})

const route = useRoute()
const canonicalUrl = computed(() => `https://feasymenu.com${route.path}`)

useHead({
  htmlAttrs: {
    lang: head.value.htmlAttrs?.lang,
    dir: head.value.htmlAttrs?.dir
  },
  link: [
    { rel: 'canonical', href: canonicalUrl },
    ...(head.value.link || []),
  ],
  meta: [
    ...(head.value.meta || []),
  ]
})
</script>

<template>
  <div class="app-root min-h-screen relative overflow-hidden">
    <!-- Prism Light Background -->
    <div class="prism-bg"></div>
    <div class="bg-noise"></div>

    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
    <ToastHub />
  </div>
</template>
