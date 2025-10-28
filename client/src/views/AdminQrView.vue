<template>
  <div>
    <h2 class="text-lg font-semibold mb-3">QR Kodları</h2>
    <div class="mb-2 text-sm text-gray-600">Tenant kodu (boş bırakılırsa tüm tenant'lar için):</div>
    <input v-model="qrTenant" placeholder="tenant kodu" class="w-full p-2 mb-3 border rounded" />
    <div class="flex gap-2 mb-3">
      <button @click="downloadQrs" class="px-3 py-2 bg-brand-500 text-white rounded">PDF İndir</button>
      <button @click="fillTenantFromStorage" class="px-3 py-2 border rounded">LocalStorage'dan Doldur</button>
    </div>
    <div v-if="qrStatus" class="mt-2 text-sm text-gray-600">{{ qrStatus }}</div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { apiFetch } from '@/utils/api'

export default {
  name: 'AdminQrView',
  setup() {
    const qrTenant = ref(localStorage.getItem('qm_tenant') || '')
    const qrStatus = ref('')

    async function downloadQrs() {
      qrStatus.value = 'İndiriliyor...'
      try {
        const tenant = qrTenant.value || undefined
        const q = tenant ? `?tenant=${encodeURIComponent(tenant)}` : ''
        const resp = await apiFetch(`/api/qr/bulk${q}`, { method: 'GET' })
        if (!resp.ok) {
          let msg = 'Sunucudan PDF alınamadı'
          try {
            const text = await resp.text()
            if (text) {
              try { const j = JSON.parse(text); msg = j.error || j.message || msg } catch { msg = text || msg }
            }
          } catch { /* ignore */ }
          throw new Error(msg)
        }
        const blob = await resp.blob()
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `qr-codes${tenant ? '-' + tenant : ''}.pdf`
        document.body.appendChild(a)
        a.click()
        a.remove()
        window.URL.revokeObjectURL(url)
        qrStatus.value = 'İndirme tamamlandı'
      } catch (e) {
        console.error(e)
        qrStatus.value = 'Hata: ' + (e.message || e)
      }
      setTimeout(() => qrStatus.value = '', 4000)
    }

    function fillTenantFromStorage() {
      try {
        const t = localStorage.getItem('qm_tenant')
        if (t) qrTenant.value = t
      } catch (e) { /* ignore */ }
    }

    return { qrTenant, qrStatus, downloadQrs, fillTenantFromStorage }
  }
}
</script>

<style scoped>
/* minimal */
</style>
