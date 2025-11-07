<template>
  <div class="space-y-6">
    <header class="flex flex-col items-stretch justify-between gap-3 sm:flex-row sm:items-center">
      <h1 class="text-2xl font-semibold">İşletme Yönetimi (Süper Admin)</h1>
      <div class="flex flex-col gap-2 sm:flex-row sm:gap-2">
        <button
          class="w-full rounded bg-brand-500 px-3 py-2 text-white sm:w-auto"
          @click="openCreate()"
        >
          Yeni İşletme
        </button>
        <button class="w-full rounded border px-3 py-2 sm:w-auto" @click="onLogout">Çıkış</button>
      </div>
    </header>

    <div class="divide-y rounded-lg bg-white shadow">
      <div
        v-for="t in tenants"
        :key="t.id"
        class="flex flex-col gap-3 p-4 md:flex-row md:items-center md:justify-between"
      >
        <!-- Sol blok: avatar + isim + paket badge + kod -->
        <div class="flex items-start gap-4 md:items-center">
          <div
            class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-gray-100 text-sm"
          >
            {{ t.code?.slice(0, 2)?.toUpperCase() }}
          </div>
          <div class="min-w-0 space-y-1">
            <div class="flex items-center gap-2">
              <div class="max-w-[60vw] truncate font-medium md:max-w-xs" :title="t.name || t.code">
                {{ t.name || t.code }}
              </div>
              <span
                class="rounded-full border px-2 py-0.5 text-xs"
                :class="{
                  'border-gray-300 text-gray-600': (t.plan || 'FREE') === 'FREE',
                  'border-amber-400 text-amber-700': t.plan === 'STANDARD',
                  'border-emerald-400 text-emerald-700': t.plan === 'PRO',
                }"
                >{{ t.plan || "FREE" }}</span
              >
            </div>
            <div class="break-all text-xs text-gray-500">{{ t.code }}</div>
          </div>
        </div>
        <!-- Sağ blok / Alt blok (mobilde): aksiyon butonları -->
        <div class="flex w-full flex-wrap gap-2 md:w-auto md:justify-end">
          <button
            class="flex w-full items-center justify-center gap-2 rounded border px-3 py-2 text-sm hover:bg-gray-50 md:w-auto"
            @click="openBootstrap(t)"
          >
            <!-- user-plus icon -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-4 w-4"
              aria-hidden="true"
            >
              <path
                d="M15 8a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM3 20a6 6 0 0 1 12 0v1H3v-1Zm16-9h-2v2a1 1 0 1 1-2 0v-2h-2a1 1 0 1 1 0-2h2V7a1 1 0 1 1 2 0v2h2a1 1 0 1 1 0 2Z"
              />
            </svg>
            <span>Kullanıcıları Oluştur</span>
          </button>
          <button
            class="w-full rounded border px-3 py-2 text-sm hover:bg-gray-50 md:w-auto"
            @click="openEdit(t)"
          >
            Düzenle
          </button>
          <button
            class="w-full rounded border px-3 py-2 text-sm text-red-600 hover:bg-red-50 md:w-auto"
            @click="remove(t)"
          >
            Sil
          </button>
        </div>
      </div>
      <div v-if="!loading && tenants.length === 0" class="p-6 text-gray-500">Kayıt yok</div>
      <div v-if="loading" class="p-6 text-gray-500">Yükleniyor…</div>
    </div>

    <!-- Create/Edit Modal -->
    <div
      v-if="showForm"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 p-3"
    >
      <div class="w-full max-w-lg space-y-4 rounded-xl bg-white p-4 shadow sm:p-6">
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
            <span class="text-sm text-gray-600">Plan</span>
            <select v-model="form.plan" class="w-full rounded border p-2">
              <option value="FREE">Ücretsiz</option>
              <option value="STANDARD">Standart</option>
              <option value="PRO">Pro</option>
            </select>
          </label>
          <label class="block" v-if="form.plan === 'PRO'">
            <span class="text-sm text-gray-600">Özel Alan Adı (CNAME)</span>
            <input
              v-model="form.customDomain"
              class="w-full rounded border p-2"
              placeholder="menu.isletmem.com"
            />
            <span class="mt-1 block text-xs text-gray-500"
              >DNS: custom alan adınızı ana domaininize CNAME ile yönlendirin.</span
            >
          </label>
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
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 p-3"
    >
      <div class="w-full max-w-lg space-y-4 rounded-xl bg-white p-4 shadow sm:p-6">
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
    plan: "FREE",
    customDomain: "",
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
      plan: "FREE",
      customDomain: "",
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
      plan: t.plan || "FREE",
      customDomain: t.customDomain || "",
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
