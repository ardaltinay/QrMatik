<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="mb-8 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.branding.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.branding.subtitle') }}</p>
      </div>
      <button 
        @click="saveSettings" 
        class="w-full sm:w-auto px-6 py-2.5 bg-brand-500 text-white font-semibold rounded-xl hover:bg-brand-600 transition-colors shadow-sm flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed" 
        :disabled="saving || currentPlan === 'FREE'"
      >
        <svg v-if="saving" class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
        </svg>
        {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
      </button>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 relative">
      
      <!-- Premium Overlay for FREE Users -->
      <div v-if="currentPlan === 'FREE'" class="absolute inset-0 z-50 backdrop-blur-[2px] bg-white/30 rounded-2xl flex items-center justify-center p-6 text-center">
        <div class="bg-white p-8 rounded-3xl shadow-xl border border-slate-200 max-w-md animate-in fade-in zoom-in duration-300">
          <div class="w-16 h-16 bg-brand-100 text-brand-600 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-2">{{ $t('admin.branding.premiumTitle') || 'Premium Özellik' }}</h3>
          <p class="text-slate-500 mb-8">{{ $t('admin.branding.premiumDesc') || 'Marka ve tema özelleştirmeleri sadece PRO ve ENTERPRISE planlarda kullanılabilir.' }}</p>
          <NuxtLink :to="localePath('/admin/upgrade')" class="inline-flex items-center justify-center px-8 py-3 bg-brand-500 text-white font-bold rounded-xl hover:bg-brand-600 transition-all shadow-lg shadow-brand-500/25">
            {{ $t('admin.upgrade.button') }}
          </NuxtLink>
        </div>
      </div>

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

        <!-- Address -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.branding.address') }}</label>
          <textarea 
            v-model="form.address" 
            rows="2"
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all resize-none" 
          ></textarea>
        </div>

        <!-- Phone -->
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.branding.phone') }}</label>
          <input 
            v-model="form.phone" 
            type="text" 
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" 
          />
        </div>

      </div>

      <!-- Preview Panel -->
      <div class="lg:col-span-7 xl:col-span-8 flex flex-col items-center justify-center p-8 bg-slate-50 rounded-2xl border border-slate-200 border-dashed min-h-[800px]">
        <h3 class="font-bold text-slate-400 mb-8 uppercase tracking-widest text-sm">{{ $t('admin.branding.preview') }}</h3>
        
        <LandingPhoneMockup 
          :primary-color="form.primaryColor"
          :accent-color="form.accentColor"
          :logo-url="form.logo"
          :font-family="form.fontFamily"
          :tenant-name="authStore.user?.tenantCode"
        />
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
const authStore = useAuthStore()
const localePath = useLocalePath()

useHead({
  title: () => `${t('admin.branding.title')} | Admin`
})

// State
const saving = ref(false)
const form = ref({
  logo: 'https://images.unsplash.com/photo-1599305445671-ac291c95aaa9?w=200&h=50&fit=crop&q=80&bg=transparent',
  primaryColor: '#0f172a', // slate-900 default
  accentColor: '#6366f1',
  fontFamily: 'Inter, sans-serif',
  welcomeMessage: t('admin.branding.defaultWelcomeLong'),
  address: '',
  phone: ''
})

const currentPlan = computed(() => authStore.user?.tenant?.subscriptionPlan || 'FREE')

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
        welcomeMessage: data.welcomeMessage || form.value.welcomeMessage,
        address: data.address || '',
        phone: data.phone || ''
      }
    }
  } catch (e) {
    console.error('Failed to load branding settings, using defaults', e)
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
        phone: form.value.phone
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
