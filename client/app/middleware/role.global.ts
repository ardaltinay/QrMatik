import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware(async (to) => {
  const path = to.path

  // DEBUG LOG: Middleware tetikleniyorsa bu yazı konsolda görünmeli
  console.log('[Middleware Run] Path:', path)

  const isProtected = path.startsWith('/admin') || path.startsWith('/super')
  if (!isProtected) return

  const authStore = useAuthStore()

  // Eğer henüz init edilmemişse veya kullanıcı yoksa sunucudan kontrol etmeyi BEKLE
  if (!authStore.user) {
    await authStore.init()
  }
  
  // Hala kullanıcı yoksa (giriş yapmamışsa)
  if (!authStore.user) {
    if (path === '/admin') return
    return navigateTo('/admin')
  }

  const role = authStore.normalizeRole(authStore.user.role)
  console.log('[Middleware Auth] Path:', path, 'FINAL_ROLE:', role)

  // Super Admin rotaları koruması
  if (path.startsWith('/super')) {
    if (role !== 'superadmin') {
      console.error('[Middleware Access Denied] Superadmin access required. Current role:', role)
      return abortNavigation(createError({ statusCode: 403, statusMessage: 'Forbidden' }))
    }
    console.log('[Middleware Access Granted] Superadmin access granted for:', path)
    return
  }

  // Super Admin Paneline Yönlendirme (Eğer /admin'e gelirse)
  if (role === 'superadmin' && (path === '/admin' || path === '/admin/')) {
    return navigateTo('/super/tenants', { replace: true })
  }

  // Spesifik rol kısıtlamaları (Admin paneli içinde)
  if (path.startsWith('/admin')) {
    // Mutfak Kontrolü
    if (role.includes('kitchen')) {
      if (path !== '/admin/kitchen') {
        console.log('[Role Redirect] Kitchen forcing to /admin/kitchen')
        return navigateTo('/admin/kitchen', { replace: true })
      }
      return
    }

    // Bar Kontrolü
    if (role.includes('bar')) {
      if (path !== '/admin/bar') {
        console.log('[Role Redirect] Bar forcing to /admin/bar')
        return navigateTo('/admin/bar', { replace: true })
      }
      return
    }

    // Kasiyer Kontrolü
    if (role.includes('cashier')) {
      if (path !== '/admin/cashier') {
        return navigateTo('/admin/cashier', { replace: true })
      }
      return
    }

    // Admin Kontrolü (Sadece kök sayfadaysa orders'a at)
    if (role.includes('admin') && (path === '/admin' || path === '/admin/')) {
      return navigateTo('/admin/orders', { replace: true })
    }
  }
})
