<template>
  <div class="max-w-xl mx-auto px-6 py-10">
    <header class="mb-6 text-center">
      <h1 class="text-2xl md:text-3xl font-bold text-gray-900">Yeni İşletme Kaydı</h1>
      <p class="text-gray-600 mt-1">Kod, renkler ve isteğe bağlı logo ile işletmenizi oluşturun. İsterseniz ilk admin kullanıcınızı da ekleyebilirsiniz.</p>
    </header>

    <div class="bg-white rounded-xl shadow p-6 space-y-4">
      <div class="grid grid-cols-1 gap-3">
        <label class="block">
          <span class="text-sm text-gray-600">Kod <span class="text-red-500">*</span></span>
          <input v-model="form.code" class="w-full p-2 border rounded" placeholder="ör. my-bistro" required />
        </label>
        <label class="block">
          <span class="text-sm text-gray-600">Ad</span>
          <input v-model="form.name" class="w-full p-2 border rounded" />
        </label>
        <label class="block">
          <span class="text-sm text-gray-600">Logo URL'si</span>
          <input v-model="form.logoUrl" class="w-full p-2 border rounded" placeholder="https://..." />
        </label>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <label class="block">
            <span class="text-sm text-gray-600">Birincil Renk <span class="text-red-500">*</span></span>
            <input v-model="form.primaryColor" class="w-full p-2 border rounded" placeholder="#6366f1" required />
          </label>
          <label class="block">
            <span class="text-sm text-gray-600">Vurgu Rengi <span class="text-red-500">*</span></span>
            <input v-model="form.accentColor" class="w-full p-2 border rounded" placeholder="#4f46e5" required />
          </label>
        </div>
        <label class="block">
          <span class="text-sm text-gray-600">Ayarlar (JSON)</span>
          <textarea v-model="form.config" rows="4" class="w-full p-2 border rounded" placeholder='{"currency":"TRY"}'></textarea>
        </label>
      </div>

      <details class="group">
        <summary class="cursor-pointer select-none text-sm text-indigo-700">İlk Admin Kullanıcı (opsiyonel)</summary>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-2">
          <input v-model="form.adminUsername" class="p-2 border rounded" placeholder="Admin kullanıcı adı" />
          <input v-model="form.adminPassword" type="password" class="p-2 border rounded" placeholder="Admin parola" />
        </div>
      </details>

      <div class="flex items-center justify-end gap-2">
        <router-link to="/" class="px-3 py-2 border rounded">Vazgeç</router-link>
        <button class="px-4 py-2 bg-brand-gradient text-white rounded shadow hover:opacity-95" :disabled="saving || !isValid" @click="submit">
          <span v-if="saving">Kaydediliyor…</span>
          <span v-else>Oluştur</span>
        </button>
      </div>
    </div>

    <div v-if="done" class="mt-6 p-4 rounded-lg border bg-green-50 text-green-800">
      <div class="font-semibold mb-1">İşletme oluşturuldu.</div>
      <div class="text-sm">
        <div class="mb-1">Kod: <strong>{{ done.code }}</strong></div>
        <div v-if="form.adminUsername">Admin kullanıcı: <strong>{{ form.adminUsername }}</strong></div>
        <div class="mt-2">Yönetim için: <code class="px-1 py-0.5 bg-white rounded border">/r/{{ done.code }}/admin</code> veya alt alan adı ile erişebilirsiniz.</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { fetchJson } from '@/utils/api';

const form = ref({ code: '', name: '', logoUrl: '', primaryColor: '', accentColor: '', config: '', adminUsername: '', adminPassword: '' });
const saving = ref(false);
const done = ref(null);

const isValid = computed(() => {
  const f = form.value;
  return Boolean(f.code && f.code.trim() && f.primaryColor && f.primaryColor.trim() && f.accentColor && f.accentColor.trim());
});

async function submit() {
  if (!isValid.value) return;
  saving.value = true;
  try {
    const res = await fetchJson('/api/public/tenant-signup', { method: 'POST', body: JSON.stringify(form.value) });
    done.value = res;
  } catch (e) {
    // Optionally handle error with a toast if available
    console.debug(e);
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
</style>
