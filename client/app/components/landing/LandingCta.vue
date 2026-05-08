<template>
  <section class="relative py-32 bg-white overflow-hidden">
    <!-- Decorative curves -->
    <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-[120%] h-[300px] bg-brand-50 rounded-t-[50%] -z-10 opacity-50"></div>
    
    <div class="container-custom text-center relative z-10">
      <h2 class="text-4xl sm:text-5xl font-bold text-slate-900 mb-8 max-w-2xl mx-auto leading-tight">
        {{ $t('landing.cta.title') }} <span class="text-brand-400">{{ $t('landing.cta.titleHighlight') }}</span> {{ $t('landing.cta.titleEnd') }}
      </h2>
      <p class="text-lg text-slate-400 mb-12 max-w-xl mx-auto font-medium">{{ $t('landing.cta.description') }}</p>
      
      <div class="flex flex-col sm:flex-row gap-4 sm:gap-6 justify-center mt-4">
        <NuxtLink :to="localePath('/signup/tenant')" class="px-12 py-5 rounded-2xl bg-slate-950 text-white font-bold text-lg hover:bg-brand-600 hover:scale-105 active:scale-95 transition-all shadow-xl shadow-black/20">
          {{ $t('landing.cta.primary') }}
        </NuxtLink>
        <a href="mailto:support@feasymenu.com" class="px-12 py-5 rounded-2xl bg-white border border-slate-200 text-slate-600 font-bold text-lg hover:bg-slate-50 transition-all">
          {{ $t('landing.cta.secondary') }}
        </a>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
</script>
