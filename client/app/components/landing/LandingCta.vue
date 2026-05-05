<template>
  <section class="relative py-24 overflow-hidden">
    <div class="absolute inset-0 gradient-glow opacity-50"></div>
    <div class="relative mx-auto max-w-4xl px-6 text-center">
      <h2 class="text-3xl sm:text-4xl font-black text-slate-900 mb-6">
        {{ $t('landing.cta.title') }} <span class="text-brand-600">{{ $t('landing.cta.titleHighlight') }}</span> {{ $t('landing.cta.titleEnd') }}
      </h2>
      <p class="text-slate-500 mb-10 max-w-xl mx-auto font-medium">{{ $t('landing.cta.description') }}</p>
      <div class="flex flex-col sm:flex-row gap-4 justify-center">
        <NuxtLink to="/signup/tenant"
          class="px-8 py-4 rounded-2xl bg-brand-600 text-white font-bold text-sm uppercase tracking-widest hover:bg-brand-500 transition-all duration-300 shadow-xl shadow-brand-600/20">
          {{ $t('landing.cta.primary') }}
        </NuxtLink>
        <a href="mailto:hello@feasymenu.com"
          class="px-8 py-4 rounded-2xl bg-white border border-slate-200 text-slate-700 font-bold text-sm uppercase tracking-widest hover:bg-slate-50 transition-all duration-300">
          {{ $t('landing.cta.secondary') }}
        </a>
      </div>
    </div>
  </section>
</template>
