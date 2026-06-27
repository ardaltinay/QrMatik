<template>
  <section ref="heroRoot" class="relative pt-16 pb-32 overflow-hidden">
    <div class="absolute inset-0 bg-gradient-to-b from-[#f9faf7] via-[#f2f4ee] to-[#edf1e9]" :style="parallaxStyleBack"></div>
    <div class="absolute -left-24 top-16 h-64 w-64 rounded-full bg-brand-300/20 blur-3xl" :style="parallaxStyleFront"></div>
    <div class="absolute -right-24 top-24 h-72 w-72 rounded-full bg-slate-900/10 blur-3xl" :style="parallaxStyleFrontAlt"></div>

    <div class="container-custom relative z-10 pt-20">

      <div class="reveal-item relative rounded-[2.6rem] border border-white/80 bg-white/72 backdrop-blur-2xl p-6 sm:p-10 lg:p-12 shadow-[0_40px_120px_-55px_rgba(26,28,24,0.7)]">
        <div class="grid lg:grid-cols-[1.05fr_0.95fr] gap-12 lg:gap-10 items-center">
          <div class="text-center lg:text-left">
            <h1 class="text-5xl sm:text-6xl xl:text-7xl font-black text-slate-900 mb-7 leading-[0.95] tracking-tight">
              {{ $t('landing.hero.titleLine1') }}
              <span class="block text-brand-700">{{ $t('landing.hero.titleLine2') }}</span>
            </h1>

            <p class="text-lg sm:text-xl text-slate-600 max-w-2xl lg:max-w-none mx-auto lg:mx-0 mb-10 font-medium leading-relaxed">
              {{ $t('landing.hero.description') }}
            </p>

            <div class="flex flex-wrap justify-center lg:justify-start gap-2.5 mb-8">
              <span
                v-for="pillar in heroPillars"
                :key="pillar"
                class="inline-flex items-center rounded-full border border-slate-200 bg-white px-3.5 py-1.5 text-[11px] font-bold uppercase tracking-[0.13em] text-slate-600"
              >
                {{ $t(pillar) }}
              </span>
            </div>

            <div class="flex flex-wrap justify-center lg:justify-start gap-4 mb-10">
              <NuxtLink
                id="hero-primary-cta"
                :to="localePath('/signup/tenant')"
                @click="trackCtaClick('hero_primary')"
                class="cta-premium inline-flex items-center gap-2 px-10 py-4 rounded-2xl bg-slate-950 text-white font-bold text-base sm:text-lg hover:bg-brand-700 hover:-translate-y-0.5 active:translate-y-0 transition-all shadow-xl shadow-black/20"
              >
                {{ $t('landing.hero.ctaPrimary') }}
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 12h14m-5-5l5 5-5 5" /></svg>
              </NuxtLink>
              <a href="#features" class="hidden sm:inline-flex items-center px-10 py-4 rounded-2xl bg-white border border-slate-200 text-slate-700 font-bold text-base sm:text-lg hover:bg-slate-50 transition-all shadow-sm">
                {{ $t('landing.features.title') }}
              </a>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
              <div v-for="metric in quickMetrics" :key="metric.labelKey" class="rounded-2xl border border-slate-100 bg-white px-4 py-3 text-left shadow-sm">
                <p class="text-[10px] uppercase tracking-[0.16em] font-bold text-slate-400 mb-1">{{ $t(metric.labelKey) }}</p>
                <p class="text-base sm:text-lg font-black text-slate-900">{{ $t(metric.valueKey) }}</p>
              </div>
            </div>
          </div>

          <div class="relative flex justify-center w-full mt-6 lg:mt-0">
            <div class="absolute inset-x-[8%] bottom-2 h-14 bg-black/10 blur-2xl rounded-full"></div>
            <LandingPhoneMockup />

            <div
              class="hidden min-[420px]:block absolute -right-1 sm:-right-3 top-1/2 -translate-y-1/2 w-[190px] sm:w-[220px] rounded-2xl border border-slate-200 bg-white/95 p-4 shadow-xl"
              @mouseenter="isDemoPaused = true"
              @mouseleave="isDemoPaused = false"
            >
              <p class="text-[10px] font-black uppercase tracking-[0.18em] text-slate-400 mb-3">
                {{ $t('landing.hero.demoFlow.title') }}
              </p>
              <div class="space-y-2.5">
                <button
                  v-for="step in demoSteps"
                  :key="step.id"
                  type="button"
                  @click="activeDemoStep = step.id"
                  class="w-full text-left rounded-xl border px-3 py-2.5 transition-all"
                  :class="activeDemoStep === step.id ? 'border-brand-300 bg-brand-50 shadow-sm' : 'border-slate-200 bg-white hover:border-brand-200'"
                >
                  <p class="text-xs font-bold text-slate-900">{{ $t(step.titleKey) }}</p>
                  <p class="text-[11px] text-slate-500 mt-1">{{ $t(step.descKey) }}</p>
                </button>
              </div>
              <div class="mt-3 h-1.5 rounded-full bg-slate-100 overflow-hidden">
                <div
                  class="h-full rounded-full bg-brand-500 transition-all duration-300"
                  :style="{ width: `${(activeDemoStep / demoSteps.length) * 100}%` }"
                ></div>
              </div>

              <Transition
                mode="out-in"
                enter-active-class="transition duration-300 ease-out"
                enter-from-class="opacity-0 translate-y-2"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-2"
              >
                <div :key="activeDemoStep" class="mt-3 rounded-xl border border-slate-200 bg-slate-50 px-3 py-2.5">
                  <p class="text-[10px] font-black uppercase tracking-[0.16em] text-slate-400">{{ $t('landing.hero.demoFlow.stateLabel') }}</p>
                  <p class="text-xs font-bold text-slate-900 mt-1">{{ $t(currentDemoState?.titleKey || 'landing.hero.demoFlow.state1Title') }}</p>
                  <p class="text-[11px] text-slate-500 mt-0.5">{{ $t(currentDemoState?.noteKey || 'landing.hero.demoFlow.state1Note') }}</p>
                </div>
              </Transition>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-8 grid grid-cols-1 sm:grid-cols-3 gap-3">
        <div v-for="proof in socialProofItems" :key="proof.labelKey" class="reveal-item rounded-2xl border border-slate-200/80 bg-white/85 px-5 py-4 shadow-sm">
          <p class="text-2xl sm:text-3xl font-black text-slate-900">{{ $t(proof.valueKey) }}</p>
          <p class="text-[11px] sm:text-xs font-bold uppercase tracking-[0.16em] text-slate-500 mt-1">{{ $t(proof.labelKey) }}</p>
        </div>
      </div>

      <div class="mt-4 grid grid-cols-1 lg:grid-cols-3 gap-4">
        <article
          v-for="story in successStories"
          :key="story.nameKey"
          class="reveal-item rounded-2xl border border-slate-200/80 bg-white/90 px-5 py-5 shadow-sm"
        >
          <p class="text-sm font-bold text-slate-700 leading-relaxed">“{{ $t(story.quoteKey) }}”</p>
          <div class="mt-4 flex items-end justify-between gap-2">
            <div>
              <p class="text-xs font-black uppercase tracking-[0.15em] text-slate-400">{{ $t(story.nameKey) }}</p>
              <p class="text-[11px] text-slate-500 font-semibold mt-1">{{ $t(story.metaKey) }}</p>
            </div>
            <p class="text-sm font-black text-emerald-600">{{ $t(story.resultKey) }}</p>
          </div>
        </article>
      </div>

      <div id="features" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-x-8 gap-y-10 max-w-7xl mx-auto mt-20">
        <div v-for="feat in realFeatures" :key="feat.key" class="reveal-item rounded-[2rem] border border-slate-100 bg-white/90 p-6 text-center group hover:-translate-y-1 transition-all shadow-sm hover:shadow-lg">
          <div class="w-16 h-16 rounded-2xl bg-brand-50 flex items-center justify-center mx-auto mb-5 group-hover:scale-110 group-hover:-rotate-3 transition-all duration-500 shadow-sm border border-brand-100/50">
            <component :is="feat.icon" class="w-7 h-7 text-brand-600" />
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-3">{{ $t(feat.titleKey) }}</h3>
          <p class="text-sm text-slate-500 leading-relaxed font-medium">
            {{ $t(feat.descKey) }}
          </p>
        </div>
      </div>

      <div class="mt-16 pt-12 border-t border-slate-200/70">
        <p class="text-center text-[11px] font-black text-slate-400 uppercase tracking-[0.2em] mb-8">{{ $t('landing.hero.trustedBy') }}</p>
        <div class="flex flex-wrap justify-center items-center gap-7 md:gap-14 opacity-50 grayscale hover:grayscale-0 transition-all duration-700">
          <div v-for="brand in mockBrands" :key="brand.name" class="flex items-center gap-3">
            <component :is="brand.icon" class="w-5 h-5 text-slate-500" />
            <span class="font-serif text-lg md:text-xl font-medium tracking-tight text-slate-700">{{ brand.name }}</span>
          </div>
        </div>
      </div>

      <Transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 translate-y-4"
        enter-to-class="opacity-100 translate-y-0"
        leave-active-class="transition duration-200 ease-in"
        leave-from-class="opacity-100 translate-y-0"
        leave-to-class="opacity-0 translate-y-4"
      >
        <div v-if="showMobileStickyCta" class="fixed bottom-4 inset-x-4 z-40 sm:hidden">
          <NuxtLink
            :to="localePath('/signup/tenant')"
            @click="trackCtaClick('hero_mobile_sticky')"
            class="cta-premium block w-full rounded-2xl bg-slate-950 text-white text-center px-5 py-4 font-black tracking-wide shadow-xl shadow-black/30"
          >
            {{ $t('landing.hero.mobileStickyCta') }}
          </NuxtLink>
        </div>
      </Transition>
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

