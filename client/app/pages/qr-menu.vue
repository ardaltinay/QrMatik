<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden bg-[#FAF9F6]">
    <!-- Ambient Backgrounds -->
    <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-brand-100/50 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3 -z-10"></div>
    <div class="absolute bottom-0 left-0 w-[500px] h-[500px] bg-slate-100 rounded-full blur-3xl translate-y-1/3 -translate-x-1/3 -z-10"></div>

    <div class="relative mx-auto max-w-5xl px-6">
      <!-- Header -->
      <header class="text-center mb-20">
        <div class="inline-flex items-center gap-2 bg-white rounded-full px-4 py-1.5 mb-6 border border-slate-100 shadow-sm">
          <span class="w-2 h-2 rounded-full bg-brand-500 animate-pulse"></span>
          <span class="text-xs font-bold uppercase tracking-widest text-slate-500">QR Menu</span>
        </div>
        <h1 class="text-4xl sm:text-5xl md:text-7xl font-black text-slate-900 mb-8 leading-tight uppercase tracking-tighter">
          {{ $t('qrMenuLanding.titleLine1') }} <br />
          <span class="text-brand-500">{{ $t('qrMenuLanding.titleLine2') }}</span>
        </h1>
        <p class="text-xl text-slate-500 leading-relaxed max-w-3xl mx-auto font-medium">
          {{ $t('qrMenuLanding.description') }}
        </p>
      </header>

      <div class="grid gap-8 md:grid-cols-2 mb-20">
        <!-- Features -->
        <section class="bg-white rounded-[2.5rem] p-8 sm:p-10 border border-slate-100 shadow-xl shadow-slate-200/40 hover:-translate-y-1 transition-all duration-500">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-brand-50 text-brand-600 shadow-sm border border-brand-100">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('qrMenuLanding.featuresTitle') }}</h2>
          </div>
          <ul class="space-y-6">
            <li v-for="f in features" :key="f" class="flex items-start gap-4 group">
              <div class="flex-shrink-0 mt-0.5">
                <div class="w-6 h-6 rounded-full bg-brand-50 border border-brand-100 flex items-center justify-center text-brand-500 group-hover:scale-110 group-hover:bg-brand-500 group-hover:text-white transition-all">
                  <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
                </div>
              </div>
              <span class="text-slate-600 font-medium leading-relaxed">
                <template v-if="$t(f).includes(':')">
                  <b class="font-black text-slate-900">{{ $t(f).split(':')[0] }}:</b>{{ $t(f).split(':').slice(1).join(':') }}
                </template>
                <template v-else>
                  {{ $t(f) }}
                </template>
              </span>
            </li>
          </ul>
        </section>

        <!-- Quick Start -->
        <section class="bg-white rounded-[2.5rem] p-8 sm:p-10 border border-slate-100 shadow-xl shadow-slate-200/40 hover:-translate-y-1 transition-all duration-500">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-50 text-emerald-600 shadow-sm border border-emerald-100">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('qrMenuLanding.quickStartTitle') }}</h2>
          </div>
          <ul class="space-y-6">
            <li v-for="(q, i) in quickStart" :key="q" class="flex items-start gap-4 group">
              <div class="flex-shrink-0 mt-0.5">
                <div class="w-7 h-7 rounded-lg bg-emerald-50 flex items-center justify-center text-emerald-600 font-black text-sm border border-emerald-100 group-hover:scale-110 group-hover:bg-emerald-500 group-hover:text-white transition-all shadow-sm">
                  {{ i + 1 }}
                </div>
              </div>
              <div class="text-slate-600 font-medium leading-relaxed mt-0.5" v-html="$t(q)"></div>
            </li>
          </ul>
        </section>
      </div>

      <!-- CTA -->
      <div class="text-center">
        <NuxtLink :to="localePath('/signup/tenant')"
          class="inline-flex items-center justify-center px-10 py-5 rounded-[1.5rem] bg-brand-600 text-white font-black text-xs uppercase tracking-[0.2em] shadow-2xl shadow-brand-600/30 hover:bg-brand-500 hover:-translate-y-1 transition-all duration-300 active:scale-95">
          {{ $t('landing.cta.primary') }}
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()
const localePath = useLocalePath()

const features = ['qrMenuLanding.feat1', 'qrMenuLanding.feat2', 'qrMenuLanding.feat3', 'qrMenuLanding.feat4']
const quickStart = ['qrMenuLanding.qs1', 'qrMenuLanding.qs2', 'qrMenuLanding.qs3', 'qrMenuLanding.qs4']
useSeoMeta({
  title: () => t('qrMenuLanding.metaTitle'),
  description: () => t('qrMenuLanding.metaDesc'),
  ogTitle: () => t('qrMenuLanding.metaTitle'),
  ogDescription: () => t('qrMenuLanding.metaDesc'),
  ogImage: 'https://feasymenu.com/og-qr-menu.png',
  twitterCard: 'summary_large_image',
})

useHead({
  script: [
    {
      type: 'application/ld+json',
      children: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'FAQPage',
        mainEntity: [
          {
            '@type': 'Question',
            name: t('qrMenuLanding.faq1Q'),
            acceptedAnswer: {
              '@type': 'Answer',
              text: t('qrMenuLanding.faq1A'),
            },
          },
          {
            '@type': 'Question',
            name: t('qrMenuLanding.faq2Q'),
            acceptedAnswer: {
              '@type': 'Answer',
              text: t('qrMenuLanding.faq2A'),
            },
          },
        ],
      }),
    },
  ],
})
</script>

<style scoped>
.accordion-enter-active { transition: all 0.3s ease-out; overflow: hidden; }
.accordion-leave-active { transition: all 0.2s ease-in; overflow: hidden; }
.accordion-enter-from, .accordion-leave-to { opacity: 0; max-height: 0; }
.accordion-enter-to, .accordion-leave-from { opacity: 1; max-height: 400px; }
</style>
