<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.users.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.users.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
        <div class="relative w-full sm:w-64">
          <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.users.searchPlaceholder')"
            class="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm"
          />
        </div>
        <button @click="openModal()" class="w-full sm:w-auto px-4 py-2.5 bg-brand-500 text-white font-semibold rounded-xl hover:bg-brand-600 transition-colors shadow-sm  flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          {{ $t('admin.users.addUser') }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs uppercase tracking-wider">
              <th class="px-6 py-4 font-semibold">{{ $t('admin.users.columns.username') }}</th>
              <th class="px-6 py-4 font-semibold">{{ $t('admin.users.columns.role') }}</th>
              <th class="px-6 py-4 font-semibold text-right">{{ $t('admin.users.columns.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading" class="animate-pulse">
              <td colspan="3" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.common.loading') }}</td>
            </tr>
            <tr v-else-if="filteredUsers.length === 0">
              <td colspan="3" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.users.emptyState') }}</td>
            </tr>
            <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-slate-50/80 transition-colors">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-brand-100 text-brand-600 flex items-center justify-center font-bold">
                    {{ user.username.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div class="font-semibold text-slate-800">{{ user.username }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold border" :class="getRoleBadgeClass(user.role)">
                  {{ $t(`admin.roles.${user.role.toLowerCase().replace(/ı/g, 'i')}`) || user.role }}
                </span>
              </td>
              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <button @click="openModal(user)" class="p-2 text-slate-400 hover:text-brand-600 hover:bg-brand-50 rounded-lg transition-colors" :title="$t('admin.users.editUser')">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button 
                    v-if="canDeleteUser(user)"
                    @click="confirmDelete(user)" 
                    class="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors" 
                    :title="$t('admin.users.deleteUser')"
                  >
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="closeModal"></div>
      <div class="bg-white rounded-2xl w-full max-w-md relative z-10  overflow-hidden animate-slide-up">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-slate-800">
            {{ isEditing ? $t('admin.users.modal.editTitle') : $t('admin.users.modal.addTitle') }}
          </h3>
          <button @click="closeModal" class="text-slate-400 hover:text-slate-600">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.users.columns.username') }} *</label>
            <input v-model="form.username" type="text" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" required />
          </div>
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.users.columns.password') }} <span v-if="!isEditing">*</span></label>
            <input v-model="form.password" type="password" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" :placeholder="isEditing ? $t('admin.users.modal.passwordHint') : ''" />
          </div>
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.users.columns.role') }} *</label>
            <select v-model="form.role" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all">
              <option value="kitchen">{{ $t('admin.roles.kitchen') }}</option>
              <option value="bar">{{ $t('admin.roles.bar') }}</option>
              <option value="cashier">{{ $t('admin.roles.cashier') }}</option>
              <option value="saloon">{{ $t('admin.roles.saloon') }}</option>
            </select>
          </div>
        </div>
        <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 flex justify-end gap-3">
          <button @click="closeModal" class="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-200 rounded-xl transition-colors">
            {{ $t('admin.common.cancel') }}
          </button>
          <button @click="saveUser" class="px-4 py-2 text-sm font-semibold bg-brand-500 text-white rounded-xl hover:bg-brand-600 transition-colors disabled:opacity-50" :disabled="saving">
            {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'admin',
})

const { t } = useI18n()
const { fetchJson } = useApi()
const uiStore = useUiStore()
const authStore = useAuthStore()

useHead({
  title: () => `${t('admin.users.title')} | Admin | feasymenu`
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
  role: 'manager'
})

// Derived
const filteredUsers = computed(() => {
  if (!users.value) return []
  if (!searchQuery.value) return users.value
  const q = searchQuery.value.toLowerCase()
  return users.value.filter((u: any) => {
    const name = String(u.username || '').toLowerCase()
    const role = String(u.role || '').toLowerCase()
    return name.includes(q) || role.includes(q)
  })
})

// Methods
async function loadUsers() {
  loading.value = true
  try {
    const data = await fetchJson('/api/users')
    users.value = Array.isArray(data) ? data : []
  } catch (e: any) {
    if (e?.status !== 401) {
      uiStore.error('Kullanıcılar yüklenirken hata oluştu')
    }
  } finally {
    loading.value = false
  }
}

function getRoleBadgeClass(role: string) {
  switch (role) {
    case 'superadmin': return 'bg-blue-100 text-blue-700 border-blue-200'
    case 'admin': return 'bg-rose-100 text-rose-700 border-rose-200'
    case 'manager': return 'bg-indigo-100 text-indigo-700 border-indigo-200'
    case 'kitchen': return 'bg-amber-100 text-amber-700 border-amber-200'
    case 'bar': return 'bg-emerald-100 text-emerald-700 border-emerald-200'
    case 'cashier': return 'bg-blue-100 text-blue-700 border-blue-200'
    case 'saloon': return 'bg-purple-100 text-purple-700 border-purple-200'
    default: return 'bg-slate-100 text-slate-700 border-slate-200'
  }
}

const isSuperAdmin = computed(() => {
  const role = String(authStore.user?.role || '').toLowerCase().trim().replace(/ı/g, 'i')
  return role === 'superadmin'
})

function canDeleteUser(targetUser: any) {
  if (!targetUser || !authStore.user) return false
  
  // 1. Kimse kendini silemez
  if (targetUser.username === authStore.user.username) return false
  
  // 2. Süper Admin her şeyi silebilir (Kendisi hariç)
  if (isSuperAdmin.value) return true
  
  // 3. Admin rolündeki biri, başka bir admin'i silemez
  const targetRole = String(targetUser.role || '').toLowerCase().trim().replace(/ı/g, 'i')
  if (targetRole === 'admin') return false
  
  // 4. Normal adminler personel rollerini silebilir
  return true
}

function openModal(user?: any) {
  if (user) {
    isEditing.value = true
    form.value = {
      id: user.id,
      username: user.username,
      password: '',
      role: user.role
    }
  } else {
    isEditing.value = false
    form.value = {
      id: '',
      username: '',
      password: '',
      role: 'kitchen'
    }
  }
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

async function saveUser() {
  if (!form.value.username) return
  if (!isEditing.value && !form.value.password) return // password required for new users

  saving.value = true
  try {
    const payload: any = {
      username: form.value.username,
      role: form.value.role
    }
    if (form.value.password) {
      payload.password = form.value.password
    }

    if (isEditing.value) {
      await fetchJson(`/api/users/${form.value.id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
    } else {
      await fetchJson('/api/users', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
    }
    
    await loadUsers()
    closeModal()
  } catch (e: any) {
    const errorMessage = e?.message || e?.toString() || t('common.error');
    // If errorMessage includes 'error.', translate it, otherwise use it directly if it doesn't have translation
    const translated = t(errorMessage);
    uiStore.error(translated.includes('error.') ? errorMessage : translated);
  } finally {
    saving.value = false
  }
}

async function confirmDelete(user: any) {
  if (confirm(t('admin.users.deleteConfirm'))) {
    try {
      await fetchJson(`/api/users/${user.id}`, { method: 'DELETE' })
      await loadUsers()
    } catch (e: any) {
      const errorMessage = e?.message || e?.toString() || t('common.error');
      const translated = t(errorMessage);
      uiStore.error(translated.includes('error.') ? errorMessage : translated);
    }
  }
}

onMounted(() => {
  // Add a small delay to ensure Nuxt hydration is complete
  setTimeout(() => {
    loadUsers()
  }, 200)
})
</script>
