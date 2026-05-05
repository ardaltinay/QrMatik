<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden">
    <div class="absolute inset-0 bg-transparent"></div>

    <div class="relative mx-auto max-w-5xl px-6">
      <!-- Header -->
      <header class="text-center mb-20">
        <h1 class="text-4xl sm:text-5xl md:text-7xl font-black text-slate-900 mb-8 uppercase tracking-tighter leading-tight">
          {{ $t('dijitalMenuLanding.titleLine1') }} <br />
          <span class="text-brand-600">{{ $t('dijitalMenuLanding.titleLine2') }}</span>
        </h1>
        <p class="text-xl text-slate-500 max-w-3xl mx-auto font-medium leading-relaxed">
          {{ $t('dijitalMenuLanding.description') }}
        </p>
      </header>

      <div class="grid gap-10 md:grid-cols-2 mb-20">
        <!-- Features -->
        <section class="bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-2xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-brand-600 text-white shadow-lg shadow-brand-600/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('dijitalMenuLanding.featuresTitle') }}</h2>
          </div>
          <ul class="space-y-5">
            <li v-for="f in features" :key="f" class="flex items-start gap-4">
              <span class="text-brand-600 font-black text-xl leading-none mt-0.5">•</span>
              <span class="text-slate-600 font-medium leading-relaxed" v-html="$t(f)"></span>
            </li>
          </ul>
        </section>

        <!-- Quick Start -->
        <section class="bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-2xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-500 text-white shadow-lg shadow-emerald-500/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('dijitalMenuLanding.quickStartTitle') }}</h2>
          </div>
          <ol class="space-y-5 list-decimal list-inside text-slate-600 font-medium leading-relaxed">
            <li v-for="q in quickStart" :key="q" v-html="$t(q)"></li>
          </ol>
        </section>
      </div>

      <!-- FAQ accordion -->
      <section class="max-w-3xl mx-auto mb-20">
        <div class="bg-white rounded-[2rem] overflow-hidden transition-all duration-500 border border-slate-100 shadow-xl shadow-slate-200/40" :class="termsOpen ? 'ring-4 ring-brand-500/5' : ''">
          <button @click="termsOpen = !termsOpen" class="w-full flex items-center justify-between p-8 text-left">
            <span class="text-lg font-black text-slate-900 uppercase tracking-tight">{{ $t('dijitalMenuLanding.termsTitle') }}</span>
            <div class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 transition-all" :class="termsOpen ? 'rotate-180 bg-brand-50 text-brand-600' : ''">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
              </svg>
            </div>
          </button>
          <Transition name="accordion">
            <div v-if="termsOpen" class="px-8 pb-8">
              <ul class="space-y-4">
                <li v-for="t in terms" :key="t" class="flex items-start gap-4 text-sm text-slate-500 font-medium">
                  <span class="text-brand-400 font-black mt-0.5">•</span>
                  <span v-html="$t(t)"></span>
                </li>
              </ul>
            </div>
          </Transition>
        </div>
      </section>

      <!-- CTA -->
      <div class="text-center">
        <NuxtLink to="/signup/tenant"
          class="inline-flex items-center justify-center px-10 py-5 rounded-[1.5rem] bg-brand-600 text-white font-black text-xs uppercase tracking-[0.2em] shadow-2xl shadow-brand-600/30 hover:bg-brand-500 hover:-translate-y-1 transition-all duration-300 active:scale-95">
          {{ $t('landing.cta.primary') }}
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()
const termsOpen = ref(false)

const features = ['dijitalMenuLanding.feat1', 'dijitalMenuLanding.feat2', 'dijitalMenuLanding.feat3', 'dijitalMenuLanding.feat4']
const quickStart = ['dijitalMenuLanding.qs1', 'dijitalMenuLanding.qs2', 'dijitalMenuLanding.qs3', 'dijitalMenuLanding.qs4']
const terms = ['dijitalMenuLanding.term1', 'dijitalMenuLanding.term2', 'dijitalMenuLanding.term3']

useSeoMeta({
  title: () => t('dijitalMenuLanding.metaTitle'),
  description: () => t('dijitalMenuLanding.metaDesc'),
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
            name: 'Dijital menü nedir?',
            acceptedAnswer: {
              '@type': 'Answer',
              text: 'Dijital menü, telefonla görüntülenen çevrim içi menüdür. feasymenu ile menünüz her zaman güncel ve stokla entegredir.',
            },
          },
          {
            '@type': 'Question',
            name: 'Akıllı QR menü nasıl çalışır?',
            acceptedAnswer: {
              '@type': 'Answer',
              text: 'Masaya yerleştirilen QR kodu okutulur; müşteri menüyü görür, sipariş verir ve durumu canlı takip eder.',
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
