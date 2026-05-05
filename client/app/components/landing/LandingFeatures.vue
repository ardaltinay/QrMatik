<template>
  <section class="relative py-24 overflow-hidden">
    <div class="mx-auto max-w-7xl px-6">
      <div class="text-center mb-16">
        <div class="inline-flex items-center gap-2 glass rounded-full px-4 py-1.5 mb-4 border-slate-200 shadow-sm">
          <span class="text-xs font-bold uppercase tracking-wider text-brand-600">{{ $t('landing.features.badge') }}</span>
        </div>
        <h2 class="text-3xl sm:text-4xl font-black text-slate-900 mb-4">
          {{ $t('landing.features.title') }} <span class="text-brand-600">{{ $t('landing.features.titleHighlight') }}</span>
        </h2>
        <p class="text-slate-500 max-w-2xl mx-auto font-medium">{{ $t('landing.features.description') }}</p>
      </div>

      <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <div
          v-for="(f, i) in features"
          :key="f.titleKey"
          class="group glass rounded-3xl p-8 hover:bg-white transition-all duration-500 hover:-translate-y-2 hover:shadow-2xl hover:shadow-slate-200/50 border-slate-100"
          :style="{ animationDelay: `${i * 0.1}s` }"
        >
          <div class="flex h-14 w-14 items-center justify-center rounded-2xl mb-6 transition-all duration-500 group-hover:scale-110 group-hover:shadow-lg" :class="f.iconBg">
            <component :is="f.icon" class="w-7 h-7" :class="f.iconColor" />
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-3">{{ $t(f.titleKey) }}</h3>
          <p class="text-sm text-slate-500 leading-relaxed font-medium">{{ $t(f.descKey) }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { QrCode, ShoppingCart, ChefHat, LayoutDashboard, TrendingUp, Package } from 'lucide-vue-next'

const features = [
  { titleKey: 'landing.features.qrMenuTitle', descKey: 'landing.features.qrMenuDesc', icon: QrCode, iconBg: 'bg-brand-500/10', iconColor: 'text-brand-400' },
  { titleKey: 'landing.features.orderTrackingTitle', descKey: 'landing.features.orderTrackingDesc', icon: ShoppingCart, iconBg: 'bg-cyan-500/10', iconColor: 'text-cyan-400' },
  { titleKey: 'landing.features.kitchenBarTitle', descKey: 'landing.features.kitchenBarDesc', icon: ChefHat, iconBg: 'bg-amber-500/10', iconColor: 'text-amber-400' },
  { titleKey: 'landing.features.adminTitle', descKey: 'landing.features.adminDesc', icon: LayoutDashboard, iconBg: 'bg-emerald-500/10', iconColor: 'text-emerald-400' },
  { titleKey: 'landing.features.reportsTitle', descKey: 'landing.features.reportsDesc', icon: TrendingUp, iconBg: 'bg-blue-500/10', iconColor: 'text-blue-400' },
  { titleKey: 'landing.features.stockTitle', descKey: 'landing.features.stockDesc', icon: Package, iconBg: 'bg-rose-500/10', iconColor: 'text-rose-400' },
]
</script>
