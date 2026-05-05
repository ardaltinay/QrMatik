<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden">
    <div class="absolute inset-0 bg-gradient-to-b from-rose-500/[0.03] via-transparent to-transparent"></div>

    <div class="relative mx-auto max-w-5xl px-6">
      <!-- Header -->
      <header class="text-center mb-20">
        <h1 class="text-4xl sm:text-5xl md:text-7xl font-black text-slate-900 mb-8 uppercase tracking-tighter leading-tight">
          {{ $t('restoranBarMenuLanding.titleLine1') }} <br />
          <span class="text-rose-600">{{ $t('restoranBarMenuLanding.titleLine2') }}</span>
        </h1>
        <p class="text-xl text-slate-500 max-w-3xl mx-auto font-medium leading-relaxed">
          {{ $t('restoranBarMenuLanding.description') }}
        </p>
      </header>

      <div class="grid gap-10 md:grid-cols-2 mb-20">
        <!-- Features -->
        <section class="bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-2xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-rose-600 text-white shadow-lg shadow-rose-600/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('restoranBarMenuLanding.featuresTitle') }}</h2>
          </div>
          <ul class="space-y-5">
            <li v-for="f in features" :key="f" class="flex items-start gap-4">
              <span class="text-rose-600 font-black text-xl leading-none mt-0.5">•</span>
              <span class="text-slate-600 font-medium leading-relaxed" v-html="$t(f)"></span>
            </li>
          </ul>
        </section>

        <!-- Quick Start -->
        <section class="bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-2xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-500 text-white shadow-lg shadow-emerald-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('restoranBarMenuLanding.quickStartTitle') }}</h2>
          </div>
          <ol class="space-y-5 list-decimal list-inside text-slate-600 font-medium leading-relaxed">
            <li v-for="q in quickStart" :key="q" v-html="$t(q)"></li>
          </ol>
        </section>
      </div>

      <!-- CTA -->
      <div class="text-center">
        <NuxtLink to="/signup/tenant"
          class="inline-flex items-center justify-center px-10 py-5 rounded-[1.5rem] bg-rose-600 text-white font-black text-xs uppercase tracking-[0.2em] shadow-2xl shadow-rose-600/30 hover:bg-rose-500 hover:-translate-y-1 transition-all duration-300 active:scale-95">
          {{ $t('landing.cta.primary') }}
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()

const features = ['restoranBarMenuLanding.feat1', 'restoranBarMenuLanding.feat2', 'restoranBarMenuLanding.feat3', 'restoranBarMenuLanding.feat4']
const quickStart = ['restoranBarMenuLanding.qs1', 'restoranBarMenuLanding.qs2', 'restoranBarMenuLanding.qs3']

useSeoMeta({
  title: () => t('restoranBarMenuLanding.metaTitle'),
  description: () => t('restoranBarMenuLanding.metaDesc'),
})

const baseUrl = 'https://feasymenu.com'
useHead({
  script: [
    {
      type: 'application/ld+json',
      children: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'BreadcrumbList',
        itemListElement: [
          {
            '@type': 'ListItem',
            position: 1,
            name: 'Anasayfa',
            item: baseUrl + '/',
          },
          {
            '@type': 'ListItem',
            position: 2,
            name: 'Restoran Bar Menü',
            item: baseUrl + '/restoran-bar-menu',
          },
        ],
      }),
    },
  ],
})
</script>
