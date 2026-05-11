<template>
  <Transition name="fade">
    <div v-if="isOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-slate-900/80 backdrop-blur-sm" @click="close"></div>
      
      <!-- Modal Content -->
      <div class="relative w-full max-w-lg bg-white rounded-[2.5rem] shadow-2xl shadow-brand-500/20 overflow-hidden flex flex-col">
        
        <!-- Header -->
        <div class="bg-gradient-to-br from-amber-500 to-orange-500 p-8 text-center relative overflow-hidden shrink-0">
          <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI4IiBoZWlnaHQ9IjgiPjxyZWN0IHdpZHRoPSI4IiBoZWlnaHQ9IjgiIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4xIi8+PC9zdmc+')] opacity-20 mix-blend-overlay"></div>
          <button @click="close" class="absolute top-4 right-4 w-8 h-8 flex items-center justify-center rounded-full bg-white/20 text-white hover:bg-white/40 transition-colors z-10">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
          
          <div class="relative z-10">
            <div class="inline-flex items-center gap-2 bg-white/20 px-3 py-1 rounded-full mb-3 backdrop-blur-md border border-white/20 shadow-sm">
              <span class="w-2 h-2 rounded-full bg-yellow-300 animate-pulse"></span>
              <span class="text-[10px] font-black uppercase tracking-widest text-white">{{ $t('loyalty.wheel.tag') }}</span>
            </div>
            <h2 class="text-3xl font-black text-white tracking-tight leading-tight mb-2">{{ $t('loyalty.wheel.title') }}</h2>
            <p class="text-white/90 text-sm font-medium">{{ $t('loyalty.wheel.desc') }}</p>
          </div>
        </div>

        <div class="p-8 flex flex-col items-center">
          
          <!-- Phase 1: Lead Gen (Phone Number) -->
          <Transition name="slide-up" mode="out-in">
            <div v-if="phase === 'lead'" class="w-full text-center space-y-6">
              <div class="w-24 h-24 mx-auto bg-orange-50 rounded-full flex items-center justify-center text-orange-500 mb-2">
                <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z" />
                </svg>
              </div>
              
              <div>
                <h3 class="text-xl font-black text-slate-900 mb-2">{{ $t('loyalty.wheel.modalTitle') }}</h3>
                <p class="text-slate-500 text-sm">{{ $t('loyalty.wheel.modalDesc') }}</p>
              </div>

              <div class="space-y-3">
                <div class="relative">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
                  </span>
                  <input 
                    v-model="email" 
                    type="email" 
                    :placeholder="$t('loyalty.wheel.emailPlaceholder')" 
                    class="w-full pl-12 pr-4 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-orange-500/20 focus:border-orange-500 outline-none transition-all font-bold text-slate-900 placeholder:font-medium placeholder:text-slate-400 text-base tracking-wide"
                  />
                </div>
                <label class="flex items-start gap-3 text-left cursor-pointer group">
                  <div class="relative flex items-center justify-center mt-0.5">
                    <input type="checkbox" v-model="acceptTerms" class="peer sr-only" />
                    <div class="w-5 h-5 border-2 border-slate-300 rounded md transition-all peer-checked:bg-orange-500 peer-checked:border-orange-500 group-hover:border-orange-400"></div>
                    <svg class="w-3.5 h-3.5 text-white absolute inset-0 m-auto opacity-0 peer-checked:opacity-100 transition-opacity pointer-events-none" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <span class="text-xs text-slate-500 leading-snug">{{ $t('loyalty.wheel.terms') }}</span>
                </label>
              </div>

              <button 
                @click="submitLead" 
                :disabled="!isValidForm"
                class="w-full py-4 bg-orange-500 text-white font-black text-sm uppercase tracking-[0.15em] rounded-2xl shadow-xl shadow-orange-500/20 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-orange-600 active:scale-95 transition-all"
              >
                {{ $t('loyalty.wheel.spinNow') }}
              </button>
            </div>

            <!-- Phase 2: Spin the Wheel -->
            <div v-else-if="phase === 'spin'" class="w-full flex flex-col items-center">
              
              <div class="relative w-64 h-64 sm:w-80 sm:h-80 mb-8">
                <!-- Pointer -->
                <div class="absolute -top-4 left-1/2 -translate-x-1/2 z-20 text-orange-600 drop-shadow-lg">
                  <svg class="w-8 h-8 rotate-180" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2L22 20H2L12 2Z"/>
                  </svg>
                </div>
                
                <!-- Wheel -->
                <div 
                  class="w-full h-full rounded-full border-4 border-slate-100 shadow-2xl overflow-hidden relative transition-transform"
                  :style="wheelStyle"
                >
                  <svg viewBox="0 0 100 100" class="w-full h-full transform -rotate-90">
                    <g v-for="(prize, index) in prizes" :key="index">
                      <!-- 6 slices = 60 degrees each = 16.66% of stroke-dasharray. Actually, using arc paths is cleaner -->
                      <path :d="getPieSlice(index, prizes.length)" :fill="prize.color" />
                      <!-- Text -->
                      <text 
                        :transform="getTextTransform(index, prizes.length)"
                        text-anchor="middle"
                        alignment-baseline="middle"
                        fill="#fff"
                        font-size="5"
                        font-weight="bold"
                        class="drop-shadow-md"
                      >
                        {{ prize.label }}
                      </text>
                    </g>
                    <!-- Center Circle -->
                    <circle cx="50" cy="50" r="15" fill="#fff" class="shadow-inner" />
                    <circle cx="50" cy="50" r="12" fill="#f8fafc" />
                    <text x="50" y="52" text-anchor="middle" font-size="4" font-weight="900" fill="#f97316">FEASY</text>
                  </svg>
                </div>
              </div>

              <button 
                v-if="!isSpinning"
                @click="spinWheel"
                class="w-full py-4 bg-orange-500 text-white font-black text-sm uppercase tracking-[0.15em] rounded-2xl shadow-xl shadow-orange-500/20 hover:bg-orange-600 active:scale-95 transition-all"
              >
                {{ $t('order.loyalty.spinNow') }}
              </button>
              <div v-else class="h-14 flex items-center justify-center">
                <p class="text-orange-500 font-bold animate-pulse uppercase tracking-widest">{{ $t('order.loyalty.spinning') }}</p>
              </div>

            </div>

            <!-- Phase 3: Result / Reward -->
            <div v-else-if="phase === 'result'" class="w-full text-center space-y-6">
              <div class="w-24 h-24 mx-auto bg-emerald-50 rounded-full flex items-center justify-center text-emerald-500 mb-2 animate-bounce">
                <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4" />
                </svg>
              </div>
              
              <div>
                <h3 class="text-2xl font-black text-slate-900 mb-2">{{ $t('order.loyalty.congrats') }}</h3>
                <p class="text-slate-500 font-medium text-lg mb-1">
                  {{ $t('order.loyalty.wonPrize') }} <strong class="text-orange-600 font-black">{{ wonPrize?.label }}</strong>
                </p>
                <p class="text-slate-400 text-sm">{{ $t('order.loyalty.useNextOrder') }}</p>
              </div>

              <div class="bg-slate-50 border-2 border-dashed border-slate-300 rounded-2xl p-6 relative group">
                <p class="text-[10px] uppercase font-black tracking-widest text-slate-400 mb-2">{{ $t('order.loyalty.yourCode') }}</p>
                <div class="text-3xl font-black tracking-widest text-slate-900">{{ rewardCode }}</div>
                
                <button 
                  @click="copyCode" 
                  class="mt-4 inline-flex items-center gap-2 bg-white border border-slate-200 px-4 py-2 rounded-xl text-xs font-bold text-slate-600 hover:bg-slate-50 hover:text-orange-600 transition-colors shadow-sm"
                >
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                  </svg>
                  <span>{{ isCopied ? $t('order.copied') : $t('order.loyalty.copyCode') }}</span>
                </button>
              </div>

              <button 
                @click="close"
                class="w-full py-4 bg-slate-900 text-white font-black text-sm uppercase tracking-[0.15em] rounded-2xl shadow-xl hover:bg-slate-800 active:scale-95 transition-all"
              >
                {{ $t('order.loyalty.closeBtn') }}
              </button>
            </div>
          </Transition>

        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUiStore } from '~/stores/ui'
