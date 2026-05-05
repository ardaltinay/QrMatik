<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden">
    <div class="absolute inset-0 bg-transparent"></div>

    <div class="relative mx-auto max-w-5xl px-6">
      <!-- Header -->
      <header class="text-center mb-20">
        <h1 class="text-4xl sm:text-5xl md:text-7xl font-black text-slate-900 mb-8 leading-tight uppercase tracking-tighter">
          {{ $t('qrMenuLanding.titleLine1') }} <br />
          <span class="text-brand-600">{{ $t('qrMenuLanding.titleLine2') }}</span>
        </h1>
        <p class="text-xl text-slate-500 leading-relaxed max-w-3xl mx-auto font-medium">
          {{ $t('qrMenuLanding.description') }}
        </p>
      </header>

      <div class="grid gap-10 md:grid-cols-2 mb-20">
        <!-- Features -->
        <section class="bg-white rounded-[2.5rem] p-10 border border-slate-100 shadow-2xl shadow-slate-200/50 hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-4 mb-8">
            <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-brand-600 text-white shadow-lg shadow-brand-600/20">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('qrMenuLanding.featuresTitle') }}</h2>
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
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            </div>
            <h2 class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('qrMenuLanding.quickStartTitle') }}</h2>
          </div>
          <ol class="space-y-5 list-decimal list-inside text-slate-600 font-medium leading-relaxed">
            <li v-for="q in quickStart" :key="q" v-html="$t(q)"></li>
          </ol>
        </section>
      </div>

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

const features = ['qrMenuLanding.feat1', 'qrMenuLanding.feat2', 'qrMenuLanding.feat3', 'qrMenuLanding.feat4']
const quickStart = ['qrMenuLanding.qs1', 'qrMenuLanding.qs2', 'qrMenuLanding.qs3', 'qrMenuLanding.qs4']
useSeoMeta({
  title: () => t('qrMenuLanding.metaTitle'),
  description: () => t('qrMenuLanding.metaDesc'),
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
            name: 'Dijital QR menü nedir? Nasıl çalışır?',
            acceptedAnswer: {
              '@type': 'Answer',
              text: 'Dijital QR menü, masaya yerleştirilen karekodun telefonla okutulmasıyla açılan çevrim içi menüdür. feasymenu ile müşteriler menüden ürün seçip QR sipariş verir ve sipariş durumunu canlı takip eder.',
            },
          },
          {
            '@type': 'Question',
            name: 'QR restoran (restorant) çözümü hangi işletmeler için uygun?',
            acceptedAnswer: {
              '@type': 'Answer',
              text: 'Restoran, kafe ve barlar için uygundur. Karekod restoran menüsüyle sipariş hızlanır; mutfak/bar ekranlarıyla hazırlık ve servis akışı düzenlenir.',
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
