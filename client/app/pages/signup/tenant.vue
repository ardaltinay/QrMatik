<template>
  <div class="relative min-h-screen pt-32 pb-24 overflow-hidden">
    <div class="absolute inset-0 bg-transparent"></div>

    <div class="relative mx-auto max-w-2xl px-6">
      <header class="text-center mb-10">
        <h1 class="text-3xl md:text-5xl font-black text-slate-900 mb-4 uppercase tracking-tighter">
          <span class="text-brand-600">{{ $t('signup.title') }}</span>
        </h1>
        <p class="text-slate-500 text-sm md:text-base max-w-lg mx-auto font-medium">
          {{ $t('signup.subtitle') }}
        </p>
      </header>

      <div class="bg-white rounded-[2.5rem] p-8 md:p-12 shadow-2xl shadow-slate-200/50 border border-slate-100">
        <div class="space-y-8">
          <!-- Code -->
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
              {{ $t('signup.fields.code') }} <span class="text-brand-600">*</span>
            </label>
            <input
              v-model="form.code"
              :class="[
                'w-full bg-slate-50 border rounded-2xl px-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium',
                codeTouched && !codeValid ? 'border-red-200 focus:ring-red-500/10' : 'border-slate-100'
              ]"
              :placeholder="$t('signup.fields.codePlaceholder')"
              required
              :pattern="codePattern"
              maxlength="63"
              @input="onCodeInput"
              @blur="codeTouched = true"
            />
            <p class="mt-2.5 text-xs text-slate-400 font-medium">
              {{ $t('signup.fields.codeHint') }}
              <code class="px-2 py-0.5 rounded-lg bg-slate-100 text-slate-600 font-mono ml-1 border border-slate-200/50">{{ exampleSubdomain }}</code>
            </p>
            <p v-if="codeTouched && form.code && !codeValid" class="mt-2 text-xs text-red-500 font-bold">
              {{ $t('signup.fields.codeError') }}
            </p>
          </div>

          <!-- Name -->
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
              {{ $t('signup.fields.name') }}
            </label>
            <input v-model="form.name" class="w-full bg-slate-50 border border-slate-100 rounded-2xl px-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium" />
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <!-- Owner Name -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.ownerName') }} <span class="text-brand-600">*</span>
              </label>
              <input
                v-model="form.ownerName"
                class="w-full bg-slate-50 border border-slate-100 rounded-2xl px-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium"
                :placeholder="$t('signup.fields.ownerNamePlaceholder')"
                required
              />
            </div>
            <!-- Owner Email -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.ownerEmail') }} <span class="text-brand-600">*</span>
              </label>
              <input
                v-model="form.ownerEmail"
                type="email"
                class="w-full bg-slate-50 border border-slate-100 rounded-2xl px-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium"
                :placeholder="$t('signup.fields.ownerEmailPlaceholder')"
                required
              />
            </div>
            <!-- Owner Password -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.ownerPassword') }} <span class="text-brand-600">*</span>
              </label>
              <input
                v-model="form.ownerPassword"
                type="password"
                class="w-full bg-slate-50 border border-slate-100 rounded-2xl px-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium"
                :placeholder="$t('signup.fields.ownerPasswordPlaceholder')"
                required
              />
            </div>
          </div>

          <!-- Logo URL -->
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
              {{ $t('signup.fields.logoUrl') }}
            </label>
            <input
              v-model="form.logoUrl"
              class="w-full bg-slate-100 border border-slate-200 rounded-2xl px-5 py-4 text-slate-400 cursor-not-allowed font-medium"
              :placeholder="$t('signup.fields.logoPlaceholder')"
              disabled
              aria-disabled="true"
            />
            <p class="mt-2.5 text-xs text-slate-400 leading-relaxed font-medium">
              {{ $t('signup.fields.logoHint') }}
            </p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <!-- Primary Color -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.primaryColor') }} <span class="text-brand-600">*</span>
              </label>
              <div class="flex items-center gap-3">
                <input
                  v-model="form.primaryColor"
                  type="color"
                  class="h-14 w-14 rounded-2xl border border-slate-200 bg-white p-1.5 cursor-pointer shadow-sm"
                />
                <input
                  v-model="form.primaryColor"
                  class="flex-1 bg-slate-50 border border-slate-100 rounded-2xl px-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-mono"
                  placeholder="#6366f1"
                  required
                />
              </div>
              <p class="mt-2.5 text-xs text-slate-400 leading-relaxed font-medium">{{ $t('signup.fields.primaryHint') }}</p>
            </div>
            <!-- Accent Color -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.accentColor') }} <span class="text-brand-600">*</span>
              </label>
              <div class="flex items-center gap-3">
                <input
                  v-model="form.accentColor"
                  type="color"
                  class="h-14 w-14 rounded-2xl border border-slate-200 bg-white p-1.5 cursor-pointer shadow-sm"
                />
                <input
                  v-model="form.accentColor"
                  class="flex-1 bg-slate-50 border border-slate-100 rounded-2xl px-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-mono"
                  placeholder="#4f46e5"
                  required
                />
              </div>
              <p class="mt-2.5 text-xs text-slate-400 leading-relaxed font-medium">{{ $t('signup.fields.accentHint') }}</p>
            </div>
          </div>
        </div>



          <details class="group mt-4">
            <summary class="cursor-pointer select-none text-sm font-bold text-slate-400 hover:text-slate-600 transition-colors uppercase tracking-widest">
              {{ $t('signup.advanced.jsonTitle') }}
            </summary>
            <textarea
              v-model="form.config"
              rows="4"
              class="mt-6 w-full bg-slate-50 border border-slate-100 rounded-2xl px-5 py-4 text-sm font-mono text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 outline-none"
              placeholder='{"currency":"TRY","timezone":"Europe/Istanbul"}'
            ></textarea>
          </details>
        </div>

        <div class="mt-10 flex flex-col sm:flex-row items-center gap-4 justify-end">
          <NuxtLink to="/" class="w-full sm:w-auto px-8 py-3.5 rounded-xl font-bold text-xs uppercase tracking-widest text-slate-400 hover:text-slate-900 hover:bg-slate-50 transition-all text-center">
            {{ $t('signup.actions.cancel') }}
          </NuxtLink>
          <button
            class="w-full sm:w-auto px-10 py-4 rounded-2xl bg-brand-600 text-white font-bold text-xs uppercase tracking-widest shadow-xl shadow-brand-600/20 hover:bg-brand-500 disabled:opacity-50 cursor-pointer transition-all"
            :disabled="saving || !isValid"
            @click="submit"
          >
            <span v-if="saving">{{ $t('signup.actions.saving') }}</span>
            <span v-else>{{ $t('signup.actions.create') }}</span>
          </button>
        </div>
      </div>

      <!-- Success Modal Overlay -->
      <Transition name="fade">
        <div v-if="done" class="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-slate-900/60 backdrop-blur-sm">
          <div class="relative w-full max-w-xl bg-white rounded-[2.5rem] p-8 md:p-12 shadow-2xl border border-slate-100 animate-in fade-in zoom-in duration-300">
            <!-- Close button -->
            <button @click="done = null" class="absolute top-6 right-6 text-slate-400 hover:text-slate-600 transition-colors p-2">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>

            <div class="flex items-center gap-4 mb-8">
              <div class="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-500 text-white shadow-lg shadow-emerald-500/20">
                <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" /></svg>
              </div>
              <div class="text-2xl font-black text-slate-900 uppercase tracking-tight">{{ $t('signup.success.title') }}</div>
            </div>
            
            <div class="space-y-6">
              <div class="p-4 bg-slate-50 rounded-2xl border border-slate-100 flex items-center justify-between">
                <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">{{ $t('signup.success.code') }}</span>
                <strong class="text-lg font-black text-brand-600 tracking-tight">{{ done.code || (done.tenant && done.tenant.code) }}</strong>
              </div>

              <div class="space-y-4">
                <p class="text-sm font-bold text-slate-700 leading-relaxed bg-brand-50/50 p-5 rounded-2xl border border-brand-100/50">
                  {{ $t('signup.success.adminLoginInfo') }}
                </p>
                <p class="text-xs font-medium text-slate-500 leading-relaxed bg-slate-50 p-5 rounded-2xl border border-slate-100">
                  {{ $t('signup.success.userCreationInfo') }}
                </p>
              </div>

              <div class="pt-6 border-t border-slate-100 flex flex-col gap-2">
                <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">{{ $t('signup.success.manageLink') }}</span>
                <a :href="adminSubdomainUrl" class="text-brand-600 hover:text-brand-700 font-bold underline transition-all break-all text-sm">
                  {{ adminSubdomainUrl }}
                </a>
              </div>
            </div>
          </div>
        </div>
      </Transition>
  </div>

