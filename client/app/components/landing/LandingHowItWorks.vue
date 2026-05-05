<template>
  <section class="relative py-24 overflow-hidden">
    <div class="absolute inset-0 bg-transparent"></div>
    <div class="relative mx-auto max-w-7xl px-6">
      <div class="text-center mb-16">
        <div class="inline-flex items-center gap-2 glass rounded-full px-4 py-1.5 mb-4 border-slate-200 shadow-sm">
          <span class="text-xs font-bold uppercase tracking-wider text-accent-600">{{ $t('landing.howItWorks.badge') }}</span>
        </div>
        <h2 class="text-3xl sm:text-4xl font-black text-slate-900 mb-4">
          {{ $t('landing.howItWorks.title') }} <span class="text-brand-600">{{ $t('landing.howItWorks.titleHighlight') }}</span>
        </h2>
      </div>

      <div class="grid md:grid-cols-3 gap-8 relative">
        <div class="hidden md:block absolute top-16 left-[20%] right-[20%] h-px bg-gradient-to-r from-brand-500/10 via-accent-500/20 to-brand-500/10"></div>

        <div v-for="(step, i) in steps" :key="step.titleKey" class="relative text-center group">
          <div class="flex items-center justify-center mb-6">
            <div class="relative">
              <div class="flex h-14 w-14 items-center justify-center rounded-2xl bg-brand-600 text-white text-xl font-bold group-hover:scale-110 transition-transform duration-300 shadow-lg shadow-brand-600/20">
                {{ i + 1 }}
              </div>
              <div class="absolute inset-0 rounded-2xl bg-brand-500/20 blur-xl opacity-0 group-hover:opacity-100 transition-opacity"></div>
            </div>
          </div>
          <div class="bg-white border border-slate-100 rounded-3xl p-8 shadow-sm group-hover:shadow-xl group-hover:shadow-slate-200/50 transition-all duration-500">
            <div class="text-4xl mb-4 transform group-hover:scale-110 transition-transform">{{ $t(step.emojiKey) }}</div>
            <h3 class="text-xl font-bold text-slate-900 mb-3">{{ $t(step.titleKey) }}</h3>
            <p class="text-sm text-slate-500 leading-relaxed font-medium">{{ $t(step.descKey) }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const steps = [
  { emojiKey: 'landing.howItWorks.step1Emoji', titleKey: 'landing.howItWorks.step1Title', descKey: 'landing.howItWorks.step1Desc' },
  { emojiKey: 'landing.howItWorks.step2Emoji', titleKey: 'landing.howItWorks.step2Title', descKey: 'landing.howItWorks.step2Desc' },
  { emojiKey: 'landing.howItWorks.step3Emoji', titleKey: 'landing.howItWorks.step3Title', descKey: 'landing.howItWorks.step3Desc' },
]
</script>
