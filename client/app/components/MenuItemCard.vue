<template>
  <div 
    class="group relative flex items-center gap-4 p-4 bg-white rounded-[2rem] border border-slate-100 shadow-sm transition-all duration-500 hover:shadow-2xl hover:shadow-brand-400/10 active:scale-[0.98] cursor-pointer animate-slide-up"
    :style="{ animationDelay: delay }"
    @click="$emit('add', item)"
  >
    <!-- Modern Image Area -->
    <div class="relative w-24 h-24 sm:w-28 sm:h-28 rounded-2xl overflow-hidden bg-slate-50 shrink-0 border border-slate-100/50">
      <NuxtImg 
        v-if="item.image" 
        :src="item.image" 
        :alt="localizedName" 
        format="webp"
        class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" 
        loading="lazy" 
      />
      <div v-else class="w-full h-full flex items-center justify-center text-slate-200">
        <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </div>

      <!-- Out of stock overlay -->
      <div v-if="isOutOfStock" class="absolute inset-0 bg-white/70 backdrop-blur-[2px] flex items-center justify-center p-2 text-center">
        <span class="text-[10px] font-black uppercase tracking-tighter text-rose-600 leading-none">
          {{ $t('menu.outOfStock') }}
        </span>
      </div>

      <!-- Stock quantity badge -->
      <div v-else-if="item.stockEnabled && item.stockQuantity !== undefined" class="absolute bottom-2 right-2 px-2 py-1 bg-white/90 backdrop-blur-sm rounded-full shadow-sm border border-slate-100 flex items-center justify-center min-w-[2.5rem]">
        <span class="text-[9px] font-black" :class="item.stockQuantity < 5 ? 'text-rose-500' : 'text-slate-600'">
          {{ item.stockQuantity }}
        </span>
      </div>
    </div>
    
    <!-- Premium Content Area -->
    <div class="flex flex-col justify-center flex-grow pr-2 min-w-0">
      <div class="flex justify-between items-start gap-2 mb-1.5">
        <h3 class="text-base sm:text-lg font-bold text-slate-900 leading-tight truncate tracking-tight">
          {{ localizedName }}
        </h3>
        <div v-if="isPopular" class="bg-brand-50 px-2.5 py-1 rounded-full text-[8px] font-black text-brand-600 uppercase tracking-widest shrink-0">
          POPULAR
        </div>
      </div>

      <p v-if="localizedDescription" class="text-xs text-slate-400 line-clamp-2 font-medium leading-relaxed mb-3">
        {{ localizedDescription }}
      </p>
      
      <div class="flex items-center justify-between mt-auto">
        <div class="text-lg sm:text-xl font-black text-slate-900 tracking-tighter">
          {{ localizedPrice }}
        </div>
        
        <div 
          class="w-10 h-10 rounded-2xl bg-slate-50 flex items-center justify-center transition-all duration-300 shadow-sm border border-slate-100 group-hover:shadow-lg group-hover:-translate-y-1"
          :style="{ 
            backgroundColor: isHovered ? (tenantColor || '#0f172a') : '',
            color: isHovered ? contrastColor : '#0f172a'
          }"
          @mouseenter="isHovered = true"
          @mouseleave="isHovered = false"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useTenant } from '~/composables/useTenant'

const props = defineProps<{
  item: {
    id: number
    name: string
    nameEn?: string
    description?: string
    descriptionEn?: string
    price: number
    priceUsd?: number
    category: string
    subcategory?: string
    image?: string
    stockEnabled?: boolean
    stockQuantity?: number
  },
  delay?: string
}>()

defineEmits<{
  (e: 'add', item: typeof props.item): void
}>()

const { tenantConfig } = useTenant()
const tenantColor = computed(() => tenantConfig.value?.primaryColor)
const isHovered = ref(false)

function getContrastColor(hexcolor: string) {
  if (!hexcolor || !hexcolor.startsWith('#')) return 'white'
  const r = parseInt(hexcolor.substring(1, 3), 16)
  const g = parseInt(hexcolor.substring(3, 5), 16)
  const b = parseInt(hexcolor.substring(5, 7), 16)
  const yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000
  return (yiq >= 128) ? '#0f172a' : 'white'
}

const contrastColor = computed(() => getContrastColor(tenantColor.value || '#0f172a'))

const isOutOfStock = computed(() => {
  return props.item.stockEnabled && props.item.stockQuantity !== undefined && props.item.stockQuantity <= 0
})

const isPopular = computed(() => false)

const { locale } = useI18n()

const localizedName = computed(() => {
  if (locale.value === 'en' && props.item.nameEn) return props.item.nameEn
  return props.item.name
})

const localizedDescription = computed(() => {
  if (locale.value === 'en' && props.item.descriptionEn) return props.item.descriptionEn
  return props.item.description
})

const localizedPrice = computed(() => {
  if (locale.value === 'en' && props.item.priceUsd != null && props.item.priceUsd > 0) {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(props.item.priceUsd)
  }
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(props.item.price)
})
</script>

<style scoped>
@keyframes slide-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.animate-slide-up {
  opacity: 0;
  animation: slide-up 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
</style>
