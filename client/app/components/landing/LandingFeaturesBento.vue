<template>
  <section class="py-24 bg-white">
    <div class="container-custom">
      <div class="text-center mb-20">
        <h2 class="text-4xl md:text-5xl font-black text-slate-900 mb-6">
          {{ $t('landing.features.bentoTitle') }}
        </h2>
        <p class="text-lg text-slate-500 max-w-2xl mx-auto font-medium">
          {{ $t('landing.features.bentoSubtitle') }}
        </p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-12 md:grid-rows-2 gap-6 h-auto min-h-[700px]">
        <!-- 1. Real-time Dashboard (Large) -->
        <div class="md:col-span-6 md:row-span-2 bg-slate-900 rounded-[2.5rem] p-8 sm:p-10 overflow-hidden relative group border border-slate-800 flex flex-col">
          <div class="relative z-10 flex flex-col h-full">
            <div class="w-14 h-14 rounded-2xl bg-brand-500 flex items-center justify-center mb-6 shadow-lg shadow-brand-500/20">
              <LayoutDashboard class="w-7 h-7 text-white" />
            </div>
            <h3 class="text-3xl font-bold text-white mb-4">{{ $t('landing.features.dashboardTitle') }}</h3>
            <p class="text-slate-400 font-medium leading-relaxed mb-8 max-w-sm">
              {{ $t('landing.features.dashboardDesc') }}
            </p>
            
            <!-- Realistic Admin Mockup -->
            <div class="mt-auto bg-slate-950 rounded-t-2xl border border-slate-800 p-4 shadow-2xl transform group-hover:translate-y-[-10px] transition-transform duration-500 relative overflow-hidden flex gap-4 h-[240px]">
              
              <!-- Mini Sidebar -->
              <div class="w-10 sm:w-12 border-r border-slate-800/80 flex flex-col gap-4 py-2 opacity-50 shrink-0">
                <div class="w-8 h-8 rounded-xl bg-brand-500/20 text-brand-400 flex items-center justify-center"><LayoutDashboard class="w-4 h-4"/></div>
                <div class="w-8 h-8 rounded-xl text-slate-500 flex items-center justify-center"><Utensils class="w-4 h-4"/></div>
                <div class="w-8 h-8 rounded-xl text-slate-500 flex items-center justify-center"><Users class="w-4 h-4"/></div>
                <div class="w-8 h-8 rounded-xl text-slate-500 flex items-center justify-center"><Settings class="w-4 h-4"/></div>
              </div>

              <!-- Main Content -->
              <div class="flex-1 flex flex-col gap-4 w-full">
                <!-- Top Stats -->
                <div class="flex gap-3">
                  <div class="flex-1 bg-slate-900 rounded-2xl p-3 sm:p-4 border border-slate-800/50 flex flex-col justify-between shadow-inner">
                    <span class="text-[9px] sm:text-[10px] text-slate-500 font-bold uppercase tracking-widest mb-1 truncate">{{ locale === 'en' ? 'Today Revenue' : 'Günlük Gelir' }}</span>
                    <span class="text-lg sm:text-xl font-black text-white">{{ locale === 'en' ? '$1,245.00' : '₺12.450' }}</span>
                  </div>
                  <div class="flex-1 bg-slate-900 rounded-2xl p-3 sm:p-4 border border-slate-800/50 flex flex-col justify-between shadow-inner">
                    <span class="text-[9px] sm:text-[10px] text-slate-500 font-bold uppercase tracking-widest mb-1 truncate">{{ locale === 'en' ? 'Active Orders' : 'Aktif Sipariş' }}</span>
                    <div class="flex items-center gap-2">
                      <span class="text-lg sm:text-xl font-black text-white">14</span>
                      <span class="text-[8px] sm:text-[9px] px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/20">+3</span>
                    </div>
                  </div>
                </div>

                <!-- Live Orders Board -->
                <div class="flex-1 bg-slate-900 rounded-2xl border border-slate-800/50 p-3 sm:p-4 overflow-hidden flex flex-col relative shadow-inner">
                  <div class="flex justify-between items-center mb-3">
                    <span class="text-xs font-bold text-white flex items-center gap-2">
                      <span class="w-2 h-2 rounded-full bg-rose-500 animate-pulse shadow-[0_0_8px_rgba(244,63,94,0.6)]"></span>
                      {{ locale === 'en' ? 'Live Kitchen Stream' : 'Canlı Mutfak Akışı' }}
                    </span>
                  </div>
                  
                  <div class="space-y-2">
                    <div class="flex items-center justify-between p-2.5 rounded-xl bg-slate-800 border border-slate-700/50 shadow-sm">
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-lg bg-amber-500/10 flex items-center justify-center text-sm border border-amber-500/20">🍔</div>
                        <div class="flex flex-col">
                          <span class="text-[11px] font-bold text-slate-200">{{ locale === 'en' ? 'Table' : 'Masa' }} 4</span>
                          <span class="text-[9px] text-slate-400 font-medium">{{ locale === 'en' ? '2 items • 3m ago' : '2 ürün • 3dk önce' }}</span>
                        </div>
                      </div>
                      <span class="text-[8px] sm:text-[9px] px-2.5 py-1 rounded-md bg-amber-500/20 text-amber-400 font-bold uppercase tracking-wider">{{ locale === 'en' ? 'Preparing' : 'Hazırlanıyor' }}</span>
                    </div>

                    <div class="flex items-center justify-between p-2.5 rounded-xl bg-slate-800 border border-slate-700/50 shadow-sm">
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-lg bg-emerald-500/10 flex items-center justify-center text-sm border border-emerald-500/20">☕️</div>
                        <div class="flex flex-col">
                          <span class="text-[11px] font-bold text-slate-200">{{ locale === 'en' ? 'Table' : 'Masa' }} 12</span>
                          <span class="text-[9px] text-slate-400 font-medium">{{ locale === 'en' ? '1 item • 1m ago' : '1 ürün • 1dk önce' }}</span>
                        </div>
                      </div>
                      <span class="text-[8px] sm:text-[9px] px-2.5 py-1 rounded-md bg-emerald-500/20 text-emerald-400 font-bold uppercase tracking-wider">{{ locale === 'en' ? 'Ready' : 'Hazır' }}</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- Gradient Fade at bottom to blend -->
              <div class="absolute bottom-0 left-0 w-full h-16 bg-gradient-to-t from-slate-950 to-transparent pointer-events-none"></div>
            </div>
          </div>
        </div>

        <!-- 2. Multilingual (Wide) -->
        <div class="md:col-span-6 md:row-span-1 bg-brand-50 rounded-[2.5rem] p-8 sm:p-10 border border-brand-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-8 group">
          <div class="flex-1">
            <div class="w-14 h-14 rounded-2xl bg-white flex items-center justify-center mb-6 shadow-sm border border-brand-200 transform transition-transform group-hover:scale-110">
              <Languages class="w-7 h-7 text-brand-600" />
            </div>
            <h3 class="text-2xl font-bold text-slate-900 mb-3">{{ $t('landing.features.multiLangTitle') }}</h3>
            <p class="text-slate-500 text-base font-medium leading-relaxed max-w-sm">
              {{ $t('landing.features.multiLangDesc') }}
            </p>
          </div>
          <div class="flex gap-3 sm:flex-col">
            <div class="px-5 py-3 bg-white rounded-2xl text-sm font-bold shadow-md border border-brand-100 flex items-center gap-2 transform -rotate-2 group-hover:rotate-0 transition-transform">🇹🇷 Türkçe</div>
            <div class="px-5 py-3 bg-white rounded-2xl text-sm font-bold shadow-sm border border-brand-100 opacity-60 flex items-center gap-2 transform rotate-3 group-hover:rotate-0 transition-transform">🇺🇸 English</div>
          </div>
        </div>

        <!-- 3. Digital Bill Request (Small) -->
        <div class="md:col-span-3 md:row-span-1 bg-indigo-50 rounded-[2.5rem] p-8 sm:p-10 border border-indigo-100 group flex flex-col">
          <div class="w-14 h-14 rounded-2xl bg-indigo-500 flex items-center justify-center mb-6 shadow-lg shadow-indigo-500/30 transform transition-transform group-hover:rotate-12">
            <ReceiptText class="w-7 h-7 text-white" />
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-3">{{ $t('landing.features.billBentoTitle') }}</h3>
          <p class="text-slate-500 text-sm font-medium leading-relaxed">
            {{ $t('landing.features.billBentoDesc') }}
          </p>
        </div>

        <!-- 4. Waiter Call (Small) -->
        <div class="md:col-span-3 md:row-span-1 bg-amber-50 rounded-[2.5rem] p-8 sm:p-10 border border-amber-100 group flex flex-col">
          <div class="w-14 h-14 rounded-2xl bg-amber-500 flex items-center justify-center mb-6 shadow-lg shadow-amber-500/30 transform transition-transform group-hover:-rotate-12">
            <BellRing class="w-7 h-7 text-white" />
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-3">{{ $t('landing.features.waiterBentoTitle') }}</h3>
          <p class="text-slate-500 text-sm font-medium leading-relaxed">
            {{ $t('landing.features.waiterBentoDesc') }}
          </p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { 
  LayoutDashboard, 
  Languages, 
  ReceiptText, 
  BellRing,
  Utensils,
  Users,
  Settings
} from 'lucide-vue-next'

const { locale } = useI18n()
</script>