const heroRoot = ref<HTMLElement | null>(null)
const localePath = useLocalePath()
const activeDemoStep = ref(1)
const isDemoPaused = ref(false)
const scrollY = ref(0)
const isMobileViewport = ref(false)
const prefersReducedMotion = ref(false)
const isFooterVisible = ref(false)
const isPrimaryCtaVisible = ref(false)
let demoTimer: ReturnType<typeof setInterval> | null = null
let scrollHandler: (() => void) | null = null
let footerObserver: IntersectionObserver | null = null
let primaryCtaObserver: IntersectionObserver | null = null

const quickMetrics = [
  { labelKey: 'landing.hero.metrics.setupLabel', valueKey: 'landing.hero.metrics.setupValue' },
  { labelKey: 'landing.hero.metrics.conversionLabel', valueKey: 'landing.hero.metrics.conversionValue' },
  { labelKey: 'landing.hero.metrics.supportLabel', valueKey: 'landing.hero.metrics.supportValue' }
]

const demoSteps = [
  { id: 1, titleKey: 'landing.hero.demoFlow.step1Title', descKey: 'landing.hero.demoFlow.step1Desc' },
  { id: 2, titleKey: 'landing.hero.demoFlow.step2Title', descKey: 'landing.hero.demoFlow.step2Desc' },
  { id: 3, titleKey: 'landing.hero.demoFlow.step3Title', descKey: 'landing.hero.demoFlow.step3Desc' },
]

