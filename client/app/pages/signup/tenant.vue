<template>
  <div class="relative min-h-screen pt-24 md:pt-32 pb-24 overflow-x-clip overflow-y-hidden bg-[#FAF9F6]">
    <div class="absolute top-0 left-0 w-[600px] h-[600px] bg-brand-100/50 rounded-full blur-3xl -translate-y-1/2 -translate-x-1/3 -z-10"></div>
    <div class="absolute bottom-0 right-0 w-[500px] h-[500px] bg-slate-100 rounded-full blur-3xl translate-y-1/3 translate-x-1/3 -z-10"></div>

    <div class="relative mx-auto max-w-4xl px-4 sm:px-6">
      <header class="text-center mb-8">
        <p class="inline-flex items-center gap-2 rounded-full border border-brand-200/70 bg-brand-50 px-4 py-1.5 text-[11px] font-black uppercase tracking-[0.18em] text-brand-700 mb-4">
          <span class="h-1.5 w-1.5 rounded-full bg-brand-500"></span>
          {{ $t('signup.panel.badge') }}
        </p>
        <h1 class="text-3xl md:text-5xl font-black text-slate-900 mb-4 tracking-tighter">
          <span class="text-brand-700">{{ $t('signup.title') }}</span>
        </h1>
        <p class="text-slate-500 text-sm md:text-base max-w-lg mx-auto font-medium">
          {{ $t('signup.subtitle') }}
        </p>
      </header>

      <div class="grid gap-3 sm:grid-cols-3 mb-6">
        <div v-for="proof in signupProofItems" :key="proof.titleKey" class="rounded-2xl border border-slate-200/80 bg-white/75 px-4 py-3 shadow-sm">
          <p class="text-[11px] font-black uppercase tracking-[0.14em] text-slate-400 mb-1">{{ $t(proof.titleKey) }}</p>
          <p class="text-sm font-bold text-slate-800">{{ $t(proof.valueKey) }}</p>
        </div>
      </div>

      <div class="bg-white rounded-[2.2rem] md:rounded-[2.5rem] p-5 sm:p-8 md:p-12 shadow-2xl shadow-slate-200/50 border border-slate-100">
        <div class="grid lg:grid-cols-[1.04fr_0.96fr] gap-8 lg:gap-10 items-start">
          <div>
            <div class="mb-8">
              <div class="rounded-xl border border-brand-200 bg-brand-50/70 px-4 py-3">
                <p class="text-[10px] font-black uppercase tracking-[0.16em] text-brand-700">1</p>
                <p class="text-xs font-bold mt-1 text-slate-900">{{ $t('signup.panel.stepAccount') }}</p>
              </div>
              <div class="mt-4 h-2 rounded-full bg-slate-100 overflow-hidden">
                <div class="h-full rounded-full bg-gradient-to-r from-brand-500 to-brand-600" :style="{ width: `${formCompletion}%` }"></div>
              </div>
              <p class="mt-2 text-xs font-bold text-slate-500">{{ $t('signup.panel.progressLabel', { percent: formCompletion }) }}</p>
            </div>

            <div class="space-y-8">
          <!-- Code -->
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
              {{ $t('signup.fields.code') }} <span class="text-brand-600">*</span>
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Link class="w-5 h-5" :class="showCodeError ? 'text-red-400' : 'text-slate-400'" />
              </div>
              <input
                v-model="form.code"
                :class="[
                  'w-full bg-slate-50 border rounded-2xl pl-11 pr-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium',
                  showCodeError ? 'border-red-200 focus:ring-red-500/10' : 'border-slate-100'
                ]"
                :placeholder="$t('signup.fields.codePlaceholder')"
                required
                :pattern="codePattern"
                maxlength="63"
                @input="onCodeInput"
                @blur="touchField('code')"
              />
            </div>
            <p class="mt-2.5 text-xs text-slate-400 font-medium">
              {{ $t('signup.fields.codeHint') }}
              <code class="mt-1 sm:mt-0 sm:ml-1 inline-block max-w-full break-all px-2 py-0.5 rounded-lg bg-slate-100 text-slate-600 font-mono border border-slate-200/50">{{ exampleSubdomain }}</code>
            </p>
            <p v-if="showCodeError" class="mt-2 text-xs text-red-500 font-bold">
              {{ $t('signup.fields.codeError') }}
            </p>
          </div>

          <!-- Name -->
          <div>
            <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
              {{ $t('signup.fields.name') }}
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <Building class="w-5 h-5 text-slate-400" />
              </div>
              <input v-model="form.name" class="w-full bg-slate-50 border border-slate-100 rounded-2xl pl-11 pr-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium" />
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <!-- Owner Name -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.ownerName') }} <span class="text-brand-600">*</span>
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <User class="w-5 h-5 text-slate-400" />
                </div>
                <input
                  v-model="form.ownerName"
                  class="w-full bg-slate-50 border rounded-2xl pl-11 pr-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium"
                  :class="showOwnerNameError ? 'border-red-200 focus:ring-red-500/10' : 'border-slate-100'"
                  :placeholder="$t('signup.fields.ownerNamePlaceholder')"
                  required
                  @blur="touchField('ownerName')"
                />
              </div>
              <p v-if="showOwnerNameError" class="mt-2 text-xs text-red-500 font-bold">{{ $t('signup.validation.ownerNameRequired') }}</p>
            </div>
            <!-- Owner Email -->
            <div>
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.ownerEmail') }} <span class="text-brand-600">*</span>
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <Mail class="w-5 h-5 text-slate-400" />
                </div>
                <input
                  v-model="form.ownerEmail"
                  type="email"
                  class="w-full bg-slate-50 border rounded-2xl pl-11 pr-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium"
                  :class="showOwnerEmailError ? 'border-red-200 focus:ring-red-500/10' : 'border-slate-100'"
                  :placeholder="$t('signup.fields.ownerEmailPlaceholder')"
                  required
                  @blur="touchField('ownerEmail')"
                />
              </div>
              <p v-if="showOwnerEmailError" class="mt-2 text-xs text-red-500 font-bold">{{ $t('signup.validation.ownerEmailInvalid') }}</p>
            </div>
            <!-- Owner Password -->
            <div class="md:col-span-2">
              <label class="block text-sm font-bold text-slate-700 mb-2.5 uppercase tracking-wide">
                {{ $t('signup.fields.ownerPassword') }} <span class="text-brand-600">*</span>
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <Lock class="w-5 h-5 text-slate-400" />
                </div>
                <input
                  v-model="form.ownerPassword"
                  type="password"
                  class="w-full bg-slate-50 border rounded-2xl pl-11 pr-5 py-4 text-slate-900 placeholder-slate-300 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all duration-300 outline-none font-medium"
                  :class="showOwnerPasswordError ? 'border-red-200 focus:ring-red-500/10' : 'border-slate-100'"
                  :placeholder="$t('signup.fields.ownerPasswordPlaceholder')"
                  required
                  @blur="touchField('ownerPassword')"
                />
              </div>
              <div class="mt-3">
                <div class="h-1.5 rounded-full bg-slate-100 overflow-hidden">
                  <div class="h-full rounded-full transition-all duration-300" :class="passwordStrength.barClass" :style="{ width: `${passwordStrength.percent}%` }"></div>
                </div>
                <p class="mt-1.5 text-xs font-bold" :class="passwordStrength.textClass">{{ $t(passwordStrength.labelKey) }}</p>
              </div>
              <p v-if="showOwnerPasswordError" class="mt-2 text-xs text-red-500 font-bold">{{ $t('signup.validation.ownerPasswordMin') }}</p>
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

              <div class="rounded-[1.6rem] border border-slate-200 bg-slate-50/80 p-4 sm:p-5">
                <p class="text-[11px] font-black uppercase tracking-[0.16em] text-slate-500 mb-2">{{ $t('signup.panel.brandingLaterTitle') }}</p>
                <p class="text-sm text-slate-600 font-medium leading-relaxed">
                  {{ $t('signup.panel.brandingLaterDesc') }}
                </p>
              </div>
            </div>

            <div class="mt-8 rounded-2xl border border-slate-200 bg-slate-50/70 px-4 py-3" v-if="showMissingSummary && missingFieldLabels.length">
              <p class="text-xs font-black uppercase tracking-[0.14em] text-rose-600">{{ $t('signup.panel.missingTitle') }}</p>
              <p class="text-xs font-semibold text-slate-600 mt-1 break-words">{{ missingFieldLabels.join(' • ') }}</p>
            </div>

            <div class="mt-10 flex flex-col sm:flex-row items-center gap-3 sm:gap-4 justify-center">
              <NuxtLink :to="localePath('/')" class="w-full sm:w-auto px-8 py-3.5 rounded-xl font-bold text-xs uppercase tracking-widest text-slate-400 hover:text-slate-900 hover:bg-slate-50 transition-all text-center">
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
            <p class="mt-4 text-center text-xs text-slate-500 font-medium">{{ $t('signup.panel.ctaHint') }}</p>
          </div>

          <aside class="hidden lg:block space-y-4 lg:sticky lg:top-28">
            <div class="rounded-[1.7rem] border border-slate-200 bg-white p-5 shadow-sm">
              <p class="text-[11px] font-black uppercase tracking-[0.16em] text-slate-400 mb-3">{{ $t('signup.panel.previewTitle') }}</p>
              <div class="rounded-2xl bg-gradient-to-br from-slate-900 to-slate-700 text-white p-4">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <p class="text-sm font-black">{{ form.name?.trim() || $t('signup.panel.previewFallback') }}</p>
                    <p class="text-[11px] text-white/75 mt-1">{{ form.code?.trim() || 'my-bistro' }}.feasymenu.com</p>
                  </div>
                  <span class="rounded-full bg-white/20 px-2.5 py-1 text-[10px] font-bold">{{ $t('signup.panel.liveBadge') }}</span>
                </div>
                <div class="mt-4 grid grid-cols-2 gap-2 text-[11px]">
                  <div class="rounded-lg bg-white/15 px-2.5 py-2 font-semibold">{{ $t('signup.panel.previewTile1') }}</div>
                  <div class="rounded-lg bg-white/15 px-2.5 py-2 font-semibold">{{ $t('signup.panel.previewTile2') }}</div>
                  <div class="rounded-lg bg-white/15 px-2.5 py-2 font-semibold">{{ $t('signup.panel.previewTile3') }}</div>
                  <div class="rounded-lg bg-white/15 px-2.5 py-2 font-semibold">{{ $t('signup.panel.previewTile4') }}</div>
                </div>
              </div>
            </div>

            <div class="rounded-[1.7rem] border border-slate-200 bg-white p-5 shadow-sm">
              <p class="text-[11px] font-black uppercase tracking-[0.16em] text-slate-400 mb-3">{{ $t('signup.panel.checklistTitle') }}</p>
              <ul class="space-y-2.5">
                <li
                  v-for="item in checklistItems"
                  :key="item.labelKey"
                  class="flex items-center justify-between rounded-xl border px-3 py-2"
                  :class="item.done ? 'border-emerald-200 bg-emerald-50/70' : 'border-slate-200 bg-slate-50/70'"
                >
                  <span class="text-xs font-bold" :class="item.done ? 'text-emerald-700' : 'text-slate-600'">{{ $t(item.labelKey) }}</span>
                  <span class="text-[11px] font-black uppercase" :class="item.done ? 'text-emerald-700' : 'text-slate-400'">{{ $t(item.done ? 'signup.panel.ready' : 'signup.panel.pending') }}</span>
                </li>
              </ul>
            </div>

            <div class="rounded-[1.7rem] border border-brand-200 bg-brand-50/70 p-5 shadow-sm">
              <p class="text-[11px] font-black uppercase tracking-[0.16em] text-brand-700 mb-2">{{ $t('signup.panel.quoteTitle') }}</p>
              <p class="text-sm font-bold text-slate-700 leading-relaxed">“{{ $t('signup.panel.quote') }}”</p>
            </div>
          </aside>
        </div>
      </div>

      <!-- Success Modal Overlay -->
      <Transition name="fade">
        <div v-if="done" class="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-slate-900/60 backdrop-blur-sm">
          <div class="relative w-full max-w-xl bg-white rounded-[2.1rem] md:rounded-[2.5rem] p-5 sm:p-8 md:p-12 shadow-2xl border border-slate-100 animate-in fade-in zoom-in duration-300">
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

              <div class="grid sm:grid-cols-2 gap-3 pt-2">
                <a :href="adminSubdomainUrl" class="rounded-xl bg-slate-950 text-white text-center px-4 py-3 text-xs font-black uppercase tracking-[0.14em] hover:bg-brand-700 transition-colors">
                  {{ $t('signup.success.openAdmin') }}
                </a>
                <a :href="adminQrUrl" class="rounded-xl border border-slate-200 text-slate-700 text-center px-4 py-3 text-xs font-black uppercase tracking-[0.14em] hover:bg-slate-50 transition-colors">
                  {{ $t('signup.success.openQr') }}
                </a>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </div>
  </div>

