<template>
  <div class="relative w-full max-w-[320px] mx-auto perspective-1000">
    <!-- Main Phone Frame -->
    <div class="relative z-20 w-full aspect-[9/19] bg-slate-900 rounded-[3rem] p-3 shadow-2xl border-[6px] border-slate-800 animate-float">
      <!-- Notch / Dynamic Island -->
      <div class="absolute top-0 left-1/2 -translate-x-1/2 w-24 h-6 bg-slate-900 rounded-b-2xl z-30"></div>
      
      <!-- Screen Content -->
      <div class="w-full h-full bg-slate-50 rounded-[2.2rem] overflow-hidden relative border border-slate-200 flex flex-col">
        <!-- Mock Floating Header -->
        <div class="p-2 pt-6">
          <div class="relative overflow-hidden rounded-[1.2rem] bg-gradient-to-br from-brand-400 to-brand-500 text-white p-2.5 shadow-lg border border-white/20 backdrop-blur-md flex items-center justify-between">
            <div class="absolute -top-10 -right-10 w-20 h-20 bg-white/20 blur-[20px] rounded-full"></div>
            
            <div class="relative z-10 flex items-center gap-2">
              <div class="w-8 h-8 rounded-xl bg-white/20 flex items-center justify-center font-black text-sm border border-white/30 shadow-inner">
                B
              </div>
              <div class="flex flex-col">
                <span class="text-[6px] uppercase tracking-widest font-black opacity-70 mb-0.5">{{ $t('common.menu') }}</span>
                <span class="text-xs font-black tracking-tight leading-none">The Bistro</span>
              </div>
            </div>
            <!-- Action Pill -->
            <div class="relative z-10 flex gap-1 bg-black/10 p-1 rounded-full border border-white/10 shadow-inner">
               <div class="w-6 h-6 rounded-full flex items-center justify-center bg-white/20">
                 <svg class="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
               </div>
            </div>
          </div>
        </div>

        <!-- Story Highlights -->
        <div class="px-3 pt-3 pb-2 flex gap-3 overflow-hidden">
          <div v-for="i in 5" :key="i" class="flex flex-col items-center gap-1 shrink-0">
            <div class="w-10 h-10 rounded-full p-[1.5px] bg-gradient-to-tr from-brand-400 to-amber-400">
              <div class="w-full h-full rounded-full border-2 border-white bg-slate-100 overflow-hidden">
                <div :class="['w-full h-full flex items-center justify-center text-[10px]', i === 1 ? 'bg-amber-100' : 'bg-slate-50']">
                  {{ i === 1 ? '🍔' : (i === 2 ? '🍕' : (i === 3 ? '🥗' : (i === 4 ? '🍹' : '🍰'))) }}
                </div>
              </div>
            </div>
            <div class="w-8 h-1 bg-slate-200 rounded-full"></div>
          </div>
        </div>

        <!-- Mock Categories -->
        <div class="px-2 pb-2 flex gap-1.5 overflow-hidden">
          <div class="px-3 py-1.5 bg-brand-500 text-white text-[8px] font-black uppercase tracking-widest rounded-xl shadow-sm">{{ locale === 'en' ? 'Popular' : 'Popüler' }}</div>
          <div class="px-3 py-1.5 bg-white text-slate-400 border border-slate-100 text-[8px] font-black uppercase tracking-widest rounded-xl shadow-sm">{{ locale === 'en' ? 'Burgers' : 'Burgerler' }}</div>
          <div class="px-3 py-1.5 bg-white text-slate-400 border border-slate-100 text-[8px] font-black uppercase tracking-widest rounded-xl shadow-sm">{{ locale === 'en' ? 'Drinks' : 'İçecekler' }}</div>
        </div>

        <!-- Mock Menu Items -->
        <div class="px-2 space-y-2 pb-20 flex-1 overflow-hidden">
          <div v-for="(item, i) in mockMenu" :key="i" 
               class="bg-white p-2 rounded-2xl shadow-sm border border-slate-100 flex gap-2.5 animate-slide-up"
               :style="{ animationDelay: (i * 150) + 'ms' }">
            <div :class="['w-12 h-12 rounded-xl flex items-center justify-center text-xl shrink-0 shadow-inner border border-slate-100/50', item.color]">
              {{ item.icon }}
            </div>
            <div class="flex flex-col justify-center flex-1 min-w-0 pr-1">
              <h4 class="text-[11px] font-bold text-slate-900 truncate">{{ item.name }}</h4>
              <p class="text-[8px] text-slate-400 line-clamp-1 mb-1 font-medium">{{ item.desc }}</p>
              <div class="flex items-center justify-between mt-auto">
                <span class="text-[10px] font-black text-slate-900">{{ item.price }}</span>
                <div class="w-5 h-5 rounded-[6px] bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-900 shadow-sm">
                  <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" /></svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Float Action Button -->
        <div class="absolute bottom-4 right-4 w-10 h-10 bg-emerald-500 rounded-full shadow-lg shadow-emerald-500/40 flex items-center justify-center text-white scale-in">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" /></svg>
          <div class="absolute -top-1 -right-1 w-4 h-4 bg-slate-900 text-white rounded-full flex items-center justify-center text-[8px] font-black border border-white shadow-sm">2</div>
        </div>

        <!-- Call Waiter Button Mock -->
        <div class="absolute bottom-4 left-4 w-10 h-10 bg-white rounded-full shadow-lg border border-slate-100 flex flex-col items-center justify-center scale-in" style="animation-delay: 1.2s">
          <svg class="w-4 h-4 text-brand-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span class="text-[5px] font-black text-brand-600 uppercase">{{ locale === 'en' ? 'CALL' : 'GARSON' }}</span>
        </div>
      </div>
    </div>

    <!-- Decorative Floating Elements -->
    <div class="absolute -top-10 -right-10 z-30 animate-bounce-slow">
      <div class="bg-white p-4 rounded-2xl shadow-xl border border-slate-100 flex items-center gap-3">
        <div class="w-8 h-8 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
        </div>
        <div>
          <p class="text-[10px] font-bold text-slate-400 uppercase tracking-tighter">{{ $t('landing.quick.mockup.newOrder') }}</p>
          <p class="text-xs font-black text-slate-900">{{ $t('landing.quick.mockup.table') }} 12 - ₺240.00</p>
        </div>
      </div>
    </div>

    <div class="absolute top-1/2 -left-16 z-10 animate-float" style="animation-delay: -2s">
      <div class="bg-white p-3 rounded-xl shadow-lg border border-slate-100 flex flex-col gap-1">
        <div class="w-12 h-2 bg-brand-100 rounded-full"></div>
        <div class="w-8 h-2 bg-slate-100 rounded-full"></div>
      </div>
    </div>

    <!-- Background Glow -->
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-full bg-brand-500/20 blur-[100px] rounded-full -z-10 animate-pulse"></div>
  </div>
