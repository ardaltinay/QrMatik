<template>
  <section class="py-20 bg-slate-950 text-white overflow-hidden relative">
    <!-- Background Decor -->
    <div class="absolute top-0 left-1/4 w-96 h-96 bg-brand-500/20 blur-[120px] rounded-full"></div>
    <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-brand-600/10 blur-[120px] rounded-full"></div>

    <div class="container-custom relative z-10">
      <div class="grid grid-cols-2 md:grid-cols-4 gap-12 text-center">
        <div v-for="stat in stats" :key="stat.label" class="flex flex-col items-center">
          <div class="text-4xl md:text-6xl font-black mb-2 text-brand-400 tabular-nums">
            {{ stat.value }}
          </div>
          <div class="text-sm md:text-base font-bold text-slate-400 uppercase tracking-widest">
            {{ $t(stat.labelKey) }}
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const stats = [
  { value: '%45', labelKey: 'landing.stats.fasterService' },
  { value: '%30', labelKey: 'landing.stats.moreRevenue' },
  { value: '%100', labelKey: 'landing.stats.easy' },
  { value: '0', labelKey: 'landing.stats.noWait' },
]
</script>
