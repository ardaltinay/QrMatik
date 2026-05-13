<template>
  <header 
    class="fixed top-0 left-0 right-0 z-50 transition-all ease-out" 
    :class="[
      isMenuOpen ? 'duration-0 bg-white py-4 border-b border-slate-100' : 'duration-300',
      {
        'bg-white/70 backdrop-blur-2xl border-b border-slate-200/50 py-3': !isMenuOpen && scrolled,
        'bg-transparent py-8': !isMenuOpen && !scrolled
      }
    ]"
  >
    <div class="container-custom flex items-center justify-between">
      <!-- Logo -->
      <NuxtLink :to="localePath('/')" class="group relative z-[60]">
        <Logo size="sm" animate />
      </NuxtLink>

      <!-- Desktop Nav -->
      <nav class="hidden md:flex items-center gap-10">
        <NuxtLink
          v-for="link in navLinks"
          :key="link.to"
          :to="localePath(link.to)"
          class="text-sm font-bold text-slate-500 hover:text-slate-900 transition-colors"
        >
          {{ link.label }}
        </NuxtLink>
      </nav>

      <!-- Actions -->
      <div class="flex items-center gap-4 md:gap-6 relative z-[60]">
        <button
          @click="toggleLocale"
          class="hidden sm:block text-xs font-bold text-slate-500 hover:text-slate-900 transition-colors uppercase tracking-widest"
        >
          {{ locale === 'tr' ? 'EN' : 'TR' }}
        </button>

        <NuxtLink
          :to="localePath('/signup/tenant')"
          class="hidden sm:block px-8 py-2.5 rounded-full border border-slate-900 text-[11px] font-bold uppercase tracking-widest text-slate-900 hover:bg-slate-900 hover:text-white transition-all duration-300"
        >
          {{ locale === 'tr' ? 'Başlat' : 'Start' }}
        </NuxtLink>

        <!-- Mobile Menu Button -->
        <button 
          @click="isMenuOpen = !isMenuOpen"
          class="md:hidden w-10 h-10 flex flex-col items-center justify-center gap-1.5 focus:outline-none"
        >
          <div class="w-6 h-0.5 bg-slate-900 transition-all duration-300" :class="{ 'rotate-45 translate-y-2': isMenuOpen }"></div>
          <div class="w-6 h-0.5 bg-slate-900 transition-all duration-300" :class="{ 'opacity-0': isMenuOpen }"></div>
          <div class="w-6 h-0.5 bg-slate-900 transition-all duration-300" :class="{ '-rotate-45 -translate-y-2': isMenuOpen }"></div>
        </button>
      </div>
    </div>

    <!-- Mobile Menu Overlay -->
    <Transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 -translate-y-4"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 -translate-y-4"
    >
      <div v-if="isMenuOpen" class="fixed inset-0 z-[50] bg-white pt-24 px-6 md:hidden">
        <nav class="flex flex-col gap-6">
          <NuxtLink
            v-for="link in navLinks"
            :key="link.to"
            :to="localePath(link.to)"
            @click="isMenuOpen = false"
            class="text-2xl font-bold text-slate-900"
          >
            {{ link.label }}
          </NuxtLink>
          
          <div class="h-px bg-slate-100 my-4"></div>
          
          <button
            @click="toggleLocale(); isMenuOpen = false"
            class="text-lg font-bold text-slate-500 uppercase tracking-widest text-left"
          >
            {{ locale === 'tr' ? 'English (EN)' : 'Türkçe (TR)' }}
          </button>

          <NuxtLink
            :to="localePath('/signup/tenant')"
            @click="isMenuOpen = false"
            class="mt-6 w-full py-4 rounded-full bg-slate-950 text-white text-center font-bold text-lg"
          >
            {{ locale === 'tr' ? 'Ücretsiz Başla' : 'Start Free' }}
          </NuxtLink>
        </nav>
      </div>
    </Transition>
  </header>
</template>

<script setup lang="ts">
const { locale, setLocale } = useI18n()
const scrolled = ref(false)
const isMenuOpen = ref(false)

const localePath = useLocalePath()

const navLinks = computed(() => [
  { label: locale.value === 'tr' ? 'QR Menü' : 'QR Menu', to: localePath('/qr-menu') },
  { label: locale.value === 'tr' ? 'Özellikler' : 'Features', to: localePath('/') + '#features' },
  { label: locale.value === 'tr' ? 'Fiyatlandırma' : 'Pricing', to: localePath('/') + '#pricing' },
  { label: locale.value === 'tr' ? 'SSS' : 'FAQ', to: localePath('/') + '#faq' }
])

onMounted(() => {
  scrolled.value = window.scrollY > 20
  window.addEventListener('scroll', () => {
    scrolled.value = window.scrollY > 20
  })
})

const toggleLocale = async () => {
  await setLocale(locale.value === 'tr' ? 'en' : 'tr')
}

// Lock body scroll when menu is open
watch(isMenuOpen, (val) => {
  if (process.client) {
    document.body.style.overflow = val ? 'hidden' : ''
  }
})
</script>
