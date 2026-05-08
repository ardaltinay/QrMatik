export default defineNuxtPlugin(async (nuxtApp) => {
  const authStore = useAuthStore()
  
  // App initialization: Restore session from HttpOnly cookie
  await authStore.init()
})