</template>

<script setup lang="ts">
import { Link, Building, User, Mail, Lock } from 'lucide-vue-next'

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const localePath = useLocalePath()

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
})

const saving = ref(false)
const done = ref<any>(null)
const submitAttempted = ref(false)
const touched = ref<Record<string, boolean>>({
  code: false,
  ownerName: false,
  ownerEmail: false,
  ownerPassword: false,
})
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

const signupProofItems = [
  { titleKey: 'signup.panel.proof1Title', valueKey: 'signup.panel.proof1Value' },
  { titleKey: 'signup.panel.proof2Title', valueKey: 'signup.panel.proof2Value' },
  { titleKey: 'signup.panel.proof3Title', valueKey: 'signup.panel.proof3Value' },
]

const passwordStrength = computed(() => {
  const pwd = form.value.ownerPassword || ''
  if (!pwd.length) {
    return {
      percent: 0,
      labelKey: 'signup.panel.passwordEmpty',
      barClass: 'bg-slate-300',
      textClass: 'text-slate-400',
    }
  }

  let score = 0
  if (pwd.length >= 8) score += 1
  if (/[A-Z]/.test(pwd) && /[a-z]/.test(pwd)) score += 1
  if (/\d/.test(pwd)) score += 1
  if (/[^A-Za-z0-9]/.test(pwd)) score += 1

  if (score <= 1) {
    return {
      percent: 28,
      labelKey: 'signup.panel.passwordWeak',
      barClass: 'bg-rose-400',
      textClass: 'text-rose-500',
    }
  }

  if (score <= 3) {
    return {
      percent: 64,
      labelKey: 'signup.panel.passwordMedium',
      barClass: 'bg-amber-400',
      textClass: 'text-amber-600',
    }
  }

  return {
    percent: 100,
    labelKey: 'signup.panel.passwordStrong',
    barClass: 'bg-emerald-500',
    textClass: 'text-emerald-600',
  }
})

