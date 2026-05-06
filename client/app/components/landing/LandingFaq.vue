<template>
  <section id="faq" class="relative py-32 bg-[#F9F9F9]">
    <div class="container-custom max-w-4xl mx-auto">
      <div class="text-center mb-24">
        <h2 class="text-4xl sm:text-5xl font-bold text-slate-900 mb-6">
          {{ $t('landing.faq.title') }}
        </h2>
        <p class="text-lg text-slate-400 font-medium">{{ $t('landing.faq.titleHighlight') }}</p>
      </div>

      <div class="space-y-6">
        <div v-for="(q, i) in faqKeys" :key="i" 
          class="bg-white rounded-[2rem] overflow-hidden transition-all duration-500 border border-slate-100 shadow-sm"
          :class="{'ring-1 ring-brand-400': openIndex === i}"
        >
          <button @click="toggle(i)" class="w-full flex items-center cursor-pointer justify-between p-8 text-left group">
            <span class="text-lg font-bold text-slate-900 pr-8 transition-colors group-hover:text-brand-400">{{ $t(q.qKey) }}</span>
            <div class="w-10 h-10 rounded-full flex items-center justify-center shrink-0 transition-all duration-300"
              :class="openIndex === i ? 'bg-brand-400 text-white rotate-180' : 'bg-slate-50 text-slate-400'">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
              </svg>
            </div>
          </button>
          
          <div v-show="openIndex === i" class="px-8 pb-8 animate-fade-in">
            <div class="h-px bg-slate-50 mb-6"></div>
            <p class="text-base text-slate-500 leading-relaxed font-medium">
              {{ $t(q.aKey) }}
            </p>
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
.animate-fade-in {
  animation: fadeIn 0.4s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
