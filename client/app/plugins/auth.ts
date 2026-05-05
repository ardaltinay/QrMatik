export default defineNuxtPlugin(async (nuxtApp) => {
  const authStore = useAuthStore()
  
  // App initialization: Restore session from HttpOnly cookie
  if (import.meta.client) {
    await authStore.init()
  }
})
