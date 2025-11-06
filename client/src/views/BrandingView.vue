<template>
  <div class="max-w-3xl">
    <h2 class="mb-4 text-xl font-semibold">Marka Ayarları</h2>

    <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
      <!-- Colors -->
      <section class="rounded-lg border bg-white p-4 shadow">
        <h3 class="mb-2 font-medium">Renkler</h3>
        <p class="mb-3 text-sm text-gray-600">Renk özelleştirme tüm planlarda kullanılabilir.</p>
        <div class="grid grid-cols-1 gap-3">
          <label class="block">
            <span class="text-sm text-gray-600">Birincil Renk</span>
            <div class="mt-1 flex items-center gap-2">
              <input v-model="primaryColor" type="color" class="h-10 w-10 rounded border p-0" />
              <input v-model="primaryColor" class="flex-1 rounded border p-2" placeholder="#6366f1" />
            </div>
          </label>
          <label class="block">
            <span class="text-sm text-gray-600">Vurgu Rengi</span>
            <div class="mt-1 flex items-center gap-2">
              <input v-model="accentColor" type="color" class="h-10 w-10 rounded border p-0" />
              <input v-model="accentColor" class="flex-1 rounded border p-2" placeholder="#4f46e5" />
            </div>
          </label>
        </div>
        <div class="mt-4 flex justify-end gap-2">
          <button class="rounded border px-3 py-2" @click="resetColors">Sıfırla</button>
          <button class="bg-brand-gradient rounded px-4 py-2 text-white shadow hover:opacity-95" :disabled="savingColors" @click="saveColors">
            <span v-if="savingColors">Kaydediliyor…</span>
            <span v-else>Kaydet</span>
          </button>
        </div>
      </section>

      <!-- Logo URL -->
      <section class="rounded-lg border bg-white p-4 shadow">
        <h3 class="mb-2 font-medium">Logo</h3>
        <div v-if="logoUrl" class="mb-3">
          <img :src="logoUrl" alt="Logo" class="h-16 max-w-full object-contain" />
        </div>
        <label class="block">
          <span class="text-sm text-gray-600">Logo URL'si</span>
          <input
            v-model="logoUrl"
            :disabled="!isPaidPlan"
            :class="['mt-1 w-full rounded border p-2', !isPaidPlan ? 'bg-gray-50 text-gray-500 cursor-not-allowed' : '']"
            placeholder="https://..."
          />
          <p v-if="!isPaidPlan" class="mt-1 text-xs text-gray-500">
            Logo özelleştirme Standart/Pro yıllık planlarda mevcuttur. Renkleri düzenleyebilirsiniz.
          </p>
        </label>
        <div class="mt-3 flex justify-end">
          <router-link v-if="!isPaidPlan" to="/admin/upgrade" class="btn btn-primary">Planı Yükselt</router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { fetchJson } from '@/utils/api';

const primaryColor = ref('');
const accentColor = ref('');
const logoUrl = ref('');
const savingColors = ref(false);
// no upload flow; URL is saved with colors

const isPaidPlan = computed(() => {
  try {
    const raw = localStorage.getItem('qm_tenant_cfg');
    if (!raw) return false;
    const cfg = JSON.parse(raw);
    const plan = String(cfg?.plan || '').toUpperCase();
    return plan && plan !== 'FREE';
  } catch {
    return false;
  }
});

function applyCssVars(cfg) {
  try {
    if (cfg?.primaryColor)
      document.documentElement.style.setProperty('--brand-primary', cfg.primaryColor);
    if (cfg?.accentColor)
      document.documentElement.style.setProperty('--brand-accent', cfg.accentColor);
  } catch { /* ignore */ }
}

async function loadConfig() {
  try {
    const cfg = await fetchJson('/api/tenant/config', { silentError: true });
    primaryColor.value = cfg.primaryColor || '#6366f1';
    accentColor.value = cfg.accentColor || '#4f46e5';
    logoUrl.value = cfg.logoUrl || '';
    // Apply immediately
    applyCssVars(cfg);
  } catch { /* ignore */ }
}

function resetColors() {
  loadConfig();
}

async function saveColors() {
  savingColors.value = true;
  try {
    const res = await fetchJson('/api/tenant/branding', {
      method: 'PUT',
      body: JSON.stringify({ primaryColor: primaryColor.value, accentColor: accentColor.value, logoUrl: isPaidPlan.value ? logoUrl.value : undefined }),
    });
    // Persist and apply immediately
    try {
      const raw = localStorage.getItem('qm_tenant_cfg');
      const cfg = raw ? JSON.parse(raw) : {};
      const next = Object.assign({}, cfg, res || {}, {
        primaryColor: primaryColor.value,
        accentColor: accentColor.value,
        logoUrl: isPaidPlan.value ? logoUrl.value : cfg.logoUrl,
      });
      localStorage.setItem('qm_tenant_cfg', JSON.stringify(next));
      applyCssVars(next);
  } catch { /* ignore */ }
    const ui = (await import('@/stores/uiStore')).useUiStore();
    ui.toastSuccess('Kaydedildi');
  } catch (e) {
    console.debug(e);
  } finally {
    savingColors.value = false;
  }
}

onMounted(() => {
  loadConfig();
});
</script>
