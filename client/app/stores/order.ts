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

    const sessionId = getOrCreateSessionId()
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

  async function loadOrders() {
    const { fetchJson } = useApi()
    try {
      const data = await fetchJson('/api/orders')
      orders.value = Array.isArray(data) ? data : []
    } catch (e) {
      console.warn('Failed to load orders:', e)
    }
  }

  async function loadSessionOrders(sessionId: string) {
    const { fetchJson } = useApi()
    try {
      const data = await fetchJson(`/api/orders/session/${encodeURIComponent(sessionId)}`)
      return Array.isArray(data) ? data : []
    } catch (e) {
      console.warn('Failed to load session orders:', e)
      return []
    }
  }

  async function updateOrderStatus(orderId: number | string, status: string) {
    const { fetchJson } = useApi()
    try {
      await fetchJson(`/api/orders/${orderId}/status`, {
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
  }
})

// ── Helpers ──────────────────────────────────────────────
function getOrCreateSessionId(): string {
  if (!import.meta.client) return crypto.randomUUID()
  try {
    let sid = localStorage.getItem('qm_order_session')
    if (!sid) {
      sid = crypto.randomUUID()
      localStorage.setItem('qm_order_session', sid)
    }
    return sid
  } catch {
    return crypto.randomUUID()
  }
}
