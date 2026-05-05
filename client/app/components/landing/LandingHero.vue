<template>
  <section class="relative min-h-screen flex items-center pt-24 pb-20 overflow-hidden perspective-content">
    <div class="mx-auto max-w-7xl px-6">
      <div class="grid lg:grid-cols-2 gap-16 items-center">
        <!-- Text -->
        <div class="animate-slide-up">
          <!-- Badge -->
          <div class="inline-flex items-center gap-2 glass px-4 py-1.5 rounded-full mb-8 border-slate-200 shadow-sm">
            <span class="flex h-2 w-2 rounded-full bg-brand-500 animate-pulse"></span>
            <span class="text-xs font-bold tracking-wider uppercase text-slate-500">{{ $t('landing.hero.badge') }}</span>
          </div>

          <h1 class="text-5xl sm:text-6xl lg:text-7xl font-black leading-[1.05] tracking-tight mb-8">
            <span class="text-slate-900">{{ $t('landing.hero.titleLine1') }}</span><br />
            <span class="text-brand-600">{{ $t('landing.hero.titleLine2') }}</span>
          </h1>

          <p class="text-lg text-slate-600/80 leading-relaxed mb-10 max-w-lg font-medium">
            {{ $t('landing.hero.description') }}
          </p>

          <!-- Stats -->
          <div class="flex items-center gap-8 mb-10">
            <div v-for="stat in stats" :key="stat.label">
              <div class="text-2xl font-black text-slate-900">{{ stat.value }}</div>
              <div class="text-[10px] text-slate-400 font-bold mt-1 uppercase tracking-widest">{{ stat.label }}</div>
            </div>
          </div>

          <!-- CTA buttons -->
          <div class="flex flex-col sm:flex-row gap-4">
            <NuxtLink
              to="/signup/tenant"
              class="px-8 py-4 rounded-2xl bg-brand-600 text-white font-bold uppercase tracking-widest text-xs hover:bg-brand-500 transition-all duration-300 shadow-xl shadow-brand-600/20 text-center"
            >
              {{ $t('landing.hero.ctaPrimary') }}
            </NuxtLink>
            <NuxtLink
              to="/qr-menu"
              class="px-8 py-4 rounded-2xl bg-white border border-slate-200 text-slate-700 font-bold uppercase tracking-widest text-xs hover:bg-slate-50 transition-all duration-300 text-center"
            >
              {{ $t('landing.hero.ctaSecondary') }}
            </NuxtLink>
          </div>
        </div>

        <!-- Visual: Phone Mockup (Light Silver Style) -->
        <div class="hidden lg:block animate-slide-up" style="animation-delay: 0.2s">
          <div class="relative transform-3d max-w-sm mx-auto">
            <!-- Phone Frame -->
            <div class="bg-slate-200/50 rounded-[3.5rem] p-3 shadow-2xl relative overflow-hidden backdrop-blur-3xl border border-white">
              <div class="bg-white rounded-[2.8rem] p-6 h-[600px] flex flex-col border border-slate-100 shadow-inner relative">
                <!-- Status Bar -->
                <div class="flex justify-between items-center mb-6 opacity-40">
                  <div class="text-[10px] font-bold text-slate-900">9:41</div>
                  <div class="flex gap-1.5">
                    <div class="w-3 h-3 rounded-full border border-slate-900"></div>
                    <div class="w-4 h-2 rounded-sm bg-slate-900"></div>
                  </div>
                </div>

                <div class="text-xs font-black text-slate-300 uppercase tracking-widest mb-6">{{ $t('landing.mockup.menuTitle') }}</div>

                <!-- Menu Items -->
                <div class="space-y-4 flex-1">
                  <div v-for="item in previewItems" :key="item.name" class="bg-slate-50/50 rounded-2xl p-4 flex items-center gap-4 group hover:bg-white hover:shadow-xl hover:shadow-slate-200/50 transition-all duration-300 border border-slate-100">
                    <div class="w-12 h-12 rounded-xl flex items-center justify-center text-2xl shadow-sm bg-white border border-slate-100">
                      {{ item.emoji }}
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="text-sm font-bold text-slate-900 mb-0.5 truncate">{{ item.name }}</div>
                      <div class="text-[10px] text-slate-400 font-medium truncate">{{ item.desc }}</div>
                    </div>
                    <div class="text-sm font-black text-brand-600">{{ item.price }}</div>
                  </div>
                </div>

                <!-- Cart Button -->
                <div class="mt-6 bg-brand-600 rounded-2xl px-5 py-4 flex items-center justify-between shadow-xl shadow-brand-600/20">
                  <div>
                    <div class="text-[10px] font-bold text-white/70 uppercase tracking-widest">{{ $t('landing.mockup.cart') }}</div>
                    <div class="text-sm font-black text-white">{{ $t('landing.mockup.cartItems') }}</div>
                  </div>
                  <div class="text-lg font-black text-white">{{ $t('landing.mockup.cartTotal') }}</div>
                </div>
              </div>
            </div>

            <!-- Floating Notifications (Depth Layers - Light Mode) -->
            <div class="absolute -top-6 -right-12 glass-strong rounded-2xl p-4 shadow-3xl transform translate-z-24 animate-float border-white shadow-slate-200/50">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-green-500 flex items-center justify-center text-white font-black text-lg">!</div>
                <div>
                  <div class="text-[10px] font-black text-slate-800 uppercase tracking-wider">{{ $t('landing.mockup.newOrder') }}</div>
                  <div class="text-xs font-bold text-brand-600">{{ $t('landing.mockup.tableNumber') }}</div>
                </div>
              </div>
            </div>

            <div class="absolute bottom-12 -left-12 glass-strong rounded-2xl p-4 shadow-3xl transform translate-z-32 animate-float border-white shadow-slate-200/50" style="animation-delay: -3s">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-brand-600 flex items-center justify-center text-lg">🍳</div>
                <div>
                  <div class="text-[10px] font-black text-slate-800 uppercase tracking-wider">{{ $t('landing.mockup.preparing') }}</div>
                  <div class="text-xs font-bold text-slate-400 font-medium">{{ $t('landing.mockup.orderNumber') }}</div>
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
const { t } = useI18n()