</template>

<script setup lang="ts">
const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()

useSeoMeta({
  title: () => t('signup.metaTitle'),
})

const form = ref({
  code: "",
  name: "",
  ownerName: "",
  ownerEmail: "",
  ownerPassword: "",
  logoUrl: "",
  primaryColor: "#0ea5e9", // Default brand color
  accentColor: "#6366f1",  // Default accent color
  config: "",
})

const saving = ref(false)
const done = ref<any>(null)
const codeTouched = ref(false)
const codePattern = "^(?=.{1,63}$)[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"

const codeValid = computed(() => {
  const v = (form.value.code || "").trim()
  if (!v) return false
  try {
    return new RegExp(codePattern).test(v)
  } catch {
    return false
  }
})

function onCodeInput(ev: Event) {
  try {
    const raw = ev && ev.target && typeof (ev.target as HTMLInputElement).value === "string" ? (ev.target as HTMLInputElement).value : ""
    let s = raw.toLowerCase().replace(/[^a-z0-9-]/g, "")
    if (s.length > 63) s = s.slice(0, 63)
    form.value.code = s
  } catch { /* ignore */ }
}

const isValid = computed(() => {
  const f = form.value
  const emailOk = /[^@\s]+@[^@\s]+\.[^@\s]+/.test((f.ownerEmail || "").trim())
  return Boolean(
    codeValid.value &&
      f.ownerName?.trim() &&
      emailOk &&
      f.ownerPassword?.trim().length >= 8 &&
      f.primaryColor?.trim() &&
      f.accentColor?.trim()
  )
})

