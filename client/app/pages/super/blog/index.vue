<template>
  <div class="space-y-8">
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight uppercase">{{ $t('admin.super.blog.title') }}</h1>
        <p class="text-slate-500 mt-1 font-medium">{{ $t('admin.super.blog.subtitle') }}</p>
      </div>
      <NuxtLink 
        :to="localePath('/super/blog/edit')" 
        class="inline-flex items-center justify-center gap-2 px-6 py-3 bg-brand-600 text-white font-bold rounded-2xl hover:bg-brand-700 transition-all shadow-lg shadow-brand-600/20 uppercase tracking-widest text-xs"
      >
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" /></svg>
        {{ $t('admin.super.blog.newPost') }}
      </NuxtLink>
    </div>

    <div v-if="pending" class="bg-white rounded-3xl p-12 border border-slate-100 flex justify-center">
      <div class="animate-spin h-8 w-8 border-t-2 border-b-2 border-brand-600 rounded-full"></div>
    </div>

    <div v-else-if="posts && posts.length > 0" class="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50/50 border-b border-slate-100">
            <th class="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">{{ $t('admin.super.blog.table.title') }}</th>
            <th class="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">{{ $t('admin.super.blog.table.slug') }}</th>
            <th class="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">{{ $t('admin.super.blog.table.date') }}</th>
            <th class="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] text-right">{{ $t('admin.super.blog.table.actions') }}</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-50">
          <tr v-for="post in posts" :key="post.id" class="hover:bg-slate-50/50 transition-colors group">
            <td class="px-6 py-4">
              <div class="font-bold text-slate-900">{{ post.titleTr }}</div>
              <div class="text-xs text-slate-400 font-medium mt-0.5">{{ post.titleEn }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="flex flex-col gap-1">
                <code class="text-[10px] bg-slate-100 px-2 py-0.5 rounded-md text-slate-600 w-fit">tr: {{ post.slugTr }}</code>
                <code class="text-[10px] bg-slate-100 px-2 py-0.5 rounded-md text-slate-600 w-fit">en: {{ post.slugEn }}</code>
              </div>
            </td>
            <td class="px-6 py-4 text-sm text-slate-500 font-medium">
              {{ formatDate(post.createdTime) }}
            </td>
            <td class="px-6 py-4 text-right">
              <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <NuxtLink 
                  :to="localePath(`/super/blog/edit?id=${post.id}`)" 
                  class="p-2 text-slate-400 hover:text-brand-600 hover:bg-brand-50 rounded-lg transition-all"
                  title="Edit"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                </NuxtLink>
                <button 
                  @click="deletePost(post.id)" 
                  class="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-all"
                  title="Delete"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="bg-white rounded-3xl p-20 border border-slate-100 text-center">
      <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mx-auto mb-6 text-slate-300">
        <svg class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10l4 4v10a2 2 0 01-2 2z" /><path stroke-linecap="round" stroke-linejoin="round" d="M14 2v4a2 2 0 002 2h4" /><path stroke-linecap="round" stroke-linejoin="round" d="M7 10h10M7 14h10M7 18h5" /></svg>
      </div>
      <h2 class="text-xl font-bold text-slate-900 mb-2 tracking-tight">{{ $t('admin.super.blog.empty.title') }}</h2>
      <p class="text-slate-500 mb-8 max-w-xs mx-auto">{{ $t('admin.super.blog.empty.description') }}</p>
      <NuxtLink 
        :to="localePath('/super/blog/edit')" 
        class="inline-flex items-center justify-center gap-2 px-8 py-3 bg-brand-600 text-white font-bold rounded-2xl hover:bg-brand-700 transition-all shadow-lg shadow-brand-600/20 uppercase tracking-widest text-xs"
      >
        {{ $t('admin.super.blog.empty.button') }}
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'admin'
})

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const localePath = useLocalePath()

const { data: posts, pending, refresh } = useAsyncData('admin-blog-posts', () => 
  fetchJson('/api/public/blog')
)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('tr-TR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

async function deletePost(id: string) {
  uiStore.confirm({
    title: t('admin.common.delete') || 'Sil',
    message: t('admin.super.blog.deleteConfirm') || 'Bu yazıyı silmek istediğinize emin misiniz?',
    isDanger: true,
    onConfirm: async () => {
      try {
        await fetchJson(`/api/admin/blog/${id}`, { method: 'DELETE' })
        uiStore.success(t('admin.common.deleted') || 'Silindi.')
        refresh()
      } catch (e: any) {
        const errorMessage = e?.message || e?.toString() || 'Error deleting post';
        uiStore.error(errorMessage);
      }
    }
  })
}
</script>
