import { useTenant } from '~/composables/useTenant'
import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
  if (process.client) {
    // Sadece ana sayfa (/) için çalışsın
    if (to.path === '/') {
      const { detectTenant } = useTenant()
      const tenantCode = detectTenant()
      const localePath = useLocalePath()

      // Eğer bir tenant subdomain'indeysek
      if (tenantCode) {
        const authStore = useAuthStore()

        // Tenant'ın ana sayfası admin sayfasıdır. 
        // /admin yönlendirmesi zaten login değilse login formunu, login ise siparişleri (veya yetkiye göre sayfayı) gösterir.
        return navigateTo(localePath('/admin'))
      }
    }
  }
})
