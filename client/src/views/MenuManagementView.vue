<template>
  <div>
    <h2 class="text-xl font-semibold mb-4">Menü Yönetimi</h2>
    <div class="mb-4 grid grid-cols-1 sm:grid-cols-5 gap-2">
      <input v-model="name" placeholder="Ürün adı" class="w-full p-2 rounded-lg border shadow-sm focus:ring-2 focus:ring-brand-200" />
      <input v-model.number="price" type="number" step="0.01" min="0" placeholder="Fiyat" class="w-full p-2 rounded-lg border shadow-sm focus:ring-2 focus:ring-brand-200" />
      <BaseSelect v-model="category" :options="categoryOptionItems" />
      <BaseSelect v-model="subcategory" :options="subOptionItems(category)" />
      <button @click="addItem" class="px-3 py-2 bg-brand-500 text-white rounded-lg shadow">Ekle</button>
    </div>
    <div class="text-sm text-amber-600 mb-3" v-if="statuses._new">{{ statuses._new }}</div>

    <div class="grid gap-3 grid-cols-1 md:grid-cols-2 xl:grid-cols-3">
      <div v-for="it in store.menu" :key="it.id" class="p-3 bg-white border rounded-lg flex flex-col gap-3 shadow-sm overflow-hidden">
        <div class="flex items-start justify-between gap-3">
          <div>
            <div class="font-medium" v-if="!editing[it.id]">{{ it.name }}</div>
            <div v-else class="flex flex-col gap-2">
              <input v-model="drafts[it.id].name" class="w-full p-2 rounded border" />
              <div class="grid grid-cols-3 gap-2">
                <input v-model.number="drafts[it.id].price" type="number" step="0.01" min="0" class="p-2 rounded border" />
                <BaseSelect v-model="drafts[it.id].category" :options="categoryOptionItems" />
                <BaseSelect v-model="drafts[it.id].subcategory" :options="subOptionItems(drafts[it.id].category)" />
              </div>
            </div>
            <div class="text-sm text-gray-500" v-if="!editing[it.id]">{{ it.category }} · {{ it.price }}₺</div>
          </div>
          <div class="flex items-center gap-2">
            <button v-if="!editing[it.id]" @click="startEdit(it)" class="px-2 py-1 border rounded">Düzenle</button>
            <template v-else>
              <button @click="saveEdit(it.id)" class="px-2 py-1 bg-green-600 text-white rounded">Kaydet</button>
              <button @click="cancelEdit(it.id)" class="px-2 py-1 border rounded">Vazgeç</button>
            </template>
          </div>
        </div>
        <div class="flex items-center gap-4 flex-wrap">
          <img v-if="it.image" :src="it.image" alt="" class="w-24 h-16 object-cover rounded border" />
          <div class="flex items-center gap-2 flex-wrap">
            <input type="file" accept="image/*" @change="e => onFileChange(it.id, e)" class="block w-full sm:w-64" />
            <button @click="upload(it.id)" :disabled="!pendingFiles[it.id] || !isUuid(it.id)" class="px-3 py-1 bg-brand-500 text-white rounded disabled:opacity-50">Yükle</button>
            <button @click="askRemove(it)" class="px-3 py-1 bg-red-50 text-red-600 rounded-lg">Sil</button>
          </div>
          <div class="text-sm text-gray-500 basis-full" v-if="statuses[it.id]">{{ statuses[it.id] }}</div>
          <div class="text-xs text-amber-600" v-else-if="!isUuid(it.id)">Önce ürünü sunucuda oluşturup kaydedin (geçersiz ID).</div>
        </div>
      </div>
    </div>

    <!-- confirm delete modal -->
    <div v-if="confirm.open" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-4 w-full max-w-sm shadow-xl">
        <div class="font-semibold mb-2">Silme Onayı</div>
        <div class="text-sm text-gray-600">“{{ confirm.name }}” ürününü silmek istediğinize emin misiniz?</div>
        <div class="mt-4 flex justify-end gap-2">
          <button @click="cancelRemoveConfirm" class="px-3 py-1 border rounded">Vazgeç</button>
          <button @click="confirmRemove" class="px-3 py-1 bg-red-600 text-white rounded">Sil</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useOrderStore } from '@/stores/orderStore'
