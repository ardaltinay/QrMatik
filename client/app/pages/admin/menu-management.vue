<template>
  <div class="p-6 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight">{{ $t('admin.menu.title') }}</h1>
        <p class="text-slate-500 font-medium mt-1">{{ $t('admin.menu.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
        <div class="relative w-full sm:w-72">
          <svg class="w-5 h-5 absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.menu.searchPlaceholder')"
            class="w-full pl-12 pr-4 py-3 rounded-2xl bg-white border border-slate-100 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none shadow-sm font-medium"
          />
        </div>
        <button @click="openModal()" class="w-full sm:w-auto px-6 py-3 bg-brand-600 text-white font-bold rounded-2xl hover:bg-brand-700 hover:-translate-y-0.5 transition-all shadow-lg shadow-brand-500/30 flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
          </svg>
          {{ $t('admin.menu.addItem') }}
        </button>
        <button @click="openCategorySortModal" class="w-full sm:w-auto px-5 py-3 bg-white border border-slate-200 text-slate-700 font-bold rounded-2xl hover:bg-slate-50 hover:-translate-y-0.5 transition-all shadow-sm flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 4h13M3 8h13M3 12h13M3 16h13" />
          </svg>
          {{ $t('admin.menu.sortCategories') }}
        </button>
      </div>
    </div>

    <!-- Bento Stats -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      <div class="bg-white rounded-[2rem] p-6 border border-slate-100 shadow-xl shadow-slate-200/40 flex items-center gap-5 hover:-translate-y-1 transition-all duration-300">
        <div class="w-16 h-16 rounded-2xl bg-brand-50 text-brand-600 flex items-center justify-center shrink-0 border border-brand-100">
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>
        </div>
        <div>
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.menu.totalItems') }}</p>
          <h3 class="text-4xl font-black text-slate-900 tracking-tight">{{ menuItems.length }}</h3>
        </div>
      </div>
      <div class="bg-white rounded-[2rem] p-6 border border-slate-100 shadow-xl shadow-slate-200/40 flex items-center gap-5 hover:-translate-y-1 transition-all duration-300">
        <div class="w-16 h-16 rounded-2xl bg-amber-50 text-amber-500 flex items-center justify-center shrink-0 border border-amber-100">
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6z" /></svg>
        </div>
        <div>
          <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">{{ $t('admin.menu.totalCategories') }}</p>
          <h3 class="text-4xl font-black text-slate-900 tracking-tight">{{ availableCategories.length }}</h3>
        </div>
      </div>
      <div class="bg-white rounded-[2rem] p-6 border border-slate-100 shadow-xl shadow-slate-200/40 flex items-center gap-5 hover:-translate-y-1 transition-all duration-300 relative overflow-hidden group">
        <div v-if="!isProPlan" class="absolute inset-0 bg-white/70 backdrop-blur-[2px] z-10 flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
          <div class="bg-brand-500 text-white text-[10px] font-black px-2 py-1 rounded shadow-sm uppercase tracking-wider mb-1">PRO PLAN</div>
          <span class="text-xs font-bold text-slate-600">{{ $t('admin.menu.proPlanRequiredStock') || 'Sadece Pro\'da' }}</span>
        </div>
        <div class="w-16 h-16 rounded-2xl flex items-center justify-center shrink-0 border transition-colors"
          :class="isProPlan ? 'bg-rose-50 text-rose-500 border-rose-100' : 'bg-slate-50 text-slate-400 border-slate-200'">
          <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
        </div>
        <div :class="!isProPlan ? 'opacity-40 blur-[1px]' : ''">
          <div class="flex items-center gap-2 mb-1">
             <p class="text-xs font-black text-slate-400 uppercase tracking-widest">{{ $t('admin.menu.outOfStock') }}</p>
             <span v-if="!isProPlan" class="text-[9px] font-black bg-slate-200 text-slate-500 px-1.5 py-0.5 rounded uppercase">PRO</span>
          </div>
          <h3 class="text-4xl font-black text-slate-900 tracking-tight">{{ isProPlan ? menuItems.filter(i => i.stockEnabled && i.stockQuantity <= 0).length : '0' }}</h3>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-[2rem] border border-slate-100 shadow-xl shadow-slate-200/40 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/50 border-b border-slate-100 text-slate-400 text-xs font-black uppercase tracking-widest">
              <th class="px-8 py-5 w-28">{{ $t('admin.menu.columns.image') }}</th>
              <th class="px-8 py-5">{{ $t('admin.menu.columns.name') }}</th>
              <th class="px-8 py-5">{{ $t('admin.menu.columns.category') }}</th>
              <th class="px-8 py-5">{{ $t('admin.menu.columns.price') }}</th>
              <th class="px-8 py-5">{{ $t('admin.menu.columns.stock') }}</th>
              <th class="px-8 py-5 text-right">{{ $t('admin.menu.columns.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <!-- Skeleton Loader -->
            <template v-if="loading">
              <tr v-for="i in 5" :key="i" class="animate-pulse">
                <td class="px-8 py-5"><div class="w-14 h-14 bg-slate-100 rounded-xl"></div></td>
                <td class="px-8 py-5"><div class="w-32 h-4 bg-slate-100 rounded-full mb-2"></div><div class="w-24 h-3 bg-slate-50 rounded-full"></div></td>
                <td class="px-8 py-5"><div class="w-20 h-6 bg-slate-100 rounded-lg"></div></td>
                <td class="px-8 py-5"><div class="w-16 h-5 bg-slate-100 rounded-md"></div></td>
                <td class="px-8 py-5"><div class="w-12 h-6 bg-slate-100 rounded-full"></div></td>
                <td class="px-8 py-5 text-right"><div class="w-8 h-8 bg-slate-100 rounded-lg ml-auto"></div></td>
              </tr>
            </template>
            
            <!-- Empty State -->
            <tr v-else-if="filteredMenu.length === 0">
              <td colspan="6" class="px-8 py-20 text-center">
                <div class="flex flex-col items-center justify-center">
                  <div class="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center text-slate-300 mb-4">
                    <svg class="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" /></svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">{{ $t('admin.menu.emptyState') }}</h3>
                  <p class="text-slate-500 font-medium text-sm max-w-sm">Sonuç bulunamadı. Yeni bir ürün ekleyebilir veya aramayı temizleyebilirsiniz.</p>
                </div>
              </td>
            </tr>

            <!-- Data Rows -->
            <tr v-for="item in filteredMenu" :key="item.id" class="hover:bg-slate-50/50 transition-colors group">
              <td class="px-8 py-4">
                <div v-if="item.image" class="w-14 h-14 rounded-xl bg-slate-100 overflow-hidden shadow-sm group-hover:shadow-md transition-shadow">
                  <NuxtImg :src="item.image" :alt="item.name" format="webp" class="w-full h-full object-cover" loading="lazy" />
                </div>
                <div v-else class="w-14 h-14 rounded-xl bg-slate-50 flex items-center justify-center text-slate-300 shadow-inner">
                  <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
              </td>
              <td class="px-8 py-4">
                <div class="font-black text-slate-900">{{ item.name }}</div>
                <div v-if="item.description" class="text-xs font-medium text-slate-500 mt-1 line-clamp-1 max-w-[200px]" :title="item.description">{{ item.description }}</div>
              </td>
              <td class="px-8 py-4">
                <div class="flex flex-wrap gap-2">
                  <span class="inline-flex items-center px-3 py-1 rounded-lg text-xs font-bold bg-slate-100 text-slate-600">
                    {{ $te(`menu.categories.${item.category}`) ? $t(`menu.categories.${item.category}`) : item.category }}
                  </span>
                  <span v-if="item.subcategory" class="inline-flex items-center px-3 py-1 rounded-lg text-xs font-bold bg-white border border-slate-200 text-slate-500 shadow-sm">
                    {{ $te(`menu.subcategories.${item.subcategory}`) ? $t(`menu.subcategories.${item.subcategory}`) : item.subcategory }}
                  </span>
                </div>
              </td>
              <td class="px-8 py-4 font-black text-slate-900 text-base">
                {{ formatPrice(item.price) }}
              </td>
              <td class="px-8 py-4">
                <div v-if="item.stockEnabled">
                  <span class="inline-flex items-center justify-center px-3 py-1 rounded-lg text-xs font-black min-w-[3.5rem] shadow-sm" 
                    :class="item.stockQuantity && item.stockQuantity > 0 ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'">
                    {{ item.stockQuantity || 0 }}
                  </span>
                </div>
                <span v-else class="text-sm font-bold text-slate-300">-</span>
              </td>
              <td class="px-8 py-4 text-right">
                <div class="flex items-center justify-end gap-1">
                  <button @click="openModal(item)" class="p-2.5 text-slate-400 hover:text-brand-600 hover:bg-brand-50 rounded-xl transition-all" :title="$t('admin.menu.editItem')">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button @click="confirmDelete(item)" class="p-2.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition-all" :title="$t('admin.menu.deleteItem')">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
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

            <div class="md:col-span-1">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.sortOrder') }}</label>
              <input v-model.number="form.sortOrder" type="number" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" placeholder="0" />
            </div>

            <div class="md:col-span-2">
              <label class="block text-sm font-semibold text-slate-700 mb-1">{{ $t('admin.menu.modal.image') }}</label>
              <input v-model="form.image" type="text" placeholder="https://" class="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
            </div>

            <div class="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Stock Control -->
              <div class="bg-slate-50 p-4 rounded-xl border border-slate-200 relative group/stock overflow-hidden">
                <!-- Simple PRO Indicator -->
                <div v-if="!isProPlan" class="absolute inset-0 z-20 flex flex-col items-center justify-center bg-white/60 backdrop-blur-[2px] p-2 transition-all group-hover/stock:backdrop-blur-[4px]">
                  <NuxtLink to="/admin/upgrade" class="flex items-center gap-2 px-3 py-1.5 bg-slate-900 text-white text-[9px] font-black tracking-widest rounded-lg shadow-lg hover:scale-105 active:scale-95 transition-all">
                    <svg class="w-3 h-3 text-amber-400" fill="currentColor" viewBox="0 0 20 20"><path d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" /></svg>
                    {{ $upper('PRO: ' + $t('admin.menu.proPlanRequiredStock')) }}
                  </NuxtLink>
                </div>

                <label class="flex items-center gap-3 mb-3" :class="!isProPlan ? 'opacity-40' : 'cursor-pointer'">
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

              <!-- Featured Toggle (Story Control) -->
              <div class="bg-slate-50 p-4 rounded-xl border border-slate-200 flex flex-col justify-center relative overflow-hidden group/featured">
                <!-- Simple PAID Indicator -->
                <div v-if="!isPaidPlan" class="absolute inset-0 z-20 flex flex-col items-center justify-center bg-white/60 backdrop-blur-[2px] p-2 transition-all group-hover/featured:backdrop-blur-[4px]">
                  <NuxtLink to="/admin/upgrade" class="flex items-center gap-2 px-3 py-1.5 bg-slate-900 text-white text-[9px] font-black tracking-widest rounded-lg shadow-lg hover:scale-105 active:scale-95 transition-all">
                    <svg class="w-3 h-3 text-amber-400" fill="currentColor" viewBox="0 0 20 20"><path d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" /></svg>
                    {{ $upper($t('admin.menu.standartOrProPlanRequiredFeatured') || 'Sadece Ücretli Planlarda') }}
                  </NuxtLink>
                </div>

                <label class="flex items-center gap-3" :class="!isPaidPlan ? 'opacity-40' : 'cursor-pointer'">
                  <div class="relative">
                    <input type="checkbox" v-model="form.isFeatured" class="sr-only peer" :disabled="!isPaidPlan" />
                    <div class="w-11 h-6 bg-slate-300 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-amber-500"></div>
                  </div>
                  <div>
                    <span class="text-sm font-semibold text-slate-700 block">{{ $t('admin.menu.modal.isFeatured') || 'Öne Çıkar (Story)' }}</span>
                    <span class="text-[10px] text-slate-500 font-medium">{{ $t('admin.menu.modal.isFeaturedDesc') || 'Bu ürünü hikayelerde gösterir.' }}</span>
                  </div>
                </label>
              </div>
            </div>

          </div>
        </div>
        <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 flex justify-center gap-3 shrink-0">
          <button @click="closeModal" class="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-200 rounded-xl transition-colors">
            {{ $t('admin.common.cancel') }}
          </button>
          <button @click="saveItem" class="px-4 py-2 text-sm font-semibold bg-brand-500 text-white rounded-xl hover:bg-brand-600 transition-colors disabled:opacity-50" :disabled="saving">
            {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- Category Sort Modal -->
    <div v-if="isCategorySortModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="isCategorySortModalOpen = false"></div>
      <div class="relative w-full max-w-md bg-white rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[80vh]">
        <div class="px-6 py-5 border-b border-slate-100 flex justify-between items-center bg-slate-50">
          <div>
            <h3 class="text-xl font-bold text-slate-900">{{ $t('admin.menu.categorySort.title') }}</h3>
            <p class="text-slate-500 text-xs mt-0.5">{{ $t('admin.menu.categorySort.subtitle') }}</p>
          </div>
          <button @click="isCategorySortModalOpen = false" class="text-slate-400 hover:text-slate-600 transition-colors">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        
        <div class="p-6 overflow-y-auto grow">
          <div class="space-y-2">
            <div 
              v-for="(cat, index) in sortingCategories" 
              :key="cat" 
              class="flex items-center justify-between p-4 bg-white border border-slate-200 rounded-2xl group hover:border-brand-500 transition-all shadow-sm"
            >
              <div class="flex items-center gap-3">
                <span class="w-6 h-6 rounded-lg bg-slate-100 flex items-center justify-center text-[10px] font-black text-slate-400">{{ index + 1 }}</span>
                <span class="font-bold text-slate-800">{{ te(`menu.categories.${cat}`) ? $t(`menu.categories.${cat}`) : cat }}</span>
              </div>
              <div class="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <button 
                  @click="moveCategory(index, -1)" 
                  :disabled="index === 0"
                  class="p-2 text-slate-400 hover:text-brand-600 disabled:opacity-20 transition-colors"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 15l7-7 7 7" /></svg>
                </button>
                <button 
                  @click="moveCategory(index, 1)" 
                  :disabled="index === sortingCategories.length - 1"
                  class="p-2 text-slate-400 hover:text-brand-600 disabled:opacity-20 transition-colors"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" /></svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 flex justify-center gap-3">
          <button @click="isCategorySortModalOpen = false" class="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-200 rounded-xl transition-colors">
            {{ $t('admin.common.cancel') }}
          </button>
          <button @click="saveCategoryOrder" class="px-4 py-2 text-sm font-semibold bg-brand-500 text-white rounded-xl hover:bg-brand-600 transition-colors shadow-lg" :disabled="savingOrder">
            {{ savingOrder ? $t('admin.common.saving') : $t('admin.common.save') }}
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
const { isProPlan, isPaidPlan, loadTenantConfig } = useTenant()

useHead({
  title: () => `${t('admin.menu.title')} | Admin`
})

// State
const menuItems = ref<any[]>([])
const loading = ref(true)
const searchQuery = ref('')

const isModalOpen = ref(false)
const isCategorySortModalOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const savingOrder = ref(false)

const sortingCategories = ref<string[]>([])

const form = ref({
  id: '',
  name: '',
  nameEn: '',
  description: '',
  descriptionEn: '',
  price: null as number | null,
  priceUsd: null as number | null,
  category: '',
  subcategory: '',
  image: '',
  sortOrder: null as number | null,
  stockEnabled: false,
  stockQuantity: 0,
})

// Configurable categories for the form (includes existing item values to avoid missing categories)

const availableCategories = computed(() => {
  const set = new Set([])
  menuItems.value.forEach(item => {
    if (item.category) set.add(item.category)
  })
  return Array.from(set)
})

const availableSubcategories = computed(() => {
  const set = new Set([])
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
    form.value = { ...item }
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
      category: '',
      subcategory: '',
      image: '',
      sortOrder: null,
      stockEnabled: false,
      stockQuantity: 0,
      isFeatured: false,
    }
  }
  isModalOpen.value = true
}

function openCategorySortModal() {
  sortingCategories.value = [...availableCategories.value]
  isCategorySortModalOpen.value = true
}

function moveCategory(index: number, direction: number) {
  const newIndex = index + direction
  if (newIndex < 0 || newIndex >= sortingCategories.value.length) return
  const item = sortingCategories.value.splice(index, 1)[0]
  sortingCategories.value.splice(newIndex, 0, item)
}

async function saveCategoryOrder() {
  savingOrder.value = true
  try {
    await fetchJson('/api/tenant/category-order', {
      method: 'PUT',
      body: JSON.stringify({ order: sortingCategories.value.join(',') })
    })
    uiStore.success(t('admin.menu.categorySort.saveSuccess'))
    isCategorySortModalOpen.value = false
    // Reload tenant config to update UI elsewhere
    if (authStore.user?.tenantCode) {
      await loadTenantConfig(authStore.user.tenantCode)
    }
  } catch (e) {
    uiStore.error(t('admin.menu.categorySort.saveError'))
  } finally {
    savingOrder.value = false
  }
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
      sortOrder: form.value.sortOrder,
      stockEnabled: form.value.stockEnabled,
      stockQuantity: form.value.stockQuantity,
      isFeatured: form.value.isFeatured || false
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
  uiStore.confirm({
    title: t('admin.common.delete') || 'Sil',
    message: t('admin.menu.deleteConfirm') || 'Bu ürünü silmek istediğinize emin misiniz?',
    isDanger: true,
    onConfirm: async () => {
      try {
        await fetchJson(`/api/menu/${item.id}`, { method: 'DELETE' })
        await loadMenu()
        uiStore.success(t('admin.common.deleted') || 'Silindi.')
      } catch (e: any) {
        const errorMessage = e?.message || e?.toString() || t('errors.serverError');
        const translated = t(errorMessage);
        uiStore.error(translated.includes('error.') ? errorMessage : translated);
      }
    }
  })
}

onMounted(async () => {
  if (authStore.user?.tenantCode) {
    await loadTenantConfig(authStore.user.tenantCode)
  }
  loadMenu()
})
</script>
