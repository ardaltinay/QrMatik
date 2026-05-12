import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
  const localePath = useLocalePath()
  
  // Normalize path by removing locale prefix if it exists for simpler checking
  const pathWithoutLocale = to.path.replace(/^\/(en|tr)(\/|$)/, '/')
  
  const isProtected = (pathWithoutLocale.startsWith('/admin/') && pathWithoutLocale !== '/admin') || pathWithoutLocale.startsWith('/super')
  const authStore = useAuthStore()

  if (isProtected && !authStore.user) {
    return navigateTo(localePath('/admin'))
  }
})
