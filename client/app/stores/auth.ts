import { defineStore } from 'pinia'

interface User {
  username: string
  role: string
  tenantCode?: string
  tenant?: {
    subscriptionPlan: string
    logoUrl?: string
    primaryColor?: string
  }
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const initializing = ref(true)

  const isLoggedIn = computed(() => !!user.value)
  const isAdmin = computed(() => hasRole('admin'))
  const isSuperAdmin = computed(() => hasRole('superadmin'))
  const isKitchen = computed(() => hasRole('kitchen'))
  const isBar = computed(() => hasRole('bar'))
  const isCashier = computed(() => hasRole('cashier'))

  async function init() {
    const { fetchJson } = useApi()
    initializing.value = true
    try {
      // Restore user info from server using the HttpOnly cookie
      const data = await fetchJson('/api/auth/me')
      if (data) {
        user.value = {
          username: data.username,
          role: data.role,
          tenantCode: data.tenantCode,
          tenant: data.tenant
        }
      }
    } catch { 
      // Not logged in or session expired
      user.value = null
    } finally {
      initializing.value = false
    }
  }

  async function login(username: string, password: string, tenantCode?: string) {
    const { fetchJson } = useApi()
    const body: Record<string, string> = { username, password }
    if (tenantCode) body.tenantCode = tenantCode

    const data = await fetchJson('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(body),
    })

    // Server sets cookies, we just update local state
    user.value = {
      username: data.user?.username || username,
      role: data.user?.role,
      tenantCode: data.user?.tenantCode,
      tenant: data.user?.tenant
    }

    return data
  }

  async function logout() {
    const { fetchJson } = useApi()
    
    try {
      // Call server to clear cookies
      await fetchJson('/api/auth/logout', { method: 'POST' })
    } catch { /* ignore */ }

    user.value = null
    // Redirect to login or home using localized path
    const localePath = useLocalePath()
    navigateTo(localePath('/admin'))
  }

  function normalizeRole(role: string): string {
    if (!role) return ''
    return role.toString()
      .replace(/İ/g, 'i')
      .replace(/I/g, 'i')
      .toLocaleLowerCase('en-US') // Force English locale for casing
      .trim()
      .replace(/ı/g, 'i')
  }

  function hasRole(...roles: string[]): boolean {
    if (!user.value?.role) return false
    const normalizedUserRole = normalizeRole(user.value.role)
    return roles.map(r => normalizeRole(r)).includes(normalizedUserRole)
  }

  return {
    user,
    initializing,
    isLoggedIn,
    isAdmin,
    isSuperAdmin,
    isKitchen,
    isBar,
    isCashier,
    init,
    login,
    logout,
    hasRole,
    normalizeRole,
  }
})
