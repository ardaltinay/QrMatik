import { useAuthStore } from '~/app/stores/auth'

export default defineNuxtRouteMiddleware((to, from) => {
  if (process.client) {
    const authStore = useAuthStore()
    const localePath = useLocalePath()

    if (!authStore.user) return

    // Örnek: authStore.user.plan 'free', 'pro', veya 'enterprise' olabilir.
    // Eğer store'da plan yoksa 'free' varsayıyoruz.
    const plan = (authStore.user as any).plan || 'free'

    // Kısıtlı sayfalar (Sadece Pro ve Enterprise planlar erişebilir)
    const restrictedForFree = [
      '/admin/reports'
    ]

    if (plan === 'free' && restrictedForFree.includes(to.path)) {
      // Plan yetersizse Yükseltme (Upgrade) sayfasına yönlendir
      return navigateTo(localePath('/admin/upgrade'))
    }
  }
})
