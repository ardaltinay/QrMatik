<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.branding.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.branding.subtitle') }}</p>
      </div>
      <button @click="saveSettings" class="w-full sm:w-auto px-6 py-2.5 bg-brand-500 text-white font-semibold rounded-xl hover:bg-brand-600 transition-colors shadow-sm  flex items-center justify-center gap-2" :disabled="saving">
        <svg v-if="saving" class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
        </svg>
        {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
      </button>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
      
      <!-- Settings Panel -->
      <div class="lg:col-span-5 xl:col-span-4 space-y-6 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm self-start">
        
        <!-- Logo Input -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.branding.logo') }}</label>
          <input 
            v-model="form.logo" 
            type="text" 
            placeholder="https://..."
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" 
          />
          <p class="text-xs text-slate-400 mt-1">{{ $t('admin.branding.logoHint') }}</p>
        </div>

        <!-- Color Input -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.branding.primaryColor') }}</label>
          <div class="flex items-center gap-3">
            <input 
              v-model="form.primaryColor" 
              type="color" 
              class="w-10 h-10 rounded cursor-pointer border-0 p-0" 
            />
            <input 
              v-model="form.primaryColor" 
              type="text" 
              class="flex-1 px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all uppercase" 
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.branding.accentColor') }}</label>
          <div class="flex items-center gap-3">
            <input 
              v-model="form.accentColor" 
              type="color" 
              class="w-10 h-10 rounded cursor-pointer border-0 p-0" 
            />
            <input 
              v-model="form.accentColor" 
              type="text" 
              class="flex-1 px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all uppercase" 
            />
          </div>
        </div>

        <!-- Font Input -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.branding.fontFamily') }}</label>
          <select 
            v-model="form.fontFamily" 
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all"
          >
            <option value="Inter, sans-serif">Inter ({{ $t('admin.branding.fonts.modern') }})</option>
            <option value="'Playfair Display', serif">Playfair Display ({{ $t('admin.branding.fonts.classic') }})</option>
            <option value="Roboto, sans-serif">Roboto ({{ $t('admin.branding.fonts.standard') }})</option>
            <option value="'Outfit', sans-serif">Outfit ({{ $t('admin.branding.fonts.rounded') }})</option>
          </select>
        </div>

        <!-- Welcome Message -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.branding.welcomeMessage') }}</label>
          <input 
            v-model="form.welcomeMessage" 
            type="text" 
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" 
          />
        </div>

      </div>

      <!-- Preview Panel -->
      <div class="lg:col-span-7 xl:col-span-8 flex flex-col items-center justify-center p-8 bg-slate-50 rounded-2xl border border-slate-200 border-dashed">
        <h3 class="font-bold text-slate-400 mb-8 uppercase tracking-widest text-sm">{{ $t('admin.branding.preview') }}</h3>
        
        <!-- Mobile Frame Mockup -->
        <div class="w-[320px] sm:w-[375px] h-[667px] bg-white rounded-[40px]  border-[8px] border-slate-900 overflow-hidden relative flex flex-col" :style="{ fontFamily: form.fontFamily }">
          
          <!-- Mock Status Bar -->
          <div class="h-6 w-full flex justify-between items-center px-6 text-[10px] font-bold text-slate-900 mt-2 z-10 shrink-0">
            <span>09:41</span>
            <div class="flex items-center gap-1">
              <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/></svg>
            </div>
          </div>

          <!-- Dynamic Header -->
          <div class="w-full flex-1 overflow-y-auto pb-8">
            <div class="w-full h-48 relative flex items-end p-6" :style="{ backgroundColor: form.primaryColor }">
              <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              <div class="relative z-10 w-full">
                <!-- Logo -->
                <div v-if="form.logo" class="h-12 w-auto max-w-[150px] mb-4 bg-white/20 p-2 rounded-lg backdrop-blur-sm">
                  <img :src="form.logo" class="h-full w-auto object-contain" />
                </div>
                <div v-else class="h-12 w-12 bg-white/20 rounded-xl mb-4 backdrop-blur-sm flex items-center justify-center">
                  <span class="text-white font-bold text-xl">R</span>
                </div>
                
                <h2 class="text-white font-bold text-2xl leading-tight">{{ form.welcomeMessage || $t('admin.branding.defaultWelcome') }}</h2>
                <p class="text-white/80 text-sm mt-1">{{ $t('admin.branding.previewSubtitle') }}</p>
              </div>
            </div>

            <!-- Mock Categories -->
            <div class="px-4 py-6">
              <div class="flex gap-3 overflow-hidden mb-6">
                <div class="px-4 py-2 bg-slate-900 text-white rounded-full text-sm font-semibold shrink-0" :style="{ backgroundColor: form.primaryColor }">{{ $t('admin.branding.previewPopular') }}</div>
                <div class="px-4 py-2 bg-slate-100 text-slate-600 rounded-full text-sm font-semibold shrink-0">{{ $t('admin.branding.previewMain') }}</div>
                <div class="px-4 py-2 bg-slate-100 text-slate-600 rounded-full text-sm font-semibold shrink-0">{{ $t('admin.branding.previewDrinks') }}</div>
              </div>

              <!-- Mock Items -->
              <div class="space-y-4">
                <div v-for="i in 3" :key="i" class="flex gap-4 items-center p-3 bg-white border border-slate-100 rounded-2xl shadow-sm">
                  <div class="w-20 h-20 bg-slate-100 rounded-xl shrink-0"></div>
                  <div class="flex-1">
                    <div class="h-4 w-3/4 bg-slate-200 rounded mb-2"></div>
                    <div class="h-3 w-1/2 bg-slate-100 rounded mb-4"></div>
                    <div class="h-4 w-1/4 rounded" :style="{ backgroundColor: form.primaryColor + '40' }"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Mock Floating Button -->
          <div class="absolute bottom-6 left-1/2 -translate-x-1/2 px-6 py-3 rounded-full shadow-lg shadow-black/20 flex items-center gap-2 text-white font-bold text-sm" :style="{ backgroundColor: form.primaryColor }">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" /></svg>
            {{ $t('admin.branding.previewCart') }}
          </div>

        </div>

      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()

useHead({
  title: () => `${t('admin.branding.title')} | Admin | feasymenu`
})

// State
const saving = ref(false)
const form = ref({
  logo: 'https://images.unsplash.com/photo-1599305445671-ac291c95aaa9?w=200&h=50&fit=crop&q=80&bg=transparent',
  primaryColor: '#0f172a', // slate-900 default
  accentColor: '#6366f1',
  fontFamily: 'Inter, sans-serif',
  welcomeMessage: t('admin.branding.defaultWelcomeLong')
})

// Methods
async function loadSettings() {
  try {
    const data = await fetchJson('/api/tenant/config')
    if (data) {
      form.value = {
        logo: data.logoUrl || form.value.logo,
        primaryColor: data.primaryColor || form.value.primaryColor,
        accentColor: data.accentColor || form.value.accentColor,
        fontFamily: data.fontFamily || form.value.fontFamily,
        welcomeMessage: data.welcomeMessage || form.value.welcomeMessage
      }
    }
  } catch (e) {
    console.error('Failed to load branding settings, using defaults', e)
  }
}

async function saveSettings() {
  saving.value = true
  try {
    // We send PUT /api/tenant/branding
    await fetchJson(`/api/tenant/branding`, {
      method: 'PUT',
      body: JSON.stringify({
        primaryColor: form.value.primaryColor,
        accentColor: form.value.accentColor,
        logoUrl: form.value.logo,
        welcomeMessage: form.value.welcomeMessage,
        fontFamily: form.value.fontFamily
      })
    })
    
    uiStore.success(t('admin.branding.saveSuccess'))
  } catch (e: any) {
    console.error('Branding save failed', e);
    const errorMessage = e?.message || e?.toString() || t('admin.branding.saveFailure');
    uiStore.error(errorMessage);
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>
