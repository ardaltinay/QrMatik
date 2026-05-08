<template>
  <section id="faq" class="relative py-32 bg-white overflow-hidden">
    <!-- Decorative background elements -->
    <div class="absolute top-0 right-0 w-[800px] h-[800px] bg-brand-50/50 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3 -z-10"></div>
    <div class="absolute bottom-0 left-0 w-[600px] h-[600px] bg-slate-50 rounded-full blur-3xl translate-y-1/3 -translate-x-1/3 -z-10"></div>

    <div class="container-custom">
      <div class="flex flex-col lg:flex-row gap-16 lg:gap-24">
        
        <!-- Left Side: Title & Info -->
        <div class="w-full lg:w-1/3 shrink-0">
          <div class="sticky top-32">
            <div class="inline-flex items-center gap-2 bg-brand-50 rounded-full px-4 py-1.5 mb-6 border border-brand-100">
              <span class="w-2 h-2 rounded-full bg-brand-500"></span>
              <span class="text-xs font-bold uppercase tracking-widest text-brand-700">FAQ</span>
            </div>
            
            <h2 class="text-4xl sm:text-5xl font-black text-slate-900 mb-6 leading-tight tracking-tight">
              {{ $t('landing.faq.title') }}
            </h2>
            
            <p class="text-lg text-slate-500 font-medium mb-10 leading-relaxed">
              {{ $t('landing.faq.titleHighlight') }}
            </p>

            <!-- Support Card -->
            <div class="bg-slate-50 rounded-3xl p-8 border border-slate-100">
              <div class="w-12 h-12 rounded-2xl bg-white flex items-center justify-center shadow-sm border border-slate-100 mb-5">
                <MessageCircle class="w-6 h-6 text-brand-500" />
              </div>
              <h4 class="text-xl font-bold text-slate-900 mb-2">{{ locale === 'en' ? 'Still have questions?' : 'Hala sorularınız mı var?' }}</h4>
              <p class="text-sm text-slate-500 mb-6 font-medium leading-relaxed">{{ locale === 'en' ? 'Can\'t find the answer you\'re looking for? Please chat to our friendly team.' : 'Aradığınız cevabı bulamadınız mı? Destek ekibimize ulaşın.' }}</p>
              <a href="mailto:support@feasymenu.com" class="inline-flex items-center justify-center px-6 py-3.5 bg-white border border-slate-200 text-slate-700 font-bold rounded-xl hover:bg-slate-50 hover:border-slate-300 hover:shadow-sm transition-all w-full gap-2 group">
                <Mail class="w-4 h-4 text-slate-400 group-hover:text-brand-500 transition-colors" />
                {{ locale === 'en' ? 'Contact Support' : 'Destek İste' }}
              </a>
            </div>
          </div>
        </div>

        <!-- Right Side: Accordion -->
        <div class="w-full lg:w-2/3">
          <div class="space-y-4">
            <div v-for="(q, i) in faqKeys" :key="i" 
              class="group bg-white rounded-[2rem] overflow-hidden transition-all duration-300 border"
              :class="openIndex === i ? 'border-brand-200 shadow-xl shadow-brand-500/5' : 'border-slate-100 shadow-sm hover:border-brand-100 hover:shadow-md'"
            >
              <button @click="toggle(i)" class="w-full flex items-center justify-between p-6 sm:p-8 text-left focus:outline-none">
                <span class="text-lg sm:text-xl font-bold transition-colors pr-6" :class="openIndex === i ? 'text-brand-600' : 'text-slate-900 group-hover:text-brand-600'">
                  {{ $t(q.qKey) }}
                </span>
                
                <!-- Custom animated toggle icon (+ / -) -->
                <div class="relative w-10 h-10 rounded-full shrink-0 flex items-center justify-center transition-all duration-300 border"
                  :class="openIndex === i ? 'bg-brand-50 border-brand-200 text-brand-600' : 'bg-slate-50 border-slate-100 text-slate-400 group-hover:bg-brand-50 group-hover:text-brand-500 group-hover:border-brand-100'">
                  
                  <!-- Horizontal line (always visible) -->
                  <div class="absolute w-3.5 h-[2.5px] bg-current rounded-full transition-transform duration-300"></div>
                  <!-- Vertical line (rotates to horizontal when open) -->
                  <div class="absolute w-[2.5px] h-3.5 bg-current rounded-full transition-transform duration-300"
                    :class="openIndex === i ? 'rotate-90 opacity-0' : 'rotate-0 opacity-100'"></div>
                </div>
              </button>
              
              <!-- Smooth Height Animation with Grid -->
              <div 
                class="grid transition-all duration-300 ease-in-out"
                :class="openIndex === i ? 'grid-rows-[1fr] opacity-100' : 'grid-rows-[0fr] opacity-0'"
              >
                <div class="overflow-hidden">
                  <div class="px-6 sm:px-8 pb-6 sm:pb-8 pt-0">
                    <div class="h-px bg-gradient-to-r from-brand-100 to-transparent mb-6"></div>
                    <p class="text-base text-slate-500 leading-relaxed font-medium">
                      {{ $t(q.aKey) }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { MessageCircle, Mail } from 'lucide-vue-next'

const { locale } = useI18n()

const openIndex = ref<number | null>(0) // Open the first one by default!
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
/* Empty style block to prevent Vite HMR error from previous removed styles */
</style>
