<template>
  <section class="relative pt-12 pb-32 overflow-hidden bg-white">
    <!-- Concave Header Area (Bowl Shape: Middle Up, Sides Down) -->
    <div class="absolute top-0 left-0 right-0 h-[480px] bg-brand-400 overflow-hidden">
      <!-- Decorative Glows -->
      <div class="absolute top-[-20%] right-[-10%] w-[500px] h-[500px] bg-white/10 blur-[130px] rounded-full"></div>
      <div class="absolute top-0 left-[-10%] w-[350px] h-[350px] bg-black/5 blur-[110px] rounded-full"></div>
      
      <!-- The Bowl Curve SVG - Pushed Higher -->
      <svg class="absolute bottom-0 left-0 w-full h-[320px] text-white" viewBox="0 0 1440 320" fill="currentColor" preserveAspectRatio="none">
        <!-- Pokes UP deep into the green area, aiming above the logo -->
        <path d="M0,320 L0,220 Q720,-100 1440,220 L1440,320 Z"></path>
      </svg>
    </div>

    <div class="container-custom pt-32 relative z-10">
      <!-- Logo in Hero (Restored to Center) -->
      <div class="mb-20 flex justify-center">
        <Logo size="lg" hide-text animate />
      </div>

      <div class="grid lg:grid-cols-2 gap-12 lg:gap-24 items-center">
        <!-- Content Left -->
        <div class="text-center lg:text-left">
          <h1 class="text-6xl sm:text-7xl xl:text-8xl font-black text-slate-900 mb-8 leading-[0.9] tracking-tighter">
            {{ $t('landing.hero.titleLine1') }} <br />
            <span class="text-brand-500">{{ $t('landing.hero.titleLine2') }}</span>
          </h1>

          <p class="text-xl sm:text-2xl text-slate-500 max-w-2xl lg:max-w-none mx-auto lg:mx-0 mb-12 font-medium leading-relaxed">
            {{ $t('landing.hero.description') }}
          </p>

          <div class="flex justify-center lg:justify-start gap-4 mb-16">
            <NuxtLink :to="localePath('/signup/tenant')" class="px-12 py-5 rounded-2xl bg-slate-950 text-white font-bold text-lg hover:bg-brand-600 hover:scale-105 active:scale-95 transition-all shadow-xl shadow-black/20">
              {{ $t('landing.hero.ctaPrimary') }}
            </NuxtLink>
            <a href="#features" class="hidden sm:flex px-12 py-5 rounded-2xl bg-white border border-slate-200 text-slate-600 font-bold text-lg hover:bg-slate-50 transition-all">
              {{ $t('landing.features.title') }}
            </a>
          </div>
        </div>

        <!-- Phone Mockup Right -->
        <div class="flex justify-center w-full mt-12 lg:mt-0 lg:block">
          <LandingPhoneMockup />
        </div>
      </div>

      <!-- Real Feature Grid (from feasymenu.com) -->
      <div id="features" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-x-12 gap-y-20 max-w-7xl mx-auto mt-40">
        <div v-for="feat in realFeatures" :key="feat.key" class="flex flex-col items-center text-center group">
          <div class="w-24 h-24 rounded-[3rem] bg-brand-50 flex items-center justify-center mb-8 group-hover:scale-110 group-hover:-rotate-3 transition-all duration-500 shadow-sm border border-brand-100/50">
            <component :is="feat.icon" class="w-10 h-10 text-brand-500" />
          </div>
          <h3 class="text-2xl font-bold text-slate-900 mb-4">{{ $t(feat.titleKey) }}</h3>
          <p class="text-base text-slate-400 max-w-[280px] leading-relaxed font-medium">
            {{ $t(feat.descKey) }}
          </p>
        </div>
      </div>

      <div class="mt-32 pt-16 border-t border-slate-100/50">
        <p class="text-center text-sm font-bold text-slate-400 uppercase tracking-widest mb-10">{{ $t('landing.hero.trustedBy') }}</p>
        <div class="flex flex-wrap justify-center items-center gap-8 md:gap-16 opacity-40 grayscale hover:grayscale-0 transition-all duration-700">
          <div v-for="brand in mockBrands" :key="brand.name" class="flex items-center gap-3">
            <component :is="brand.icon" class="w-6 h-6 text-slate-400" />
            <span class="font-serif text-xl md:text-2xl font-medium tracking-tight text-slate-600">{{ brand.name }}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { 
  QrCode, 
  MousePointer2, 
  MonitorPlay, 
  Settings2, 
  BarChart3, 
  Archive,
  Utensils,
  Flame,
  Coffee,
  Fish,
  Pizza,
  ShieldCheck,
  Gift
} from 'lucide-vue-next'

const localePath = useLocalePath()

const mockBrands = [
  { name: 'Le Bistro', icon: Utensils },
  { name: 'Urban Grill', icon: Flame },
  { name: 'Sunset Cafe', icon: Coffee },
  { name: 'Sushi Zen', icon: Fish },
  { name: 'Pizzeria Roma', icon: Pizza }
]

const realFeatures = [
  { key: 'f1', icon: QrCode, titleKey: 'landing.features.qrMenuTitle', descKey: 'landing.features.qrMenuDesc' },
  { key: 'f2', icon: MousePointer2, titleKey: 'landing.features.orderTrackingTitle', descKey: 'landing.features.orderTrackingDesc' },
  { key: 'f3', icon: MonitorPlay, titleKey: 'landing.features.kitchenBarTitle', descKey: 'landing.features.kitchenBarDesc' },
  { key: 'f4', icon: Settings2, titleKey: 'landing.features.adminTitle', descKey: 'landing.features.adminDesc' },
  { key: 'f5', icon: BarChart3, titleKey: 'landing.features.reportsTitle', descKey: 'landing.features.reportsDesc' },
  { key: 'f6', icon: ShieldCheck, titleKey: 'landing.features.geofencingTitle', descKey: 'landing.features.geofencingDesc' },
  { key: 'f7', icon: Gift, titleKey: 'landing.features.loyaltyTitle', descKey: 'landing.features.loyaltyDesc' },
  { key: 'f8', icon: Archive, titleKey: 'landing.features.stockTitle', descKey: 'landing.features.stockDesc' },
]
</script>