</template>

<script setup lang="ts">
const { locale } = useI18n()

const mockMenu = computed(() => {
  const isEn = locale.value === 'en'
  return [
    { 
      name: isEn ? 'Truffle Burger' : 'Trüf Burger', 
      price: isEn ? '$14.00' : '₺240.00', 
      desc: isEn ? '100% beef patty, truffle mayo' : '100% dana köfte, trüf mayonez', 
      icon: '🍔', color: 'bg-amber-100' 
    },
    { 
      name: 'Margherita Pizza', 
      price: isEn ? '$11.00' : '₺180.00', 
      desc: isEn ? 'Fresh basil, mozzarella' : 'Taze fesleğen, mozzarella', 
      icon: '🍕', color: 'bg-rose-100' 
    },
    { 
      name: 'Iced Latte', 
      price: isEn ? '$4.50' : '₺85.00', 
      desc: isEn ? 'Double espresso, oat milk' : 'Çift shot espresso, yulaf sütü', 
      icon: '☕️', color: 'bg-stone-100' 
    },
  ]
})
</script>

<style scoped>
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0); }
  50% { transform: translateY(-20px) rotate(2deg); }
}

@keyframes bounce-slow {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes slide-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes scale-in {
  from { transform: scale(0); }
  to { transform: scale(1); }
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-bounce-slow {
  animation: bounce-slow 4s ease-in-out infinite;
}

.animate-slide-up {
  animation: slide-up 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.scale-in {
  animation: scale-in 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards 1s;
  transform: scale(0);
}

.perspective-1000 {
  perspective: 1000px;
}
</style>