import { getOrCreateSessionId } from '~/stores/order'

const props = defineProps<{
  isOpen: boolean
}>()

const emit = defineEmits(['close'])

const phase = ref<'lead' | 'spin' | 'result'>('lead')
const { t } = useI18n()
const uiStore = useUiStore()

const email = ref('')
const acceptTerms = ref(false)

const isSpinning = ref(false)
const wheelRotation = ref(0)
const rewardCode = ref('TATLI15')
const wonPrize = ref<any>(null)
const isCopied = ref(false)
const { fetchJson } = useApi()

const isValidForm = computed(() => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value) && acceptTerms.value
})

const customPrizes = ref<any[]>([])

const prizes = computed(() => {
  if (customPrizes.value && customPrizes.value.length > 0) return customPrizes.value
  return [
    { label: t('loyalty.prizes.discount15') || '%15 İndirim', color: '#f97316', discountPercent: 15 },
    { label: t('loyalty.prizes.freeDrink') || 'Ücretsiz İçecek', color: '#fbbf24', discountPercent: 0 },
    { label: t('loyalty.prizes.discount10') || '%10 İndirim', color: '#f97316', discountPercent: 10 }, 
    { label: t('loyalty.prizes.surpriseDessert') || 'Sürpriz Tatlı', color: '#fbbf24', discountPercent: 0 },
    { label: t('loyalty.prizes.discount20') || '%20 İndirim', color: '#ea580c', discountPercent: 20 },
    { label: t('loyalty.prizes.pass') || 'Pas', color: '#94a3b8', discountPercent: 0 },
  ]
})

