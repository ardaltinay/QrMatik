import { useAuthStore } from '~/app/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
  console.log('[Auth Middleware Run] Path:', to.path)
  // Gerçek senaryoda (Cookie tabanlı auth) SSR'da çalışır.
  // Mock ortamımızda localStorage kullandığımız için client-side'da kontrol ediyoruz.
  if (process.client) {
    // Sadece /admin/* ve /super/* sayfalarını koruma altına alıyoruz
    // /admin sayfası (login) hariç tutulmalı
    const isProtected = (to.path.startsWith('/admin/') && to.path !== '/admin') || to.path.startsWith('/super')
    const authStore = useAuthStore()

    if (isProtected && !authStore.user) {
      return navigateTo('/admin')
    }
  }
})
