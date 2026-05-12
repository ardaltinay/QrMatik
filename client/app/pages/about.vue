<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden bg-[#FAF9F6]">
    <!-- Ambient Backgrounds -->
    <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-brand-100/50 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3 -z-10"></div>
    <div class="absolute bottom-0 left-0 w-[500px] h-[500px] bg-slate-100 rounded-full blur-3xl translate-y-1/3 -translate-x-1/3 -z-10"></div>

    <div class="relative mx-auto max-w-5xl px-6">
      <div class="text-center mb-16">
        <div class="inline-flex items-center gap-2 bg-white rounded-full px-4 py-1.5 mb-6 border border-slate-100 shadow-sm">
          <span class="w-2 h-2 rounded-full bg-brand-500"></span>
          <span class="text-xs font-bold uppercase tracking-widest text-slate-500">{{ $i18n.locale === 'tr' ? 'Biz Kimiz' : 'Who We Are' }}</span>
        </div>
        <h1 class="text-4xl sm:text-5xl md:text-6xl font-black text-slate-900 mb-6 uppercase tracking-tight">
          {{ $t('about.title') }}
        </h1>
        <p class="text-lg md:text-xl text-slate-500 leading-relaxed max-w-2xl mx-auto font-medium">
          {{ $t('about.description') }}
        </p>
      </div>

      <!-- Asymmetrical Bento Grid -->
      <div class="grid gap-6 md:gap-8 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
        
        <!-- Story (Spans 2 columns, Brand Theme) -->
        <div class="md:col-span-2 lg:col-span-2 bg-gradient-to-br from-brand-500 to-brand-700 rounded-[2.5rem] p-8 md:p-10 shadow-xl shadow-brand-500/20 hover:-translate-y-1 transition-all duration-500 text-white relative overflow-hidden group">
          <div class="absolute -right-20 -bottom-20 w-64 h-64 bg-white/10 rounded-full blur-3xl group-hover:bg-white/20 transition-colors"></div>
          <div class="flex items-center gap-4 mb-8 relative z-10">
            <div class="flex h-14 w-14 items-center justify-center rounded-2xl bg-white/20 text-white backdrop-blur-sm border border-white/20 shadow-sm">
              <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <h2 class="text-2xl font-black text-white uppercase tracking-tight">{{ $t('about.storyTitle') }}</h2>
          </div>
          <p class="text-base md:text-lg leading-relaxed text-brand-50 font-medium relative z-10">
            {{ $t('about.storyDesc') }}
          </p>
        </div>

        <!-- Highlights (1 column, Clean White) -->
        <div class="bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/40 hover:-translate-y-1 transition-all duration-500">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-slate-50 text-slate-700 border border-slate-100 shadow-sm">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h2 class="text-xl font-black text-slate-900 uppercase tracking-tight">{{ $t('about.highlightsTitle') }}</h2>
          </div>
          <ul class="space-y-4">
            <li v-for="h in highlights" :key="h" class="flex items-start gap-3 text-sm group">
              <div class="flex-shrink-0 mt-0.5">
                <div class="w-5 h-5 rounded-full bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-400 group-hover:bg-slate-800 group-hover:text-white transition-all">
                  <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
                </div>
              </div>
              <span class="text-slate-600 font-medium">{{ $t(h) }}</span>
            </li>
          </ul>
        </div>

        <!-- Security (2 columns, Clean White) -->
        <div class="md:col-span-2 bg-white rounded-[2.5rem] p-8 md:p-10 border border-slate-100 shadow-xl shadow-slate-200/40 hover:-translate-y-1 transition-all duration-500">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-14 w-14 items-center justify-center rounded-2xl bg-amber-50 text-amber-500 border border-amber-100 shadow-sm">
              <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('about.securityTitle') }}</h2>
          </div>
          <div class="grid sm:grid-cols-2 gap-4">
            <div v-for="s in security" :key="s" class="flex items-start gap-3 p-4 rounded-2xl bg-slate-50 border border-slate-100 group hover:border-amber-200 transition-colors">
              <svg class="w-5 h-5 text-amber-500 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" /></svg>
              <span class="text-slate-700 font-medium text-sm leading-relaxed">{{ $t(s) }}</span>
            </div>
          </div>
        </div>

        <!-- Support (1 column, Dark Mode Contrast) -->
        <div class="bg-slate-950 text-white rounded-[2.5rem] p-8 border border-slate-800 shadow-2xl hover:-translate-y-1 transition-all duration-500 relative overflow-hidden group">
          <div class="absolute -right-10 -top-10 w-40 h-40 bg-brand-500/20 rounded-full blur-2xl group-hover:bg-brand-500/30 transition-colors"></div>
          <div class="flex items-center gap-4 mb-8 relative z-10">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-white/10 text-white border border-white/10 shadow-sm backdrop-blur-md">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 class="text-xl font-black text-white uppercase tracking-tight">{{ $t('about.supportTitle') }}</h2>
          </div>
          <ul class="space-y-4 relative z-10">
            <li v-for="s in support" :key="s" class="flex items-start gap-3 text-sm">
              <svg class="w-5 h-5 text-brand-400 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              <span class="text-slate-300 font-medium" v-html="$t(s)"></span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()

const highlights = [
  'about.hl1',
  'about.hl2',
  'about.hl3',
  'about.hl4',
  'about.hl5',
]

const security = [
  'about.sec1',
  'about.sec2',
  'about.sec3',
  'about.sec4',
]

const support = [
  'about.sup1',
  'about.sup2',
  'about.sup3',
]

useSeoMeta({
  title: () => t('about.metaTitle'),
  description: () => t('about.metaDesc'),
  ogTitle: () => t('about.metaTitle'),
  ogDescription: () => t('about.metaDesc'),
  ogImage: 'https://feasymenu.com/og-image.png',
  twitterCard: 'summary_large_image',
})
</script>
