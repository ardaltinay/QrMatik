<template>
  <section class="relative py-32 overflow-hidden">
    <div class="absolute inset-0 bg-gradient-to-b from-[#f4f5f1] via-[#edf0e8] to-[#e8ece3]"></div>
    <div class="absolute left-[-15%] top-[8%] h-80 w-80 rounded-full bg-brand-300/35 blur-3xl"></div>
    <div class="absolute right-[-10%] bottom-[-8%] h-96 w-96 rounded-full bg-slate-950/10 blur-3xl"></div>
    
    <div class="container-custom text-center relative z-10">
      <div class="max-w-4xl mx-auto panel-glass rounded-[2.5rem] p-8 sm:p-12">
      <h2 class="text-4xl sm:text-5xl font-bold text-slate-900 mb-8 max-w-2xl mx-auto leading-tight">
        {{ $t('landing.cta.title') }} <span class="text-brand-400">{{ $t('landing.cta.titleHighlight') }}</span> {{ $t('landing.cta.titleEnd') }}
      </h2>
      <p class="text-lg text-slate-600 mb-12 max-w-xl mx-auto font-medium">{{ $t('landing.cta.description') }}</p>
      
      <div class="flex flex-col sm:flex-row gap-4 sm:gap-6 justify-center mt-4">
        <NuxtLink :to="localePath('/signup/tenant')" class="px-12 py-5 rounded-2xl bg-slate-950 text-white font-bold text-lg hover:bg-brand-700 hover:scale-105 active:scale-95 transition-all shadow-xl shadow-black/20">
          {{ $t('landing.cta.primary') }}
        </NuxtLink>
        <a href="mailto:support@feasymenu.com" class="px-12 py-5 rounded-2xl bg-white border border-slate-200 text-slate-700 font-bold text-lg hover:bg-slate-50 transition-all shadow-sm">
          {{ $t('landing.cta.secondary') }}
        </a>
      </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
</script>
