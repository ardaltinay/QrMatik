import { getRequestHeader } from 'h3'

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
    // 1. Subdomain (Works on both client and server)
    let host = ''
    if (import.meta.server) {
      const event = useRequestEvent()
      host = getRequestHeader(event, 'host') || ''
    } else {
      host = window.location.hostname || ''
    }

    if (host) {
      // Remove port if present
      host = host.split(':')[0]
      
      if (host.endsWith('.localhost')) {
        const dot = host.indexOf('.')
        if (dot > 0) return host.slice(0, dot)
      } else {
        const hostParts = host.split('.')
        // Handle cases like test.feasymenu.com (length > 2)
        if (hostParts.length > 2 && hostParts[0] !== 'www') {
          return hostParts[0] || null
        }
      }
    }

    // 2. Query params (Server-side compatible)
    let search = ''
    if (import.meta.server) {
      const event = useRequestEvent()
      search = useRequestURL(event).search
    } else {
      search = window.location.search
    }

    const params = new URLSearchParams(search)
    const q = params.get('tenant') || params.get('code') || params.get('restaurant') || params.get('restaurantCode')
    if (q) return q

    // 3. Path (Server-side compatible)
    let path = ''
    if (import.meta.server) {
      const event = useRequestEvent()
      path = useRequestURL(event).pathname
    } else {
      path = window.location.pathname
    }

    const parts = path.split('/')
    const idx = parts.indexOf('r')
    if (idx >= 0 && parts.length > idx + 1) return parts[idx + 1] || null

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
  async function initTenant() {
    const detected = detectTenant()
    if (detected) {
      tenantCode.value = detected
      await loadTenantConfig(detected)
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
