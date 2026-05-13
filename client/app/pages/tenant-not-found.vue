<template>
  <div class="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4">
    <div class="max-w-md w-full bg-white rounded-3xl p-8  border border-slate-100 text-center">
      
      <!-- Icon -->
      <div class="w-20 h-20 bg-rose-50 rounded-2xl flex items-center justify-center mx-auto mb-6">
        <svg class="w-10 h-10 text-rose-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      </div>

      <h1 class="text-2xl font-bold text-slate-900 mb-3">{{ $t('admin.actions.tenantNotFound.title') }}</h1>
      <p class="text-slate-500 text-sm mb-8 leading-relaxed">
        <span class="block font-medium mb-1">{{ $t('admin.actions.tenantNotFound.subtitle') }}</span>
        {{ $t('admin.actions.tenantNotFound.desc') }}
      </p>

      <NuxtLink 
        :to="localePath('/')" 
        class="inline-flex items-center justify-center gap-2 w-full px-6 py-3.5 bg-brand-600 text-white font-semibold rounded-xl hover:bg-brand-700 transition-colors shadow-lg "
      >
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
        {{ $t('admin.actions.tenantNotFound.cta') }}
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'blank'
})

const { t } = useI18n()
const localePath = useLocalePath()

useHead({
  title: () => `${t('admin.actions.tenantNotFound.title')}`
})
</script>
