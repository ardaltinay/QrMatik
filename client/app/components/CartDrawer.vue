<template>
  <div>
    <!-- Backdrop -->
    <Transition
      enter-active-class="transition-opacity duration-300"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-300"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div 
        v-if="isOpen" 
        class="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-[60]"
        @click="$emit('update:isOpen', false)"
      ></div>
    </Transition>

    <!-- Drawer -->
    <Transition
      enter-active-class="transition-transform duration-300 ease-out"
      enter-from-class="translate-y-full sm:translate-y-0 sm:translate-x-full"
      enter-to-class="translate-y-0 sm:translate-x-0"
      leave-active-class="transition-transform duration-300 ease-in"
      leave-from-class="translate-y-0 sm:translate-x-0"
      leave-to-class="translate-y-full sm:translate-y-0 sm:translate-x-full"
    >
      <div 
        v-if="isOpen"
        class="fixed bottom-0 sm:top-0 sm:bottom-auto sm:right-0 sm:h-screen w-full sm:w-[420px] bg-white border-t sm:border-t-0 sm:border-l border-slate-100 z-[70] flex flex-col rounded-t-[2.5rem] sm:rounded-none shadow-2xl overflow-hidden max-h-[90vh] sm:max-h-screen"
      >
        <!-- Header -->
        <div class="px-8 py-6 border-b border-slate-50 flex items-center justify-between shrink-0 bg-white/80 backdrop-blur-md">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-2xl flex items-center justify-center shadow-sm text-white" :style="{ backgroundColor: tenantConfig?.primaryColor || '#0f172a' }">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </div>
            <div>
              <h2 class="text-xl font-black text-slate-900 tracking-tight">{{ $t('menu.cart') }}</h2>
              <p class="text-xs text-slate-400 font-bold uppercase tracking-wider">{{ orderStore.cartCount }} {{ $t('menu.quantity') }}</p>
            </div>
          </div>
          
          <div class="flex items-center gap-3">
            <button 
              v-if="orderStore.cart.length > 0"
              @click="orderStore.clearCart()"
              class="text-xs font-bold text-slate-400 hover:text-rose-500 transition-colors px-3 py-2 rounded-lg hover:bg-rose-50 uppercase tracking-widest"
            >
              {{ $t('menu.clearAll') }}
            </button>
            <button 
              @click="$emit('update:isOpen', false)"
              class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 hover:text-slate-900 hover:bg-slate-100 transition-all shadow-sm"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Items -->
        <div class="flex-grow overflow-y-auto p-8 scrollbar-hide">
          <div v-if="orderStore.cart.length === 0" class="flex flex-col items-center justify-center h-full text-center py-12">
            <div class="w-28 h-28 rounded-[2.5rem] bg-slate-50 flex items-center justify-center text-slate-200 mb-8 shadow-inner">
              <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </div>
            <p class="text-xl font-bold text-slate-900 mb-2">{{ $t('menu.cartEmpty') }}</p>
            <p class="text-sm text-slate-400 font-medium max-w-[250px]">{{ $t('menu.cartEmptyDesc') }}</p>
            <button 
              @click="$emit('update:isOpen', false)"
              class="mt-10 px-8 py-3.5 rounded-2xl text-white font-bold text-xs uppercase tracking-widest hover:opacity-90 transition-all shadow-xl"
              :style="{ backgroundColor: tenantConfig?.accentColor || tenantConfig?.primaryColor || '#0f172a' }"
            >
              {{ $t('menu.viewMenu') }}
            </button>
          </div>

          <div v-else class="space-y-6">
            <div 
              v-for="(cartItem, idx) in orderStore.cart" 
              :key="idx"
              class="group flex gap-5 p-5 rounded-3xl bg-slate-50/50 border border-slate-100 relative hover:bg-white hover:shadow-xl hover:shadow-slate-200/50 transition-all duration-300"
            >
              <div v-if="getMenuItem(cartItem.itemId)?.image" class="w-20 h-20 rounded-2xl overflow-hidden shrink-0 shadow-sm">
                <img :src="getMenuItem(cartItem.itemId)!.image" class="w-full h-full object-cover" />
              </div>
              <div v-else class="w-20 h-20 rounded-2xl bg-slate-100 flex items-center justify-center text-slate-300 shrink-0">
                <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>

              <div class="flex-grow min-w-0">
                <h4 class="text-slate-900 font-bold truncate tracking-tight">{{ getLocalizedName(getMenuItem(cartItem.itemId)) || $t('menu.unknownProduct') }}</h4>
                <div class="font-black text-sm mt-1.5" :style="{ color: tenantConfig?.accentColor || tenantConfig?.primaryColor || '#0f172a' }">
                  {{ formatPrice(getLocalizedItemPrice(getMenuItem(cartItem.itemId))) }}
                </div>
                <div v-if="cartItem.note" class="text-[10px] text-slate-500 mt-3 bg-white p-2 rounded-xl border border-slate-100 inline-flex max-w-full font-medium">
                  <svg class="w-3.5 h-3.5 mr-1.5 shrink-0 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                  </svg>
                  <span class="truncate">{{ cartItem.note }}</span>
                </div>
              </div>

              <div class="flex flex-col items-end justify-between shrink-0">
                <button 
                  @click="orderStore.removeFromCart(cartItem.itemId, cartItem.note)"
                  class="text-slate-300 hover:text-rose-500 p-1 -mr-1 transition-colors"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
                <div class="flex items-center gap-2 bg-white rounded-xl p-1.5 border border-slate-100 shadow-sm">
                  <button 
                    @click="orderStore.updateQty(cartItem.itemId, cartItem.note, cartItem.qty - 1)"
                    class="w-7 h-7 flex items-center justify-center rounded-lg text-slate-400 hover:text-brand-600 hover:bg-brand-50 transition-all font-bold"
                  >
                    -
                  </button>
                  <span class="text-sm font-black text-slate-900 w-5 text-center">{{ cartItem.qty }}</span>
                  <button 
                    @click="orderStore.updateQty(cartItem.itemId, cartItem.note, cartItem.qty + 1)"
                    class="w-7 h-7 flex items-center justify-center rounded-lg text-slate-400 hover:text-brand-600 hover:bg-brand-50 transition-all font-bold"
                  >
                    +
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div v-if="orderStore.cart.length > 0" class="p-8 bg-white border-t border-slate-50 shrink-0 shadow-[0_-10px_40px_rgba(0,0,0,0.02)]">
          <div class="flex justify-between items-end mb-8">
            <span class="text-slate-400 font-bold uppercase tracking-widest text-[10px]">{{ $t('menu.total') }}</span>
            <span class="text-4xl font-black text-slate-900 tracking-tight">{{ formatPrice(orderStore.cartTotal) }}</span>
          </div>
          
          <button 
            @click="submitOrder"
            :disabled="isSubmitting"
            class="w-full relative flex justify-center items-center px-8 py-5 rounded-[1.5rem] text-white font-black text-sm uppercase tracking-[0.2em] shadow-2xl hover:opacity-90 hover:-translate-y-1 active:scale-95 transition-all duration-300 disabled:opacity-70 disabled:hover:translate-y-0"
            :style="{ backgroundColor: tenantConfig?.accentColor || tenantConfig?.primaryColor || '#0f172a' }"
          >
            <span v-if="!isSubmitting">{{ $t('menu.placeOrder') }}</span>
            <div v-else class="flex items-center gap-3">
              <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ $t('menu.submitting') }}
            </div>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'
