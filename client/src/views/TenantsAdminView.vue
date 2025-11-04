<template>
  <div class="space-y-6">
    <header class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold">İşletme Yönetimi (Süper Admin)</h1>
      <div class="flex gap-2">
        <button class="rounded bg-brand-500 px-3 py-2 text-white" @click="openCreate()">
          Yeni İşletme
        </button>
        <button class="rounded border px-3 py-2" @click="onLogout">Çıkış</button>
      </div>
    </header>

    <div class="divide-y rounded-lg bg-white shadow">
      <div v-for="t in tenants" :key="t.id" class="flex items-center justify-between p-4">
        <div class="flex items-center gap-4">
          <div class="flex h-10 w-10 items-center justify-center rounded-full bg-gray-100 text-sm">
            {{ t.code?.slice(0, 2)?.toUpperCase() }}
          </div>
          <div>
            <div class="font-medium">{{ t.name || t.code }}</div>
            <div class="text-xs text-gray-500">{{ t.code }}</div>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <button class="rounded border px-2 py-1 text-sm" @click="openBootstrap(t)">
            Kullanıcıları Oluştur
          </button>
          <button class="rounded border px-2 py-1 text-sm" @click="openEdit(t)">Düzenle</button>
          <button class="rounded border px-2 py-1 text-sm text-red-600" @click="remove(t)">
            Sil
          </button>
        </div>
      </div>
      <div v-if="!loading && tenants.length === 0" class="p-6 text-gray-500">Kayıt yok</div>
      <div v-if="loading" class="p-6 text-gray-500">Yükleniyor…</div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
      <div class="w-full max-w-lg space-y-4 rounded-xl bg-white p-6 shadow">
        <h3 class="text-lg font-semibold">{{ form.id ? "İşletme Düzenle" : "Yeni İşletme" }}</h3>
        <div class="grid grid-cols-1 gap-3">
          <label class="block">
            <span class="text-sm text-gray-600">Kod <span class="text-red-500">*</span></span>
            <input
              v-model="form.code"
              :disabled="!!form.id"
              class="w-full rounded border p-2"
              placeholder="ör. my-bistro"
              required
            />
          </label>
          <label class="block">
            <span class="text-sm text-gray-600">Ad</span>
            <input v-model="form.name" class="w-full rounded border p-2" />
          </label>
          <label class="block">
            <span class="text-sm text-gray-600">Logo URL'si</span>
            <input
              v-model="form.logoUrl"
              class="w-full rounded border p-2"
              placeholder="https://..."
            />
          </label>
          <div class="grid grid-cols-2 gap-3">
            <label class="block">
              <span class="text-sm text-gray-600"
                >Birincil Renk <span class="text-red-500">*</span></span
              >
              <input
                v-model="form.primaryColor"
                class="w-full rounded border p-2"
                placeholder="#0ea5e9"
                required
              />
            </label>
            <label class="block">
              <span class="text-sm text-gray-600"
                >Vurgu Rengi <span class="text-red-500">*</span></span
              >
              <input
                v-model="form.accentColor"
                class="w-full rounded border p-2"
                placeholder="#f59e0b"
                required
              />
            </label>
          </div>
          <label class="block">
            <span class="text-sm text-gray-600">Ayarlar (JSON)</span>
            <textarea
              v-model="form.config"
              rows="5"
              class="w-full rounded border p-2"
              placeholder='{"currency":"TRY"}'
            ></textarea>
          </label>
        </div>
        <div class="flex justify-end gap-2">
          <button class="rounded border px-3 py-2" @click="closeForm" :disabled="formSaving">
            Vazgeç
          </button>
          <button
            class="rounded bg-brand-500 px-3 py-2 text-white"
            @click="save"
            :disabled="formSaving || !isFormValid"
          >
            <span v-if="formSaving">Kaydediliyor…</span>
            <span v-else>Kaydet</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Bootstrap Users Modal -->
    <div
      v-if="showBootstrap"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/30"
    >
      <div class="w-full max-w-lg space-y-4 rounded-xl bg-white p-6 shadow">
        <h3 class="text-lg font-semibold">Kullanıcıları Oluştur ({{ selected?.code }})</h3>
        <div class="grid grid-cols-1 gap-3">
          <div class="grid grid-cols-2 gap-3">
            <input
              v-model="bootstrap.adminUsername"
              class="rounded border p-2"
              placeholder="Admin kullanıcı adı"
            />
            <input
              v-model="bootstrap.adminPassword"
              class="rounded border p-2"
              type="password"
              placeholder="Admin parola"
            />
          </div>
          <div class="grid grid-cols-2 gap-3">
            <input
              v-model="bootstrap.kitchenUsername"
              class="rounded border p-2"
              placeholder="Mutfak kullanıcı adı"
            />
            <input
              v-model="bootstrap.kitchenPassword"
              class="rounded border p-2"
              type="password"
              placeholder="Mutfak parola"
            />
          </div>
          <div class="grid grid-cols-2 gap-3">
            <input
              v-model="bootstrap.barUsername"
              class="rounded border p-2"
              placeholder="Bar kullanıcı adı"
            />
            <input
              v-model="bootstrap.barPassword"
              class="rounded border p-2"
              type="password"
              placeholder="Bar parola"
            />
          </div>
        </div>
        <div class="flex justify-end gap-2">
          <button
            class="rounded border px-3 py-2"
            @click="closeBootstrap"
            :disabled="bootstrapSaving"
          >
            Vazgeç
          </button>
          <button
            class="rounded bg-brand-500 px-3 py-2 text-white"
            @click="saveBootstrap"
            :disabled="bootstrapSaving"
          >
            <span v-if="bootstrapSaving">Kaydediliyor…</span>
            <span v-else>Kaydet</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted, computed } from "vue";
  import { fetchJson } from "@/utils/api";
  import { useUiStore } from "@/stores/uiStore";
  import { useAuthStore } from "@/stores/authStore";
  import { useRouter } from "vue-router";

  const ui = useUiStore();
  const auth = useAuthStore();
  const router = useRouter();
  const tenants = ref([]);
  const loading = ref(false);
  const formSaving = ref(false);
  const bootstrapSaving = ref(false);

  const showForm = ref(false);
  const form = ref({
    id: null,
    code: "",
    name: "",
    logoUrl: "",
    primaryColor: "",
    accentColor: "",
    config: "",
  });

  const showBootstrap = ref(false);
  const selected = ref(null);
  const bootstrap = ref({
    adminUsername: "",
    adminPassword: "",
    kitchenUsername: "",
    kitchenPassword: "",
    barUsername: "",
    barPassword: "",
  });

  async function load() {
    loading.value = true;
    try {
      tenants.value = await fetchJson("/api/tenants");
    } catch (e) {
      console.debug(e);
    } finally {
      loading.value = false;
    }
  }

  function openCreate() {
    form.value = {
      id: null,
      code: "",
      name: "",
      logoUrl: "",
      primaryColor: "",
      accentColor: "",
      config: "",
    };
    showForm.value = true;
  }
  function openEdit(t) {
    form.value = {
      id: t.id,
      code: t.code,
      name: t.name,
      logoUrl: t.logoUrl,
      primaryColor: t.primaryColor,
      accentColor: t.accentColor,
      config: t.config || "",
    };
    showForm.value = true;
  }
  function closeForm() {
    showForm.value = false;
  }
  async function save() {
    const f = form.value;
    const missing = !f.code?.trim() || !f.primaryColor?.trim() || !f.accentColor?.trim();
    if (missing) {
      ui.toastError("Kod, Birincil Renk ve Vurgu Rengi zorunludur.");
      return;
    }
    formSaving.value = true;
    let ok = false;
    try {
      if (f.id) {
        await fetchJson(`/api/tenants/${f.id}`, { method: "PUT", body: JSON.stringify(f) });
        try {
          ui.toastOk("Güncellendi");
        } catch (err) {
          /* ignore toast error */
        }
      } else {
        await fetchJson("/api/tenants", { method: "POST", body: JSON.stringify(f) });
        try {
          ui.toastOk("Oluşturuldu");
        } catch (err) {
          /* ignore toast error */
        }
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
    bootstrap.value = {
      adminUsername: "",
      adminPassword: "",
      kitchenUsername: "",
      kitchenPassword: "",
      barUsername: "",
      barPassword: "",
    };
    showBootstrap.value = true;
  }
  function closeBootstrap() {
    showBootstrap.value = false;
  }
  async function saveBootstrap() {
    bootstrapSaving.value = true;
    let ok = false;
    try {
      await fetchJson(`/api/tenants/${selected.value.code}/bootstrap-users`, {
        method: "POST",
        body: JSON.stringify(bootstrap.value),
      });
      try {
        ui.toastOk("Bootstrap tamam");
      } catch (err) {
        /* ignore toast error */
      }
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
      await fetchJson(`/api/tenants/${t.id}`, { method: "DELETE" });
      try {
        ui.toastOk("Silindi");
      } catch (err) {
        /* ignore toast error */
      }
      ok = true;
    } catch (e) {
      console.debug(e);
    } finally {
      if (ok) await load();
    }
  }

  async function onLogout() {
    try {
      const { useOrderStore } = await import("@/stores/orderStore");
      const store = useOrderStore();
      store.resetAfterLogout();
    } catch {
      /* ignore */
    }
    try {
      auth.logout();
    } finally {
      try {
        router.push({ name: "home" });
      } catch {
        /* ignore */
      }
    }
  }

  onMounted(load);

  const isFormValid = computed(() => {
    const f = form.value || {};
    return Boolean(
      f.code &&
        String(f.code).trim() &&
        f.primaryColor &&
        String(f.primaryColor).trim() &&
        f.accentColor &&
        String(f.accentColor).trim(),
    );
  });
</script>

<style scoped></style>
