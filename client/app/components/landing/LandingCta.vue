<template>
  <section class="relative py-32 bg-white overflow-hidden">
    <!-- Decorative curves -->
    <div class="absolute bottom-0 left-1/2 -translate-x-1/2 w-[120%] h-[300px] bg-brand-50 rounded-t-[50%] -z-10 opacity-50"></div>
    
    <div class="container-custom text-center relative z-10">
      <h2 class="text-4xl sm:text-5xl font-bold text-slate-900 mb-8 max-w-2xl mx-auto leading-tight">
        {{ $t('landing.cta.title') }} <span class="text-brand-400">{{ $t('landing.cta.titleHighlight') }}</span> {{ $t('landing.cta.titleEnd') }}
      </h2>
      <p class="text-lg text-slate-400 mb-12 max-w-xl mx-auto font-medium">{{ $t('landing.cta.description') }}</p>
      
      <div class="flex flex-col sm:flex-row gap-6 justify-center">
        <NuxtLink to="/signup/tenant" class="btn-primary px-12">
          {{ $t('landing.cta.primary') }}
        </NuxtLink>
        <a href="mailto:support@feasymenu.com" class="btn-secondary px-12">
          {{ $t('landing.cta.secondary') }}
        </a>
      </div>
    </div>
  </section>
</template>