const emailOk = computed(() => /[^@\s]+@[^@\s]+\.[^@\s]+/.test((form.value.ownerEmail || '').trim()))

const showCodeError = computed(() => (touched.value.code || submitAttempted.value) && !codeValid.value)
const showOwnerNameError = computed(() => (touched.value.ownerName || submitAttempted.value) && !form.value.ownerName?.trim())
const showOwnerEmailError = computed(() => (touched.value.ownerEmail || submitAttempted.value) && !emailOk.value)
const showOwnerPasswordError = computed(() => (touched.value.ownerPassword || submitAttempted.value) && (form.value.ownerPassword?.trim().length || 0) < 8)

const formCompletion = computed(() => {
  let score = 0
  if (codeValid.value) score += 1
  if (form.value.ownerName?.trim()) score += 1
  if (emailOk.value) score += 1
  if ((form.value.ownerPassword?.trim().length || 0) >= 8) score += 1
  return Math.round((score / 4) * 100)
})

const checklistItems = computed(() => [
  { labelKey: 'signup.fields.code', done: codeValid.value },
  { labelKey: 'signup.fields.ownerName', done: Boolean(form.value.ownerName?.trim()) },
  { labelKey: 'signup.fields.ownerEmail', done: emailOk.value },
  { labelKey: 'signup.fields.ownerPassword', done: (form.value.ownerPassword?.trim().length || 0) >= 8 },
])

const missingFieldLabels = computed(() => {
  return checklistItems.value
    .filter((item) => !item.done)
    .map((item) => t(item.labelKey))
})

const showMissingSummary = computed(() => {
  return submitAttempted.value || Object.values(touched.value).some(Boolean)
})

function touchField(field: 'code' | 'ownerName' | 'ownerEmail' | 'ownerPassword') {
  touched.value[field] = true
}

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
  return Boolean(
    codeValid.value &&
      f.ownerName?.trim() &&
      emailOk.value &&
      f.ownerPassword?.trim().length >= 8
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
    const sample = "my-bistro"
    return `${sample}.${baseDomain}`
  } catch {
    return "my-bistro.feasymenu.com"
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
    const path = localePath('/admin')
    return `${protocol}//${code}.${baseDomain}${port}${path}`
  } catch {
    return ""
  }
})

const adminQrUrl = computed(() => {
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
    const path = localePath('/admin/qr')
    return `${protocol}//${code}.${baseDomain}${port}${path}`
  } catch {
    return ""
  }
})

async function submit() {
  submitAttempted.value = true
  touchField('code')
  touchField('ownerName')
  touchField('ownerEmail')
  touchField('ownerPassword')

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
