import { useTenant } from '~/composables/useTenant'
import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
  if (process.client) {
    const localePath = useLocalePath()
    const pathWithoutLocale = to.path.replace(/^\/(en|tr)(\/|$)/, '/')

    // Sadece ana sayfa (/) için çalışsın
    if (pathWithoutLocale === '/') {
      const { detectTenant } = useTenant()
      const tenantCode = detectTenant()
      
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
