<template>
  <div class="min-h-screen bg-slate-50 pb-32 font-sans" :style="{ fontFamily: tenantConfig?.fontFamily || 'Plus Jakarta Sans, sans-serif' }">
    <!-- Premium Floating App Header -->
    <header class="sticky top-0 z-40 w-full pt-3 pb-3 px-3 sm:px-4 transition-all duration-300">
      <div 
        class="relative overflow-hidden rounded-[2rem] shadow-2xl shadow-slate-900/10 border border-white/20 backdrop-blur-2xl transition-all duration-500"
        :style="{ 
          background: `linear-gradient(135deg, ${effectiveColor}F2 0%, ${effectiveColor} 100%)`, 
          color: headerTextColor 
        }"
      >
        <!-- Decorative Ambient Glows -->
        <div class="absolute -top-24 -right-24 w-48 h-48 bg-white/20 blur-[50px] rounded-full animate-pulse pointer-events-none"></div>
        <div class="absolute -bottom-12 -left-12 w-32 h-32 bg-black/10 blur-[40px] rounded-full pointer-events-none"></div>

        <div class="relative z-10 flex items-center justify-between gap-3 p-4 sm:p-5">
          
          <!-- Logo & Brand Info -->
          <div class="flex items-center gap-4">
            <div class="relative shrink-0">
              <div v-if="effectiveLogo" class="h-14 w-14 sm:h-16 sm:w-16 rounded-2xl overflow-hidden bg-white p-2 shadow-inner border border-slate-100/50 transform transition-transform hover:scale-105">
                <NuxtImg :src="effectiveLogo" format="webp" preload fetchpriority="high" class="h-full w-full object-contain" alt="Restaurant Logo" />
              </div>
              <div v-else class="h-14 w-14 sm:h-16 sm:w-16 rounded-2xl bg-white/20 flex items-center justify-center text-white font-black text-3xl border border-white/30 shadow-inner backdrop-blur-md">
                {{ (tenantConfig?.name || 'F').charAt(0) }}
              </div>
              
              <!-- Premium Table Badge -->
              <div v-if="tableCode" class="absolute -bottom-2 -right-2 bg-slate-900 text-white text-[10px] font-black uppercase tracking-widest px-2.5 py-1 rounded-full shadow-xl border border-slate-700/50 flex items-center gap-1.5 z-10">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
                {{ tableCode }}
              </div>
            </div>
            
            <div class="flex flex-col justify-center">
              <span class="text-[9px] uppercase tracking-[0.2em] font-black opacity-70 mb-0.5 drop-shadow-sm">{{ $t('common.menu') }}</span>
              <h1 class="text-xl sm:text-2xl font-black tracking-tight leading-none drop-shadow-sm line-clamp-1 max-w-[140px] sm:max-w-[200px]">
                {{ tenantConfig?.name }}
              </h1>
            </div>
          </div>
          
          <!-- Action Buttons Pill -->
          <div class="flex items-center gap-1 bg-black/10 p-1.5 rounded-full backdrop-blur-md border border-white/10 shadow-inner">
            <!-- Info Button -->
            <button 
              @click="isInfoOpen = true"
              class="w-10 h-10 sm:w-11 sm:h-11 rounded-full flex items-center justify-center transition-all duration-300 relative group overflow-hidden hover:bg-white/20 active:scale-90"
            >
              <svg class="w-5 h-5 relative z-10" :style="{ color: headerTextColor }" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            </button>

            <!-- Orders Button -->
            <NuxtLink 
              v-if="hasSession"
              to="/order/history"
              class="w-10 h-10 sm:w-11 sm:h-11 rounded-full flex items-center justify-center transition-all duration-300 relative group overflow-hidden hover:bg-white/20 active:scale-90"
              :title="$t('order.myOrders')"
            >
              <svg class="w-5 h-5 relative z-10" :style="{ color: headerTextColor }" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </NuxtLink>
          </div>
        </div>
      </div>
    </header>

    <!-- Story Highlights (Featured Menu Items) -->
    <div v-if="storyItems?.length > 0" class="pt-6 pb-2 pl-4 sm:pl-6 overflow-x-auto scrollbar-hide flex items-center gap-4">
      <div 
        v-for="(story, idx) in storyItems" 
        :key="idx" 
        @click="openStory(idx)"
        class="flex flex-col items-center gap-2 shrink-0 cursor-pointer group"
      >
        <div 
          class="w-20 h-20 rounded-full p-[3px] transition-transform duration-300 group-hover:scale-105"
          :style="{ background: `linear-gradient(45deg, ${effectiveColor || '#f43f5e'}, #fbbf24, #f43f5e)` }"
        >
          <div class="w-full h-full rounded-full border-[3px] border-slate-50 overflow-hidden bg-white">
            <NuxtImg :src="story.image" class="w-full h-full object-cover" />
          </div>
        </div>
        <span class="text-[10px] font-black uppercase tracking-widest text-slate-700 w-20 text-center truncate drop-shadow-sm">
          {{ getLocalizedName(story) }}
        </span>
      </div>
    </div>

    <!-- Categories Navigation (Immediately below header) -->
    <nav class="sticky top-[112px] sm:top-[128px] z-30 w-full bg-slate-50/90 backdrop-blur-xl border-b border-slate-200/50 py-3 px-4 sm:px-6 overflow-hidden shadow-sm">
      <div class="flex items-center gap-2 overflow-x-auto scrollbar-hide">
        <button
          v-for="cat in categories"
          :key="cat"
          @click="selectedCategory = cat"
          class="whitespace-nowrap px-6 py-2.5 rounded-2xl text-[10px] font-black uppercase tracking-widest transition-all duration-300"
          :style="{ 
            backgroundColor: selectedCategory === cat ? effectiveColor : 'white',
            color: selectedCategory === cat ? headerTextColor : '#64748b',
          }"
          :class="[
            selectedCategory === cat ? 'shadow-lg shadow-brand-400/20 scale-105' : 'border border-slate-100 hover:bg-slate-50',
          ]"
        >
          {{ $te(`menu.categories.${cat}`) ? $t(`menu.categories.${cat}`) : formatCategoryName(cat) }}
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="p-6">
      <!-- Special Offer / Info Card -->
      <div class="mb-8 p-6 rounded-[2rem] bg-white border border-slate-100 flex items-center gap-4 shadow-sm animate-fade-in">
        <div class="w-12 h-12 rounded-2xl bg-brand-50 flex items-center justify-center shrink-0">
          <svg class="w-6 h-6 text-brand-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        </div>
        <div>
          <h4 class="text-xs font-black text-slate-900 uppercase tracking-widest mb-1">{{ $t('menu.allergenWarning') }}</h4>
          <p class="text-[10px] font-bold text-slate-400 leading-tight">{{ $t('menu.specialRequest') }}</p>
        </div>
      </div>

      <!-- Menu Grid -->
      <div v-if="orderStore.loading && orderStore.menu.length === 0" class="flex flex-col items-center justify-center py-40">
        <div class="w-12 h-12 border-4 border-slate-200 border-t-brand-500 rounded-full animate-spin"></div>
      </div>
      
      <div v-else-if="groupedMenu.reduce((acc, g) => acc + g.items.length, 0) === 0" class="text-center py-40 text-slate-300 font-black uppercase tracking-widest text-[10px]">
        {{ $t('menu.emptyCategory') }}
      </div>

      <div v-else class="space-y-8">
        <div v-for="group in groupedMenu" :key="group.name || 'default'" class="space-y-4">
          <!-- Subcategory Header -->
          <div v-if="group.name" class="flex items-center gap-3 px-1 animate-fade-in">
            <h3 class="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400">{{ group.name }}</h3>
            <div class="h-px bg-slate-100 grow"></div>
          </div>
          
          <div class="space-y-4">
            <MenuItemCard 
              v-for="(item, index) in group.items" 
              :key="item.id" 
              :item="item" 
              :delay="`${index * 100}ms`"
              @add="openNoteModal" 
            />
          </div>
        </div>
      </div>
    </main>

    <!-- Floating Call Waiter Button (Bottom Left) -->
    <div class="fixed bottom-8 left-8 z-50">
      <button 
        @click="handleCallWaiter"
        :disabled="isWaiterCooldown"
        class="relative w-18 h-18 sm:w-20 sm:h-20 rounded-full flex items-center justify-center shadow-[0_15px_40px_rgba(245,158,11,0.4)] transition-all duration-500 overflow-hidden group active:scale-95"
        :class="[isWaiterCooldown ? 'bg-slate-100 text-slate-400 cursor-not-allowed opacity-80' : 'bg-gradient-to-br from-amber-400 to-orange-600 text-white hover:scale-110']"
      >
        <!-- Animated Background Rings -->
        <div v-if="!isWaiterCooldown" class="absolute inset-0 z-0">
          <div class="absolute inset-0 bg-white/20 animate-ping duration-[3000ms] rounded-full"></div>
          <div class="absolute inset-0 bg-white/10 animate-pulse duration-[2000ms] rounded-full scale-125"></div>
        </div>

        <div v-if="!isWaiterCooldown" class="relative z-10 flex flex-col items-center">
          <svg class="w-7 h-7 sm:w-8 sm:h-8 text-white drop-shadow-md" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span class="text-[8px] sm:text-[9px] font-black uppercase tracking-widest mt-1 text-white drop-shadow-sm">{{ $t('menu.callWaiter') }}</span>
        </div>
        <div v-else class="flex flex-col items-center">
          <span class="text-xl font-black leading-none">{{ waiterCooldownSeconds }}</span>
          <span class="text-[7px] font-black uppercase tracking-tighter opacity-50">SEC</span>
        </div>
      </button>
    </div>

    <!-- Floating Action Button (FAB) for Cart -->
    <Transition name="scale">
      <div v-if="orderStore.cartCount > 0 && !isCartOpen" class="fixed bottom-8 right-8 z-50 animate-bounce-in">
        <div class="relative">
          <button 
            @click="isCartOpen = true"
            class="relative w-16 h-16 text-white rounded-full flex items-center justify-center shadow-2xl shadow-slate-900/40 active:scale-90 transition-all group overflow-hidden"
            :style="{ backgroundColor: effectiveColor }"
          >
            <div class="absolute inset-0 bg-white/20 opacity-0 group-hover:opacity-100 transition-opacity"></div>
            <svg class="w-8 h-8 relative z-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </button>
          
          <div class="absolute -top-1 -right-1 min-w-[24px] h-6 px-1.5 bg-emerald-500 text-white rounded-full flex items-center justify-center text-[11px] font-black border-2 border-white shadow-lg z-20 pointer-events-none">
            {{ orderStore.cartCount }}
          </div>
        </div>
      </div>
    </Transition>

    <CartDrawer v-model:isOpen="isCartOpen" :tableCode="tableCode" @orderSuccess="onOrderSuccess" />

    <!-- Add Note Modal -->
    <Transition name="fade">
      <div v-if="noteModal.open" class="fixed inset-0 z-[100] flex items-end sm:items-center justify-center p-6">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-md" @click="closeNoteModal"></div>
        <div class="relative w-full max-w-md bg-white rounded-[2.5rem] p-8 shadow-2xl">
          <div class="mb-6">
            <h3 class="text-xl font-black text-slate-900 tracking-tight mb-1">{{ getLocalizedName(noteModal.item) }}</h3>
            <p class="text-xs font-bold text-brand-500 uppercase tracking-widest">{{ formatPrice(getLocalizedItemPrice(noteModal.item)) }}</p>
          </div>

          <div class="space-y-6">
            <div class="p-5 bg-slate-50 rounded-2xl flex items-center justify-between border border-slate-100">
              <span class="text-[10px] font-black uppercase tracking-widest text-slate-400">{{ $t('menu.quantity') }}</span>
              <div class="flex items-center gap-6">
                <button @click="noteModal.qty = Math.max(1, noteModal.qty - 1)" class="w-10 h-10 rounded-xl bg-white shadow-sm flex items-center justify-center text-xl font-black text-slate-900 border border-slate-100">-</button>
                <span class="text-lg font-black w-4 text-center">{{ noteModal.qty }}</span>
                <button @click="noteModal.qty++" class="w-10 h-10 rounded-xl bg-white shadow-sm flex items-center justify-center text-xl font-black text-slate-900 border border-slate-100">+</button>
              </div>
            </div>

            <div>
              <label class="block text-[10px] font-black uppercase tracking-widest text-slate-400 mb-3 ml-1">{{ $t('menu.specialRequest') }}</label>
              <textarea
                v-model="noteModal.note"
                rows="3"
                class="w-full bg-slate-50 rounded-2xl p-5 text-sm font-medium text-slate-900 border border-slate-100 focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none resize-none"
                :placeholder="$t('menu.requestPlaceholder')"
              ></textarea>
            </div>

            <button 
              @click="confirmAddWithNote"
              class="w-full py-5 rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl shadow-black/10 active:scale-95 transition-all"
              :style="{ 
                backgroundColor: effectiveColor,
                color: headerTextColor
              }"
            >
              {{ $t('menu.addToCart') }} • {{ formatPrice(getLocalizedItemPrice(noteModal.item) * noteModal.qty) }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
    <!-- Business Info Drawer -->
    <Transition name="fade">
      <div v-if="isInfoOpen" class="fixed inset-0 z-[100] flex items-end sm:items-center justify-center p-6">
        <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-md" @click="isInfoOpen = false"></div>
        <div class="relative w-full max-w-md bg-white rounded-[2.5rem] p-10 shadow-2xl animate-in slide-in-from-bottom duration-500">
          <div class="flex flex-col items-center text-center mb-8">
            <div class="w-20 h-20 rounded-3xl bg-slate-50 flex items-center justify-center mb-6 shadow-inner border border-slate-100">
              <NuxtImg v-if="effectiveLogo" :src="effectiveLogo" format="webp" class="w-12 h-12 object-contain" alt="Restaurant Logo" />
              <div v-else class="text-3xl font-black text-slate-200">{{ (tenantConfig?.name || 'F').charAt(0) }}</div>
            </div>
            <h2 class="text-2xl font-black text-slate-900 tracking-tight mb-2">{{ tenantConfig?.name }}</h2>
            <p class="text-sm font-bold text-slate-400 uppercase tracking-widest">{{ $t('common.contactInfo') }}</p>
          </div>

          <div class="space-y-4 mb-10">
            <div class="flex items-center gap-4 p-5 bg-slate-50 rounded-2xl border border-slate-100">
              <div class="w-10 h-10 rounded-xl bg-white shadow-sm flex items-center justify-center text-slate-400">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
              </div>
              <div class="flex flex-col overflow-hidden">
                <span class="text-[10px] font-black uppercase tracking-tighter text-slate-400 leading-none mb-1">{{ $t('common.email') }}</span>
                <span class="text-sm font-bold text-slate-900 truncate">{{ tenantConfig?.ownerEmail || 'info@feasymenu.com' }}</span>
              </div>
            </div>
            
            <div class="flex items-center gap-4 p-5 bg-slate-50 rounded-2xl border border-slate-100">
              <div class="w-10 h-10 rounded-xl bg-white shadow-sm flex items-center justify-center text-slate-400">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
              </div>
              <div class="flex flex-col">
                <span class="text-[10px] font-black uppercase tracking-tighter text-slate-400 leading-none mb-1">{{ $t('common.location') }}</span>
                <span class="text-sm font-bold text-slate-900">{{ tenantConfig?.address || 'Istanbul, Turkey' }}</span>
              </div>
            </div>
          </div>

          <button 
            @click="isInfoOpen = false"
            class="w-full py-5 rounded-2xl bg-slate-950 text-white font-black text-xs uppercase tracking-widest shadow-xl active:scale-95 transition-all"
            :style="{ backgroundColor: effectiveColor }"
          >
            {{ $t('common.close') }}
          </button>
        </div>
      </div>
    </Transition>

    <!-- Call Waiter Confirmation Modal -->
    <Transition name="fade">
      <div v-if="isWaiterConfirmOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm" @click="isWaiterConfirmOpen = false"></div>
        <div class="bg-white rounded-3xl w-full max-w-sm overflow-hidden shadow-2xl relative animate-in fade-in zoom-in duration-300">
          <div class="p-8 text-center">
            <div class="w-16 h-16 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-amber-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
            </div>
            <h3 class="text-xl font-black text-slate-800 mb-2">{{ $t('menu.callWaiter') }}</h3>
            <p class="text-slate-600 text-sm mb-8">{{ $t('menu.callWaiterConfirm') }}</p>
            <div class="flex gap-3">
              <button 
                @click="isWaiterConfirmOpen = false"
                class="flex-1 py-4 bg-slate-100 text-slate-600 font-bold rounded-2xl hover:bg-slate-200 transition-colors"
              >
                {{ $t('common.cancel') }}
              </button>
              <button 
                @click="confirmCallWaiter"
                class="flex-1 py-4 bg-amber-600 text-white font-bold rounded-2xl hover:bg-amber-500 transition-all shadow-lg shadow-amber-600/20 active:scale-95"
              >
                {{ $t('common.confirm') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Story Viewer Modal -->
    <Transition
      enter-active-class="transition-opacity duration-300"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-300"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="activeStoryIndex !== null && storyItems[activeStoryIndex]" class="fixed inset-0 z-[100] bg-slate-950 flex flex-col">
        <!-- Progress Bars -->
        <div class="flex gap-1 p-4 pt-6 shrink-0 absolute top-0 inset-x-0 z-10">
          <div v-for="(story, idx) in storyItems" :key="idx" class="h-1 flex-1 bg-white/30 rounded-full overflow-hidden">
            <div 
              class="h-full bg-white" 
              :style="{ width: getStoryProgress(idx) + '%' }"
            ></div>
          </div>
        </div>
        <!-- Close Button -->
        <button @click="closeStory" class="absolute top-12 right-4 z-10 w-10 h-10 bg-black/40 backdrop-blur-md rounded-full flex items-center justify-center text-white">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
        </button>

        <!-- Story Content -->
        <div class="flex-1 relative flex flex-col justify-end p-8" @click="handleStoryTap">
          <NuxtImg :src="storyItems[activeStoryIndex].image" class="absolute inset-0 w-full h-full object-cover opacity-80" />
          <div class="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/20 to-transparent pointer-events-none"></div>
          
          <div class="relative z-10 mb-8 pointer-events-none">
            <div class="inline-flex items-center gap-2 bg-white/20 backdrop-blur-md rounded-full px-4 py-1.5 mb-4 border border-white/20">
              <span class="w-2 h-2 rounded-full bg-amber-400 animate-pulse"></span>
              <span class="text-[10px] font-black uppercase tracking-widest text-white">{{ $t('menu.featured') }}</span>
            </div>
            <h2 class="text-3xl font-black text-white mb-2 leading-tight">{{ getLocalizedName(storyItems[activeStoryIndex]) }}</h2>
            <p class="text-white/80 font-medium text-sm line-clamp-3 mb-6">{{ getLocalizedDesc(storyItems[activeStoryIndex]) }}</p>
            <div class="flex items-center gap-4 pointer-events-auto">
              <span class="text-2xl font-black text-white">{{ formatPrice(getLocalizedItemPrice(storyItems[activeStoryIndex])) }}</span>
              <button 
                @click.stop="addStoryToCart(storyItems[activeStoryIndex])"
                class="flex-1 py-4 bg-white text-slate-950 rounded-2xl font-black text-xs uppercase tracking-widest flex items-center justify-center gap-2 hover:bg-slate-100 transition-colors active:scale-95"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" /></svg>
                {{ $t('menu.addToCart') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { useOrderStore } from '~/stores/order'
import { useTenant } from '~/composables/useTenant'
import { useUiStore } from '~/stores/ui'

definePageMeta({ layout: 'customer' })

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()
const orderStore = useOrderStore()
const uiStore = useUiStore()
const { tenantConfig, detectTenant, loadTenantConfig } = useTenant()

const tableCode = ref<string>('')
const isCartOpen = ref(false)
const isInfoOpen = ref(false)
const hasSession = ref(false)
const isWaiterConfirmOpen = ref(false)
const isWaiterCooldown = ref(false)
const waiterCooldownSeconds = ref(0)
let waiterTimer: any = null

onMounted(() => {
  const tParam = route.query.t || route.query.table
  const storedTable = localStorage.getItem('qm_table_code')
  
  if (tParam) {
    const newTable = String(tParam)
    tableCode.value = newTable
    localStorage.setItem('qm_table_code', tableCode.value)
  } else {
    if (storedTable) tableCode.value = storedTable
  }

  if (import.meta.client) {
    hasSession.value = !!localStorage.getItem('qm_order_session')
  }
  loadData()
})

onBeforeUnmount(() => {
  if (waiterTimer) clearInterval(waiterTimer)
})

async function handleCallWaiter() {
  if (isWaiterCooldown.value) return
  
  if (!tableCode.value) {
    uiStore.error(t('menu.scanQR'))
    return
  }

  isWaiterConfirmOpen.value = true
}

async function confirmCallWaiter() {
  isWaiterConfirmOpen.value = false
  
  try {
    await orderStore.callWaiter(tableCode.value)
    uiStore.success(t('menu.waiterCalled'))
    
    // Start 60s cooldown
    isWaiterCooldown.value = true
    waiterCooldownSeconds.value = 60
    waiterTimer = setInterval(() => {
      waiterCooldownSeconds.value--
      if (waiterCooldownSeconds.value <= 0) {
        if (waiterTimer) clearInterval(waiterTimer)
        isWaiterCooldown.value = false
      }
    }, 1000)
  } catch (e) {
    uiStore.error(t('menu.waiterCallError'))
  }
}

function getContrastColor(hexcolor: string) {
  if (!hexcolor) return 'white'
  if (!hexcolor.startsWith('#')) return 'white'
  const r = parseInt(hexcolor.substring(1, 3), 16)
  const g = parseInt(hexcolor.substring(3, 5), 16)
  const b = parseInt(hexcolor.substring(5, 7), 16)
  const yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000
  return (yiq >= 128) ? '#0f172a' : 'white'
}

const effectiveColor = computed(() => {
  if (tenantConfig.value?.plan === 'FREE') return '#94a684' // FeasyMenu Brand Color (Sage Green)
  return tenantConfig.value?.primaryColor || '#94a684' // Default to Sage Green instead of dark navy
})

const effectiveLogo = computed(() => {
  if (tenantConfig.value?.plan === 'FREE') return null
  return tenantConfig.value?.logoUrl
})

const headerTextColor = computed(() => getContrastColor(effectiveColor.value))

async function loadData() {
  const code = detectTenant()
  if (code) {
    await loadTenantConfig(code)
    
    // Set i18n locale based on tenant preference if available
    if (tenantConfig.value?.locale) {
      const preferredLocale = tenantConfig.value.locale.split('-')[0] // 'tr-TR' -> 'tr'
      if (['tr', 'en'].includes(preferredLocale)) {
        locale.value = preferredLocale
      }
    }
  }
  if (orderStore.menu.length === 0) await orderStore.loadMenu()
}

function formatCategoryName(cat: string) {
  if (!cat) return ''
  return cat.split('_').map(word => word.charAt(0).toLocaleUpperCase(locale.value) + word.slice(1).toLocaleLowerCase(locale.value)).join(' ')
}

const categories = computed(() => {
  const set = new Set<string>()
  orderStore.menu.forEach(m => {
    if (m.category) set.add(m.category)
  })
  const allCats = Array.from(set)
  
  if (tenantConfig.value?.categoryOrder) {
    const order = tenantConfig.value.categoryOrder.split(',')
    return allCats.sort((a, b) => {
      let idxA = order.indexOf(a)
      let idxB = order.indexOf(b)
      if (idxA === -1) idxA = 999
      if (idxB === -1) idxB = 999
      if (idxA !== idxB) return idxA - idxB
      return a.localeCompare(b)
    })
  }
  
  return allCats.sort((a, b) => a.localeCompare(b))
})

// --- Story Feature Logic ---
const storyItems = computed(() => {
  if (!orderStore.menu) return []
  
  // Only show stories if on a paid plan (optional, depends on policy)
  // if (tenantConfig.value?.plan === 'FREE') return []

  const withImages = orderStore.menu.filter(m => m.image && m.price > 0)
  
  // Prioritize "isFeatured" items, then fallback to others
  return withImages.sort((a, b) => {
    if (a.isFeatured && !b.isFeatured) return -1
    if (!a.isFeatured && b.isFeatured) return 1
    return 0
  }).slice(0, 8)
})

const activeStoryIndex = ref<number | null>(null)
let storyInterval: any = null
const storyProgress = ref(0) // 0 to 100

function openStory(idx: number) {
  activeStoryIndex.value = idx
  startStoryTimer()
}

function closeStory() {
  activeStoryIndex.value = null
  if (storyInterval) clearInterval(storyInterval)
}

function handleStoryTap(e: MouseEvent) {
  const width = window.innerWidth
  const x = e.clientX
  if (x > width * 0.7) {
    nextStory()
  } else if (x < width * 0.3) {
    prevStory()
  }
}

function nextStory() {
  if (activeStoryIndex.value === null) return
  if (activeStoryIndex.value < storyItems.value.length - 1) {
    activeStoryIndex.value++
    startStoryTimer()
  } else {
    closeStory()
  }
}

function prevStory() {
  if (activeStoryIndex.value === null) return
  if (activeStoryIndex.value > 0) {
    activeStoryIndex.value--
    startStoryTimer()
  }
}

function startStoryTimer() {
  if (storyInterval) clearInterval(storyInterval)
  storyProgress.value = 0
  
  storyInterval = setInterval(() => {
    storyProgress.value += 100 / (3000 / 50)
    if (storyProgress.value >= 100) {
      nextStory()
    }
  }, 50)
}

function getStoryProgress(idx: number) {
  if (activeStoryIndex.value === null) return 0
  if (idx < activeStoryIndex.value) return 100
  if (idx === activeStoryIndex.value) return storyProgress.value
  return 0
}

function addStoryToCart(item: any) {
  orderStore.addToCart(item.id, 1)
  uiStore.success(locale.value === 'en' ? `Added ${getLocalizedName(item)} to cart` : `${getLocalizedName(item)} sepete eklendi`)
  closeStory()
  isCartOpen.value = true
}

function getLocalizedDesc(item: any) {
  if (!item) return ''
  if (locale.value === 'en' && item.descriptionEn) {
    return item.descriptionEn
  }
  return item.description
}
// --- End Story Feature Logic ---

const selectedCategory = ref<string>('')
watch(categories, (cats) => {
  if (cats.length > 0 && (!selectedCategory.value || !cats.includes(selectedCategory.value))) {
    selectedCategory.value = cats[0] || ''
  }
}, { immediate: true })

const groupedMenu = computed(() => {
  const filtered = orderStore.menu
    .filter(m => m.category === selectedCategory.value)
    .sort((a, b) => {
      const orderA = a.sortOrder ?? 999
      const orderB = b.sortOrder ?? 999
      if (orderA !== orderB) return orderA - orderB
      return a.name.localeCompare(b.name)
    })
  
  const groups: Record<string, any[]> = {}
  
  filtered.forEach(item => {
    const sub = item.subcategory || 'default'
    if (!groups[sub]) groups[sub] = []
    groups[sub].push(item)
  })
  
  // Return as sorted array: default (no subcategory) first, then others alphabetically
  return Object.entries(groups)
    .sort(([a], [b]) => {
      if (a === 'default') return -1
      if (b === 'default') return 1
      return a.localeCompare(b)
    })
    .map(([name, items]) => ({
      name: name === 'default' ? null : name,
      items
    }))
})

const noteModal = ref({ open: false, item: null as any, note: '', qty: 1 })
function openNoteModal(item: any) { noteModal.value = { open: true, item, note: '', qty: 1 } }
function closeNoteModal() { noteModal.value.open = false }
function confirmAddWithNote() {
  if (noteModal.value.item?.id) orderStore.addToCart(noteModal.value.item.id, noteModal.value.qty, noteModal.value.note)
  closeNoteModal()
}

function getLocalizedName(item: any) {
  return (locale.value === 'en' && item?.nameEn) ? item.nameEn : item?.name || ''
}
function getLocalizedItemPrice(item: any) {
  return (locale.value === 'en' && item?.priceUsd > 0) ? item.priceUsd : item?.price || 0
}
function formatPrice(p: number) {
  return new Intl.NumberFormat(locale.value === 'en' ? 'en-US' : 'tr-TR', { 
    style: 'currency', currency: locale.value === 'en' ? 'USD' : 'TRY' 
  }).format(p)
}
function onOrderSuccess(order: any) {
  hasSession.value = true
  if (order?.id) router.push(`/order/${order.id}`)
}

const schemaData = computed(() => {
  if (!tenantConfig.value) return null
  const url = `https://feasymenu.com/menu?t=${tenantConfig.value.code || tableCode.value}`
  return {
    '@context': 'https://schema.org',
    '@type': 'Restaurant',
    name: tenantConfig.value.name || t('common.menu'),
    image: tenantConfig.value.logoUrl || 'https://feasymenu.com/og-image.jpg',
    url: url,
    hasMenu: {
      '@type': 'Menu',
      name: 'Dijital QR Menü',
      url: url
    }
  }
})

useSeoMeta({
  title: () => `${tenantConfig.value?.name || t('common.menu')} | FeasyMenu`,
  description: () => `${tenantConfig.value?.name || 'Restoran'} için dijital QR menüyü inceleyin, fiyatları görün ve temassız sipariş verin.`,
  ogTitle: () => `${tenantConfig.value?.name || t('common.menu')} | Dijital Menü`,
  ogDescription: () => `${tenantConfig.value?.name || 'Restoran'} QR menüsü.`,
  ogImage: () => tenantConfig.value?.logoUrl || 'https://feasymenu.com/og-image.jpg'
})

useHead({
  script: [
    {
      type: 'application/ld+json',
      innerHTML: () => schemaData.value ? JSON.stringify(schemaData.value) : ''
    }
  ]
})
</script>

<style scoped>
@keyframes slide-up {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

.animate-slide-up {
  opacity: 0;
  animation: slide-up 1s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes bounce-in {
  0% { transform: scale(0); opacity: 0; }
  70% { transform: scale(1.1); }
  100% { transform: scale(1); opacity: 1; }
}

.animate-bounce-in {
  animation: bounce-in 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.animate-fade-in {
  opacity: 0;
  animation: fade-in 1s ease forwards;
}

.scrollbar-hide::-webkit-scrollbar { display: none; }
.scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }

.scale-enter-active, .scale-leave-active { transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); }
.scale-enter-from, .scale-leave-to { opacity: 0; transform: scale(0); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