const demoStates = [
  { id: 1, titleKey: 'landing.hero.demoFlow.state1Title', noteKey: 'landing.hero.demoFlow.state1Note' },
  { id: 2, titleKey: 'landing.hero.demoFlow.state2Title', noteKey: 'landing.hero.demoFlow.state2Note' },
  { id: 3, titleKey: 'landing.hero.demoFlow.state3Title', noteKey: 'landing.hero.demoFlow.state3Note' },
]

const currentDemoState = computed(() => demoStates.find((state) => state.id === activeDemoStep.value) ?? demoStates[0])

const motionFactor = computed(() => {
  if (prefersReducedMotion.value) return 0
  return isMobileViewport.value ? 0.45 : 1
})

const parallaxStyleBack = computed(() => ({
  transform: `translate3d(0, ${Math.min(scrollY.value * 0.06 * motionFactor.value, 42)}px, 0)`,
}))

const parallaxStyleFront = computed(() => ({
  transform: `translate3d(0, ${Math.min(scrollY.value * 0.1 * motionFactor.value, 54)}px, 0)`,
}))

const parallaxStyleFrontAlt = computed(() => ({
  transform: `translate3d(0, ${Math.max(scrollY.value * -0.08 * motionFactor.value, -44)}px, 0)`,
}))

