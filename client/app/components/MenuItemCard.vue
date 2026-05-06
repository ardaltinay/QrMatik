<template>
  <div 
    class="group relative flex items-center gap-4 p-3 bg-white rounded-3xl border border-slate-100 shadow-sm transition-all duration-500 hover:shadow-xl hover:shadow-brand-400/5 active:scale-[0.98] cursor-pointer"
    @click="$emit('add', item)"
  >
    <!-- Compact Image Area -->
    <div class="relative w-24 h-24 sm:w-32 sm:h-32 rounded-2xl overflow-hidden bg-slate-50 shrink-0">
      <img 
        v-if="item.image" 
        :src="item.image" 
        :alt="localizedName" 
        class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" 
        loading="lazy" 
      />
      <div v-else class="w-full h-full flex items-center justify-center text-slate-200">
        <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </div>

      <!-- Out of stock overlay -->
      <div v-if="isOutOfStock" class="absolute inset-0 bg-white/60 backdrop-blur-[2px] flex items-center justify-center p-2 text-center">
        <span class="text-[8px] font-black uppercase tracking-tighter text-rose-600 leading-none">
          {{ $t('menu.outOfStock') }}
        </span>
      </div>
    </div>
    
    <!-- Elegant Content Area -->
    <div class="flex flex-col justify-center flex-grow pr-2 min-w-0">
      <div class="flex justify-between items-start gap-2 mb-1">
        <h3 class="text-base sm:text-lg font-bold text-slate-900 leading-tight truncate">
          {{ localizedName }}
        </h3>
        <div v-if="isPopular" class="bg-brand-50 px-2 py-0.5 rounded-md text-[8px] font-black text-brand-600 uppercase tracking-widest shrink-0">
          POPULAR
        </div>
      </div>

      <p v-if="localizedDescription" class="text-xs text-slate-400 line-clamp-2 font-medium leading-relaxed mb-3 pr-4">
        {{ localizedDescription }}
      </p>
      
      <div class="flex items-center justify-between mt-auto">
        <div class="text-lg sm:text-xl font-black text-slate-900 tracking-tighter">
          {{ localizedPrice }}
        </div>
        
        <div class="w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 group-hover:bg-brand-400 group-hover:text-white transition-all duration-300">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
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