import { apiFetch } from '@/utils/api'
import { primaryLabel, subLabel } from '@/utils/format'
import BaseSelect from '@/components/BaseSelect.vue'

export default {
  components: { BaseSelect },
  setup() {
    const store = useOrderStore()
    const name = ref('')
    const price = ref(0)
    const category = ref('')
    const subcategory = ref('')
    const pendingFiles = reactive({})
    const statuses = reactive({})
    const editing = reactive({})
    const drafts = reactive({})
    const confirm = reactive({ open: false, id: null, name: '' })

  onMounted(async () => { if (!store.menuLoaded) { try { await store.loadMenu() } catch (e) { /* ignore */ } } })

    const defaultCategoryOptions = ['food','drink']
    const defaultSubs = { food: ['starter','main','dessert','pizza','salad'], drink: ['soda','wine','alcoholic','non-alcoholic'] }
    const tenantCfg = (() => { try { const raw = localStorage.getItem('qm_tenant_cfg'); return raw ? JSON.parse(raw) : null } catch { return null } })()
    const cfg = (() => { try { return tenantCfg && tenantCfg.config ? JSON.parse(tenantCfg.config) : null } catch { return null } })()
    const categoryOptions = computed(() => (cfg && Array.isArray(cfg.categories) && cfg.categories.length ? cfg.categories : defaultCategoryOptions))
    function subOptions(cat) {
      if (!cat) return []
      if (cfg && cfg.subs && Array.isArray(cfg.subs[cat])) return cfg.subs[cat]
      return defaultSubs[cat] || []
    }
    const categoryOptionItems = computed(() => categoryOptions.value.map(c => ({ value: c, label: primaryLabel(c) })))
    function subOptionItems(cat) { return subOptions(cat).map(s => ({ value: s, label: subLabel(s) })) }

    function addItem() {
      if (!name.value) return
      if (!category.value) { statuses['_new'] = 'Kategori seçin'; setTimeout(()=>{statuses['_new']=''},2000); return }
      if (price.value == null || Number(price.value) < 0) { statuses['_new'] = 'Fiyat 0 veya üzeri olmalı'; setTimeout(()=>{statuses['_new']=''},2000); return }
      const body = { name: name.value, price: price.value, category: category.value, subcategory: subcategory.value }
      apiFetch('/api/menu', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
        .then(async res => {
          if (!res.ok) throw new Error('Ürün oluşturma başarısız')
          const dto = await res.json()
          // normalize into store shape
          const newItem = {
            id: dto.id,
            name: dto.name,
            price: typeof dto.price === 'number' ? dto.price : Number(dto.price || 0),
            category: dto.category || 'Genel',
            primary: (dto.category || '').toLowerCase().includes('drink') ? 'drink' : 'food',
            sub: dto.subcategory || 'main',
            image: dto.image || ''
          }
          store.menu.push(newItem)
          // persist updated menu cache per tenant
          try {
            const tenant = localStorage.getItem('qm_tenant') || 'default'
            const payload = { ts: Date.now(), menu: store.menu }
            localStorage.setItem('qm_menu_cache_' + tenant, JSON.stringify(payload))
          } catch (e) { /* ignore */ }
          name.value = ''
          price.value = 0
          category.value = ''
          subcategory.value = ''
        })
        .catch(async (e) => {
          try {
            const { useUiStore } = await import('@/stores/uiStore')
            useUiStore().toastError(e.message || 'Hata')
          } catch (e) { /* ignore */ }
        })
    }

    async function remove(id) {
      if (!isUuid(id)) { store.menu = store.menu.filter(m => m.id !== id); return }
      statuses[id] = 'Siliniyor...'
      try {
        const res = await apiFetch(`/api/menu/${id}`, { method: 'DELETE' })
        if (!res.ok && res.status !== 204 && res.status !== 200 && res.status !== 404) throw new Error('Silme başarısız')
        store.menu = store.menu.filter(m => m.id !== id)
        try {
          const tenant = localStorage.getItem('qm_tenant') || 'default'
          const payload = { ts: Date.now(), menu: store.menu }
          localStorage.setItem('qm_menu_cache_' + tenant, JSON.stringify(payload))
        } catch (e) { /* ignore */ }
        statuses[id] = 'Silindi'
      } catch (e) {
        statuses[id] = 'Hata: ' + (e.message || e)
      } finally {
        setTimeout(() => { statuses[id] = '' }, 2000)
      }
    }

    function askRemove(it) { confirm.open = true; confirm.id = it.id; confirm.name = it.name }
    function cancelRemoveConfirm() { confirm.open = false; confirm.id = null; confirm.name = '' }
    async function confirmRemove() { if (!confirm.id) return; await remove(confirm.id); cancelRemoveConfirm() }

    function onFileChange(id, ev) {
      const f = ev.target.files && ev.target.files[0]
      if (f) pendingFiles[id] = f
    }

    async function upload(id) {
      const f = pendingFiles[id]
      if (!f) return
      statuses[id] = 'Yükleniyor...'
      try {
        const form = new FormData()
        form.append('file', f)
        const res = await (await import('@/utils/api')).apiFetch(`/api/menu/${id}/image`, { method: 'POST', body: form })
        if (!res.ok) {
          let msg = 'Yükleme başarısız'
          try {
            const text = await res.text()
            try { const j = JSON.parse(text); msg = j.error || text } catch { msg = text || msg }
          } catch { /* ignore */ }
          throw new Error(msg)
        }
        const data = await res.json()
        const it = store.menu.find(x => x.id === id)
        if (it && data.image) it.image = data.image
        statuses[id] = 'Yüklendi'
        delete pendingFiles[id]
      } catch (e) {
        console.error(e)
        statuses[id] = 'Hata: ' + (e.message || e)
        try {
          const { useUiStore } = await import('@/stores/uiStore')
          const ui = useUiStore()
          ui.toastError(statuses[id])
        } catch { /* ignore */ }
      }
      setTimeout(() => { statuses[id] = '' }, 3000)
    }

    function isUuid(v) {
      return typeof v === 'string' && /^[0-9a-fA-F-]{36}$/.test(v)
    }

    function startEdit(it) {
      editing[it.id] = true
      drafts[it.id] = { name: it.name, price: it.price, category: it.category || it.primary, subcategory: it.sub || it.subcategory }
    }

    function cancelEdit(id) {
      editing[id] = false
      delete drafts[id]
    }

    async function saveEdit(id) {
      const d = drafts[id]
      if (!d) return
      statuses[id] = 'Kaydediliyor...'
      try {
        const res = await apiFetch(`/api/menu/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name: d.name, price: d.price, category: d.category, subcategory: d.subcategory }) })
        if (!res.ok) throw new Error('Güncelleme başarısız')
        const dto = await res.json()
        const idx = store.menu.findIndex(x => x.id === id)
        if (idx >= 0) {
          store.menu[idx] = {
            ...store.menu[idx],
            name: dto.name,
            price: typeof dto.price === 'number' ? dto.price : Number(dto.price || 0),
            category: dto.category || store.menu[idx].category,
            primary: (dto.category || '').toLowerCase().includes('drink') ? 'drink' : 'food',
            sub: dto.subcategory || store.menu[idx].sub
          }
        }
        // persist cache
        try {
          const tenant = localStorage.getItem('qm_tenant') || 'default'
          const payload = { ts: Date.now(), menu: store.menu }
          localStorage.setItem('qm_menu_cache_' + tenant, JSON.stringify(payload))
  } catch (e) { /* ignore */ }
        statuses[id] = 'Güncellendi'
        cancelEdit(id)
      } catch (e) {
        statuses[id] = 'Hata: ' + (e.message || e)
  try { const { useUiStore } = await import('@/stores/uiStore'); useUiStore().toastError(statuses[id]) } catch (e2) { /* ignore */ }
      } finally {
        setTimeout(() => { statuses[id] = '' }, 2500)
      }
    }

    return { store, name, price, category, subcategory, addItem, remove, onFileChange, upload, pendingFiles, statuses, isUuid, editing, drafts, startEdit, cancelEdit, saveEdit, categoryOptions, subOptions, categoryOptionItems, subOptionItems, confirm, askRemove, cancelRemoveConfirm, confirmRemove }
  }
}
</script>