const stats = computed(() => [
  { value: t('landing.hero.stat1Value'), label: t('landing.hero.stat1Label') },
  { value: t('landing.hero.stat2Value'), label: t('landing.hero.stat2Label') },
  { value: t('landing.hero.stat3Value'), label: t('landing.hero.stat3Label') },
])

const previewItems = computed(() => [
  { name: t('landing.mockup.item1Name'), desc: t('landing.mockup.item1Desc'), price: t('landing.mockup.item1Price'), emoji: '🥩' },
  { name: t('landing.mockup.item2Name'), desc: t('landing.mockup.item2Desc'), price: t('landing.mockup.item2Price'), emoji: '🥗' },
  { name: t('landing.mockup.item3Name'), desc: t('landing.mockup.item3Desc'), price: t('landing.mockup.item3Price'), emoji: '🍋' },
  { name: t('landing.mockup.item4Name'), desc: t('landing.mockup.item4Desc'), price: t('landing.mockup.item4Price'), emoji: '🍰' },
])
</script>

<style scoped>
.perspective-content {
  perspective: 2000px;
}

.transform-3d {
  transform: rotateX(18deg) rotateY(-12deg) rotateZ(0deg);
  transform-style: preserve-3d;
  transition: all 1.2s cubic-bezier(0.16, 1, 0.3, 1);
}

.transform-3d:hover {
  transform: rotateX(0deg) rotateY(0deg) rotateZ(0deg);
}

.translate-z-24 { transform: translateZ(80px); }
.translate-z-32 { transform: translateZ(120px); }
</style>
