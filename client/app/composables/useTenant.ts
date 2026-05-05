/**
 * Tenant detection and configuration composable.
 * Detects tenant from subdomain, query params, or path.
 */
export function useTenant() {
  const tenantCode = useState<string | null>('tenantCode', () => null)
  const tenantConfig = useState<Record<string, any> | null>('tenantConfig', () => null)

  /**
   * Detect tenant from current URL context.
   */
  function detectTenant(): string | null {
    if (!import.meta.client) return null

    try {
      // 1. Query params: ?tenant=, ?code=, ?restaurant=
      const params = new URLSearchParams(window.location.search)
      const q = params.get('tenant') || params.get('code') || params.get('restaurant') || params.get('restaurantCode')
      if (q) return q

      // 2. Path: /r/:tenant/...
      const path = window.location.pathname || ''
      const parts = path.split('/')
      const idx = parts.indexOf('r')
      if (idx >= 0 && parts.length > idx + 1) return parts[idx + 1] || null

      // 3. Subdomain
      const host = window.location.hostname || ''
      if (host.endsWith('.localhost')) {
        const dot = host.indexOf('.')
        if (dot > 0) return host.slice(0, dot)
      } else {
        const hostParts = host.split('.')
        if (hostParts.length > 2) return hostParts[0] || null
      }
    } catch { /* ignore */ }

    return null
  }

  const hasTenant = computed(() => !!tenantCode.value)

  const isPaidPlan = computed(() => {
    const plan = String(tenantConfig.value?.plan || '').toUpperCase()
    return plan !== '' && plan !== 'FREE'
  })

  const isProPlan = computed(() => {
    return String(tenantConfig.value?.plan || '').toUpperCase() === 'PRO'
  })

  /**
   * Load tenant config from API.
   */
  async function loadTenantConfig(code: string) {
    const { fetchJson } = useApi()
    try {
      const cfg = await fetchJson(`/api/tenant/config?code=${encodeURIComponent(code)}`)
      tenantConfig.value = cfg
      tenantCode.value = code
      return cfg
    } catch (e) {
      console.warn('Failed to load tenant config:', e)
      return null
    }
  }

  /**
   * Initialize tenant from URL context only.
   */
  function initTenant() {
    if (!import.meta.client) return

    const detected = detectTenant()
    if (detected) {
      tenantCode.value = detected
      loadTenantConfig(detected)
    }
  }

  return {
    tenantCode,
    tenantConfig,
    hasTenant,
    isPaidPlan,
    isProPlan,
    detectTenant,
    loadTenantConfig,
    initTenant,
  }
}