const exampleSubdomain = computed(() => {
  try {
    const host = import.meta.client ? window.location.hostname : "feasymenu.com"
    let baseDomain = "feasymenu.com"
    if (host === "localhost" || host.endsWith(".localhost")) {
      baseDomain = "localhost"
    } else {
      const parts = host.split(".")
      if (parts.length >= 2) baseDomain = parts.slice(-2).join(".")
    }
    const sample = baseDomain === "localhost" ? "restoranim" : "benim-restoranim"
    return `${sample}.${baseDomain}`
  } catch {
    return "benim-restoranim.feasymenu.com"
  }
})

const adminSubdomainUrl = computed(() => {
  try {
    const code = done.value
      ? String(done.value.code || (done.value.tenant && done.value.tenant.code) || "").trim()
      : ""
    if (!code) return ""
    const loc = import.meta.client ? window.location : null
    const hostname = loc?.hostname || "feasymenu.com"
    const protocol = loc?.protocol || "https:"
    const port = loc?.port ? ":" + loc.port : ""
    
    let baseDomain = "feasymenu.com"
    if (hostname === "localhost" || hostname.endsWith(".localhost")) {
      baseDomain = "localhost"
    } else {
      const parts = hostname.split(".")
      if (parts.length >= 2) baseDomain = parts.slice(-2).join(".")
    }
    return `${protocol}//${code}.${baseDomain}${port}/admin`
  } catch {
    return ""
  }
})

async function submit() {
  if (!isValid.value) return
  saving.value = true
  try {
    const res = await fetchJson("/api/public/tenant-signup", {
      method: "POST",
      body: JSON.stringify(form.value),
    })
    done.value = res
  } catch (e: any) {
    // We could use the toast system here if we want
    console.error(e)
    const errorMessage = e?.message || e?.toString() || t('errors.serverError');
    uiStore.error(errorMessage);
  } finally {
    saving.value = false
  }
}
</script>
