<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">{{ $t('admin.menu.title') }}</h1>
        <p class="text-slate-500 text-sm mt-1">{{ $t('admin.menu.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
        <div class="relative w-full sm:w-64">
          <svg class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.menu.searchPlaceholder')"
            class="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none shadow-sm"
          />
        </div>
        <button @click="openModal()" class="w-full sm:w-auto px-4 py-2.5 bg-brand-500 text-white font-semibold rounded-xl hover:bg-brand-600 transition-colors shadow-sm  flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          {{ $t('admin.menu.addItem') }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs uppercase tracking-wider">
              <th class="px-6 py-4 font-semibold w-24">{{ $t('admin.menu.columns.image') }}</th>
              <th class="px-6 py-4 font-semibold">{{ $t('admin.menu.columns.name') }}</th>
              <th class="px-6 py-4 font-semibold">{{ $t('admin.menu.columns.category') }}</th>
              <th class="px-6 py-4 font-semibold">{{ $t('admin.menu.columns.price') }}</th>
              <th class="px-6 py-4 font-semibold">{{ $t('admin.menu.columns.stock') }}</th>
              <th class="px-6 py-4 font-semibold text-right">{{ $t('admin.menu.columns.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading" class="animate-pulse">
              <td colspan="6" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.common.loading') }}</td>
            </tr>
            <tr v-else-if="filteredMenu.length === 0">
              <td colspan="6" class="px-6 py-8 text-center text-slate-400">{{ $t('admin.menu.emptyState') }}</td>
            </tr>
            <tr v-for="item in filteredMenu" :key="item.id" class="hover:bg-slate-50/80 transition-colors">
              <td class="px-6 py-4">
                <div v-if="item.image" class="w-12 h-12 rounded-lg bg-slate-100 overflow-hidden border border-slate-200">
                  <img :src="item.image" :alt="item.name" class="w-full h-full object-cover" loading="lazy" />
                </div>
                <div v-else class="w-12 h-12 rounded-lg bg-slate-100 flex items-center justify-center text-slate-400 border border-slate-200">
                  <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="font-semibold text-slate-800">{{ item.name }}</div>
                <div v-if="item.description" class="text-xs text-slate-500 mt-1 line-clamp-1 max-w-[200px]" :title="item.description">{{ item.description }}</div>
              </td>
              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
                  {{ $te(`menu.categories.${item.category}`) ? $t(`menu.categories.${item.category}`) : item.category }}
                </span>
                <span v-if="item.subcategory" class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium bg-slate-50 text-slate-500 border border-slate-200 ml-1">
                  {{ $te(`menu.subcategories.${item.subcategory}`) ? $t(`menu.subcategories.${item.subcategory}`) : item.subcategory }}
                </span>
              </td>
              <td class="px-6 py-4 font-bold text-slate-800">
                {{ formatPrice(item.price) }}
              </td>
              <td class="px-6 py-4">
                <div v-if="item.stockEnabled">
                  <span class="inline-flex items-center px-2 py-1 rounded-md text-xs font-bold border" 
                    :class="item.stockQuantity && item.stockQuantity > 0 ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-rose-50 text-rose-700 border-rose-200'">
                    {{ item.stockQuantity || 0 }} {{ $t('admin.menu.columns.stock') }}
                  </span>
                </div>
                <span v-else class="text-xs text-slate-400">-</span>
              </td>
              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2">
                  <button @click="openModal(item)" class="p-2 text-slate-400 hover:text-brand-600 hover:bg-brand-50 rounded-lg transition-colors" :title="$t('admin.menu.editItem')">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button @click="confirmDelete(item)" class="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors" :title="$t('admin.menu.deleteItem')">
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
      <div class="bg-white rounded-2xl w-full max-w-2xl relative z-10  overflow-hidden animate-slide-up flex flex-col max-h-[90vh]">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between shrink-0">
          <h3 class="text-lg font-bold text-slate-800">
            {{ isEditing ? $t('admin.menu.modal.editTitle') : $t('admin.menu.modal.addTitle') }}
          </h3>
          <button @click="closeModal" class="text-slate-400 hover:text-slate-600">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 overflow-y-auto space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.name') }} (TR)</label>
              <input v-model="form.name" type="text" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
            </div>

            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.name') }} (EN)</label>
              <input v-model="form.nameEn" type="text" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
            </div>
            
            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.description') }} (TR)</label>
              <textarea v-model="form.description" rows="2" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all"></textarea>
            </div>

            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.description') }} (EN)</label>
              <textarea v-model="form.descriptionEn" rows="2" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all"></textarea>
            </div>

            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.price') }} (TRY)</label>
              <input v-model.number="form.price" type="number" step="0.01" min="0" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
            </div>

            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.price') }} (USD)</label>
              <input v-model.number="form.priceUsd" type="number" step="0.01" min="0" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
            </div>

            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.category') }} *</label>
              <input v-model="form.category" list="categoriesList" type="text" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" required />
              <datalist id="categoriesList">
                <option v-for="cat in availableCategories" :key="cat" :value="cat">{{ $te(`menu.categories.${cat}`) ? $t(`menu.categories.${cat}`) : cat }}</option>
              </datalist>
            </div>

            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.subcategory') }}</label>
              <input v-model="form.subcategory" list="subcategoriesList" type="text" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
              <datalist id="subcategoriesList">
                <option v-for="sub in availableSubcategories" :key="sub" :value="sub">{{ $te(`menu.subcategories.${sub}`) ? $t(`menu.subcategories.${sub}`) : sub }}</option>
              </datalist>
            </div>

            <div class="md:col-span-2">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.image') }}</label>
              <input v-model="form.image" type="text" placeholder="https://" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
            </div>

            <div class="md:col-span-2 bg-slate-50 p-4 rounded-xl border border-slate-200 mt-2 relative overflow-hidden">
              <div v-if="!isProPlan" class="absolute inset-0 z-20 bg-slate-100/70 backdrop-blur-sm flex items-center justify-center">
                <NuxtLink to="/admin/upgrade" class="px-5 py-2.5 bg-amber-500 text-white text-xs font-black tracking-widest uppercase rounded-xl shadow-lg hover:bg-amber-600 hover:scale-105 transition-all flex items-center gap-2">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
                  {{ $t('admin.menu.proPlanRequiredStock') }}
                </NuxtLink>
              </div>

              <label class="flex items-center gap-3 mb-3" :class="!isProPlan ? 'opacity-50' : 'cursor-pointer'">
                <div class="relative">
                  <input type="checkbox" v-model="form.stockEnabled" class="sr-only peer" :disabled="!isProPlan" />
                  <div class="w-11 h-6 bg-slate-300 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-brand-500"></div>
                </div>
                <span class="text-sm font-semibold text-slate-700">{{ $t('admin.menu.modal.stockEnabled') }}</span>
              </label>

              <div v-if="form.stockEnabled">
                <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.stockQuantity') }}</label>
                <input v-model.number="form.stockQuantity" type="number" min="0" :disabled="!isProPlan" class="w-full px-4 py-2 bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all disabled:opacity-50" />
              </div>
            </div>

          </div>
        </div>
        <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 flex justify-end gap-3 shrink-0">
          <button @click="closeModal" class="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-200 rounded-xl transition-colors">
            {{ $t('admin.common.cancel') }}
          </button>
          <button @click="saveItem" class="px-4 py-2 text-sm font-semibold bg-brand-500 text-white rounded-xl hover:bg-brand-600 transition-colors disabled:opacity-50" :disabled="saving">
            {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'
import { useTenant } from '~/composables/useTenant'

definePageMeta({
  layout: 'admin',
})

const { t, te } = useI18n()
const { fetchJson } = useApi()
const orderStore = useOrderStore()
const uiStore = useUiStore()
const authStore = useAuthStore()
const { isProPlan, loadTenantConfig } = useTenant()

useHead({
  title: () => `${t('admin.menu.title')} | Admin | feasymenu`
})

// State
const menuItems = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')

const isModalOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)

const form = ref({
  id: '',
  name: '',
  nameEn: '',
  description: '',
  descriptionEn: '',
  price: null as number | null,
  priceUsd: null as number | null,
  category: 'main_courses',
  subcategory: '',
  image: '',
  stockEnabled: false,
  stockQuantity: 0
})

// Configurable categories for the form (includes existing item values to avoid missing categories)
const defaultCategories = ['main_courses', 'beverages', 'desserts', 'salads', 'starters']
const defaultSubcategories = ['hot', 'cold', 'vegan', 'gluten_free', 'popular']

const availableCategories = computed(() => {
  const set = new Set(defaultCategories)
  menuItems.value.forEach(item => {
    if (item.category) set.add(item.category)
  })
  return Array.from(set)
})

const availableSubcategories = computed(() => {
  const set = new Set(defaultSubcategories)
  menuItems.value.forEach(item => {
    if (item.subcategory) set.add(item.subcategory)
  })
  return Array.from(set)
})

// Derived
const filteredMenu = computed(() => {
  if (!searchQuery.value) return menuItems.value
  const q = searchQuery.value.toLowerCase()
  return menuItems.value.filter((m: any) => 
    m.name.toLowerCase().includes(q) || 
    m.category.toLowerCase().includes(q) ||
    (m.subcategory && m.subcategory.toLowerCase().includes(q))
  )
})

// Methods
async function loadMenu() {
  loading.value = true
  try {
    const data = await fetchJson('/api/menu')
    menuItems.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('Failed to load menu', e)
  } finally {
    loading.value = false
  }
}

function formatPrice(p: number) {
  return new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(p)
}

function openModal(item?: any) {
  if (item) {
    isEditing.value = true
    form.value = {
      id: item.id,
      name: item.name,
      nameEn: item.nameEn || '',
      description: item.description || '',
      descriptionEn: item.descriptionEn || '',
      price: item.price,
      priceUsd: item.priceUsd || null,
      category: item.category,
      subcategory: item.subcategory || '',
      image: item.image || '',
      stockEnabled: item.stockEnabled || false,
      stockQuantity: item.stockQuantity || 0
    }
  } else {
    isEditing.value = false
    form.value = {
      id: '',
      name: '',
      nameEn: '',
      description: '',
      descriptionEn: '',
      price: null,
      priceUsd: null,
      category: 'main_courses',
      subcategory: '',
      image: '',
      stockEnabled: false,
      stockQuantity: 0
    }
  }
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

async function saveItem() {
  const hasTr = form.value.name?.trim() && form.value.price != null && form.value.price >= 0;
  const hasEn = form.value.nameEn?.trim() && form.value.priceUsd != null && form.value.priceUsd >= 0;

  if (!hasTr && !hasEn) {
    uiStore.error(t('admin.menu.validationError') || 'Please fill either Turkish (Name & Price) or English (Name & Price).');
    return;
  }
  
  if (!form.value.category) return

  saving.value = true
  try {
    const payload: any = {
      name: form.value.name || '',
      nameEn: form.value.nameEn || null,
      description: form.value.description,
      descriptionEn: form.value.descriptionEn || null,
      price: form.value.price || 0,
      priceUsd: form.value.priceUsd || null,
      category: form.value.category,
      subcategory: form.value.subcategory,
      image: form.value.image,
      stockEnabled: form.value.stockEnabled,
      stockQuantity: form.value.stockQuantity
    }

    if (isEditing.value) {
      await fetchJson(`/api/menu/${form.value.id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
    } else {
      await fetchJson('/api/menu', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
    }
    
    await loadMenu()
    closeModal()
  } catch (e: any) {
    const errorMessage = e?.message || e?.toString() || t('errors.serverError');
    const translated = t(errorMessage);
    uiStore.error(translated.includes('error.') ? errorMessage : translated);
  } finally {
    saving.value = false
  }
}

async function confirmDelete(item: any) {
  if (confirm(t('admin.menu.deleteConfirm'))) {
    try {
      await fetchJson(`/api/menu/${item.id}`, { method: 'DELETE' })
      await loadMenu()
    } catch (e: any) {
      const errorMessage = e?.message || e?.toString() || t('errors.serverError');
      const translated = t(errorMessage);
      uiStore.error(translated.includes('error.') ? errorMessage : translated);
    }
  }
}

onMounted(async () => {
  if (authStore.user?.tenantCode) {
    await loadTenantConfig(authStore.user.tenantCode)
  }
  loadMenu()
})
</script>
