/**
 * API fetch wrapper for authenticated requests.
 * Automatically attaches tenant header and JWT token via HttpOnly cookies.
 */
export function useApi() {
  const config = useRuntimeConfig()

  /**
   * Raw fetch with credentials support.
   */
  async function apiFetch(path: string, options: RequestInit = {}): Promise<Response> {
    const headers: Record<string, string> = {
      ...(options.headers as Record<string, string> || {}),
    }

    if (import.meta.server) {
      const proxyHeaders = useRequestHeaders(['cookie', 'authorization', 'user-agent'])
      Object.assign(headers, proxyHeaders)
    }

    if (options.body && typeof options.body === 'string' && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json'
    }

    try {
      let fetchUrl = path
      if (import.meta.server && path.startsWith('/')) {
        fetchUrl = `${config.public.apiBase || 'http://127.0.0.1:8080'}${path}`
      }

      const response = await fetch(fetchUrl, {
        ...options,
        headers,
        credentials: 'include'
      })
      return response
    } catch (err) {
      throw err
    }
  }

  /**
   * Fetch JSON with automatic error handling and token refresh.
   */
  async function fetchJson<T = any>(path: string, options: RequestInit = {}): Promise<T> {
    if (!options.headers) {
      options.headers = {}
    }
    if (!(options.headers as Record<string, string>)['Content-Type'] && options.body) {
      (options.headers as Record<string, string>)['Content-Type'] = 'application/json'
    }

    let res = await apiFetch(path, options)

    // Token expired (401) and not already a login/refresh request
    if (res.status === 401 && !path.includes('/api/auth/')) {
      // Temporarily removed authStore call to debug circular dependency
      // const authStore = useAuthStore() 
      try {
        const refreshRes = await apiFetch('/api/auth/refresh', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({})
        })

        if (refreshRes.ok) {
          res = await apiFetch(path, options)
        }
      } catch (err) {
        // authStore.logout()
        throw err
      }
    }

    if (!res.ok) {
      let errorData: any = null
      const text = await res.text().catch(() => '')
      try {
        errorData = JSON.parse(text)
      } catch (e) { }

      // Handle account suspended (403)
      if (res.status === 403 && errorData?.code === 'account_suspended') {
        if (!import.meta.server) {
          // Force redirect to login on client side
          const localePath = useLocalePath();
          window.location.href = localePath('/admin');
        }
      }

      const errorMessage = errorData?.message || text || `Request failed with status ${res.status}`
      const err = new Error(errorMessage)
        ; (err as any).data = errorData
        ; (err as any).status = res.status
      throw err
    }

    if (res.status === 204 || res.status === 205) {
      return undefined as any
    }

    const contentType = res.headers.get('content-type') || ''
    const body = await res.text().catch(() => '')
    if (!body) {
      return undefined as any
    }

    if (contentType.includes('application/json')) {
      return JSON.parse(body)
    }

    return body as any
  }

  return { apiFetch, fetchJson }
}
