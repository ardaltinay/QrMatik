import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware(async (to) => {
  const path = to.path

  const authStore = useAuthStore()
  const { detectTenant } = useTenant()

  // 1. Tenant Detection (Subdomain aware)
  const tenantCode = detectTenant()

  const localePath = useLocalePath()

  // If we are on a tenant subdomain but at the root, redirect to admin
  // Skip redirect if there is a hash (e.g. #features) so landing page links work
  if (tenantCode && (path === '/' || path === '') && !to.hash) {
    return navigateTo(localePath('/admin'))
  }

  // Normalize path by removing locale prefix if it exists for simpler checking
  const pathWithoutLocale = path.replace(/^\/(en|tr)(\/|$)/, '/')
  const isProtected = pathWithoutLocale.startsWith('/admin') || pathWithoutLocale.startsWith('/super')
  if (!isProtected) return

  // Eğer henüz init edilmemişse veya kullanıcı yoksa sunucudan kontrol etmeyi BEKLE
  if (!authStore.user) {
    await authStore.init()
  }

  // Hala kullanıcı yoksa (giriş yapmamışsa)
  if (!authStore.user) {
    if (path === '/admin' || path === localePath('/admin')) return
    return navigateTo(localePath('/admin'))
  }

  const role = authStore.normalizeRole(authStore.user.role)
  // Super Admin rotaları koruması
  if (path.startsWith('/super')) {
    if (role !== 'superadmin') {
      return abortNavigation(createError({ statusCode: 403, statusMessage: 'Forbidden' }))
    }
    return
  }

  // Super Admin Paneline Yönlendirme (Eğer /admin'e gelirse)
  if (role === 'superadmin' && (path === '/admin' || path === '/admin/' || path === localePath('/admin'))) {
    return navigateTo(localePath('/super/tenants'), { replace: true })
  }

  // Spesifik rol kısıtlamaları (Admin paneli içinde)
  if (path.startsWith('/admin')) {
    // Mutfak Kontrolü
    if (role.includes('kitchen')) {
      if (path !== localePath('/admin/kitchen')) {
        return navigateTo(localePath('/admin/kitchen'), { replace: true })
      }
      return
    }

    // Bar Kontrolü
    if (role.includes('bar')) {
      if (path !== localePath('/admin/bar')) {
        return navigateTo(localePath('/admin/bar'), { replace: true })
      }
      return
    }

    // Kasiyer Kontrolü
    if (role.includes('cashier')) {
      if (path !== localePath('/admin/cashier')) {
        return navigateTo(localePath('/admin/cashier'), { replace: true })
      }
      return
    }

    // Admin Kontrolü (Sadece kök sayfadaysa orders'a at)
    if (role.includes('admin') && (path === '/admin' || path === '/admin/' || path === localePath('/admin'))) {
      return navigateTo(localePath('/admin/orders'), { replace: true })
    }
  }
})
