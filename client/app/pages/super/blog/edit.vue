<template>
  <div class="max-w-5xl mx-auto space-y-8">
    <div class="flex items-center gap-4">
      <NuxtLink :to="localePath('/super/blog')" class="p-3 bg-white border border-slate-200 rounded-2xl text-slate-400 hover:text-slate-900 transition-colors">
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" /></svg>
      </NuxtLink>
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight uppercase">
          {{ isEdit ? $t('admin.super.blog.edit.title') : $t('admin.super.blog.edit.newTitle') }}
        </h1>
        <p class="text-slate-500 mt-1 font-medium">{{ $t('admin.super.blog.edit.subtitle') }}</p>
      </div>
    </div>

    <div class="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm p-8 md:p-12 space-y-10">
      <!-- General Info -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.slug') }}</label>
          <input 
            v-model="form.slug" 
            type="text" 
            placeholder="e.g. digital-menu-advantages"
            class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-medium"
          />
        </div>
        <div>
          <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.coverImage') }}</label>
          <input 
            v-model="form.imageUrl" 
            type="text" 
            :placeholder="$t('admin.super.blog.edit.coverImagePlaceholder')"
            class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-medium"
          />
        </div>
      </div>

      <div class="border-t border-slate-50 pt-10 grid grid-cols-1 md:grid-cols-2 gap-12">
        <!-- Turkish Content -->
        <div class="space-y-6">
          <div class="flex items-center gap-3 mb-2">
            <span class="text-xl">🇹🇷</span>
            <h2 class="text-lg font-black text-slate-900 uppercase tracking-tight">{{ $t('admin.super.blog.edit.turkishContent') }}</h2>
          </div>
          
          <div>
            <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.fields.title') }} (TR)</label>
            <input v-model="form.titleTr" type="text" class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-bold text-lg" />
          </div>

          <div>
            <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.fields.excerpt') }} (TR)</label>
            <textarea v-model="form.excerptTr" rows="3" class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-medium text-sm"></textarea>
          </div>

          <div>
            <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.fields.content') }} (TR)</label>
            <textarea v-model="form.contentTr" rows="12" class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-mono text-xs"></textarea>
          </div>
        </div>

        <!-- English Content -->
        <div class="space-y-6 border-l border-slate-50 pl-0 md:pl-12">
          <div class="flex items-center gap-3 mb-2">
            <span class="text-xl">🇬🇧</span>
            <h2 class="text-lg font-black text-slate-900 uppercase tracking-tight">{{ $t('admin.super.blog.edit.englishContent') }}</h2>
          </div>

          <div>
            <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.fields.title') }} (EN)</label>
            <input v-model="form.titleEn" type="text" class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-bold text-lg" />
          </div>

          <div>
            <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.fields.excerpt') }} (EN)</label>
            <textarea v-model="form.excerptEn" rows="3" class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-medium text-sm"></textarea>
          </div>

          <div>
            <label class="block text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2.5">{{ $t('admin.super.blog.edit.fields.content') }} (EN)</label>
            <textarea v-model="form.contentEn" rows="12" class="w-full px-5 py-4 rounded-2xl bg-slate-50 border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none font-mono text-xs"></textarea>
          </div>
        </div>
      </div>

      <div class="pt-10 border-t border-slate-50 flex justify-end gap-4">
        <NuxtLink :to="localePath('/super/blog')" class="px-8 py-4 rounded-2xl font-bold text-xs uppercase tracking-widest text-slate-400 hover:text-slate-900 hover:bg-slate-50 transition-all">
          {{ $t('common.cancel') }}
        </NuxtLink>
        <button 
          @click="save" 
          :disabled="saving"
          class="px-12 py-4 rounded-2xl bg-brand-600 text-white font-bold text-xs uppercase tracking-widest shadow-xl shadow-brand-600/20 hover:bg-brand-700 disabled:opacity-50 transition-all flex items-center gap-2"
        >
          <svg v-if="saving" class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
          {{ isEdit ? $t('admin.super.blog.edit.actions.update') : $t('admin.super.blog.edit.actions.publish') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'admin'
})

const route = useRoute()
const router = useRouter()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const localePath = useLocalePath()

const isEdit = computed(() => !!route.query.id)
const saving = ref(false)

const form = ref({
  id: null as string | null,
  slug: '',
  titleTr: '',
  titleEn: '',
  excerptTr: '',
  excerptEn: '',
  contentTr: '',
  contentEn: '',
  imageUrl: ''
})

onMounted(async () => {
  if (isEdit.value) {
    try {
      const data = await fetchJson(`/api/public/blog/id/${route.query.id}`)
      Object.assign(form.value, data)
    } catch (e) {
      console.error(e)
    }
  }
})

async function save() {
  saving.value = true
  try {
    await fetchJson('/api/admin/blog', {
      method: 'POST',
      body: JSON.stringify(form.value)
    })
    router.push('/super/blog')
  } catch (e) {
    const errorMessage = e?.message || e?.toString() || 'Error saving post';
    uiStore.error(errorMessage);
  } finally {
    saving.value = false
  }
}
</script>
