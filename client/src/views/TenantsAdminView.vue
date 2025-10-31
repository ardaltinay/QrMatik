<template>
  <div class="space-y-6">
    <header class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold">İşletme Yönetimi (Süper Admin)</h1>
      <div class="flex gap-2">
        <button class="px-3 py-2 bg-brand-500 text-white rounded" @click="openCreate()">Yeni İşletme</button>
        <button class="px-3 py-2 border rounded" @click="onLogout">Çıkış</button>
      </div>
    </header>

    <div class="bg-white rounded-lg shadow divide-y">
      <div v-for="t in tenants" :key="t.id" class="p-4 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-sm">
            {{ t.code?.slice(0,2)?.toUpperCase() }}
          </div>
          <div>
            <div class="font-medium">{{ t.name || t.code }}</div>
            <div class="text-xs text-gray-500">{{ t.code }}</div>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <button class="px-2 py-1 text-sm border rounded" @click="openBootstrap(t)">Kullanıcıları Oluştur</button>
          <button class="px-2 py-1 text-sm border rounded" @click="openEdit(t)">Düzenle</button>
          <button class="px-2 py-1 text-sm border rounded text-red-600" @click="remove(t)">Sil</button>
        </div>
      </div>
      <div v-if="!loading && tenants.length === 0" class="p-6 text-gray-500">Kayıt yok</div>
      <div v-if="loading" class="p-6 text-gray-500">Yükleniyor…</div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showForm" class="fixed inset-0 bg-black/30 flex items-center justify-center z-50">
      <div class="w-full max-w-lg bg-white rounded-xl shadow p-6 space-y-4">
        <h3 class="font-semibold text-lg">{{ form.id ? 'İşletme Düzenle' : 'Yeni İşletme' }}</h3>
  <div class="grid grid-cols-1 gap-3">
          <label class="block">
            <span class="text-sm text-gray-600">Kod <span class="text-red-500">*</span></span>
            <input v-model="form.code" :disabled="!!form.id" class="w-full p-2 border rounded" placeholder="ör. my-bistro" required />
          </label>
          <label class="block">
            <span class="text-sm text-gray-600">Ad</span>
            <input v-model="form.name" class="w-full p-2 border rounded" />
          </label>
          <label class="block">
            <span class="text-sm text-gray-600">Logo URL'si</span>
            <input v-model="form.logoUrl" class="w-full p-2 border rounded" placeholder="https://..." />
          </label>
          <div class="grid grid-cols-2 gap-3">
            <label class="block">
              <span class="text-sm text-gray-600">Birincil Renk <span class="text-red-500">*</span></span>
              <input v-model="form.primaryColor" class="w-full p-2 border rounded" placeholder="#0ea5e9" required />
            </label>
            <label class="block">
              <span class="text-sm text-gray-600">Vurgu Rengi <span class="text-red-500">*</span></span>
              <input v-model="form.accentColor" class="w-full p-2 border rounded" placeholder="#f59e0b" required />
            </label>
          </div>
          <label class="block">
            <span class="text-sm text-gray-600">Ayarlar (JSON)</span>
            <textarea v-model="form.config" rows="5" class="w-full p-2 border rounded" placeholder='{"currency":"TRY"}'></textarea>
          </label>
        </div>
        <div class="flex justify-end gap-2">
          <button class="px-3 py-2 border rounded" @click="closeForm" :disabled="formSaving">Vazgeç</button>
          <button class="px-3 py-2 bg-brand-500 text-white rounded" @click="save" :disabled="formSaving || !isFormValid">
            <span v-if="formSaving">Kaydediliyor…</span>
            <span v-else>Kaydet</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Bootstrap Users Modal -->
    <div v-if="showBootstrap" class="fixed inset-0 bg-black/30 flex items-center justify-center z-50">
      <div class="w-full max-w-lg bg-white rounded-xl shadow p-6 space-y-4">
        <h3 class="font-semibold text-lg">Kullanıcıları Oluştur ({{ selected?.code }})</h3>
        <div class="grid grid-cols-1 gap-3">
          <div class="grid grid-cols-2 gap-3">
            <input v-model="bootstrap.adminUsername" class="p-2 border rounded" placeholder="Admin kullanıcı adı" />
            <input v-model="bootstrap.adminPassword" class="p-2 border rounded" type="password" placeholder="Admin parola" />
          </div>
          <div class="grid grid-cols-2 gap-3">
            <input v-model="bootstrap.kitchenUsername" class="p-2 border rounded" placeholder="Mutfak kullanıcı adı" />
            <input v-model="bootstrap.kitchenPassword" class="p-2 border rounded" type="password" placeholder="Mutfak parola" />
          </div>
          <div class="grid grid-cols-2 gap-3">
            <input v-model="bootstrap.barUsername" class="p-2 border rounded" placeholder="Bar kullanıcı adı" />
            <input v-model="bootstrap.barPassword" class="p-2 border rounded" type="password" placeholder="Bar parola" />
          </div>
        </div>
        <div class="flex justify-end gap-2">
          <button class="px-3 py-2 border rounded" @click="closeBootstrap" :disabled="bootstrapSaving">Vazgeç</button>
          <button class="px-3 py-2 bg-brand-500 text-white rounded" @click="saveBootstrap" :disabled="bootstrapSaving">
            <span v-if="bootstrapSaving">Kaydediliyor…</span>
            <span v-else>Kaydet</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { fetchJson } from '@/utils/api';
