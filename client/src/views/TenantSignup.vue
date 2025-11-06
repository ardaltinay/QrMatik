<template>
  <div class="mx-auto max-w-xl px-6 py-10">
    <header class="mb-6 text-center">
      <h1 class="text-2xl font-bold text-gray-900 md:text-3xl">Yeni İşletme Kaydı</h1>
      <p class="mt-1 text-gray-600">
        Kod, renkler ve isteğe bağlı logo ile işletmenizi oluşturun. İsterseniz ilk admin
        kullanıcınızı da ekleyebilirsiniz.
      </p>
    </header>

    <div class="space-y-4 rounded-xl bg-white p-6 shadow">
      <div class="grid grid-cols-1 gap-3">
        <label class="block">
          <span class="text-sm text-gray-600">Kod <span class="text-red-500">*</span></span>
          <input
            v-model="form.code"
            :class="[
              'w-full rounded border p-2',
              codeTouched && !codeValid ? 'border-red-500' : '',
            ]"
            placeholder="ör. my-bistro"
            required
            :pattern="codePattern"
            title="Sadece küçük harf, rakam ve tire; başta/sonda tire olamaz"
            maxlength="63"
            @input="onCodeInput"
            @blur="codeTouched = true"
          />
          <p class="mt-1 text-xs text-gray-500">
            Bu alan alt alan adı (subdomain) olarak URL'de gözükecektir. Örn:
            <code class="rounded border bg-white px-1 py-0.5">{{ exampleSubdomain }}</code>
          </p>
          <p v-if="codeTouched && form.code && !codeValid" class="mt-1 text-xs text-red-600">
            Sadece küçük harf, rakam ve tire kullanın; başta/sonda tire olamaz.
          </p>
        </label>
        <label class="block">
          <span class="text-sm text-gray-600">İşletme adı</span>
          <input v-model="form.name" class="w-full rounded border p-2" />
        </label>
        <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <label class="block">
            <span class="text-sm text-gray-600"
              >İşletme sahibi adı soyadı <span class="text-red-500">*</span></span
            >
            <input
              v-model="form.ownerName"
              class="w-full rounded border p-2"
              placeholder="Ad Soyad"
              required
            />
          </label>
          <label class="block">
            <span class="text-sm text-gray-600"
              >İşletme sahibi e-posta <span class="text-red-500">*</span></span
            >
            <input
              v-model="form.ownerEmail"
              type="email"
              class="w-full rounded border p-2"
              placeholder="ornek@eposta.com"
              required
            />
          </label>
        </div>
        <label class="block">
          <span class="text-sm text-gray-600">Logo URL'si</span>
          <input
            v-model="form.logoUrl"
            class="w-full cursor-not-allowed rounded border bg-gray-50 p-2 text-gray-500"
            placeholder="Standart/Pro planlarda etkinleşir"
            disabled
            aria-disabled="true"
          />
          <p class="mt-1 text-xs text-gray-500">
            Logo özelleştirme Standart/Pro yıllık planlarda mevcuttur. Ücretsiz planda renk
            özelleştirmesi kullanılabilir. Planı yükselttikten sonra admin panelinden logo
            ekleyebilirsiniz.
          </p>
        </label>
        <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <label class="block">
            <span class="text-sm text-gray-600"
              >Birincil renk <span class="text-red-500">*</span></span
            >
            <div class="flex items-center gap-2">
              <input
                v-model="form.primaryColor"
                type="color"
                class="h-10 w-10 rounded border p-0"
                aria-label="Birincil renk"
              />
              <input
                v-model="form.primaryColor"
                class="flex-1 rounded border p-2"
                placeholder="#6366f1"
                required
              />
            </div>
            <p class="mt-1 text-xs text-gray-500">
              CTA butonları, ana vurgu alanları ve QR menü başlığı gibi kritik alanlarda
              kullanılacak ana renk.
            </p>
          </label>
          <label class="block">
            <span class="text-sm text-gray-600"
              >Vurgu rengi <span class="text-red-500">*</span></span
            >
            <div class="flex items-center gap-2">
              <input
                v-model="form.accentColor"
                type="color"
                class="h-10 w-10 rounded border p-0"
                aria-label="Vurgu rengi"
              />
              <input
                v-model="form.accentColor"
                class="flex-1 rounded border p-2"
                placeholder="#4f46e5"
                required
              />
            </div>
            <p class="mt-1 text-xs text-gray-500">
              Rozetler, linkler ve ikincil vurgu alanlarında tamamlayıcı olarak kullanılacak
              yardımcı renk.
            </p>
          </label>
        </div>
      </div>

      <details class="group">
        <summary class="cursor-pointer select-none text-sm text-indigo-700">
          İlk kullanıcılar (opsiyonel)
        </summary>
        <div class="mt-2 grid grid-cols-1 gap-3 sm:grid-cols-2">
          <input
            v-model="form.adminUsername"
            class="rounded border p-2"
            placeholder="Admin kullanıcı adı"
          />
          <input
            v-model="form.adminPassword"
            type="password"
            class="rounded border p-2"
            placeholder="Admin parola"
          />
          <input
            v-model="form.kitchenUsername"
            class="rounded border p-2"
            placeholder="Mutfak kullanıcı adı"
          />
          <input
            v-model="form.kitchenPassword"
            type="password"
            class="rounded border p-2"
            placeholder="Mutfak parola"
          />
          <input
            v-model="form.barUsername"
            class="rounded border p-2"
            placeholder="Bar kullanıcı adı"
          />
          <input
            v-model="form.barPassword"
            type="password"
            class="rounded border p-2"
            placeholder="Bar parola"
          />
        </div>
      </details>

      <details class="group">
        <summary class="cursor-pointer select-none text-sm text-gray-700">
          Gelişmiş ayarlar (JSON) — opsiyonel
        </summary>
        <textarea
          v-model="form.config"
          rows="4"
          class="mt-2 w-full rounded border p-2"
          placeholder='{"currency":"TRY","timezone":"Europe/Istanbul"}'
        ></textarea>
      </details>

      <div class="flex items-center justify-center gap-2">
        <router-link to="/" class="rounded border px-3 py-2">Vazgeç</router-link>
        <button
          class="bg-brand-gradient rounded px-4 py-2 text-white shadow hover:opacity-95"
          :disabled="saving || !isValid"
          @click="submit"
        >
          <span v-if="saving">Kaydediliyor…</span>
          <span v-else>Oluştur</span>
        </button>
      </div>
    </div>

    <div v-if="done" class="mt-6 rounded-lg border bg-green-50 p-4 text-green-800">
      <div class="mb-1 font-semibold">İşletme oluşturuldu.</div>
      <div class="text-sm">
        <div class="mb-1">
          Kod: <strong>{{ done.code }}</strong>
        </div>
        <div v-if="form.adminUsername">
          Admin kullanıcı: <strong>{{ form.adminUsername }}</strong>
        </div>
        <div class="mt-2">
          Yönetim için:
          <code class="rounded border bg-white px-1 py-0.5">{{ adminSubdomainUrl }}</code>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, computed } from "vue";
  import { fetchJson } from "@/utils/api";

  const form = ref({
    code: "",
    name: "",
    ownerName: "",
    ownerEmail: "",
    logoUrl: "",
    primaryColor: "",
    accentColor: "",
    config: "",
    adminUsername: "",
    adminPassword: "",
    kitchenUsername: "",
    kitchenPassword: "",
    barUsername: "",
    barPassword: "",
  });
  const saving = ref(false);
  const done = ref(null);
  const codeTouched = ref(false);
  const codePattern = "^(?=.{1,63}$)[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
  const codeValid = computed(() => {
    const v = (form.value.code || "").trim();
    if (!v) return false;
    try {
      return new RegExp(codePattern).test(v);
    } catch {
      return false;
    }
  });

  function onCodeInput(ev) {
    try {
      const raw = ev && ev.target && typeof ev.target.value === "string" ? ev.target.value : "";
      let s = raw.toLowerCase().replace(/[^a-z0-9-]/g, "");
      if (s.length > 63) s = s.slice(0, 63);
      form.value.code = s;
    } catch {
      /* ignore */
    }
  }

  const isValid = computed(() => {
    const f = form.value;
    const emailOk = /[^@\s]+@[^@\s]+\.[^@\s]+/.test((f.ownerEmail || "").trim());
    return Boolean(
      codeValid.value &&
        f.ownerName &&
        f.ownerName.trim() &&
        emailOk &&
        f.primaryColor &&
        f.primaryColor.trim() &&
        f.accentColor &&
        f.accentColor.trim(),
    );
  });

  const exampleSubdomain = computed(() => {
    try {
      const host = typeof window !== "undefined" ? window.location.hostname : "qrmatik.cloud";
      let baseDomain = "qrmatik.cloud";
      if (host === "localhost" || host.endsWith(".localhost")) {
        baseDomain = "localhost";
      } else {
        const parts = host.split(".");
        if (parts.length >= 2) baseDomain = parts.slice(-2).join(".");
      }
      const sample = baseDomain === "localhost" ? "restoranim" : "benim-restoranim";
      return `${sample}.${baseDomain}`;
    } catch {
      return "benim-restoranim.qrmatik.cloud";
    }
  });

  const adminSubdomainUrl = computed(() => {
    try {
      const code = done.value && done.value.code ? String(done.value.code).trim() : "";
      if (!code) return "";
      const loc = typeof window !== "undefined" ? window.location : null;
      const hostname = loc && loc.hostname ? loc.hostname : "qrmatik.cloud";
      const protocol = loc && loc.protocol ? loc.protocol : "https:";
      const port = loc && loc.port ? ":" + loc.port : "";
      let baseDomain = "qrmatik.cloud";
      if (hostname === "localhost" || hostname.endsWith(".localhost")) {
        baseDomain = "localhost";
      } else {
        const parts = hostname.split(".");
        if (parts.length >= 2) baseDomain = parts.slice(-2).join(".");
      }
      return `${protocol}//${code}.${baseDomain}${port}/admin`;
    } catch {
      return "";
    }
  });

  async function submit() {
    if (!isValid.value) return;
    saving.value = true;
    try {
      const res = await fetchJson("/api/public/tenant-signup", {
        method: "POST",
        body: JSON.stringify(form.value),
      });
      done.value = res;
    } catch (e) {
      // Optionally handle error with a toast if available
      console.debug(e);
    } finally {
      saving.value = false;
    }
  }
</script>

<style scoped></style>
