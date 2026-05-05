<template>
  <section class="relative py-24">
    <div class="mx-auto max-w-3xl px-6">
      <div class="text-center mb-16">
        <h2 class="text-3xl sm:text-4xl font-black text-slate-900 mb-4">
          {{ $t('landing.faq.title') }} 
          <span class="text-brand-600">{{ $t('landing.faq.titleHighlight') }}</span>
        </h2>
      </div>
      <div class="space-y-4">
        <div v-for="(q, i) in faqKeys" :key="i" 
          class="bg-white rounded-2xl overflow-hidden transition-all duration-500 hover:border-slate-200 border border-slate-100"
          :class="openIndex === i ? 'border-brand-500/30 shadow-2xl shadow-slate-200/50' : 'shadow-sm'">
          <button @click="toggle(i)" class="w-full flex items-center cursor-pointer justify-between p-6 text-left group">
            <span class="font-bold text-slate-900 pr-4 group-hover:text-brand-600 transition-colors">{{ $t(q.qKey) }}</span>
            <div class="w-8 h-8 rounded-full flex items-center justify-center transition-all duration-300"
              :class="openIndex === i ? 'bg-brand-600 text-white rotate-180' : 'bg-slate-50 text-slate-400'">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
              </svg>
            </div>
          </button>
          <div class="grid transition-all duration-300 ease-in-out" 
            :class="openIndex === i ? 'grid-rows-[1fr] opacity-100 pb-6' : 'grid-rows-[0fr] opacity-0'">
            <div class="overflow-hidden">
              <div class="px-6 text-base text-slate-500 leading-relaxed font-medium">
                {{ $t(q.aKey) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const openIndex = ref<number | null>(null)
function toggle(i: number) { openIndex.value = openIndex.value === i ? null : i }

const faqKeys = [
  { qKey: 'landing.faq.q1', aKey: 'landing.faq.a1' },
  { qKey: 'landing.faq.q2', aKey: 'landing.faq.a2' },
  { qKey: 'landing.faq.q3', aKey: 'landing.faq.a3' },
  { qKey: 'landing.faq.q4', aKey: 'landing.faq.a4' },
  { qKey: 'landing.faq.q5', aKey: 'landing.faq.a5' },
  { qKey: 'landing.faq.q6', aKey: 'landing.faq.a6' },
]
</script>

<style scoped>
/* Grid-based accordion for smooth transition without fixed max-height */
</style>
