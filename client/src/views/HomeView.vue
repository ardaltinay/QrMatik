<template>
  <section class="min-h-screen flex items-center">
    <div class="container mx-auto px-6 py-20">
      <div class="grid md:grid-cols-2 gap-12 items-center">
        <div>
          <h1 class="text-4xl sm:text-5xl font-bold text-gray-900 mb-4">QrMatik — Mobil Sipariş ve Yönetim</h1>
          <p class="text-gray-600 mb-6">QR ile menüye hızlı eriş, mobilden sipariş ver; mutfak ve bar ekibi anında bildirim alır. Hızlı, güvenilir ve mobil öncelikli.</p>
          <div class="flex gap-3">
            <router-link v-if="!showMyOrders" to="/menu" class="flex-1 min-w-0 text-center px-3 sm:px-4 py-2 sm:py-3 text-sm sm:text-base bg-indigo-600 text-white rounded-md shadow hover:bg-indigo-700">Menüyü Görüntüle</router-link>
            <router-link v-else :to="orderDetailLink" class="flex-1 min-w-0 text-center px-3 sm:px-4 py-2 sm:py-3 text-sm sm:text-base bg-yellow-500 text-white rounded-md shadow hover:bg-yellow-600">Siparişlerimi Gör</router-link>
            <router-link v-if="isAdmin" to="/admin" class="flex-1 min-w-0 text-center px-3 sm:px-4 py-2 sm:py-3 text-sm sm:text-base border border-gray-200 rounded-md hover:bg-gray-50">Admin Panel</router-link>
          </div>
        </div>
        <div class="bg-white rounded-xl shadow p-6">
          <h3 class="font-semibold mb-4">Popüler Ürünler</h3>
          <div class="grid grid-cols-2 gap-4">
            <div class="p-3 border rounded">
              <div class="font-medium">Margherita</div>
              <div class="text-sm text-gray-500">Pizza · 45₺</div>
            </div>
            <div class="p-3 border rounded">
              <div class="font-medium">Pepperoni</div>
              <div class="text-sm text-gray-500">Pizza · 55₺</div>
            </div>
            <div class="p-3 border rounded">
              <div class="font-medium">Caesar Salad</div>
              <div class="text-sm text-gray-500">Salata · 30₺</div>
            </div>
            <div class="p-3 border rounded">
              <div class="font-medium">Coca-Cola</div>
              <div class="text-sm text-gray-500">İçecek · 10₺</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { apiFetch } from '@/utils/api'
import { useAuthStore } from '@/stores/authStore'

export default {
  name: 'HomeView',
  setup() {
    const orderSession = ref(null)
    const showMyOrders = ref(false)
    const auth = useAuthStore()
    const isAdmin = computed(() => auth.user && auth.user.role === 'admin')

    function readOrderSession() {
      try { orderSession.value = localStorage.getItem('qm_order_session') } catch { orderSession.value = null }
    }

    const lastOrderId = ref(null)
    function readLastOrder() {
      try { lastOrderId.value = localStorage.getItem('qm_last_order') } catch { lastOrderId.value = null }
    }

    const orderDetailLink = computed(() => {
      if (orderSession.value) return { name: 'my-orders', params: { sessionId: orderSession.value } }
      return { path: '/menu' }
    })

    function isLocalSessionActive() {
      try {
        const sid = orderSession.value
        if (!sid) return false
        const exp = localStorage.getItem('qm_order_session_expires')
        if (!exp) return true // expiry yoksa iyimser davran (flicker'ı önle)
        const now = Date.now()
        const expTs = Date.parse(exp)
        if (!Number.isFinite(expTs)) return true
        return expTs > now
      } catch { return false }
    }

    async function evaluateSessionActivity() {
      // check session existence
      if (!orderSession.value) return
      // check expiry from localStorage
      try {
        const exp = localStorage.getItem('qm_order_session_expires')
        if (!exp) return
        const now = Date.now()
        const expTs = Date.parse(exp)
        if (!Number.isFinite(expTs) || expTs <= now) {
          // expired -> clear locally
          try { localStorage.removeItem('qm_order_session'); localStorage.removeItem('qm_order_session_expires') } catch { /* ignore */ }
          orderSession.value = null
          showMyOrders.value = false
          return
        }
      } catch { /* ignore */ }
      // fetch session orders to check active ones
      try {
        const res = await apiFetch('/api/orders/session/' + encodeURIComponent(orderSession.value))
        if (res.status === 410) {
          // expired on server -> clear
          try { localStorage.removeItem('qm_order_session'); localStorage.removeItem('qm_order_session_expires') } catch { /* ignore */ }
          orderSession.value = null
          showMyOrders.value = false
          return
        }
        if (!res.ok) { showMyOrders.value = false; return }
        const data = await res.json()
        const hasActive = Array.isArray(data) && data.some(o => String(o.status || '').toLowerCase() !== 'payment_completed')
        showMyOrders.value = !!hasActive
      } catch { showMyOrders.value = false }
    }

    function onOrderSession(ev) {
      try {
        const sid = ev && ev.detail && ev.detail.sessionId
        if (sid) orderSession.value = sid
        const oid = ev && ev.detail && ev.detail.orderId
        if (oid) {
          try { localStorage.setItem('qm_last_order', String(oid)) } catch { /* ignore */ }
        }
      } catch { /* ignore */ }
      // event sonrası depodan en güncel değerleri oku
      readOrderSession(); readLastOrder();
      // Önce yerel durumla hızlı karar ver, sonra async doğrula
      showMyOrders.value = isLocalSessionActive()
      evaluateSessionActivity()
    }

    function onStorage(ev) {
      if (!ev) return
      if (ev.key === 'qm_order_session') {
        orderSession.value = ev.newValue
        showMyOrders.value = isLocalSessionActive()
        evaluateSessionActivity()
      }
      if (ev.key === 'qm_last_order') {
        readLastOrder()
      }
    }

    onMounted(() => {
      readOrderSession(); readLastOrder();
      showMyOrders.value = isLocalSessionActive()
      evaluateSessionActivity()
      try { window.addEventListener('qm:orderSession', onOrderSession) } catch { /* ignore */ }
      try { window.addEventListener('storage', onStorage) } catch { /* ignore */ }
    })

    onBeforeUnmount(() => {
      try { window.removeEventListener('qm:orderSession', onOrderSession) } catch { /* ignore */ }
      try { window.removeEventListener('storage', onStorage) } catch { /* ignore */ }
    })

    return { orderSession, isAdmin, orderDetailLink, showMyOrders }
  }
}
</script>
