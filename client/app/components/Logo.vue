<template>
  <div class="flex items-center gap-2 group" :class="className">
    <div 
      class="relative flex items-center justify-center shrink-0 transition-all duration-700"
      :class="[
        size === 'sm' ? 'w-8 h-8' : size === 'lg' ? 'w-16 h-16' : 'w-12 h-12',
        shadow ? 'drop-shadow-[0_4px_12px_rgba(255,116,74,0.15)]' : ''
      ]"
    >
      <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-full group-hover:scale-110 transition-transform">
        <!-- Fluid F Path -->
        <path d="M12 34C12 34 15 20 18 8C18 8 28 6 34 14" stroke="currentColor" stroke-width="4" stroke-linecap="round" class="text-slate-900 group-hover:text-brand-500 transition-colors"/>
        <path d="M16 22C16 22 26 20 30 25" stroke="currentColor" stroke-width="4" stroke-linecap="round" class="text-slate-700 group-hover:text-brand-500 transition-colors opacity-60"/>
        <!-- Accent Dot -->
        <circle cx="34" cy="14" r="3" fill="oklch(0.68 0.21 35)" :class="animate ? 'animate-pulse' : ''"/>
      </svg>
    </div>
    
    <div v-if="!hideText" class="flex flex-col">
      <span 
        class="font-black text-slate-900 tracking-[-0.02em] leading-none uppercase transition-colors"
        :class="[
          size === 'sm' ? 'text-lg' : size === 'lg' ? 'text-3xl' : 'text-2xl',
          dark ? 'text-white' : ''
        ]"
      >
        feasy<span class="text-brand-500">menu</span>
      </span>
      <span 
        v-if="showTagline" 
        class="text-[9px] font-bold text-slate-400 uppercase tracking-[0.4em] mt-1.5 leading-none"
      >
        {{ $t('nav.tagline') }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  size?: 'sm' | 'md' | 'lg'
  hideText?: boolean
  showTagline?: boolean
  className?: string
  shadow?: boolean
  animate?: boolean
  dark?: boolean
}>()
</script>
