<template>
  <div class="menu">
    <h1 class="text-2xl font-semibold mb-4">Menü</h1>

    <!-- filtreler (üstte ve sağa hizalı) -->
    <div class="flex gap-2 mb-3 justify-start flex-wrap">
      <button v-for="p in primaries" :key="p" @click="primary = p" :class="['px-3 py-1 rounded-full', primary === p ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700']">{{ tPrimary(p) }}</button>
    </div>

    <div class="flex gap-2 mb-4 flex-wrap justify-start">
      <button @click="sub = 'all'" :class="['px-2 py-1 rounded', sub === 'all' ? 'bg-indigo-50 text-indigo-700' : 'bg-gray-100 text-gray-700']">Tümü</button>
      <button v-for="s in subsForPrimary" :key="s" @click="sub = s" :class="['px-2 py-1 rounded', sub === s ? 'bg-indigo-50 text-indigo-700' : 'bg-gray-100 text-gray-700']">{{ tSub(s) }}</button>
    </div>

    <div class="grid">
      <MenuItemCard
        v-for="item in filtered"
        :key="item.id"
        :item="item"
        @add="store.addToCart(item.id)"
      />
    </div>
  <CartDrawer :mobileOpen="mobileOpen" @update:mobileOpen="v => mobileOpen = v" />

    <!-- mobile sticky mini cart bar -->
    <div v-if="store.cart.length" class="md:hidden fixed left-4 right-4 bottom-4 z-40">
      <button @click="mobileOpen = true" class="w-full bg-indigo-600 text-white rounded-full px-4 py-3 flex items-center justify-between shadow-lg">
        <div class="font-medium">Sepet ({{ store.cart.length }})</div>
        <div class="text-sm">Öğe: {{ totalItems }}</div>
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useOrderStore } from '@/stores/orderStore'
import { primaryLabel, subLabel } from '@/utils/format'
import MenuItemCard from '@/components/MenuItemCard.vue'
import CartDrawer from '@/components/CartDrawer.vue'

export default {
  name: 'MenuView',
  components: { MenuItemCard, CartDrawer },
  setup() {
    const store = useOrderStore()
    const mobileOpen = ref(false)
    if (!store.menuLoaded) { void store.loadMenu() }

    const totalItems = computed(() => store.cart.reduce((s,i)=>s+i.qty,0))

    const primaries = computed(() => {
      const set = new Set(store.menu.map(m => m.primary))
      return Array.from(set)
    })

  const primary = ref(primaries.value[0] || 'food')
    const sub = ref('all')

    const subsForPrimary = computed(() => {
      const list = store.menu.filter(m => m.primary === primary.value).map(m => m.sub)
      return Array.from(new Set(list))
    })

    const filtered = computed(() => store.menu.filter(m => m.primary === primary.value && (sub.value === 'all' || m.sub === sub.value)))

    // read optional table code from URL (e.g. ?table=Table-01 or ?t=Table-01) and persist
    onMounted(() => {
      try {
        const params = new URLSearchParams(window.location.search)
        const table = params.get('table') || params.get('t')
        if (table) {
          try { localStorage.setItem('qm_table_code', table) } catch (e) { /* ignore */ }
        }
      } catch (e) { /* ignore */ }
    })

    // when menu loads, ensure selected primary is valid
    watch(primaries, (list) => {
      if (Array.isArray(list) && list.length && !list.includes(primary.value)) {
        primary.value = list[0]
      }
    }, { immediate: true })

    // Etiketler utils'ten
    function tPrimary(p) { return primaryLabel(p) }
    function tSub(s) { return subLabel(s) }

    return { store, mobileOpen, totalItems, primaries, primary, sub, subsForPrimary, filtered, tPrimary, tSub }
  }
}
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
</style>