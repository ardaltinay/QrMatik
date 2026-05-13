import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware((to, from) => {
  if (process.client) {
    const authStore = useAuthStore()
    const localePath = useLocalePath()

    if (!authStore.user) return

    // Örnek: authStore.user.plan 'free', 'pro', veya 'enterprise' olabilir.
    // Eğer store'da plan yoksa 'free' varsayıyoruz.
    const plan = authStore.user?.tenant?.subscriptionPlan || 'FREE'

    // Kısıtlı sayfalar (Sadece Pro ve Enterprise planlar erişebilir)
    const restrictedForFree: string[] = []

    if (plan === 'free' && restrictedForFree.includes(to.path)) {
      // Plan yetersizse Yükseltme (Upgrade) sayfasına yönlendir
      return navigateTo(localePath('/admin/upgrade'))
    }
  }
})