const showMobileStickyCta = computed(() => {
  return scrollY.value > 140 && !isFooterVisible.value && !isPrimaryCtaVisible.value
})

function trackCtaClick(source: string) {
  if (!import.meta.client) return

  const payload = {
    event: 'landing_cta_click',
    source,
    at: Date.now(),
  }

  const customWindow = window as Window & { dataLayer?: Array<Record<string, unknown>> }
  if (Array.isArray(customWindow.dataLayer)) {
    customWindow.dataLayer.push(payload)
  }

  window.dispatchEvent(new CustomEvent('landing-cta-click', { detail: payload }))
}

onMounted(() => {
  isMobileViewport.value = window.innerWidth < 768
  prefersReducedMotion.value = window.matchMedia('(prefers-reduced-motion: reduce)').matches

  scrollHandler = () => {
    scrollY.value = window.scrollY
  }
  scrollHandler()
  window.addEventListener('scroll', scrollHandler, { passive: true })

  if (!prefersReducedMotion.value) {
    const demoRotationSpeed = isMobileViewport.value ? 3200 : 2400
    demoTimer = setInterval(() => {
      if (isDemoPaused.value) return
      activeDemoStep.value = activeDemoStep.value >= demoSteps.length ? 1 : activeDemoStep.value + 1
    }, demoRotationSpeed)
  }

  const footerEl = document.querySelector('footer')
  if (footerEl) {
    footerObserver = new IntersectionObserver(
      (entries) => {
        isFooterVisible.value = entries.some((entry) => entry.isIntersecting)
      },
      { threshold: 0.05 }
    )
    footerObserver.observe(footerEl)
  }

  const primaryCtaEl = document.getElementById('hero-primary-cta')
  if (primaryCtaEl instanceof HTMLElement) {
    primaryCtaObserver = new IntersectionObserver(
      (entries) => {
        isPrimaryCtaVisible.value = entries.some((entry) => entry.isIntersecting)
      },
      { threshold: 0.55 }
    )
    primaryCtaObserver.observe(primaryCtaEl)
  }
})

onUnmounted(() => {
  if (demoTimer) clearInterval(demoTimer)
  if (scrollHandler) window.removeEventListener('scroll', scrollHandler)
  footerObserver?.disconnect()
  primaryCtaObserver?.disconnect()
})

const socialProofItems = [
  { valueKey: 'landing.hero.socialProof.restaurantsValue', labelKey: 'landing.hero.socialProof.restaurantsLabel' },
  { valueKey: 'landing.hero.socialProof.ordersValue', labelKey: 'landing.hero.socialProof.ordersLabel' },
  { valueKey: 'landing.hero.socialProof.conversionValue', labelKey: 'landing.hero.socialProof.conversionLabel' },
]

const heroPillars = [
  'landing.hero.pillars.problem',
  'landing.hero.pillars.solution',
  'landing.hero.pillars.result',
]

const successStories = [
  {
    nameKey: 'landing.hero.stories.story1Name',
    metaKey: 'landing.hero.stories.story1Meta',
    quoteKey: 'landing.hero.stories.story1Quote',
    resultKey: 'landing.hero.stories.story1Result',
  },
  {
    nameKey: 'landing.hero.stories.story2Name',
    metaKey: 'landing.hero.stories.story2Meta',
    quoteKey: 'landing.hero.stories.story2Quote',
    resultKey: 'landing.hero.stories.story2Result',
  },
  {
    nameKey: 'landing.hero.stories.story3Name',
    metaKey: 'landing.hero.stories.story3Meta',
    quoteKey: 'landing.hero.stories.story3Quote',
    resultKey: 'landing.hero.stories.story3Result',
  },
]

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

useStaggerReveal(heroRoot)
</script>
