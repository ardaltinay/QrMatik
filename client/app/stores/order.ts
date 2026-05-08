import { defineStore } from 'pinia'

interface CartItem {
  itemId: number
  qty: number
  note: string
}

interface MenuItem {
  id: number
  name: string
  nameEn?: string
  description?: string
  descriptionEn?: string
  price: number
  priceUsd?: number
  category: string
  subcategory?: string
  image?: string
  stockEnabled?: boolean
  stockQuantity?: number
}

interface Order {
  id: number | string
  status: string
  kitchenStatus?: string
  barStatus?: string
  items: any[]
  total: number
  tableCode?: string
  sessionId?: string
  createdAt?: string
  customerName?: string
}

export const useOrderStore = defineStore('order', () => {
  const menu = ref<MenuItem[]>([])
  const cart = ref<CartItem[]>([])
  const orders = ref<Order[]>([])
  const loading = ref(false)

  // ── Menu ────────────────────────────────────────────────
  async function loadMenu() {
    const { fetchJson } = useApi()
    try {
      loading.value = true
      const data = await fetchJson('/api/menu')
      menu.value = Array.isArray(data) ? data : []
    } catch (e) {
      console.warn('Failed to load menu:', e)
    } finally {
      loading.value = false
    }
  }

  function menuItemById(id: number): MenuItem | undefined {
    return menu.value.find(m => m.id === id)
  }

  // ── Cart ────────────────────────────────────────────────
  function addToCart(itemId: number, qty = 1, note = '') {
    const existing = cart.value.find(c => c.itemId === itemId && c.note === note)
    if (existing) {
      existing.qty += qty
    } else {
      cart.value.push({ itemId, qty, note })
    }
  }

  function removeFromCart(itemId: number, note = '') {
    cart.value = cart.value.filter(c => !(c.itemId === itemId && c.note === note))
  }

  function updateQty(itemId: number, note: string, qty: number) {
    const item = cart.value.find(c => c.itemId === itemId && c.note === note)
    if (item) {
      if (qty <= 0) {
        removeFromCart(itemId, note)
      } else {
        item.qty = qty
      }
    }
  }

  function clearCart() {
    cart.value = []
  }

  const cartTotal = computed(() => {
    // We can infer locale from Nuxt/i18n, but inside store it's best to read a reactive locale or cookie
    // Alternatively we can use vue-i18n if we inject it, but pinia stores can access useNuxtApp().$i18n
    const nuxtApp = useNuxtApp()
    const locale = nuxtApp.$i18n ? (nuxtApp.$i18n as any).locale.value : 'tr'
    
    return cart.value.reduce((sum, c) => {
      const m = menuItemById(c.itemId)
      if (!m) return sum
      
      let itemPrice = m.price
      if (locale === 'en' && m.priceUsd != null && m.priceUsd > 0) {
        itemPrice = m.priceUsd
      }
      return sum + (itemPrice * c.qty)
    }, 0)
  })

  const cartCount = computed(() => {
    return cart.value.reduce((sum, c) => sum + c.qty, 0)
  })

  // ── Orders ──────────────────────────────────────────────
  async function createOrder(tableCode: string) {
    if (cart.value.length === 0) return null
    const { fetchJson } = useApi()

    const sessionId = getOrCreateSessionId(tableCode)
    const body = {
      tableCode,
      sessionId,
      lines: cart.value.map(c => ({
        itemId: c.itemId,
        quantity: c.qty,
        note: c.note || undefined,
      })),
    }

    try {
      const order = await fetchJson('/api/orders', {
        method: 'POST',
        body: JSON.stringify(body),
      })

      clearCart()

      // Store session
      if (import.meta.client && order.sessionId) {
        const sessionKey = tableCode ? `qm_session_${tableCode}` : 'qm_order_session'
        localStorage.setItem(sessionKey, order.sessionId)
        localStorage.setItem('qm_order_session', order.sessionId)
        if (order.sessionExpiresAt) {
          localStorage.setItem('qm_order_session_expires', order.sessionExpiresAt)
        }
      }

      return order
    } catch (e) {
      console.error('Failed to create order:', e)
      throw e
    }
  }

  async function getOrder(id: string) {
    const sid = localStorage.getItem('qm_order_session')
    const currentTable = localStorage.getItem('qm_table_code')
    const { fetchJson } = useApi()
    
    try {
      const data = await fetchJson(`/api/orders/${id}`, {
        headers: {
          'X-Session-Id': sid || ''
        }
      })
      
      if (data) {
        // Double check: If this order belongs to a DIFFERENT table than what we have in localStorage, REJECT.
        // (Unless we are in admin mode, but this is the customer-side store method)
        if (currentTable && data.tableCode && data.tableCode !== currentTable) {
          console.error('Order table mismatch!', data.tableCode, 'vs', currentTable)
          return null
        }
        return data
      }
      return null
    } catch (e) {
      console.error('Error fetching order:', e)
      return null
    }
  }

  async function loadOrders() {
    const { fetchJson } = useApi()
    try {
      const data = await fetchJson('/api/orders')
      orders.value = Array.isArray(data) ? data : []
    } catch (e) {
      console.warn('Failed to load orders:', e)
    }
  }

  async function loadSessionOrders(sessionId: string, tableCode?: string) {
    const { fetchJson } = useApi()
    try {
      let url = `/api/orders/session/${encodeURIComponent(sessionId)}`
      if (tableCode) {
        url += `?tableCode=${encodeURIComponent(tableCode)}`
      }
      const data = await fetchJson(url)
      return Array.isArray(data) ? data : []
    } catch (e) {
      console.warn('Failed to load session orders:', e)
      return []
    }
  }

  async function updateOrderStatus(orderId: number | string, status: string, target?: string) {
    const { fetchJson } = useApi()
    try {
      let url = `/api/orders/${orderId}/status`
      if (target) {
        url += `?target=${encodeURIComponent(target)}`
      }
      await fetchJson(url, {
        method: 'PUT',
        body: JSON.stringify({ status }),
      })
      // Refresh
      await loadOrders()
    } catch (e) {
      console.error('Failed to update order status:', e)
      throw e
    }
  }

  async function callWaiter(tableCode: string) {
    const { fetchJson } = useApi()
    const sessionId = getOrCreateSessionId(tableCode)
    try {
      return await fetchJson('/api/orders/call-waiter', {
        method: 'POST',
        body: JSON.stringify({ tableCode, sessionId }),
      })
    } catch (e) {
      console.error('Failed to call waiter:', e)
      throw e
    }
  }

  return {
    menu,
    cart,
    orders,
    loading,
    loadMenu,
    menuItemById,
    addToCart,
    removeFromCart,
    updateQty,
    clearCart,
    cartTotal,
    cartCount,
    createOrder,
    loadOrders,
    loadSessionOrders,
    updateOrderStatus,
    callWaiter,
  }
})

// ── Helpers ──────────────────────────────────────────────
export function getOrCreateSessionId(tableCode?: string): string {
  if (!import.meta.client) return crypto.randomUUID()
  try {
    const key = tableCode ? `qm_session_${tableCode}` : 'qm_order_session'
    let sid = localStorage.getItem(key)
    
    if (!sid) {
      sid = crypto.randomUUID()
      localStorage.setItem(key, sid)
    }
    return sid
  } catch {
    return crypto.randomUUID()
  }
}