import { useUiStore } from '@/stores/uiStore';
import { useAuthStore } from '@/stores/authStore';
import { useRouter } from 'vue-router';

const ui = useUiStore();
const auth = useAuthStore();
const router = useRouter();
const tenants = ref([]);
const loading = ref(false);
const formSaving = ref(false);
const bootstrapSaving = ref(false);

const showForm = ref(false);
const form = ref({ id: null, code: '', name: '', logoUrl: '', primaryColor: '', accentColor: '', config: '' });

const showBootstrap = ref(false);
const selected = ref(null);
const bootstrap = ref({ adminUsername: '', adminPassword: '', kitchenUsername: '', kitchenPassword: '', barUsername: '', barPassword: '' });

async function load() {
  loading.value = true;
  try {
    tenants.value = await fetchJson('/api/tenants');
  } catch (e) {
    console.debug(e);
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  form.value = { id: null, code: '', name: '', logoUrl: '', primaryColor: '', accentColor: '', config: '' };
  showForm.value = true;
}
function openEdit(t) {
  form.value = { id: t.id, code: t.code, name: t.name, logoUrl: t.logoUrl, primaryColor: t.primaryColor, accentColor: t.accentColor, config: t.config || '' };
  showForm.value = true;
}
function closeForm() {
  showForm.value = false;
}
async function save() {
  const f = form.value;
  const missing = !f.code?.trim() || !f.primaryColor?.trim() || !f.accentColor?.trim();
  if (missing) {
    ui.toastError('Kod, Birincil Renk ve Vurgu Rengi zorunludur.');
    return;
  }
  formSaving.value = true;
  let ok = false;
  try {
    if (f.id) {
      await fetchJson(`/api/tenants/${f.id}`, { method: 'PUT', body: JSON.stringify(f) });
  try { ui.toastOk('Güncellendi'); } catch (err) { /* ignore toast error */ }
    } else {
      await fetchJson('/api/tenants', { method: 'POST', body: JSON.stringify(f) });
  try { ui.toastOk('Oluşturuldu'); } catch (err) { /* ignore toast error */ }
    }
    ok = true;
  } catch (e) {
    console.debug(e);
  } finally {
    if (ok) {
      showForm.value = false;
      await load();
    }
    formSaving.value = false;
  }
}

function openBootstrap(t) {
  selected.value = t;
  bootstrap.value = { adminUsername: '', adminPassword: '', kitchenUsername: '', kitchenPassword: '', barUsername: '', barPassword: '' };
  showBootstrap.value = true;
}
function closeBootstrap() {
  showBootstrap.value = false;
}
async function saveBootstrap() {
  bootstrapSaving.value = true;
  let ok = false;
  try {
    await fetchJson(`/api/tenants/${selected.value.code}/bootstrap-users`, { method: 'POST', body: JSON.stringify(bootstrap.value) });
  try { ui.toastOk('Bootstrap tamam'); } catch (err) { /* ignore toast error */ }
    ok = true;
  } catch (e) {
    console.debug(e);
  } finally {
    if (ok) {
      showBootstrap.value = false;
      await load();
    }
    bootstrapSaving.value = false;
  }
}

async function remove(t) {
  let ok = false;
  try {
    if (!confirm(`${t.code} silinsin mi?`)) return;
    await fetchJson(`/api/tenants/${t.id}`, { method: 'DELETE' });
    try { ui.toastOk('Silindi'); } catch (err) { /* ignore toast error */ }
    ok = true;
  } catch (e) {
    console.debug(e);
  } finally {
    if (ok) await load();
  }
}

async function onLogout() {
  try {
    const { useOrderStore } = await import('@/stores/orderStore');
    const store = useOrderStore();
    store.resetAfterLogout();
  } catch { /* ignore */ }
  try {
    auth.logout();
  } finally {
    try { router.push({ name: 'home' }); } catch { /* ignore */ }
  }
}

onMounted(load);

const isFormValid = computed(() => {
  const f = form.value || {};
  return Boolean((f.code && String(f.code).trim()) && (f.primaryColor && String(f.primaryColor).trim()) && (f.accentColor && String(f.accentColor).trim()));
});
</script>

<style scoped>
</style>
