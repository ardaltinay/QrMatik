<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden">
    <!-- Background effects -->
    <div class="absolute inset-0 bg-transparent"></div>

    <div class="relative mx-auto max-w-4xl px-6">
      <div class="text-center mb-16">
        <h1 class="text-4xl sm:text-5xl font-black text-slate-900 mb-6 uppercase tracking-tight">
          {{ $t('about.title') }}
        </h1>
        <p class="text-lg text-slate-500 leading-relaxed max-w-2xl mx-auto font-medium">
          {{ $t('about.description') }}
        </p>
      </div>

      <div class="grid gap-8 md:grid-cols-2">
        <!-- Highlights -->
        <div class="bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-6">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-brand-600 text-white shadow-lg shadow-brand-600/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h2 class="text-xl font-black text-slate-900 uppercase tracking-tight">{{ $t('about.highlightsTitle') }}</h2>
          </div>
          <ul class="space-y-4">
            <li v-for="h in highlights" :key="h" class="flex items-start gap-3 text-sm">
              <svg class="w-5 h-5 text-brand-600 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
              <span class="text-slate-600 font-medium">{{ $t(h) }}</span>
            </li>
          </ul>
        </div>

        <!-- Story -->
        <div class="bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-6">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-500 text-white shadow-lg shadow-emerald-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <h2 class="text-xl font-black text-slate-900 uppercase tracking-tight">{{ $t('about.storyTitle') }}</h2>
          </div>
          <p class="text-sm leading-relaxed text-slate-600 font-medium">
            {{ $t('about.storyDesc') }}
          </p>
        </div>

        <!-- Security -->
        <div class="bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-6">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-amber-500 text-white shadow-lg shadow-amber-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
            </div>
            <h2 class="text-xl font-black text-slate-900 uppercase tracking-tight">{{ $t('about.securityTitle') }}</h2>
          </div>
          <ul class="space-y-4">
            <li v-for="s in security" :key="s" class="flex items-start gap-3 text-sm">
              <svg class="w-5 h-5 text-amber-600 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" /></svg>
              <span class="text-slate-600 font-medium">{{ $t(s) }}</span>
            </li>
          </ul>
        </div>

        <!-- Support -->
        <div class="bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-6">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-cyan-500 text-white shadow-lg shadow-cyan-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 class="text-xl font-black text-slate-900 uppercase tracking-tight">{{ $t('about.supportTitle') }}</h2>
          </div>
          <ul class="space-y-4">
            <li v-for="s in support" :key="s" class="flex items-start gap-3 text-sm">
              <svg class="w-5 h-5 text-cyan-600 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              <span class="text-slate-600 font-medium" v-html="$t(s)"></span>
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
})
</script>
