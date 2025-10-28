import { defineStore } from 'pinia'
import { ref } from 'vue'
import { apiFetch, fetchJson } from '@/utils/api'

// Simple demo store: in-memory; in real app replace with API calls/websockets
export const useOrderStore = defineStore('order', () => {
  const menu = ref([])
  const menuLoaded = ref(false)
  let menuLoading = null

  // Load menu from persistent cache (per-tenant) to avoid redundant fetches across reloads
  try {
    const tenant = (typeof localStorage !== 'undefined' && localStorage.getItem('qm_tenant')) || 'default'
    const cached = (typeof localStorage !== 'undefined') ? localStorage.getItem('qm_menu_cache_' + tenant) : null
    if (cached) {
      try {
        const obj = JSON.parse(cached)
        if (obj && Array.isArray(obj.menu)) {
          menu.value = obj.menu
          menuLoaded.value = true
        }
      } catch { /* ignore parse issues */ }
    }
  } catch { /* ignore storage issues */ }

  const cart = ref([]) // {itemId, qty}
  const orders = ref([])
  const ordersById = ref({}) // cache by id -> normalized order
  let nextOrderId = 1000
  // BroadcastChannel for simple cross-tab realtime demo
  let bc = null
  if (typeof window !== 'undefined' && 'BroadcastChannel' in window) {
    bc = new BroadcastChannel('orders')
    bc.onmessage = (ev) => {
      const { type, payload } = ev.data || {}
      if (type === 'create') {
        // avoid duplicating if already have
        if (!orders.value.find(o => o.id === payload.id)) orders.value.push(payload)
      }
      if (type === 'update') {
        const o = orders.value.find(x => x.id === payload.id)
        if (o) o.status = payload.status
      }
    }
  }

  // load menu from server (with fallback)
  async function loadMenu() {
    // already loaded
    if (menuLoaded.value || (Array.isArray(menu.value) && menu.value.length > 0)) return
    // if a request is in-flight, await it
    if (menuLoading) { try { await menuLoading } catch { /* ignore */ } return }
    // start single in-flight request
    menuLoading = (async () => {
      try {
        const data = await fetchJson('/api/menu')
        menu.value = (Array.isArray(data) ? data : []).map((it) => ({
          id: it.id,
          name: it.name,
          price: typeof it.price === 'number' ? it.price : Number(it.price || 0),
          category: it.category || 'Genel',
          primary: it.primary ?? it.primaryType ?? (it.category && it.category.toLowerCase().includes('drink') ? 'drink' : 'food'),
          sub: it.sub || it.subcategory || 'main',
          image: it.image || 'https://picsum.photos/seed/menu' + (it.id || Math.random()) + '/400/240'
        }))
        menuLoaded.value = true
        // persist normalized menu per tenant
        try {
          const tenant = (typeof localStorage !== 'undefined' && localStorage.getItem('qm_tenant')) || 'default'
          const payload = { ts: Date.now(), menu: menu.value }
          localStorage.setItem('qm_menu_cache_' + tenant, JSON.stringify(payload))
        } catch { /* ignore storage issues */ }
      } catch (err) {
        console.debug('Failed to fetch menu, using local fallback', err)
        menu.value = [
          { id: 1, name: 'Margherita', price: 45, category: 'Pizza', primary: 'food', sub: 'main', image: 'https://picsum.photos/seed/1/400/240' },
          { id: 2, name: 'Pepperoni', price: 55, category: 'Pizza', primary: 'food', sub: 'main', image: 'https://picsum.photos/seed/2/400/240' },
          { id: 3, name: 'Caesar Salad', price: 30, category: 'Salad', primary: 'food', sub: 'starter', image: 'https://picsum.photos/seed/3/400/240' }
        ]
        menuLoaded.value = true
        // persist fallback too (keeps UI consistent offline)
        try {
          const tenant = (typeof localStorage !== 'undefined' && localStorage.getItem('qm_tenant')) || 'default'
          const payload = { ts: Date.now(), menu: menu.value }
          localStorage.setItem('qm_menu_cache_' + tenant, JSON.stringify(payload))
        } catch { /* ignore storage issues */ }
      } finally {
        menuLoading = null
      }
    })()
    try { await menuLoading } catch { /* ignore */ }
  }

  // load recent orders from server (admin/dev)
  async function loadOrders() {
    try {
      const data = await fetchJson('/api/orders')
      const mapped = data.map(o => ({
        id: o.id,
        table: o.tableCode || o.table || 'guest',
        items: (Array.isArray(o.lines) ? o.lines : []).map(l => ({ itemId: l.itemId, qty: l.quantity, name: l.name, price: l.price })),
        status: (o.status || 'NEW').toLowerCase(),
        createdAt: o.createdAt || o.createdTime || new Date().toISOString(),
        total: (typeof o.total === 'number' ? o.total : Number(o.total || 0))
      }))
      orders.value = mapped
      for (const o of mapped) {
        ordersById.value[String(o.id)] = o
      }
      // set nextOrderId higher than max
      const maxId = orders.value.reduce((m, x) => Math.max(m, x.id || 0), 0)
      nextOrderId = maxId + 1
    } catch (err) {
      console.debug('Failed to fetch orders from server', err)
      // leave orders empty as fallback
    }
  }

  // not auto-loading to avoid redundant requests; views call loadMenu lazily when needed

  function addToCart(itemId) {
    const existing = cart.value.find(i => i.itemId === itemId)
    if (existing) existing.qty++
    else cart.value.push({ itemId, qty: 1 })
  }

  function removeFromCart(itemId) {
    cart.value = cart.value.filter(i => i.itemId !== itemId)
  }

  function clearCart() {
    cart.value = []
  }

  function createOrder(table = null) {
    if (cart.value.length === 0) return null
    const items = cart.value.map(i => ({ ...i }))
    // enrich with snapshot of name and price to avoid menu fetches on tracking page
    const enrichedItems = items.map(i => {
      const mi = menu.value.find(m => m.id === i.itemId)
      return Object.assign({}, i, { name: mi?.name, price: mi?.price })
    })
    // calculate total locally (snapshot)
    const calcTotal = enrichedItems.reduce((sum, it) => sum + (Number(it.price || 0) * (it.qty || 1)), 0)
    const order = {
      id: nextOrderId++,
      table: table || null,
      items: enrichedItems,
      status: 'new', // new -> preparing -> ready -> served
      createdAt: new Date().toISOString(),
      total: calcTotal
    }
    orders.value.push(order)
    // try to POST to server; include existing sessionId if present
    try {
  const payload = { status: order.status, createdAt: order.createdAt, lines: enrichedItems.map(i => ({ itemId: i.itemId, quantity: i.qty, price: i.price, name: i.name })) }
  // tableCode sadece geçerli bir masa kodu varsa gönderilsin
  if (order.table && order.table !== 'guest') payload.tableCode = order.table
      const existingSession = (() => { try { return localStorage.getItem('qm_order_session') } catch { return null } })()
      if (existingSession) payload.sessionId = existingSession

      apiFetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }).then(async r => {
        if (!r.ok) {
          console.debug('server order create failed', r.status)
          return
        }
        const created = await r.json()
        const idx = orders.value.findIndex(o => o.id === order.id)
        if (idx >= 0) {
          const serverTotal = (typeof created.total === 'number' ? created.total : Number(created.total || 0))
          orders.value[idx] = { ...orders.value[idx], id: created.id, sessionId: created.sessionId || orders.value[idx].sessionId, total: serverTotal || orders.value[idx].total }
        }
        // cache server version from DTO lines
        try {
          const parsedItems = (Array.isArray(created.lines) ? created.lines : []).map(l => ({ itemId: l.itemId, qty: l.quantity, name: l.name, price: l.price }))
          const normalized = { id: created.id, table: created.tableCode || created.table || order.table || 'guest', items: parsedItems, status: (created.status || order.status).toLowerCase(), createdAt: created.createdAt || created.createdTime || order.createdAt, total: (typeof created.total === 'number' ? created.total : Number(created.total || 0)) }
          ordersById.value[String(created.id)] = normalized
        } catch { /* ignore */ }
        // En son sipariş ID'sini gerçek sunucu ID'siyle güncelle
        try { localStorage.setItem('qm_last_order', String(created.id)) } catch { /* ignore */ }
        const sess = r.headers.get('X-Order-Session') || (created && created.sessionId)
        if (sess) {
          try { localStorage.setItem('qm_order_session', sess) } catch { /* ignore */ }
          // persist expiry from header or DTO if present
          try {
            const expHeader = r.headers.get('X-Order-Expires')
            const exp = expHeader || (created && (created.sessionExpiresAt || created.sessionExpiry || created.expiresAt))
            if (exp) localStorage.setItem('qm_order_session_expires', String(exp))
          } catch { /* ignore */ }
          window.dispatchEvent(new CustomEvent('qm:orderSession', { detail: { sessionId: sess, orderId: created.id } }))
        }
      }).catch(e => console.debug('order create network error', e))
    } catch (e) { console.debug('order create sync error', e) }
    clearCart()
    // persist last order id so customer can return to it
  try { localStorage.setItem('qm_last_order', String(order.id)) } catch { console.debug('localStorage not available') }
  // also dispatch a storage-like event for same-tab listeners
  try { window.dispatchEvent(new CustomEvent('qm:lastOrder', { detail: { id: order.id } })) } catch { /* ignore */ }
    if (bc) bc.postMessage({ type: 'create', payload: order })
    return order
  }

  // load all orders for given sessionId
  async function loadSessionOrders(sessionId) {
    if (!sessionId) return []
    try {
      const res = await apiFetch('/api/orders/session/' + encodeURIComponent(sessionId))
      if (res.status === 410) {
        // session expired on server: clear locally and notify
        try {
          localStorage.removeItem('qm_order_session')
          localStorage.removeItem('qm_order_session_expires')
        } catch { /* ignore */ }
        try { window.dispatchEvent(new CustomEvent('qm:orderSessionExpired', { detail: { sessionId } })) } catch { /* ignore */ }
        return []
      }
      if (!res.ok) {
        console.debug('Failed to fetch session orders', res.status)
        return []
      }
      const data = await res.json()
      const mapped = data.map(o => ({ id: o.id, table: o.tableCode || o.table, items: (Array.isArray(o.lines) ? o.lines : []).map(l => ({ itemId: l.itemId, qty: l.quantity, name: l.name, price: l.price })), status: (o.status || '').toLowerCase(), createdAt: o.createdAt || o.createdTime, total: (typeof o.total === 'number' ? o.total : Number(o.total || 0)) }))
      for (const o of mapped) { ordersById.value[String(o.id)] = o }
      return mapped
    } catch (err) {
      console.debug('Failed to fetch session orders', err)
      return []
    }
  }

  async function updateOrderStatus(orderId, status) {
    // optimistic update for snappy UI
    const normalized = String(status || '').toLowerCase()
    const o = orders.value.find(x => x.id === orderId)
    if (o) o.status = normalized
    const key = String(orderId)
    if (ordersById.value[key]) ordersById.value[key].status = normalized
    if (bc) bc.postMessage({ type: 'update', payload: { id: orderId, status: normalized } })

    // persist to server
    try {
      const payload = { status: normalized.toUpperCase() }
      const updated = await fetchJson(`/api/orders/${encodeURIComponent(orderId)}/status`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      const serverStatus = String(updated?.status || normalized).toLowerCase()
      const o2 = orders.value.find(x => x.id === orderId)
      if (o2) o2.status = serverStatus
      if (ordersById.value[key]) ordersById.value[key].status = serverStatus
      if (serverStatus === 'payment_completed') {
        // Only clear session if no other active orders remain in this session
        try {
          const sid = (typeof localStorage !== 'undefined') ? localStorage.getItem('qm_order_session') : null
          if (sid) {
            const r = await apiFetch('/api/orders/session/' + encodeURIComponent(sid))
            if (r.status === 410) {
              // server says expired
              try { localStorage.removeItem('qm_order_session'); localStorage.removeItem('qm_order_session_expires') } catch { /* ignore */ }
            } else if (r.ok) {
              const data = await r.json()
              const hasActive = Array.isArray(data) && data.some(o => String(o.status || '').toLowerCase() !== 'payment_completed')
              if (!hasActive) {
                try { localStorage.removeItem('qm_order_session'); localStorage.removeItem('qm_order_session_expires') } catch { /* ignore */ }
              }
            }
          }
        } catch { /* ignore */ }
      }
    } catch {
      // revert UI on failure
      const prev = ordersById.value[key]?.status || o?.status || 'new'
      if (o) o.status = prev
      if (ordersById.value[key]) ordersById.value[key].status = prev
      // error toast already shown by fetchJson
      throw new Error('status update failed')
    }
  }

  function getOrderById(id) {
    const key = String(id)
    return ordersById.value[key] || orders.value.find(o => String(o.id) === key)
  }

  return {
    menu,
    menuLoaded,
    cart,
    orders,
    ordersById,
    loadMenu,
    loadOrders,
    loadSessionOrders,
    addToCart,
    removeFromCart,
    clearCart,
    createOrder,
    updateOrderStatus,
    getOrderById
  }
})
