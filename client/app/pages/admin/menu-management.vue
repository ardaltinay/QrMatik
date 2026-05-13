<template>
  <div class="p-4 md:p-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 mb-10">
      <div>
        <h1 class="text-3xl font-black text-slate-900 tracking-tight flex items-center gap-3">
          {{ $t('admin.menu.title') }}
          <span class="bg-brand-100 text-brand-600 text-[10px] font-black px-2 py-1 rounded-lg uppercase tracking-wider">Editor</span>
        </h1>
        <p class="text-slate-500 font-medium mt-1">{{ $t('admin.menu.subtitle') }}</p>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 w-full lg:w-auto">
        <div class="relative flex-1 sm:min-w-[300px]">
          <svg class="w-5 h-5 absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-brand-500 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            :placeholder="$t('admin.menu.searchPlaceholder')"
            class="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-white border border-slate-200 focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 transition-all outline-none shadow-sm font-medium"
          />
        </div>
        <div class="flex gap-2">
          <button @click="openModal()" class="flex-1 sm:w-auto px-6 py-3.5 bg-brand-600 text-white font-bold rounded-2xl hover:bg-brand-700 hover:-translate-y-0.5 active:translate-y-0 transition-all shadow-lg shadow-brand-500/25 flex items-center justify-center gap-2">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
            <span class="whitespace-nowrap">{{ $t('admin.menu.addItem') }}</span>
          </button>
          <button @click="openCategorySortModal" class="p-3.5 bg-white border border-slate-200 text-slate-600 font-bold rounded-2xl hover:bg-slate-50 hover:border-slate-300 transition-all shadow-sm flex items-center justify-center" :title="$t('admin.menu.sortCategories')">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3 4h13M3 8h13M3 12h13M3 16h13" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Bento Stats -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
      <!-- Total Items -->
      <div class="group bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col justify-between hover:border-brand-200 transition-all duration-500 overflow-hidden relative">
        <div class="absolute -right-8 -top-8 w-32 h-32 bg-brand-50 rounded-full group-hover:scale-150 transition-transform duration-700 ease-out z-0 opacity-50"></div>
        <div class="relative z-10">
          <div class="w-14 h-14 rounded-2xl bg-brand-100 text-brand-600 flex items-center justify-center border border-brand-200 mb-6 group-hover:rotate-6 transition-transform">
            <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>
          </div>
          <p class="text-xs font-black text-slate-400 uppercase tracking-[0.2em] mb-2">{{ $t('admin.menu.totalItems') }}</p>
          <h3 class="text-5xl font-black text-slate-900 tracking-tighter">{{ menuItems.length }}</h3>
        </div>
      </div>

      <!-- Categories -->
      <div class="group bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col justify-between hover:border-amber-200 transition-all duration-500 overflow-hidden relative">
        <div class="absolute -right-8 -top-8 w-32 h-32 bg-amber-50 rounded-full group-hover:scale-150 transition-transform duration-700 ease-out z-0 opacity-50"></div>
        <div class="relative z-10">
          <div class="w-14 h-14 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center border border-amber-200 mb-6 group-hover:rotate-6 transition-transform">
            <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6z" /></svg>
          </div>
          <p class="text-xs font-black text-slate-400 uppercase tracking-[0.2em] mb-2">{{ $t('admin.menu.totalCategories') }}</p>
          <h3 class="text-5xl font-black text-slate-900 tracking-tighter">{{ availableCategories.length }}</h3>
        </div>
      </div>

      <!-- Stock Alerts -->
      <div class="group bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/50 flex flex-col justify-between hover:border-rose-200 transition-all duration-500 overflow-hidden relative">
        <div v-if="!isProPlan" class="absolute inset-0 bg-white/80 backdrop-blur-[4px] z-20 flex flex-col items-center justify-center p-6 text-center">
          <div class="bg-slate-900 text-white text-[10px] font-black px-3 py-1.5 rounded-full shadow-xl uppercase tracking-widest mb-3 animate-bounce">PRO ONLY</div>
          <NuxtLink :to="localePath('/admin/upgrade')" class="text-xs font-bold text-slate-600 underline decoration-slate-300 hover:text-brand-600 transition-colors">{{ $t('admin.menu.proPlanRequiredStock') }}</NuxtLink>
        </div>
        <div class="absolute -right-8 -top-8 w-32 h-32 bg-rose-50 rounded-full group-hover:scale-150 transition-transform duration-700 ease-out z-0 opacity-50"></div>
        <div class="relative z-10">
          <div class="w-14 h-14 rounded-2xl bg-rose-100 text-rose-600 flex items-center justify-center border border-rose-200 mb-6 group-hover:rotate-6 transition-transform">
            <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          </div>
          <p class="text-xs font-black text-slate-400 uppercase tracking-[0.2em] mb-2">{{ $t('admin.menu.outOfStock') }}</p>
          <h3 class="text-5xl font-black text-slate-900 tracking-tighter">{{ isProPlan ? menuItems.filter(i => i.stockEnabled && i.stockQuantity <= 0).length : '0' }}</h3>
        </div>
      </div>
    </div>

    <!-- Main Content Container -->
    <div class="bg-white rounded-[2.5rem] border border-slate-100 shadow-2xl shadow-slate-200/60 overflow-hidden">
      <!-- Desktop Table (Hidden on Mobile) -->
      <div class="hidden md:block overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/50 border-b border-slate-100 text-slate-400 text-[10px] font-black uppercase tracking-[0.2em]">
              <th class="px-8 py-6 w-24">{{ $t('admin.menu.columns.image') }}</th>
              <th class="px-8 py-6">{{ $t('admin.menu.columns.name') }}</th>
              <th class="px-8 py-6">{{ $t('admin.menu.columns.category') }}</th>
              <th class="px-8 py-6">{{ $t('admin.menu.columns.price') }}</th>
              <th class="px-8 py-6">{{ $t('admin.menu.columns.stock') }}</th>
              <th class="px-8 py-6 text-right">{{ $t('admin.menu.columns.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <!-- Loading -->
            <template v-if="loading">
              <tr v-for="i in 5" :key="i" class="animate-pulse">
                <td class="px-8 py-6"><div class="w-14 h-14 bg-slate-100 rounded-2xl"></div></td>
                <td class="px-8 py-6"><div class="w-48 h-5 bg-slate-100 rounded-lg mb-2"></div><div class="w-32 h-3 bg-slate-50 rounded-lg"></div></td>
                <td class="px-8 py-6"><div class="w-24 h-8 bg-slate-50 rounded-xl"></div></td>
                <td class="px-8 py-6"><div class="w-16 h-6 bg-slate-100 rounded-lg"></div></td>
                <td class="px-8 py-6"><div class="w-12 h-8 bg-slate-100 rounded-xl"></div></td>
                <td class="px-8 py-6"><div class="w-20 h-10 bg-slate-100 rounded-xl ml-auto"></div></td>
              </tr>
            </template>

            <!-- Real Data -->
            <tr v-for="item in filteredMenu" :key="item.id" class="hover:bg-slate-50/70 transition-all group">
              <td class="px-8 py-5">
                <div v-if="item.image" class="w-14 h-14 rounded-2xl bg-slate-100 overflow-hidden shadow-sm group-hover:shadow-md group-hover:scale-105 transition-all">
                  <NuxtImg :src="item.image" :alt="item.name" format="webp" class="w-full h-full object-cover" loading="lazy" />
                </div>
                <div v-else class="w-14 h-14 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-300">
                  <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
                </div>
              </td>
              <td class="px-8 py-5">
                <div class="font-bold text-slate-900 text-lg group-hover:text-brand-600 transition-colors">{{ item.name }}</div>
                <div class="text-xs font-medium text-slate-400 mt-0.5 line-clamp-1 opacity-70 group-hover:opacity-100">{{ item.description || 'No description' }}</div>
              </td>
              <td class="px-8 py-5">
                <div class="flex flex-wrap gap-1.5">
                  <span class="px-3 py-1.5 rounded-xl text-[10px] font-black bg-slate-100 text-slate-600 border border-slate-200 uppercase tracking-wider">
                    {{ $te(`menu.categories.${item.category}`) ? $t(`menu.categories.${item.category}`) : item.category }}
                  </span>
                </div>
              </td>
              <td class="px-8 py-5 font-black text-slate-900">
                {{ formatPrice(item.price) }}
              </td>
              <td class="px-8 py-5">
                <div v-if="item.stockEnabled">
                  <span class="px-3 py-1.5 rounded-xl text-xs font-black min-w-[3rem] inline-block text-center shadow-sm"
                    :class="item.stockQuantity > 0 ? 'bg-emerald-50 text-emerald-600 border border-emerald-100' : 'bg-rose-50 text-rose-600 border border-rose-100'">
                    {{ item.stockQuantity }}
                  </span>
                </div>
                <span v-else class="text-slate-300 font-bold">-</span>
              </td>
              <td class="px-8 py-5 text-right">
                <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 translate-x-2 group-hover:translate-x-0 transition-all duration-300">
                  <button @click="openModal(item)" class="p-3 bg-white text-slate-400 hover:text-brand-600 hover:bg-brand-50 border border-slate-100 hover:border-brand-200 rounded-2xl shadow-sm hover:shadow transition-all">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                  </button>
                  <button @click="confirmDelete(item)" class="p-3 bg-white text-slate-400 hover:text-rose-600 hover:bg-rose-50 border border-slate-100 hover:border-rose-200 rounded-2xl shadow-sm hover:shadow transition-all">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile Cards (Visible on Mobile) -->
      <div class="md:hidden divide-y divide-slate-50">
        <div v-for="item in filteredMenu" :key="item.id" class="p-5 active:bg-slate-50 transition-colors">
          <div class="flex items-center gap-4">
            <div v-if="item.image" class="w-20 h-20 rounded-2xl bg-slate-100 overflow-hidden shrink-0 shadow-sm">
              <NuxtImg :src="item.image" :alt="item.name" format="webp" class="w-full h-full object-cover" loading="lazy" />
            </div>
            <div v-else class="w-20 h-20 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-300 shrink-0">
              <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex justify-between items-start mb-1">
                <h4 class="font-bold text-slate-900 truncate pr-2">{{ item.name }}</h4>
                <div class="font-black text-brand-600 text-sm">{{ formatPrice(item.price) }}</div>
              </div>
              <div class="flex items-center gap-2 mb-3">
                <span class="px-2 py-0.5 rounded-lg text-[9px] font-black bg-slate-100 text-slate-500 uppercase tracking-widest">{{ item.category }}</span>
                <span v-if="item.stockEnabled" class="text-[10px] font-bold" :class="item.stockQuantity > 0 ? 'text-emerald-500' : 'text-rose-500'">
                  {{ item.stockQuantity }} {{ $t('admin.menu.columns.stock') }}
                </span>
              </div>
              <div class="flex gap-2">
                <button @click="openModal(item)" class="flex-1 py-2 bg-slate-100 text-slate-600 rounded-xl text-xs font-bold hover:bg-brand-50 hover:text-brand-600 transition-colors">
                  {{ $t('admin.common.edit') }}
                </button>
                <button @click="confirmDelete(item)" class="px-3 py-2 bg-rose-50 text-rose-600 rounded-xl text-xs font-bold">
                   <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="!loading && filteredMenu.length === 0" class="py-32 px-8 text-center flex flex-col items-center justify-center">
        <div class="w-32 h-32 bg-slate-50 rounded-full flex items-center justify-center text-slate-200 mb-8 border border-slate-100">
          <svg class="w-16 h-16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" /></svg>
        </div>
        <h3 class="text-2xl font-black text-slate-900 mb-3">{{ $t('admin.menu.emptyState') }}</h3>
        <p class="text-slate-500 font-medium max-w-sm mb-8">Henüz hiç ürün eklenmemiş veya arama kriterlerinize uygun sonuç bulunamadı.</p>
        <button @click="openModal()" class="px-8 py-3.5 bg-brand-600 text-white font-bold rounded-2xl hover:bg-brand-700 transition-all shadow-xl shadow-brand-500/25">
          {{ $t('admin.menu.addItem') }}
        </button>
      </div>
    </div>

    <!-- MODERN MODAL: Add/Edit Item -->
    <div v-if="isModalOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-md transition-opacity duration-300" @click="closeModal"></div>
      
      <div class="relative w-full max-w-2xl bg-white rounded-[2.5rem] shadow-[0_32px_64px_-16px_rgba(0,0,0,0.2)] overflow-hidden flex flex-col max-h-[90vh] animate-in fade-in zoom-in slide-in-from-bottom-8 duration-500 ease-out border border-slate-100">
        <!-- Modal Header -->
        <div class="px-8 py-7 border-b border-slate-100 flex items-center justify-between bg-white relative z-10">
          <div>
            <h3 class="text-2xl font-black text-slate-900 tracking-tight">
              {{ isEditing ? $t('admin.menu.modal.editTitle') : $t('admin.menu.modal.addTitle') }}
            </h3>
            <p class="text-slate-400 text-sm font-medium mt-0.5">Ürün detaylarını aşağıdan düzenleyin.</p>
          </div>
          <button @click="closeModal" class="p-3 bg-slate-50 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-2xl transition-all">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        <!-- Modal Body -->
        <div class="p-8 overflow-y-auto grow space-y-8 bg-slate-50/30">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Language Groups -->
            <div class="space-y-4">
               <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">TÜRKÇE İÇERİK</label>
               <div class="space-y-4 bg-white p-6 rounded-3xl border border-slate-100 shadow-sm">
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-1.5">{{ $t('admin.menu.modal.name') }}</label>
                    <input v-model="form.name" type="text" class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-medium" />
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-1.5">{{ $t('admin.menu.modal.description') }}</label>
                    <textarea v-model="form.description" rows="3" class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-medium resize-none"></textarea>
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-1.5">{{ $t('admin.menu.modal.price') }} (TRY)</label>
                    <div class="relative">
                       <input v-model.number="form.price" type="number" step="0.01" class="w-full pl-10 pr-4 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-black" />
                       <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-bold">₺</span>
                    </div>
                  </div>
               </div>
            </div>

            <div class="space-y-4">
               <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest px-1">ENGLISH CONTENT</label>
               <div class="space-y-4 bg-white p-6 rounded-3xl border border-slate-100 shadow-sm">
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-1.5">{{ $t('admin.menu.modal.name') }}</label>
                    <input v-model="form.nameEn" type="text" class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-medium" />
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-1.5">{{ $t('admin.menu.modal.description') }}</label>
                    <textarea v-model="form.descriptionEn" rows="3" class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-medium resize-none"></textarea>
                  </div>
                  <div>
                    <label class="block text-sm font-bold text-slate-700 mb-1.5">{{ $t('admin.menu.modal.price') }} (USD)</label>
                    <div class="relative">
                       <input v-model.number="form.priceUsd" type="number" step="0.01" class="w-full pl-10 pr-4 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-black" />
                       <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-bold">$</span>
                    </div>
                  </div>
               </div>
            </div>

            <!-- Categorization & Settings -->
            <div class="md:col-span-2 grid grid-cols-1 md:grid-cols-3 gap-6">
               <div class="bg-white p-6 rounded-3xl border border-slate-100 shadow-sm md:col-span-1">
                 <label class="block text-sm font-bold text-slate-700 mb-2">{{ $t('admin.menu.modal.category') }} *</label>
                 <input v-model="form.category" list="catList" class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
                 <datalist id="catList">
                    <option v-for="c in availableCategories" :key="c" :value="c" />
                 </datalist>
               </div>
               <div class="bg-white p-6 rounded-3xl border border-slate-100 shadow-sm md:col-span-1">
                 <label class="block text-sm font-bold text-slate-700 mb-2">{{ $t('admin.menu.modal.subcategory') }}</label>
                 <input v-model="form.subcategory" list="subList" class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
                 <datalist id="subList">
                    <option v-for="s in availableSubcategories" :key="s" :value="s" />
                 </datalist>
               </div>
               <div class="bg-white p-6 rounded-3xl border border-slate-100 shadow-sm md:col-span-1">
                 <label class="block text-sm font-bold text-slate-700 mb-2">{{ $t('admin.menu.modal.sortOrder') }}</label>
                 <input v-model.number="form.sortOrder" type="number" class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition-all" />
               </div>
            </div>

            <!-- Visuals -->
            <div class="md:col-span-2 bg-white p-6 rounded-3xl border border-slate-100 shadow-sm">
                <label class="block text-sm font-bold text-slate-700 mb-3">{{ $t('admin.menu.modal.image') }} (URL)</label>
                <div class="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
                   <div class="w-24 h-24 rounded-2xl bg-slate-50 border border-slate-100 shrink-0 overflow-hidden">
                      <img v-if="form.image" :src="form.image" class="w-full h-full object-cover" />
                      <div v-else class="w-full h-full flex items-center justify-center text-slate-300">
                         <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
                      </div>
                   </div>
                   <input v-model="form.image" type="text" placeholder="https://..." class="w-full px-5 py-3.5 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all font-medium" />
                </div>
            </div>

            <!-- Features -->
            <div class="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Stock Control -->
                <div class="relative bg-white p-6 rounded-3xl border border-slate-100 shadow-sm group/feat">
                   <div v-if="!isProPlan" class="absolute inset-0 bg-white/70 backdrop-blur-sm z-10 flex flex-col items-center justify-center rounded-3xl">
                      <span class="text-[10px] font-black bg-slate-900 text-white px-3 py-1 rounded-full mb-2">PRO</span>
                      <NuxtLink :to="localePath('/admin/upgrade')" class="text-xs font-bold text-slate-600 underline decoration-slate-200">Kilitli</NuxtLink>
                   </div>
                   <div class="flex items-center justify-between mb-4">
                      <div class="flex items-center gap-3">
                         <div class="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center border border-emerald-100">
                            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
                         </div>
                         <span class="font-bold text-slate-700">{{ $t('admin.menu.modal.stockEnabled') }}</span>
                      </div>
                      <label class="relative inline-flex items-center cursor-pointer">
                        <input type="checkbox" v-model="form.stockEnabled" class="sr-only peer" :disabled="!isProPlan">
                        <div class="w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-brand-500"></div>
                      </label>
                   </div>
                   <p class="text-[10px] text-slate-400 font-medium mb-4 leading-tight">{{ $t('admin.menu.stockEnabledDesc') }}</p>
                   <input v-if="form.stockEnabled" v-model.number="form.stockQuantity" type="number" class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl font-black text-center" />
                </div>

                <!-- Story Feature -->
                <div class="relative bg-white p-6 rounded-3xl border border-slate-100 shadow-sm group/feat">
                   <div v-if="!isPaidPlan" class="absolute inset-0 bg-white/70 backdrop-blur-sm z-10 flex flex-col items-center justify-center rounded-3xl">
                      <span class="text-[10px] font-black bg-amber-500 text-white px-3 py-1 rounded-full mb-2">PAID</span>
                      <NuxtLink :to="localePath('/admin/upgrade')" class="text-xs font-bold text-slate-600 underline decoration-slate-200">Kilitli</NuxtLink>
                   </div>
                   <div class="flex items-center justify-between">
                      <div class="flex items-center gap-3">
                         <div class="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center border border-amber-100">
                            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-7.714 2.143L11 21l-2.286-6.857L1 12l7.714-2.143L11 3z" /></svg>
                         </div>
                         <div>
                            <span class="font-bold text-slate-700 block">{{ $t('admin.menu.modal.isFeatured') }}</span>
                            <span class="text-[10px] text-slate-400 font-medium">{{ $t('admin.menu.isFeaturedDesc') }}</span>
                         </div>
                      </div>
                      <label class="relative inline-flex items-center cursor-pointer">
                        <input type="checkbox" v-model="form.isFeatured" class="sr-only peer" :disabled="!isPaidPlan">
                        <div class="w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-amber-500"></div>
                      </label>
                   </div>
                </div>
            </div>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="px-8 py-7 bg-white border-t border-slate-100 flex justify-center gap-4 shrink-0 relative z-10">
          <button @click="closeModal" class="px-8 py-4 text-sm font-black text-slate-500 hover:text-slate-700 bg-slate-50 hover:bg-slate-100 rounded-[1.5rem] transition-all">
            {{ $t('admin.common.cancel') }}
          </button>
          <button @click="saveItem" class="px-10 py-4 text-sm font-black bg-brand-600 text-white rounded-[1.5rem] hover:bg-brand-700 hover:shadow-2xl hover:shadow-brand-500/30 active:scale-95 transition-all disabled:opacity-50" :disabled="saving">
            {{ saving ? $t('admin.common.saving') : $t('admin.common.save') }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- MODERN MODAL: Category Sort -->
    <div v-if="isCategorySortModalOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-md transition-opacity duration-300" @click="isCategorySortModalOpen = false"></div>
      
      <div class="relative w-full max-w-md bg-white rounded-[2.5rem] shadow-2xl overflow-hidden flex flex-col max-h-[80vh] animate-in fade-in zoom-in slide-in-from-bottom-8 duration-500">
        <div class="px-8 py-7 border-b border-slate-100 flex flex-col bg-white">
            <h3 class="text-2xl font-black text-slate-900 tracking-tight">{{ $t('admin.menu.categorySort.title') }}</h3>
            <p class="text-slate-400 text-sm font-medium mt-0.5">{{ $t('admin.menu.categorySort.subtitle') }}</p>
        </div>
        
        <div class="p-6 overflow-y-auto grow space-y-3 bg-slate-50/30">
            <div 
              v-for="(cat, index) in sortingCategories" 
              :key="cat" 
              class="flex items-center justify-between p-5 bg-white border border-slate-100 rounded-2xl group hover:border-brand-300 hover:shadow-lg hover:shadow-slate-200/50 transition-all cursor-move"
            >
              <div class="flex items-center gap-4">
                <span class="w-8 h-8 rounded-xl bg-slate-50 flex items-center justify-center text-[10px] font-black text-slate-400 border border-slate-100">{{ index + 1 }}</span>
                <span class="font-bold text-slate-700">{{ te(`menu.categories.${cat}`) ? $t(`menu.categories.${cat}`) : cat }}</span>
              </div>
              <div class="flex items-center gap-1">
                <button @click="moveCategory(index, -1)" :disabled="index === 0" class="p-2 text-slate-300 hover:text-brand-600 disabled:opacity-10 transition-all"><svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 15l7-7 7 7" /></svg></button>
                <button @click="moveCategory(index, 1)" :disabled="index === sortingCategories.length - 1" class="p-2 text-slate-300 hover:text-brand-600 disabled:opacity-10 transition-all"><svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" /></svg></button>
              </div>
            </div>
        </div>

        <div class="px-8 py-7 bg-white border-t border-slate-100 flex justify-center gap-4 shrink-0">
          <button @click="isCategorySortModalOpen = false" class="px-8 py-4 text-sm font-black text-slate-500 hover:text-slate-700 bg-slate-50 hover:bg-slate-100 rounded-2xl transition-all">
            {{ $t('admin.common.cancel') }}
          </button>
          <button @click="saveCategoryOrder" class="px-8 py-4 text-sm font-black bg-brand-600 text-white rounded-2xl hover:bg-brand-700 transition-all shadow-lg shadow-brand-500/25" :disabled="savingOrder">
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
const localePath = useLocalePath()
const { fetchJson } = useApi()
const orderStore = useOrderStore()
const uiStore = useUiStore()
const authStore = useAuthStore()
const tenant = useTenant()
const isProPlan = tenant.isProPlan
const isPaidPlan = tenant.isPaidPlan
const { loadTenantConfig } = tenant

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
  isFeatured: false,
})

// Configurable categories for the form
const availableCategories = computed(() => {
  const set = new Set<string>()
  menuItems.value.forEach(item => {
    if (item.category) set.add(item.category)
  })
  return Array.from(set)
})

const availableSubcategories = computed(() => {
  const set = new Set<string>()
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
    uiStore.error(t(errorMessage));
  } finally {
    saving.value = false
  }
}

async function confirmDelete(item: any) {
  uiStore.confirm({
    title: t('admin.common.delete'),
    message: t('admin.menu.deleteConfirm'),
    isDanger: true,
    onConfirm: async () => {
      try {
        await fetchJson(`/api/menu/${item.id}`, { method: 'DELETE' })
        await loadMenu()
        uiStore.success(t('admin.common.deleted'))
      } catch (e: any) {
        uiStore.error(e?.message || 'Delete failed')
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
