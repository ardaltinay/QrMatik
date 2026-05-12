<template>
  <div class="flex items-center gap-3 group" :class="className">
    <div 
      class="relative flex items-center justify-center shrink-0 transition-all duration-700"
      :class="[
        size === 'xs' ? 'w-8 h-8' : size === 'sm' ? 'w-10 h-10' : size === 'lg' ? 'w-24 h-24' : 'w-16 h-16',
      ]"
    >
      <!-- Enhanced Background Glow -->
      <div v-if="animate" class="absolute inset-0 bg-brand-400/30 blur-2xl rounded-full group-hover:bg-brand-500/40 transition-colors duration-700 animate-pulse"></div>

      <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-full relative z-10 group-hover:scale-110 transition-transform duration-500 ease-out">
        <!-- High Contrast F Path -->
        <path d="M14 34C14 34 16 18 20 8C20 8 28 6 34 12" stroke="#1A1A1A" stroke-width="5" stroke-linecap="round" class="group-hover:stroke-brand-600 transition-colors duration-500"/>
        <path d="M18 20C18 20 28 18 32 23" stroke="#1A1A1A" stroke-width="5" stroke-opacity="0.3" stroke-linecap="round" class="group-hover:stroke-brand-600 transition-colors duration-500"/>
        
        <!-- Vibrant Accent Dot -->
        <circle cx="34" cy="12" r="4" fill="#3D8E74" class="group-hover:fill-brand-600 transition-colors duration-500">
           <animate v-if="animate" attributeName="r" values="4;5;4" dur="2s" repeatCount="indefinite" />
        </circle>
      </svg>
    </div>
    
    <div v-if="!hideText" class="flex flex-col">
      <span 
        class="font-black text-slate-900 tracking-[-0.04em] leading-none transition-colors"
        :class="[
          size === 'xs' ? 'text-xl' : size === 'sm' ? 'text-2xl' : size === 'lg' ? 'text-5xl' : 'text-4xl',
          dark ? 'text-white' : ''
        ]"
      >
        feasy<span class="text-brand-600 group-hover:text-brand-700 transition-colors">menu</span>
      </span>
      <span 
        v-if="showTagline" 
        class="text-[10px] font-bold text-slate-500 uppercase tracking-[0.5em] mt-2 leading-none"
        :class="dark ? 'text-white/60' : 'text-slate-500'"
      >
        {{ $t('nav.tagline') }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  size?: 'xs' | 'sm' | 'md' | 'lg'
  hideText?: boolean
  showTagline?: boolean
  className?: string
  shadow?: boolean
  animate?: boolean
  dark?: boolean
}>()
</script>
