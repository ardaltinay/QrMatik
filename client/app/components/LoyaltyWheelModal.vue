<template>
  <Transition name="modal-overlay">
    <div v-if="isOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <!-- High-End Backdrop -->
      <div class="absolute inset-0 bg-slate-950/60 backdrop-blur-md transition-all duration-500" @click="close"></div>
      
      <!-- Modern Modal Content -->
      <div class="relative w-full max-w-lg bg-white rounded-[3rem] shadow-[0_40px_100px_-20px_rgba(0,0,0,0.4)] overflow-hidden flex flex-col animate-modal-in border border-white/20">
        
        <!-- Premium Header -->
        <div class="bg-gradient-to-br from-brand-500 via-orange-500 to-amber-500 p-10 text-center relative overflow-hidden shrink-0">
          <!-- Animated Background Pattern -->
          <div class="absolute inset-0 opacity-10 mix-blend-overlay animate-pulse-slow">
             <svg width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
               <defs><pattern id="dots" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="2" cy="2" r="2" fill="white"/></pattern></defs>
               <rect width="100%" height="100%" fill="url(#dots)"/>
             </svg>
          </div>
          
          <button @click="close" class="absolute top-6 right-6 w-10 h-10 flex items-center justify-center rounded-2xl bg-white/20 text-white hover:bg-white/40 backdrop-blur-md transition-all z-20 group">
            <svg class="w-6 h-6 group-hover:rotate-90 transition-transform duration-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
          
          <div class="relative z-10">
            <div class="inline-flex items-center gap-2 bg-black/20 px-4 py-1.5 rounded-full mb-4 backdrop-blur-xl border border-white/10 shadow-inner">
              <span class="w-2 h-2 rounded-full bg-yellow-400 animate-ping"></span>
              <span class="text-[10px] font-black uppercase tracking-[0.2em] text-white/90">{{ $t('loyalty.wheel.tag') }}</span>
            </div>
            <h2 class="text-4xl font-black text-white tracking-tighter leading-none mb-3 drop-shadow-sm">{{ $t('loyalty.wheel.title') }}</h2>
            <p class="text-white/80 text-sm font-medium tracking-tight">{{ $t('loyalty.wheel.desc') }}</p>
          </div>
        </div>

        <div class="p-10 flex flex-col items-center bg-slate-50/30">
          
          <!-- Phase 1: Lead Gen -->
          <Transition name="slide-fade" mode="out-in">
            <div v-if="phase === 'lead'" class="w-full text-center space-y-8">
              <div class="w-24 h-24 mx-auto bg-white rounded-[2.5rem] shadow-xl shadow-orange-500/10 flex items-center justify-center text-orange-500 mb-2 border border-orange-50 relative overflow-hidden group">
                <div class="absolute inset-0 bg-orange-500 opacity-0 group-hover:opacity-5 transition-opacity"></div>
                <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z" />
                </svg>
              </div>
              
              <div>
                <h3 class="text-2xl font-black text-slate-900 mb-2 tracking-tight">{{ $t('loyalty.wheel.modalTitle') }}</h3>
                <p class="text-slate-500 font-medium text-sm leading-relaxed">{{ $t('loyalty.wheel.modalDesc') }}</p>
              </div>

              <div class="space-y-4">
                <div class="relative group">
                  <span class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-brand-500 transition-colors">
                    <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
                  </span>
                  <input 
                    v-model="email" 
                    type="email" 
                    :placeholder="$t('loyalty.wheel.emailPlaceholder')" 
                    class="w-full pl-14 pr-6 py-4.5 bg-white border border-slate-200 rounded-[1.5rem] focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-bold text-slate-900 placeholder:font-medium placeholder:text-slate-400 text-lg shadow-sm"
                  />
                </div>
                <label class="flex items-start gap-4 text-left cursor-pointer group px-2">
                  <div class="relative flex items-center justify-center mt-1">
                    <input type="checkbox" v-model="acceptTerms" class="peer sr-only" />
                    <div class="w-6 h-6 border-2 border-slate-200 rounded-xl transition-all peer-checked:bg-brand-500 peer-checked:border-brand-500 group-hover:border-brand-300"></div>
                    <svg class="w-4 h-4 text-white absolute inset-0 m-auto opacity-0 peer-checked:opacity-100 transition-opacity pointer-events-none" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="4">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <span class="text-xs text-slate-400 font-medium leading-tight select-none">{{ $t('loyalty.wheel.terms') }}</span>
                </label>
              </div>

              <button 
                @click="submitLead" 
                :disabled="!isValidForm"
                class="w-full py-5 bg-brand-600 text-white font-black text-sm uppercase tracking-[0.2em] rounded-[1.5rem] shadow-2xl shadow-brand-500/30 disabled:opacity-30 disabled:grayscale transition-all hover:bg-brand-700 active:scale-[0.98]"
              >
                {{ $t('loyalty.wheel.spinNow') }}
              </button>
            </div>

            <!-- Phase 2: Spin the Wheel -->
            <div v-else-if="phase === 'spin'" class="w-full flex flex-col items-center">
              
              <div class="relative w-72 h-72 sm:w-96 sm:h-96 mb-10">
                <!-- Pointer Container -->
                <div class="absolute -top-6 left-1/2 -translate-x-1/2 z-30 drop-shadow-2xl">
                   <div class="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-xl border-4 border-orange-500">
                      <svg class="w-6 h-6 text-orange-600 transform rotate-180" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2L22 20H2L12 2Z"/>
                      </svg>
                   </div>
                </div>
                
                <!-- Physical Wheel -->
                <div 
                  class="w-full h-full rounded-full border-[12px] border-white shadow-[0_32px_64px_-12px_rgba(0,0,0,0.3)] overflow-hidden relative transition-transform ring-4 ring-slate-100"
                  :style="wheelStyle"
                >
                  <svg viewBox="0 0 100 100" class="w-full h-full transform -rotate-90">
                    <defs>
                      <filter id="shadow">
                        <feDropShadow dx="0.5" dy="0.5" stdDeviation="0.5" />
                      </filter>
                    </defs>
                    <g v-for="(prize, index) in prizes" :key="index">
                      <path :d="getPieSlice(index, prizes.length)" :fill="prize.color" />
                      <!-- Slice Separators -->
                      <line x1="50" y1="50" :x2="getSliceLineX(index, prizes.length)" :y2="getSliceLineY(index, prizes.length)" stroke="rgba(255,255,255,0.2)" stroke-width="0.5" />
                      
                      <!-- Text with Shadow -->
                      <text 
                        :transform="getTextTransform(index, prizes.length)"
                        text-anchor="middle"
                        alignment-baseline="middle"
                        fill="#fff"
                        font-size="4.5"
                        font-weight="900"
                        style="filter: url(#shadow);"
                        class="uppercase tracking-tighter"
                      >
                        {{ prize.label }}
                      </text>
                    </g>
                    <!-- Center Decorative -->
                    <circle cx="50" cy="50" r="14" fill="white" class="shadow-xl" />
                    <circle cx="50" cy="50" r="11" fill="#f8fafc" />
                    <text x="50" y="52" text-anchor="middle" font-size="3.5" font-weight="950" fill="#f97316" class="tracking-widest">FEASY</text>
                  </svg>
                </div>
                
                <!-- Outer Lights Effect -->
                <div class="absolute inset-[-12px] border-[1px] border-white/20 rounded-full"></div>
              </div>

              <button 
                v-if="!isSpinning"
                @click="spinWheel"
                class="w-full py-5 bg-orange-500 text-white font-black text-sm uppercase tracking-[0.2em] rounded-[1.5rem] shadow-2xl shadow-orange-500/30 hover:bg-orange-600 active:scale-95 transition-all"
              >
                {{ $t('order.loyalty.spinNow') }}
              </button>
              <div v-else class="h-16 flex items-center justify-center">
                <div class="flex items-center gap-3 bg-orange-50 px-6 py-3 rounded-2xl border border-orange-100">
                   <div class="w-2 h-2 rounded-full bg-orange-500 animate-ping"></div>
                   <p class="text-orange-600 font-black text-xs uppercase tracking-[0.2em]">{{ $t('order.loyalty.spinning') }}</p>
                </div>
              </div>

            </div>

            <!-- Phase 3: Result -->
            <div v-else-if="phase === 'result'" class="w-full text-center space-y-8">
              <div class="relative">
                <div class="w-24 h-24 mx-auto bg-emerald-50 rounded-[2.5rem] flex items-center justify-center text-emerald-500 border border-emerald-100 animate-bounce shadow-xl shadow-emerald-500/10">
                  <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4" />
                    <circle cx="12" cy="12" r="9" />
                  </svg>
                </div>
              </div>
              
              <div>
                <h3 class="text-3xl font-black text-slate-900 mb-2 tracking-tight">{{ $t('order.loyalty.congrats') }}</h3>
                <p class="text-slate-500 font-semibold text-lg">
                  {{ $t('order.loyalty.wonPrize') }} <br/>
                  <span class="text-brand-600 font-black text-2xl uppercase tracking-tight">{{ wonPrize?.label }}</span>
                </p>
              </div>

              <!-- Premium Reward Card -->
              <div class="bg-white border-2 border-slate-100 rounded-[2.5rem] p-8 shadow-2xl shadow-slate-200/50 relative overflow-hidden group/card">
                <div class="absolute top-0 right-0 p-4 opacity-5">
                   <svg class="w-20 h-20" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                </div>
                <p class="text-[10px] uppercase font-black tracking-[0.3em] text-slate-400 mb-3">{{ $t('order.loyalty.yourCode') }}</p>
                <div class="text-4xl font-black tracking-[0.2em] text-slate-900 mb-6 bg-slate-50 py-4 rounded-2xl border border-slate-100 shadow-inner">{{ rewardCode }}</div>
                
                <button 
                  @click="copyCode" 
                  class="w-full flex items-center justify-center gap-3 bg-slate-900 text-white py-4 rounded-2xl text-xs font-black uppercase tracking-widest hover:bg-slate-800 transition-all shadow-lg shadow-slate-900/20"
                >
                  <svg v-if="!isCopied" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" /></svg>
                  <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
                  <span>{{ isCopied ? $t('order.copied') : $t('order.loyalty.copyCode') }}</span>
                </button>
              </div>

              <button 
                @click="close"
                class="text-slate-400 font-bold hover:text-slate-600 transition-colors uppercase tracking-widest text-[10px]"
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
  const rawPrizes = (customPrizes.value && customPrizes.value.length > 0) 
    ? customPrizes.value 
    : [
        { type: 'discount', label: t('admin.loyalty.prizes.percentageDiscount') || 'İndirim', color: '#f97316', discountPercent: 15 },
        { type: 'freeDrink', label: t('admin.loyalty.prizes.freeDrink') || 'İçecek', color: '#fbbf24', discountPercent: 0 },
        { type: 'surpriseDessert', label: t('admin.loyalty.prizes.surpriseDessert') || 'Tatlı', color: '#ec4899', discountPercent: 0 }, 
        { type: 'pass', label: t('admin.loyalty.prizes.pass') || 'Pas', color: '#94a3b8', discountPercent: 0 },
      ]
  
  return rawPrizes.map(p => {
    if (p.type === 'discount' && p.discountPercent > 0) {
      return { ...p, label: `%${p.discountPercent} OFF` }
    }
    return p
  })
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
  if (isSpinning.value) return
  emit('close')
  setTimeout(() => {
    const hasSpun = import.meta.client && localStorage.getItem('feasymenu_has_spun') === 'true'
    if (!hasSpun) phase.value = 'lead'
    isCopied.value = false
  }, 500)
}

