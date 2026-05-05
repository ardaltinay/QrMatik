<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden">
    <div class="absolute inset-0 bg-transparent"></div>

    <div class="relative mx-auto max-w-4xl px-6">
      <header class="text-center mb-20">
        <h1 class="text-4xl sm:text-5xl md:text-7xl font-black text-slate-900 mb-8 uppercase tracking-tighter">
          {{ $t('blog.title') }}
        </h1>
        <p class="text-lg text-slate-500 max-w-2xl mx-auto font-medium leading-relaxed">
          {{ $t('blog.description') }}
        </p>
      </header>

      <div v-if="pending" class="flex items-center justify-center min-h-[400px]">
        <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-brand-600"></div>
      </div>

      <div v-else-if="error" class="text-center py-20 bg-white rounded-[2.5rem] border border-slate-100 shadow-sm">
        <div class="w-16 h-16 bg-rose-50 text-rose-500 rounded-2xl flex items-center justify-center mx-auto mb-6">
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
        </div>
        <h2 class="text-xl font-bold text-slate-900 mb-2 uppercase tracking-tight">{{ $t('errors.serverError') }}</h2>
        <p class="text-slate-500 mb-8 font-medium">Sunucuya ulaşılamıyor, lütfen daha sonra tekrar deneyiniz.</p>
        <button @click="refresh" class="px-8 py-3 bg-brand-600 text-white font-bold rounded-2xl hover:bg-brand-700 transition-all uppercase tracking-widest text-xs">Tekrar Dene</button>
      </div>

      <div v-else class="grid gap-10">
        <div v-for="post in posts" :key="post.slug" 
          class="bg-white rounded-[2.5rem] p-8 md:p-12 border border-slate-100 shadow-2xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-500 group">
          <NuxtLink :to="`/blog/${post.slug}`" class="block">
            <h2 class="text-2xl md:text-3xl font-black text-slate-900 mb-4 group-hover:text-brand-600 transition-colors duration-300 tracking-tight">
              {{ locale === 'tr' ? post.titleTr : post.titleEn }}
            </h2>
            <div class="inline-flex px-3 py-1 rounded-lg bg-brand-50 text-[10px] font-black text-brand-600 uppercase tracking-widest mb-6">
              {{ formatDate(post.createdTime) }}
            </div>
            <p class="text-base text-slate-500 leading-relaxed mb-8 line-clamp-2 font-medium">
              {{ locale === 'tr' ? post.excerptTr : post.excerptEn }}
            </p>
            <div class="flex items-center text-sm font-black text-brand-600 uppercase tracking-widest group-hover:gap-3 transition-all">
              {{ $t('blog.readMore') }}
              <svg class="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                <path stroke-linecap="round" stroke-linejoin="round" d="M14 5l7 7m0 0l-7 7m7-7H3" />
              </svg>
            </div>
          </NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { locale, t } = useI18n()
const { fetchJson } = useApi()

const { data: posts, pending, error, refresh } = useAsyncData('blog-posts', () => 
  fetchJson('/api/public/blog')
)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString(locale.value === 'tr' ? 'tr-TR' : 'en-US', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

useSeoMeta({
  title: () => t('blog.metaTitle'),
  description: () => t('blog.metaDesc'),
})

useHead({
  script: [
    {
      type: 'application/ld+json',
      innerHTML: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'ItemList',
        itemListElement: posts.value?.map((post: any, index: number) => ({
          '@type': 'ListItem',
          position: index + 1,
          url: `https://feasymenu.com/blog/${post.slug}`
        })) || []
      })
    }
  ]
})
</script>
