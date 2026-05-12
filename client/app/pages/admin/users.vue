<template>
  <div class="p-4 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 mb-10">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight flex items-center gap-3">
          {{ $t('admin.users.title') }}
          <span class="bg-indigo-100 text-indigo-600 text-[10px] font-black px-2 py-1 rounded-lg uppercase tracking-wider">Ekip</span>
        </h1>
        <p class="text-slate-500 font-medium mt-1">{{ $t('admin.users.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full lg:w-auto">
        <div class="relative flex-1 sm:min-w-[300px]">
          <svg class="w-5 h-5 absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.users.searchPlaceholder')"
            class="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-white border border-slate-200 focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none shadow-sm font-medium"
          />
        </div>
        <button @click="openModal()" class="w-full sm:w-auto px-6 py-3.5 bg-brand-600 text-white font-black rounded-2xl hover:bg-brand-700 hover:-translate-y-0.5 active:translate-y-0 transition-all shadow-lg shadow-brand-500/25 flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          <span class="whitespace-nowrap">{{ $t('admin.users.addUser') }}</span>
        </button>
      </div>
    </div>

    <!-- Stats Section -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
      <div class="bg-white rounded-[2rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center gap-6">
        <div class="w-16 h-16 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center border border-indigo-100 shadow-inner">
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
        </div>
        <div>
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.users.stats.total') || 'Toplam Ekip' }}</p>
          <h3 class="text-4xl font-black text-slate-900 tracking-tighter">{{ users.length }}</h3>
        </div>
      </div>
      
      <div class="bg-white rounded-[2rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center gap-6">
        <div class="w-16 h-16 rounded-2xl bg-rose-50 text-rose-500 flex items-center justify-center border border-rose-100 shadow-inner">
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" /></svg>
        </div>
        <div>
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.users.stats.admins') || 'Yöneticiler' }}</p>
          <h3 class="text-4xl font-black text-slate-900 tracking-tighter">1</h3>
        </div>
      </div>

      <div class="bg-slate-900 rounded-[2rem] p-8 shadow-2xl flex items-center gap-6 group overflow-hidden relative">
        <div class="absolute inset-0 bg-gradient-to-r from-brand-600/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
        <div class="w-16 h-16 rounded-2xl bg-white/10 text-white flex items-center justify-center backdrop-blur-md relative z-10">
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" /></svg>
        </div>
        <div class="relative z-10">
          <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.users.stats.accessControl') }}</p>
          <h3 class="text-xl font-bold text-white tracking-tight leading-tight">{{ $t('admin.users.stats.roleBasedActive') }}</h3>
        </div>
      </div>
    </div>

    <!-- Users Container -->
    <div class="bg-white rounded-[2.5rem] border border-slate-100 shadow-2xl shadow-slate-200/60 overflow-hidden">
      <!-- Desktop Table -->
      <div class="hidden md:block overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/50 border-b border-slate-100 text-slate-400 text-[10px] font-black uppercase tracking-[0.2em]">
              <th class="px-8 py-6">{{ $t('admin.users.columns.username') }}</th>
              <th class="px-8 py-6">{{ $t('admin.users.columns.role') }}</th>
              <th class="px-8 py-6 text-right">{{ $t('admin.users.columns.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-slate-50/70 transition-all group">
              <td class="px-8 py-5">
                <div class="flex items-center gap-4">
                  <div class="w-12 h-12 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-black text-lg border border-indigo-100 shadow-sm group-hover:scale-110 transition-transform">
                    {{ user.username.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div class="font-bold text-slate-900 text-lg">{{ user.username }}</div>
                    <div class="text-xs font-medium text-slate-400">#{{ user.id.toString().slice(-4) }}</div>
                  </div>
                </div>
              </td>
              <td class="px-8 py-5">
                <span class="inline-flex items-center px-4 py-2 rounded-xl text-[10px] font-black uppercase tracking-widest border shadow-sm transition-all" :class="getRoleBadgeClass(user.role)">
                   <div class="w-1.5 h-1.5 rounded-full mr-2 opacity-50" :class="getRoleDotClass(user.role)"></div>
                   {{ $t(`admin.roles.${user.role.toLowerCase().replace(/ı/g, 'i')}`) || user.role }}
                </span>
              </td>
              <td class="px-8 py-5 text-right">
                <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 translate-x-2 group-hover:translate-x-0 transition-all">
                  <button @click="openModal(user)" class="p-3 bg-white text-slate-400 hover:text-brand-600 hover:bg-brand-50 border border-slate-100 hover:border-brand-200 rounded-2xl shadow-sm transition-all" title="Edit">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                  </button>
                  <button v-if="canDeleteUser(user)" @click="confirmDelete(user)" class="p-3 bg-white text-slate-400 hover:text-rose-600 hover:bg-rose-50 border border-slate-100 hover:border-rose-200 rounded-2xl shadow-sm transition-all" title="Delete">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile View -->
      <div class="md:hidden divide-y divide-slate-50">
        <div v-for="user in filteredUsers" :key="user.id" class="p-6 active:bg-slate-50 transition-colors">
          <div class="flex items-center gap-5">
            <div class="w-16 h-16 rounded-[1.5rem] bg-indigo-50 text-indigo-600 flex items-center justify-center font-black text-2xl border border-indigo-100 shadow-sm shrink-0">
               {{ user.username.charAt(0).toUpperCase() }}
            </div>
            <div class="flex-1 min-w-0">
               <div class="flex justify-between items-start mb-2">
                  <h4 class="font-black text-slate-900 truncate pr-2 text-xl tracking-tight leading-none">{{ user.username }}</h4>
                  <button @click="openModal(user)" class="p-2 text-slate-400 hover:text-brand-600">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                  </button>
               </div>
               <div class="flex items-center gap-3">
                  <span class="inline-flex items-center px-3 py-1 rounded-lg text-[9px] font-black uppercase tracking-widest border" :class="getRoleBadgeClass(user.role)">
                    {{ $t(`admin.roles.${user.role.toLowerCase().replace(/ı/g, 'i')}`) || user.role }}
                  </span>
                  <button v-if="canDeleteUser(user)" @click="confirmDelete(user)" class="text-[10px] font-bold text-rose-500 uppercase tracking-widest">Kaldır</button>
               </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Empty State -->
      <div v-if="!loading && filteredUsers.length === 0" class="py-32 px-8 text-center flex flex-col items-center justify-center">
        <div class="w-24 h-24 bg-slate-50 rounded-full flex items-center justify-center text-slate-200 mb-6 border border-slate-100 shadow-inner">
          <svg class="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
        </div>
        <h3 class="text-2xl font-black text-slate-900 mb-2 tracking-tight">{{ $t('admin.users.emptyState') }}</h3>
        <p class="text-slate-500 font-medium max-w-sm mb-8 text-sm">Hiç kullanıcı bulunamadı veya arama kriterlerinize uygun sonuç yok.</p>
        <button @click="openModal()" class="px-8 py-3.5 bg-brand-600 text-white font-black rounded-2xl hover:bg-brand-700 shadow-xl shadow-brand-500/25 transition-all">
          Yeni Kullanıcı Ekle
        </button>
      </div>
    </div>

    <!-- MODERN MODAL: Add/Edit User -->
    <div v-if="isModalOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-md transition-opacity duration-300" @click="closeModal"></div>
      
      <div class="relative w-full max-w-md bg-white rounded-[2.5rem] shadow-2xl overflow-hidden animate-modal-in border border-white/20 flex flex-col max-h-[90vh]">
        <!-- Modal Header -->
        <div class="px-10 py-8 border-b border-slate-100 flex items-center justify-between shrink-0 bg-white relative z-10">
          <div>
            <h3 class="text-2xl font-black text-slate-900 tracking-tight">
              {{ isEditing ? $t('admin.users.modal.editTitle') : $t('admin.users.modal.addTitle') }}
            </h3>
            <p class="text-slate-400 text-sm font-medium mt-0.5">Personel yetkilerini buradan belirleyin.</p>
          </div>
          <button @click="closeModal" class="p-3 bg-slate-50 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-2xl transition-all">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        <!-- Modal Body -->
        <div class="p-10 overflow-y-auto grow space-y-6 bg-slate-50/30">
          <div class="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-sm space-y-5">
            <div>
              <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.users.columns.username') }} *</label>
              <input v-model="form.username" type="text" class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-bold" required />
            </div>
            
            <div>
              <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.users.columns.password') }} <span v-if="!isEditing">*</span></label>
              <input v-model="form.password" type="password" class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-bold" :placeholder="isEditing ? $t('admin.users.modal.passwordHint') : '••••••••'" />
            </div>

            <div>
              <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2 px-1">{{ $t('admin.users.columns.role') }} *</label>
              <div class="grid grid-cols-2 gap-2">
                 <button 
                   v-for="role in ['kitchen', 'bar', 'cashier', 'saloon']" 
                   :key="role"
                   @click="form.role = role"
                   type="button"
                   class="px-4 py-3 rounded-xl border text-[10px] font-black uppercase tracking-widest transition-all"
                   :class="form.role === role ? 'bg-brand-600 border-brand-600 text-white shadow-lg shadow-brand-500/30 scale-105' : 'bg-slate-50 border-slate-100 text-slate-500 hover:bg-slate-100'"
                 >
                   {{ $t(`admin.roles.${role}`) }}
                 </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="px-10 py-8 bg-white border-t border-slate-100 flex flex-col gap-3 shrink-0 relative z-10">
          <button @click="saveUser" class="w-full py-4.5 bg-brand-600 text-white font-black text-sm uppercase tracking-[0.2em] rounded-2xl shadow-2xl shadow-brand-500/30 hover:bg-brand-700 active:scale-[0.98] transition-all disabled:opacity-50" :disabled="saving">
            {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
          </button>
          <button @click="closeModal" class="w-full py-3 text-slate-400 font-black hover:text-slate-600 transition-all uppercase tracking-widest text-xs">
            {{ $t('admin.common.cancel') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()

useHead({
  title: () => `${t('admin.users.title')} | Admin`
})

// State
const users = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')
const isModalOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const form = ref({
  id: '',
  username: '',
  password: '',
  role: ''
})

async function loadUsers() {
  loading.value = true
  try {
    const data = await fetchJson('/api/users')
    users.value = Array.isArray(data) ? data : []
  } catch (e: any) {
    if (e?.status !== 401) uiStore.error('Kullanıcılar yüklenemedi')
  } finally {
    loading.value = false
  }
}

const filteredUsers = computed(() => {
  if (!users.value) return []
  if (!searchQuery.value) return users.value
  const q = searchQuery.value.toLowerCase()
  return users.value.filter((u: any) => 
    String(u.username || '').toLowerCase().includes(q) || 
    String(u.role || '').toLowerCase().includes(q)
  )
})

function getRoleBadgeClass(role: string) {
  const r = String(role || '').toLowerCase().trim().replace(/ı/g, 'i')
  switch (r) {
    case 'superadmin': return 'bg-violet-50 text-violet-700 border-violet-100'
    case 'admin': return 'bg-rose-50 text-rose-700 border-rose-100'
    case 'kitchen': return 'bg-amber-50 text-amber-700 border-amber-100'
    case 'bar': return 'bg-cyan-50 text-cyan-700 border-cyan-100'
    case 'cashier': return 'bg-emerald-50 text-emerald-700 border-emerald-100'
    case 'saloon': return 'bg-purple-50 text-purple-700 border-purple-100'
    default: return 'bg-slate-50 text-slate-700 border-slate-100'
  }
}

function getRoleDotClass(role: string) {
  const r = String(role || '').toLowerCase().trim().replace(/ı/g, 'i')
  switch (r) {
    case 'superadmin': return 'bg-violet-500'
    case 'admin': return 'bg-rose-500'
    case 'kitchen': return 'bg-amber-500'
    case 'bar': return 'bg-cyan-500'
    case 'cashier': return 'bg-emerald-500'
    case 'saloon': return 'bg-purple-500'
    default: return 'bg-slate-500'
  }
}

const isSuperAdmin = computed(() => 
  String(authStore.user?.role || '').toLowerCase().trim().replace(/ı/g, 'i') === 'superadmin'
)

function canDeleteUser(targetUser: any) {
  if (!targetUser || !authStore.user) return false
  if (targetUser.username === authStore.user.username) return false
  if (isSuperAdmin.value) return true
  if (String(targetUser.role || '').toLowerCase().includes('admin')) return false
  return true
}

function openModal(user?: any) {
  if (user) {
    isEditing.value = true
    form.value = { id: user.id, username: user.username, password: '', role: user.role }
  } else {
    isEditing.value = false
    form.value = { id: '', username: '', password: '', role: 'kitchen' }
  }
  isModalOpen.value = true
}

function closeModal() { isModalOpen.value = false }

async function saveUser() {
  if (!form.value.username) return
  if (!isEditing.value && !form.value.password) return

  saving.value = true
  try {
    const payload: any = { username: form.value.username, role: form.value.role }
    if (form.value.password) payload.password = form.value.password
    
    if (isEditing.value) {
      await fetchJson(`/api/users/${form.value.id}`, { method: 'PUT', body: JSON.stringify(payload) })
    } else {
      await fetchJson('/api/users', { method: 'POST', body: JSON.stringify(payload) })
    }
    await loadUsers()
    closeModal()
    uiStore.success('Başarıyla kaydedildi')
  } catch (e: any) {
    uiStore.error(e?.message || 'Hata oluştu')
  } finally {
    saving.value = false
  }
}

async function confirmDelete(user: any) {
  uiStore.confirm({
    title: t('admin.common.delete'),
    message: t('admin.users.deleteConfirm'),
    isDanger: true,
    onConfirm: async () => {
      try {
        await fetchJson(`/api/users/${user.id}`, { method: 'DELETE' })
        await loadUsers()
        uiStore.success(t('admin.common.deleted'))
      } catch (e: any) { uiStore.error('Silinemedi') }
    }
  })
}

onMounted(() => setTimeout(loadUsers, 200))
</script>

<style scoped>
@keyframes modal-in {
  from { transform: translateY(30px) scale(0.95); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}
.animate-modal-in { animation: modal-in 0.5s cubic-bezier(0.16, 1, 0.3, 1); }
.py-4\.5 { padding-top: 1.125rem; padding-bottom: 1.125rem; }
</style>
