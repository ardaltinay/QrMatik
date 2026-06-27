<template>
  <header class="fixed inset-x-0 top-0 z-50 transition-all duration-300" :class="scrolled ? 'pt-2 sm:pt-3' : 'pt-4 sm:pt-5'">
    <div class="pointer-events-none fixed left-0 right-0 top-0 h-[2px] z-[80] bg-transparent">
      <div class="h-full bg-gradient-to-r from-brand-600 via-brand-400 to-brand-300 transition-[width] duration-150" :style="{ width: `${scrollProgress}%` }"></div>
    </div>
    <div class="container-custom">
      <div
        class="relative flex items-center justify-between rounded-2xl border px-4 sm:px-6 transition-all duration-300"
        :class="[
          isMenuOpen
            ? 'bg-white border-slate-200 shadow-xl shadow-slate-300/40 py-3'
            : scrolled
              ? 'bg-white/88 border-slate-200/70 backdrop-blur-2xl shadow-xl shadow-slate-300/35 py-2.5'
              : 'bg-white/55 border-white/80 backdrop-blur-xl shadow-lg shadow-slate-300/20 py-3.5'
        ]"
        :style="headerStyle"
      >
        <div class="pointer-events-none absolute inset-0 rounded-2xl bg-gradient-to-r from-brand-50/55 via-white/20 to-slate-100/30"></div>

        <NuxtLink :to="localePath('/')" class="group relative z-[60] flex items-center">
          <Logo size="sm" animate />
        </NuxtLink>

        <nav class="hidden md:flex items-center gap-2 relative z-[60]">
          <NuxtLink
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            class="px-4 py-2 rounded-xl text-sm font-bold text-slate-600 hover:text-slate-900 hover:bg-white transition-all"
          >
            {{ link.label }}
          </NuxtLink>
        </nav>

        <div class="flex items-center gap-2 sm:gap-3 relative z-[60]">
          <button
            @click="toggleLocale"
            class="hidden sm:inline-flex items-center justify-center rounded-xl border border-slate-200 bg-white px-3 py-2 text-[11px] font-black tracking-[0.14em] text-slate-600 hover:text-slate-900 hover:border-slate-300 transition-colors"
          >
            {{ locale === 'tr' ? 'EN' : 'TR' }}
          </button>

          <NuxtLink
            :to="localePath('/signup/tenant')"
            class="hidden sm:inline-flex items-center rounded-xl bg-slate-950 px-5 py-2.5 text-[11px] font-black uppercase tracking-[0.14em] text-white hover:bg-brand-700 transition-colors"
          >
            {{ $t('nav.start') }}
          </NuxtLink>

          <button
            @click="isMenuOpen = !isMenuOpen"
            class="md:hidden inline-flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 bg-white/90 focus:outline-none"
            :aria-label="isMenuOpen ? $t('nav.closeMenu') : $t('nav.openMenu')"
          >
            <div class="relative h-4 w-5">
              <span class="absolute left-0 top-0 h-0.5 w-5 rounded bg-slate-900 transition-all" :class="isMenuOpen ? 'translate-y-[7px] rotate-45' : ''"></span>
              <span class="absolute left-0 top-[7px] h-0.5 w-5 rounded bg-slate-900 transition-all" :class="isMenuOpen ? 'opacity-0' : 'opacity-100'"></span>
              <span class="absolute left-0 top-[14px] h-0.5 w-5 rounded bg-slate-900 transition-all" :class="isMenuOpen ? '-translate-y-[7px] -rotate-45' : ''"></span>
            </div>
          </button>
        </div>
      </div>
    </div>

    <Transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 -translate-y-2"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 -translate-y-2"
    >
      <div v-if="isMenuOpen" class="fixed inset-0 z-[45] bg-slate-950/20 backdrop-blur-sm pt-20 px-4 md:hidden">
        <div class="mx-auto max-w-md rounded-[1.8rem] border border-slate-200 bg-white p-6 shadow-2xl shadow-slate-900/20">
        <nav class="flex flex-col gap-2">
          <NuxtLink
            v-for="link in navLinks"
            :key="link.to"
            :to="link.to"
            @click="isMenuOpen = false"
            class="rounded-2xl px-4 py-3 text-lg font-bold text-slate-800 hover:bg-slate-50"
          >
            {{ link.label }}
          </NuxtLink>
          
          <div class="h-px bg-slate-100 my-3"></div>
          
          <button
            @click="toggleLocale(); isMenuOpen = false"
            class="rounded-2xl px-4 py-3 text-sm font-black uppercase tracking-[0.14em] text-slate-600 text-left border border-slate-200"
          >
            {{ $t('nav.switchLocaleLong') }}
          </button>

          <NuxtLink
            :to="localePath('/signup/tenant')"
            @click="isMenuOpen = false"
            class="mt-4 w-full py-4 rounded-2xl bg-slate-950 text-white text-center font-bold text-base"
          >
            {{ $t('nav.getStartedFree') }}
          </NuxtLink>
        </nav>
        </div>
      </div>
    </Transition>
  </header>
</template>

<script setup lang="ts">
const { locale, setLocale, t } = useI18n()
const scrolled = ref(false)
const isMenuOpen = ref(false)
const scrollY = ref(0)
const scrollProgress = ref(0)

const localePath = useLocalePath()
let handleScroll: (() => void) | null = null

const navLinks = computed(() => [
  { label: t('nav.qrMenu'), to: localePath('/qr-menu') },
  { label: t('landing.features.title'), to: localePath('/') + '#features' },
  { label: t('common.pricing'), to: localePath('/') + '#pricing' },
  { label: t('nav.faq'), to: localePath('/') + '#faq' },
  { label: t('common.blog'), to: localePath('/blog') }
])

const headerStyle = computed(() => {
  const progress = Math.min(scrollY.value / 160, 1)
  const scale = 1 - progress * 0.015
  const translateY = -progress * 2
  return {
    transform: `translateY(${translateY}px) scale(${scale})`,
    transformOrigin: 'top center'
  }
})

onMounted(() => {
  handleScroll = () => {
    scrollY.value = window.scrollY
    scrolled.value = scrollY.value > 20
    const maxScroll = Math.max(document.documentElement.scrollHeight - window.innerHeight, 1)
    scrollProgress.value = Math.min((scrollY.value / maxScroll) * 100, 100)
  }
  handleScroll()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  if (handleScroll) {
    window.removeEventListener('scroll', handleScroll)
  }
})

const toggleLocale = async () => {
  await setLocale(locale.value === 'tr' ? 'en' : 'tr')
}

// Lock body scroll when menu is open
watch(isMenuOpen, (val) => {
  if (import.meta.client) {
    document.body.style.overflow = val ? 'hidden' : ''
  }
})
</script>
