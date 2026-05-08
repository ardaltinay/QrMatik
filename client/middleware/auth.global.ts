import { useAuthStore } from '~/app/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
  // Sadece /admin/* ve /super/* sayfalarını koruma altına alıyoruz
  // /admin sayfası (login) hariç tutulmalı
  const isProtected = (to.path.startsWith('/admin/') && to.path !== '/admin') || to.path.startsWith('/super')
  const authStore = useAuthStore()

  if (isProtected && !authStore.user) {
    return navigateTo('/admin')
  }
})
