import { useAuthStore } from '~/app/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
  // Sadece /admin/* ve /super/* sayfalarını koruma altına alıyoruz
  // /admin sayfası (login) hariç tutulmalı
  const localePath = useLocalePath()
  const isProtected = (to.path.startsWith(localePath('/admin/')) && to.path !== localePath('/admin')) || to.path.startsWith(localePath('/super'))
  const authStore = useAuthStore()

  if (isProtected && !authStore.user) {
    return navigateTo(localePath('/admin'))
  }
})
