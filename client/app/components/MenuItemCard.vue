<template>
  <div class="group relative flex flex-row sm:flex-col overflow-hidden rounded-2xl bg-white border border-slate-100 hover:border-brand-200 hover:shadow-xl hover:shadow-slate-200/40 transition-all duration-300 transform hover:-translate-y-1">
    
    <!-- Image Area -->
    <div v-if="item.image" class="w-24 h-24 sm:w-full sm:h-36 shrink-0 overflow-hidden relative bg-slate-50">
      <img :src="item.image" :alt="localizedName" class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" loading="lazy" />
      <div v-if="isPopular" class="absolute top-2 left-2 bg-white/90 backdrop-blur-md text-rose-500 text-[8px] font-black uppercase tracking-widest px-2 py-1 rounded-full shadow-sm border border-white/50">
        ✨
      </div>
    </div>
    <div v-else class="w-24 h-24 sm:w-full sm:h-36 shrink-0 bg-slate-50 flex items-center justify-center text-slate-200">
      <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
        <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
    </div>
    
    <!-- Content Area -->
    <div class="flex flex-col flex-grow p-3 sm:p-4 min-w-0">
      <div class="flex flex-col h-full">
        <div class="mb-1">
          <h3 class="text-sm sm:text-base font-black text-slate-900 group-hover:text-brand-600 transition-colors tracking-tight leading-tight truncate">{{ localizedName }}</h3>
          <p v-if="localizedDescription" class="text-[10px] sm:text-xs text-slate-400 mt-0.5 line-clamp-2 font-medium leading-tight">{{ localizedDescription }}</p>
        </div>
        
        <div class="mt-auto flex items-center justify-between gap-2 pt-2">
          <div class="text-sm sm:text-lg font-black tracking-tighter" :style="{ color: tenantConfig?.accentColor || tenantConfig?.primaryColor || '#0f172a' }">
            {{ localizedPrice }}
          </div>
          
          <button 
            v-if="!isOutOfStock"
            @click="$emit('add', item)"
            class="flex items-center justify-center h-8 w-8 sm:h-10 sm:w-10 rounded-xl text-white hover:opacity-90 transition-all duration-300 shadow-lg shadow-brand-500/10 active:scale-90 group/btn overflow-hidden relative"
            :style="{ backgroundColor: tenantConfig?.accentColor || tenantConfig?.primaryColor || '#0f172a' }"
          >
            <svg class="w-5 h-5 relative z-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
          </button>
          <span v-else class="text-[8px] font-black uppercase tracking-widest text-rose-500 bg-rose-50 px-2 py-1 rounded-lg border border-rose-100">
            {{ $t('menu.outOfStock') }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useTenant } from '~/composables/useTenant'

const { tenantConfig } = useTenant()

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
  }
}>()

defineEmits<{
  (e: 'add', item: typeof props.item): void
}>()

const isOutOfStock = computed(() => {
  return props.item.stockEnabled && props.item.stockQuantity !== undefined && props.item.stockQuantity <= 0
})

const isPopular = computed(() => {
  // Just a visual logic hook for demonstration, e.g. maybe driven by sales data later
  return false 
})

const { locale } = useI18n()

const localizedName = computed(() => {
  if (locale.value === 'en' && props.item.nameEn) {
    return props.item.nameEn
  }
  return props.item.name
})

const localizedDescription = computed(() => {
  if (locale.value === 'en' && props.item.descriptionEn) {
    return props.item.descriptionEn
  }
  return props.item.description
})

const localizedPrice = computed(() => {
  if (locale.value === 'en' && props.item.priceUsd != null && props.item.priceUsd > 0) {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(props.item.priceUsd)
  }
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(props.item.price)
})
</script>
