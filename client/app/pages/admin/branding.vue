<template>
  <div class="p-4 md:p-8 max-w-[1800px] mx-auto space-y-10">
    <!-- Header Section -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 bg-white/50 backdrop-blur-xl p-8 rounded-[3rem] border border-white shadow-2xl shadow-slate-200/40">
      <div class="flex items-center gap-6">
        <div class="w-16 h-16 rounded-2xl bg-indigo-600 text-white flex items-center justify-center shadow-lg shadow-indigo-500/30">
           <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.172-1.172a4 4 0 115.656 5.656L10 17.657" /></svg>
        </div>
        <div>
          <h1 class="text-3xl font-black text-slate-900 tracking-tight leading-none mb-2">{{ $t('admin.branding.title') }}</h1>
          <p class="text-slate-500 font-medium text-sm">{{ $t('admin.branding.subtitle') || 'İşletme görünümünü ve güvenlik ayarlarını özelleştirin' }}</p>
        </div>
      </div>

      <button 
        @click="saveSettings" 
        :disabled="saving"
        class="w-full lg:w-auto px-10 py-3.5 bg-brand-600 text-white font-black text-xs uppercase tracking-[0.2em] rounded-2xl hover:bg-brand-700 hover:-translate-y-0.5 active:translate-y-0 transition-all shadow-xl shadow-brand-500/30 flex items-center justify-center gap-3 disabled:opacity-50"
      >
        <div v-if="saving" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
        <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
        {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
      </button>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-10 relative min-h-[800px]">
      <!-- Settings Panel -->
      <div class="lg:col-span-5 xl:col-span-4 space-y-6">
        
        <!-- Logo & Core Brand -->
        <div class="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 space-y-8 relative overflow-hidden">
           <!-- Section Restriction Overlay -->
           <div v-if="!isPaidPlan" class="absolute inset-0 z-20 backdrop-blur-[2px] bg-white/40 flex flex-col items-center justify-center p-6 text-center border-2 border-white/50">
             <svg class="w-8 h-8 text-indigo-600 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>
             <h4 class="text-sm font-black text-slate-900 mb-1 uppercase tracking-widest">{{ $t('admin.branding.premiumTitle') }}</h4>
             <p class="text-[10px] text-slate-500 font-bold mb-4 leading-tight">{{ $t('admin.branding.premiumDesc') }}</p>
             <NuxtLink :to="localePath('/admin/upgrade')" class="px-6 py-2 bg-brand-600 text-white font-black text-[10px] uppercase tracking-widest rounded-xl hover:bg-brand-700 shadow-lg shadow-brand-500/20 transition-all">
                {{ $t('admin.upgrade.button') }}
             </NuxtLink>
           </div>

           <h3 class="text-lg font-black text-slate-900 uppercase tracking-widest flex items-center gap-3">
              <span class="w-1.5 h-1.5 rounded-full bg-indigo-500"></span>
              {{ $t('admin.branding.logo') }}
           </h3>

           <div>
              <div class="flex items-center gap-6 mb-4">
                 <div class="w-20 h-20 rounded-2xl bg-slate-50 border-2 border-dashed border-slate-200 flex items-center justify-center overflow-hidden">
                    <img v-if="form.logo" :src="form.logo" class="max-w-full max-h-full object-contain p-2" />
                    <svg v-else class="w-8 h-8 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
                 </div>
                 <div class="flex-1">
                    <input 
                      v-model="form.logo" 
                      type="text" 
                      placeholder="https://logo-url.png"
                      class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-bold text-slate-700 shadow-inner" 
                    />
                    <p class="text-[10px] text-slate-400 mt-2 font-bold">{{ $t('admin.branding.logoHint') }}</p>
                 </div>
              </div>
           </div>

           <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 pt-4 border-t border-slate-50">
              <div>
                <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.primaryColor') }}</label>
                <div class="flex items-center gap-3">
                  <div class="relative w-12 h-12 rounded-xl overflow-hidden shadow-md border-2 border-white cursor-pointer group">
                    <input v-model="form.primaryColor" type="color" class="absolute inset-[-50%] w-[200%] h-[200%] cursor-pointer border-none bg-transparent">
                  </div>
                  <input v-model="form.primaryColor" type="text" class="flex-1 px-4 py-2.5 bg-slate-50 border border-slate-100 rounded-xl text-xs font-black uppercase outline-none focus:ring-2 focus:ring-indigo-500/10 transition-all" />
                </div>
              </div>
              <div>
                <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.accentColor') }}</label>
                <div class="flex items-center gap-3">
                  <div class="relative w-12 h-12 rounded-xl overflow-hidden shadow-md border-2 border-white cursor-pointer group">
                    <input v-model="form.accentColor" type="color" class="absolute inset-[-50%] w-[200%] h-[200%] cursor-pointer border-none bg-transparent">
                  </div>
                  <input v-model="form.accentColor" type="text" class="flex-1 px-4 py-2.5 bg-slate-50 border border-slate-100 rounded-xl text-xs font-black uppercase outline-none focus:ring-2 focus:ring-indigo-500/10 transition-all" />
                </div>
              </div>
           </div>

           <div class="pt-4 border-t border-slate-50">
             <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.fontFamily') }}</label>
             <select 
               v-model="form.fontFamily" 
               class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-bold text-slate-700 shadow-inner appearance-none"
             >
               <option value="Inter, sans-serif">Inter (Modern & Clean)</option>
               <option value="'Playfair Display', serif">Playfair Display (Elegant & Classic)</option>
               <option value="Roboto, sans-serif">Roboto (Industrial & Standard)</option>
               <option value="'Outfit', sans-serif">Outfit (Rounded & Friendly)</option>
             </select>
           </div>
        </div>

        <!-- Store Info & Messages -->
        <div class="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 space-y-6 relative overflow-hidden">
           <!-- Section Restriction Overlay (Store info is free, but welcome message maybe premium? No, let's keep it free for now or restrict only specific fields if needed) -->
           <!-- For now, we'll keep Store Info accessible to FREE users since it contains address/phone which are useful -->
           
           <h3 class="text-lg font-black text-slate-900 uppercase tracking-widest flex items-center gap-3">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
              {{ $t('admin.branding.welcomeMessage') }}
           </h3>
           
           <textarea 
             v-model="form.welcomeMessage" 
             rows="3"
             class="w-full px-5 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-bold text-slate-700 shadow-inner resize-none text-sm leading-relaxed" 
           ></textarea>

           <div class="space-y-4 pt-4 border-t border-slate-50">
              <div>
                <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.address') }}</label>
                <input v-model="form.address" type="text" class="w-full px-5 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-indigo-500/10 outline-none transition-all font-bold text-slate-700 text-sm" />
              </div>
              <div>
                <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.phone') }}</label>
                <input v-model="form.phone" type="text" class="w-full px-5 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-indigo-500/10 outline-none transition-all font-bold text-slate-700 text-sm" />
              </div>
           </div>
        </div>

        <!-- Location & Geofencing -->
        <div class="bg-white p-8 rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 space-y-6">
           <h3 class="text-lg font-black text-slate-900 uppercase tracking-widest flex items-center gap-3">
              <span class="w-1.5 h-1.5 rounded-full bg-orange-500"></span>
              {{ $t('admin.branding.locationTitle') || 'Konum & Coğrafi Sınır' }}
           </h3>
           <p class="text-[11px] text-slate-400 font-bold leading-relaxed">{{ $t('admin.branding.locationDesc') || 'Siparişlerin sadece restoran yakınındayken verilebilmesini sağlamak için konum bilgilerini girin.' }}</p>

           <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
             <div>
               <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.latitude') || 'Enlem' }}</label>
               <input v-model.number="form.latitude" type="number" step="any" class="w-full px-5 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-indigo-500/10 outline-none transition-all font-bold text-slate-700 text-sm" />
             </div>
             <div>
               <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.longitude') || 'Boylam' }}</label>
               <input v-model.number="form.longitude" type="number" step="any" class="w-full px-5 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-indigo-500/10 outline-none transition-all font-bold text-slate-700 text-sm" />
             </div>
           </div>

           <div>
             <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.branding.threshold') || 'Sipariş Mesafesi (Metre)' }}</label>
             <input v-model.number="form.locationThreshold" type="number" class="w-full px-5 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-indigo-500/10 outline-none transition-all font-bold text-slate-700 text-sm" placeholder="Örn: 500" />
             <p class="text-[10px] text-slate-400 mt-2 font-bold">{{ $t('admin.branding.thresholdHint') || 'Boş bırakılırsa konum kontrolü devre dışı kalır.' }}</p>
           </div>

           <button 
             @click="getCurrentLocation" 
             type="button"
             class="w-full py-3 bg-slate-900 text-white font-black text-[10px] uppercase tracking-widest rounded-xl hover:bg-slate-800 transition-all flex items-center justify-center gap-2"
           >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
              {{ $t('admin.branding.useCurrentLocation') || 'ŞUANKİ KONUMUMU KULLAN' }}
           </button>
        </div>
      </div>

      <!-- Live Stüdyo Preview -->
      <div class="lg:col-span-7 xl:col-span-8 flex flex-col items-center justify-center p-12 bg-slate-50/50 rounded-[3rem] border-2 border-dashed border-slate-200 relative overflow-hidden">
        <!-- Preview Restriction Overlay -->
        <div v-if="!isPaidPlan" class="absolute inset-0 z-20 backdrop-blur-[3px] bg-white/20 flex flex-col items-center justify-center p-8 text-center">
             <div class="bg-white p-10 rounded-[3rem] shadow-2xl border border-slate-100 max-w-sm">
                <div class="w-16 h-16 bg-brand-50 text-brand-600 rounded-2xl flex items-center justify-center mx-auto mb-6">
                   <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                </div>
                <h4 class="text-xl font-black text-slate-900 mb-3 tracking-tight">Önizleme Kısıtlı</h4>
                <p class="text-sm text-slate-500 font-bold mb-8 leading-relaxed">Canlı stüdyo önizlemesi ve tema özelleştirmeleri için planınızı yükseltin.</p>
                <NuxtLink :to="localePath('/admin/upgrade')" class="inline-flex px-8 py-3.5 bg-brand-600 text-white font-black text-xs uppercase tracking-widest rounded-2xl hover:bg-brand-700 shadow-xl shadow-brand-500/20 transition-all">
                   {{ $t('admin.upgrade.button') }}
                </NuxtLink>
             </div>
        </div>
        <div class="absolute top-0 right-0 w-96 h-96 bg-indigo-500/5 blur-[100px] rounded-full"></div>
        <div class="absolute bottom-0 left-0 w-96 h-96 bg-orange-500/5 blur-[100px] rounded-full"></div>

        <div class="relative z-10 w-full flex flex-col items-center">
           <div class="mb-12 text-center">
              <span class="px-4 py-2 bg-white text-indigo-600 text-[10px] font-black uppercase tracking-[0.3em] rounded-full shadow-lg border border-indigo-50">
                 LIVE STÜDYO ÖNİZLEME
              </span>
           </div>
           
           <div class="transform scale-110 lg:scale-[1.2] transition-transform duration-700 hover:rotate-1">
              <LandingPhoneMockup 
                :primary-color="form.primaryColor"
                :accent-color="form.accentColor"
                :logo-url="form.logo"
                :font-family="form.fontFamily"
                :tenant-name="authStore.user?.tenantCode || 'The Bistro'"
              />
           </div>

           <div class="mt-20 flex gap-4 opacity-50 grayscale hover:grayscale-0 hover:opacity-100 transition-all duration-700">
              <div class="px-6 py-2 bg-white rounded-full border border-slate-100 shadow-sm flex items-center gap-3">
                 <div class="w-2 h-2 rounded-full bg-slate-300"></div>
                 <span class="text-[9px] font-black text-slate-400 tracking-widest uppercase">Responsive</span>
              </div>
              <div class="px-6 py-2 bg-white rounded-full border border-slate-100 shadow-sm flex items-center gap-3">
                 <div class="w-2 h-2 rounded-full bg-emerald-500"></div>
                 <span class="text-[9px] font-black text-slate-400 tracking-widest uppercase">Brand Active</span>
              </div>
           </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

definePageMeta({ layout: 'admin' })

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()
const localePath = useLocalePath()
const { isPaidPlan } = useTenant()

useHead({ title: () => `${t('admin.branding.title')} | Admin` })

const saving = ref(false)
const form = ref({
  logo: 'https://images.unsplash.com/photo-1599305445671-ac291c95aaa9?w=200&h=50&fit=crop&q=80&bg=transparent',
  primaryColor: '#94a684',
  accentColor: '#5e6d51',
  fontFamily: 'Inter, sans-serif',
  welcomeMessage: t('admin.branding.defaultWelcomeLong'),
  address: '',
  phone: '',
  latitude: null as number | null,
  longitude: null as number | null,
  locationThreshold: null as number | null
})

const currentPlan = computed(() => {
  const p = authStore.user?.tenant?.subscriptionPlan || authStore.tenantConfig?.plan || 'FREE'
  return String(p).toUpperCase()
})

async function loadSettings() {
  try {
    const data = await fetchJson('/api/tenant/config')
    if (data) {
      form.value = {
        logo: data.logoUrl || form.value.logo,
        primaryColor: data.primaryColor || form.value.primaryColor,
        accentColor: data.accentColor || form.value.accentColor,
        fontFamily: data.fontFamily || form.value.fontFamily,
        welcomeMessage: data.welcomeMessage || form.value.welcomeMessage,
        address: data.address || '',
        phone: data.phone || '',
        latitude: data.latitude,
        longitude: data.longitude,
        locationThreshold: data.locationThreshold
      }
    }
  } catch (e) {
    console.error('Branding load error', e)
  }
}

async function saveSettings() {
  saving.value = true
  try {
    await fetchJson(`/api/tenant/branding`, {
      method: 'PUT',
      body: JSON.stringify({
        primaryColor: form.value.primaryColor,
        accentColor: form.value.accentColor,
        logoUrl: form.value.logo,
        welcomeMessage: form.value.welcomeMessage,
        fontFamily: form.value.fontFamily,
        address: form.value.address,
        phone: form.value.phone,
        latitude: form.value.latitude,
        longitude: form.value.longitude,
        locationThreshold: form.value.locationThreshold
      })
    })
    uiStore.success(t('admin.branding.saveSuccess'))
  } catch (e: any) {
    uiStore.error(e?.message || 'Hata oluştu')
  } finally {
    saving.value = false
  }
}

function getCurrentLocation() {
  if (!navigator.geolocation) {
    uiStore.error('Tarayıcınız konum bilgisini desteklemiyor.')
    return
  }
  
  navigator.geolocation.getCurrentPosition((pos) => {
    form.value.latitude = pos.coords.latitude
    form.value.longitude = pos.coords.longitude
    uiStore.success('Konum başarıyla alındı.')
  }, (err) => {
    uiStore.error('Konum alınamadı: ' + err.message)
  })
}

onMounted(loadSettings)
</script>

<style scoped>
@keyframes up {
  from { opacity: 0; transform: translateY(40px) scale(0.9); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.animate-in-up { animation: up 0.7s cubic-bezier(0.16, 1, 0.3, 1); }

.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