import { useTenant } from '~/composables/useTenant'

const { t, locale } = useI18n()
const { tenantConfig } = useTenant()

const props = defineProps<{
  isOpen: boolean
  tableCode?: string
}>()

const emit = defineEmits<{
  (e: 'update:isOpen', val: boolean): void
  (e: 'orderSuccess', order: any): void
}>()

const orderStore = useOrderStore()
const isSubmitting = ref(false)
const uiStore = useUiStore()

function getMenuItem(id: number) {
  return orderStore.menuItemById(id)
}

function getLocalizedName(item: any) {
  if (!item) return ''
  if (locale.value === 'en' && item.nameEn) {
    return item.nameEn
  }
  return item.name
}

function getLocalizedItemPrice(item: any) {
  if (!item) return 0
  if (locale.value === 'en' && item.priceUsd != null && item.priceUsd > 0) {
    return item.priceUsd
  }
  return item.price
}

function formatPrice(p: number) {
  if (locale.value === 'en') {
    // If the cart total itself is calculated as USD in orderStore, format it as USD here
    // Notice: orderStore.cartTotal relies on orderStore's locale logic as well.
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(p)
  }
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(p)
}

async function submitOrder() {
  if (isSubmitting.value || orderStore.cart.length === 0) return
  if (!props.tableCode) {
    const errorMessage = t('menu.scanQR');
    uiStore.error(errorMessage);
    return
  }

  try {
    isSubmitting.value = true
    const order = await orderStore.createOrder(props.tableCode)
    if (order) {
      emit('update:isOpen', false)
      emit('orderSuccess', order)
    }
  } catch (error) {
    const errorMessage = error?.message || error?.toString() || t('menu.orderError');
    uiStore.error(errorMessage);
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
</style>
