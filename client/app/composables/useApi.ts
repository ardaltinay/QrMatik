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

    if (options.body && typeof options.body === 'string' && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json'
    }

    // Add timeout handling
    const controller = new AbortController()
    const id = setTimeout(() => controller.abort(), 10000) // 10s timeout

    try {
      const response = await fetch(path, { 
        ...options, 
        headers,
        credentials: 'include', // Important for HttpOnly cookies
        signal: controller.signal
      })
      clearTimeout(id)
      return response
    } catch (err) {
      clearTimeout(id)
      throw err
    }
  }

  /**
   * Fetch JSON with automatic error handling and token refresh.
   */
  async function fetchJson<T = any>(path: string, options: RequestInit = {}): Promise<T> {
    const authStore = useAuthStore()

    if (!options.headers) {
      options.headers = {}
    }
    if (!(options.headers as Record<string, string>)['Content-Type'] && options.body) {
      (options.headers as Record<string, string>)['Content-Type'] = 'application/json'
    }

    let res = await apiFetch(path, options)

    // Token expired (401) and not already a login/refresh request
    if (res.status === 401 && !path.includes('/api/auth/')) {
      try {
        // Attempt refresh (browser sends qm_refresh_token cookie automatically)
        const refreshRes = await apiFetch('/api/auth/refresh', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({})
        })

        if (refreshRes.ok) {
          // Retry original request with new token (cookie updated by server)
          res = await apiFetch(path, options)
        } else {
          authStore.logout()
          throw new Error('Session expired')
        }
      } catch (err) {
        authStore.logout()
        throw err
      }
    }

    if (!res.ok) {
      let errorData: any = null
      const text = await res.text().catch(() => '')
      try {
        errorData = JSON.parse(text)
      } catch (e) {
        // ignore
      }
      
      const errorMessage = errorData?.message || text || 'Unknown error'
      const err = new Error(errorMessage)
      ;(err as any).data = errorData
      ;(err as any).status = res.status
      throw err
    }

    // Handle empty successful responses (204/205 or empty body)
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
