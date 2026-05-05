<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden">
    <div class="absolute inset-0 bg-transparent"></div>

    <div v-if="pending" class="flex items-center justify-center min-h-[400px]">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-brand-600"></div>
    </div>

    <div v-else-if="error" class="text-center py-20 px-6">
      <div class="w-16 h-16 bg-rose-50 text-rose-500 rounded-2xl flex items-center justify-center mx-auto mb-6">
        <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
      </div>
      <h2 class="text-xl font-bold text-slate-900 mb-2 uppercase tracking-tight">{{ $t('errors.serverError') }}</h2>
      <p class="text-slate-500 mb-8 font-medium max-w-xs mx-auto">Sunucuya ulaşılamıyor veya yazı bulunamadı.</p>
      <NuxtLink to="/blog" class="inline-flex items-center text-sm font-black text-brand-600 uppercase tracking-widest hover:gap-4 transition-all">
        {{ $t('blog.backToBlog') }}
      </NuxtLink>
    </div>

    <article v-else-if="post" class="relative mx-auto max-w-4xl px-6">
      <header class="mb-20 text-center">
        <div class="inline-flex px-4 py-1.5 rounded-xl bg-brand-50 text-[11px] font-black text-brand-600 uppercase tracking-[0.2em] mb-6">
          {{ formatDate(post.createdTime) }}
        </div>
        <h1 class="text-4xl sm:text-5xl md:text-7xl font-black text-slate-900 mb-8 uppercase tracking-tighter leading-tight">
          {{ locale === 'tr' ? post.titleTr : post.titleEn }}
        </h1>
        <p class="text-xl text-slate-500 leading-relaxed max-w-3xl mx-auto font-medium">
          {{ locale === 'tr' ? post.excerptTr : post.excerptEn }}
        </p>
      </header>

      <div 
        class="bg-white rounded-[3rem] p-10 md:p-16 shadow-2xl shadow-slate-200/50 border border-slate-100 prose prose-slate max-w-none prose-brand prose-p:text-slate-600 prose-p:leading-loose prose-h2:text-slate-900 prose-h2:font-black prose-h2:uppercase prose-h2:tracking-tight prose-strong:text-slate-900 prose-li:text-slate-600"
        v-html="locale === 'tr' ? post.contentTr : post.contentEn"
      >
      </div>

      <div class="mt-20 text-center">
        <NuxtLink to="/blog" class="inline-flex items-center text-sm font-black text-brand-600 uppercase tracking-widest hover:gap-4 transition-all group">
          <svg class="w-6 h-6 mr-3 group-hover:-translate-x-2 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          {{ $t('blog.backToBlog') }}
        </NuxtLink>
      </div>
    </article>

    <div v-else class="text-center py-20">
      <h2 class="text-2xl font-black text-slate-900 mb-4 uppercase tracking-tighter">Post not found</h2>
      <NuxtLink to="/blog" class="text-brand-600 font-bold uppercase tracking-widest text-xs">Return to Blog</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const { locale } = useI18n()
const { fetchJson } = useApi()

const { data: post, pending, error, refresh } = useAsyncData(`blog-${route.params.slug}`, () => 
  fetchJson(`/api/public/blog/${route.params.slug}`)
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
  title: () => (post.value ? (locale.value === 'tr' ? post.value.titleTr : post.value.titleEn) : 'Blog') + ' | feasymenu',
  description: () => post.value ? (locale.value === 'tr' ? post.value.excerptTr : post.value.excerptEn) : '',
})
</script>