onMounted(async () => {
  if (import.meta.client) {
    const hasSpun = localStorage.getItem('feasymenu_has_spun') === 'true'
    if (hasSpun) {
      const savedCode = localStorage.getItem('feasymenu_reward_code')
      const savedPrize = localStorage.getItem('feasymenu_reward_prize')
      if (savedCode && savedPrize) {
        rewardCode.value = savedCode
        wonPrize.value = { label: savedPrize }
        phase.value = 'result'
      }
    }
  }

  try {
    const camp = await fetchJson('/api/loyalty/campaign')
    if (camp && camp.active && camp.prizesJson) {
      customPrizes.value = JSON.parse(camp.prizesJson)
    }
  } catch (e) {}
})

function close() {
  if (isSpinning.value) return // Don't close while spinning
  emit('close')
  // Reset after animation
  setTimeout(() => {
    const hasSpun = import.meta.client && localStorage.getItem('feasymenu_has_spun') === 'true'
    if (!hasSpun) {
      phase.value = 'lead'
    }
    email.value = ''
    acceptTerms.value = false
    wheelRotation.value = 0
    isCopied.value = false
  }, 300)
}

function submitLead() {
  if (!isValidForm.value) return
  // Here we would normally save the lead to the API
  phase.value = 'spin'
}

async function spinWheel() {
  if (isSpinning.value) return
  isSpinning.value = true
  
  // Target a random prize (except Pas ideally)
  const targetIndex = Math.floor(Math.random() * (prizes.value.length - 1)) 
  wonPrize.value = prizes.value[targetIndex]
  
  try {
    const res = await fetchJson('/api/loyalty/spin', {
      method: 'POST',
      headers: {
        'X-Session-Id': getOrCreateSessionId()
      },
      body: JSON.stringify({
        email: email.value,
        prizeLabel: wonPrize.value.label,
        discountPercent: wonPrize.value.discountPercent || 0
      })
    })
    rewardCode.value = res?.code || 'FEASY15'
    
    if (import.meta.client) {
      localStorage.setItem('feasymenu_has_spun', 'true')
      localStorage.setItem('feasymenu_reward_code', rewardCode.value)
      localStorage.setItem('feasymenu_reward_prize', wonPrize.value.label)
    }
  } catch (e: any) {
    const msg = e.data?.message || t('order.loyalty.alreadySpun', 'Zaten bir kez çarkı çevirdiniz.')
    uiStore.error(msg)
    isSpinning.value = false
    close()
    return
  }

  // Calculate rotation (spin at least 5 times)
  const sliceAngle = 360 / prizes.value.length
  // Center of the target slice
  const targetAngle = (targetIndex * sliceAngle) + (sliceAngle / 2)
  // We need to rotate such that the target angle lands at the TOP (which is 270 degrees in SVG coordinates)
  // Actually, simpler: pointer is at top (0 degrees).
  // Current rotation is 0.
  const spins = 5 * 360
  // Offset to make the target slice land on top
  const finalRotation = spins + (360 - targetAngle)

  wheelRotation.value = finalRotation

  setTimeout(() => {
    isSpinning.value = false
    phase.value = 'result'
    // Fire confetti if available
    try {
      import('canvas-confetti').then(confetti => {
        confetti.default({
          particleCount: 100,
          spread: 70,
          origin: { y: 0.6 },
          colors: ['#f97316', '#fbbf24', '#ffffff']
        })
      })
    } catch(e) {}
  }, 4000) // 4 seconds spin duration
}

function copyCode() {
  navigator.clipboard.writeText(rewardCode.value)
  isCopied.value = true
  setTimeout(() => {
    isCopied.value = false
  }, 2000)
}

const wheelStyle = computed(() => {
  return {
    transform: `rotate(${wheelRotation.value}deg)`,
    transitionDuration: isSpinning.value ? '4s' : '0s',
    transitionTimingFunction: 'cubic-bezier(0.25, 1, 0.5, 1)' // ease-out smooth
  }
})

// Math helper for drawing SVG pie slices
function getPieSlice(index: number, total: number) {
  const cx = 50, cy = 50, radius = 50
  const startAngle = (index * 360) / total
  const endAngle = ((index + 1) * 360) / total

  const start = polarToCartesian(cx, cy, radius, startAngle)
  const end = polarToCartesian(cx, cy, radius, endAngle)
  const largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1"

  return [
    "M", cx, cy,
    "L", start.x, start.y,
    "A", radius, radius, 0, largeArcFlag, 1, end.x, end.y,
    "Z"
  ].join(" ")
}

function polarToCartesian(centerX: number, centerY: number, radius: number, angleInDegrees: number) {
  const angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0
  return {
    x: centerX + (radius * Math.cos(angleInRadians)),
    y: centerY + (radius * Math.sin(angleInRadians))
  }
}

function getTextTransform(index: number, total: number) {
  const cx = 50, cy = 50, radius = 30 // distance from center
  const angle = ((index + 0.5) * 360) / total
  const coords = polarToCartesian(cx, cy, radius, angle)
  // Rotate text to align with radius
  return `translate(${coords.x}, ${coords.y}) rotate(${angle})`
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
