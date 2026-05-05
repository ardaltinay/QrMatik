<template>
  <header class="fixed top-0 left-0 right-0 z-50 transition-all duration-500 ease-out" :class="scrolled ? 'bg-white/70 backdrop-blur-2xl border-b border-slate-200/50 shadow-2xl shadow-slate-900/5 py-3' : 'bg-transparent py-6'">
    <div class="mx-auto max-w-7xl flex items-center justify-between px-6">
      <NuxtLink to="/" class="group relative z-10 flex items-center gap-2">
        <Logo show-tagline shadow animate />
      </NuxtLink>

      <!-- Desktop nav -->
      <nav class="hidden md:flex items-center gap-2 bg-white/50 backdrop-blur-md px-2 py-1.5 rounded-full border border-white/60 shadow-sm" :class="{ 'bg-slate-100/50': !scrolled }">
        <NuxtLink
          v-for="link in navLinks"
          :key="link.to"
          :to="link.to"
          class="px-5 py-2.5 rounded-full text-[13px] font-black uppercase tracking-widest text-slate-500 hover:text-brand-600 hover:bg-white transition-all duration-300 relative group overflow-hidden"
        >
          <div class="absolute inset-0 bg-brand-50 translate-y-[100%] group-hover:translate-y-0 transition-transform duration-300 ease-out z-0"></div>
          <span class="relative z-10">{{ link.label }}</span>
        </NuxtLink>
      </nav>

      <div class="hidden md:flex items-center gap-3">
        <!-- Language switcher -->
        <button
          @click="toggleLocale"
          class="w-10 h-10 flex items-center justify-center rounded-full bg-white/50 border border-slate-200 text-[11px] font-black cursor-pointer text-slate-400 hover:text-slate-900 hover:border-slate-300 transition-all duration-300 uppercase tracking-widest shadow-sm"
          :title="locale === 'tr' ? 'Switch to English' : 'Türkçe\'ye geç'"
        >
          {{ locale === 'tr' ? 'EN' : 'TR' }}
        </button>

        <NuxtLink
          to="/signup/tenant"
          class="relative overflow-hidden px-8 py-3.5 rounded-full bg-slate-900 text-xs font-black uppercase tracking-[0.2em] text-white shadow-xl shadow-slate-900/20 hover:shadow-slate-900/40 transition-all duration-300 hover:-translate-y-0.5 group"
        >
          <div class="absolute inset-0 bg-gradient-to-r from-brand-600 to-brand-500 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
          <span class="relative z-10">{{ $t('nav.getStartedFree') }}</span>
        </NuxtLink>
      </div>

      <!-- Mobile hamburger -->
      <button @click="mobileOpen = !mobileOpen" class="md:hidden relative z-10 w-12 h-12 rounded-full bg-white/80 backdrop-blur-md border border-slate-200 flex items-center justify-center hover:bg-slate-50 transition-all shadow-md">
        <svg v-if="!mobileOpen" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-slate-900" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
        <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-slate-900" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <!-- Mobile menu -->
    <Transition name="slide-down">
      <div v-if="mobileOpen" class="md:hidden absolute top-full left-4 right-4 mt-2 rounded-[2.5rem] bg-white/95 backdrop-blur-3xl border border-slate-200 shadow-2xl overflow-hidden p-4 origin-top">
        <nav class="flex flex-col gap-2">
          <NuxtLink
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            class="px-6 py-4 rounded-2xl text-sm font-black uppercase tracking-widest text-slate-500 hover:text-brand-600 hover:bg-slate-50 transition-all active:scale-95"
            @click="mobileOpen = false"
          >
            {{ link.label }}
          </NuxtLink>

          <div class="h-px bg-slate-100 my-2 mx-4"></div>

          <!-- Mobile language switcher -->
          <button
            @click="toggleLocale"
            class="flex items-center gap-3 px-6 py-4 rounded-2xl text-sm font-black uppercase tracking-widest text-slate-400 hover:text-slate-900 hover:bg-slate-50 transition-all text-left active:scale-95"
          >
            <span class="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-lg">🌐</span>
            {{ locale === 'tr' ? 'Switch to English' : 'Türkçe\'ye Geç' }}
          </button>

          <NuxtLink
            to="/signup/tenant"
            class="mt-2 px-6 py-5 rounded-3xl bg-slate-900 text-center text-white font-black uppercase tracking-[0.2em] text-xs shadow-xl shadow-slate-900/20 active:scale-95 transition-all"
            @click="mobileOpen = false"
          >
            {{ $t('nav.getStartedFree') }}
          </NuxtLink>
        </nav>
      </div>
    </Transition>
  </header>
</template>

<script setup lang="ts">
const { locale, setLocale, t } = useI18n()
const mobileOpen = ref(false)
const scrolled = ref(false)

const navLinks = computed(() => [
  { to: '/', label: t('common.home') },
  { to: '/qr-menu', label: t('nav.qrMenu') },
  { to: '/about', label: t('common.about') },
  { to: '/blog', label: t('common.blog') },
])

function toggleLocale() {
  const newLocale = locale.value === 'tr' ? 'en' : 'tr'
  setLocale(newLocale)
  const i18nCookie = useCookie('fm_i18n', { path: '/', maxAge: 60 * 60 * 24 * 365 })
  i18nCookie.value = newLocale
}

onMounted(() => {
  const onScroll = () => {
    scrolled.value = window.scrollY > 20
  }
  window.addEventListener('scroll', onScroll, { passive: true })
  onScroll()

  onUnmounted(() => {
    window.removeEventListener('scroll', onScroll)
  })
})
</script>

<style scoped>
.slide-down-enter-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-down-leave-active { transition: all 0.2s ease-in; }
.slide-down-enter-from, .slide-down-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