function submitLead() {
  if (!isValidForm.value) return
  phase.value = 'spin'
}

async function spinWheel() {
  if (isSpinning.value) return
  isSpinning.value = true
  
  const targetIndex = Math.floor(Math.random() * (prizes.value.length - 1)) 
  wonPrize.value = prizes.value[targetIndex]
  
  try {
    const res = await fetchJson('/api/loyalty/spin', {
      method: 'POST',
      headers: { 'X-Session-Id': getOrCreateSessionId() },
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
    uiStore.error(e.data?.message || 'Error spinning')
    isSpinning.value = false
    close()
    return
  }

  const sliceAngle = 360 / prizes.value.length
  const targetAngle = (targetIndex * sliceAngle) + (sliceAngle / 2)
  const spins = 8 * 360 // More spins for dramatic effect
  wheelRotation.value = spins + (360 - targetAngle)

  setTimeout(() => {
    isSpinning.value = false
    phase.value = 'result'
    try {
      import('canvas-confetti').then(confetti => {
        confetti.default({
          particleCount: 150,
          spread: 80,
          origin: { y: 0.6 },
          colors: ['#f97316', '#fbbf24', '#ffffff']
        })
      })
    } catch(e) {}
  }, 4000)
}

function copyCode() {
  navigator.clipboard.writeText(rewardCode.value)
  isCopied.value = true
  setTimeout(() => isCopied.value = false, 2000)
}

const wheelStyle = computed(() => ({
  transform: `rotate(${wheelRotation.value}deg)`,
  transitionDuration: isSpinning.value ? '4s' : '0s',
  transitionTimingFunction: 'cubic-bezier(0.1, 0.8, 0.1, 1)' 
}))

function getPieSlice(index: number, total: number) {
  const cx = 50, cy = 50, radius = 50
  const startAngle = (index * 360) / total
  const endAngle = ((index + 1) * 360) / total
  const start = polarToCartesian(cx, cy, radius, startAngle)
  const end = polarToCartesian(cx, cy, radius, endAngle)
  const largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1"
  return ["M", cx, cy, "L", start.x, start.y, "A", radius, radius, 0, largeArcFlag, 1, end.x, end.y, "Z"].join(" ")
}

function polarToCartesian(centerX: number, centerY: number, radius: number, angleInDegrees: number) {
  const angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0
  return {
    x: centerX + (radius * Math.cos(angleInRadians)),
    y: centerY + (radius * Math.sin(angleInRadians))
  }
}

function getSliceLineX(index: number, total: number) { return polarToCartesian(50, 50, 50, (index * 360) / total).x }
function getSliceLineY(index: number, total: number) { return polarToCartesian(50, 50, 50, (index * 360) / total).y }

function getTextTransform(index: number, total: number) {
  const angle = ((index + 0.5) * 360) / total
  const coords = polarToCartesian(50, 50, 32, angle)
  return `translate(${coords.x}, ${coords.y}) rotate(${angle})`
}
</script>

<style scoped>
.modal-overlay-enter-active, .modal-overlay-leave-active { transition: opacity 0.5s ease; }
.modal-overlay-enter-from, .modal-overlay-leave-to { opacity: 0; }

@keyframes modal-in {
  from { transform: translateY(40px) scale(0.9); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}
.animate-modal-in { animation: modal-in 0.7s cubic-bezier(0.16, 1, 0.3, 1); }

@keyframes pulse-slow {
  0%, 100% { opacity: 0.1; }
  50% { opacity: 0.2; }
}
.animate-pulse-slow { animation: pulse-slow 4s infinite; }

.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.4s ease; }
.slide-fade-enter-from { opacity: 0; transform: translateY(20px); }
.slide-fade-leave-to { opacity: 0; transform: translateY(-20px); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
