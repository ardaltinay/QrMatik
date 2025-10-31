export async function apiFetch(path, opts = {}) {
  const headers = Object.assign({}, opts.headers || {});
  try {
    const tenant = localStorage.getItem("qm_tenant");
    if (tenant) headers["X-Tenant"] = tenant;
    const sessionRaw = localStorage.getItem("qm_session");
    if (sessionRaw) {
      try {
        const sess = JSON.parse(sessionRaw);
        if (sess && sess.token) headers["Authorization"] = "Bearer " + sess.token;
      } catch {
        /* ignore */
      }
    }
  } catch {
    /* ignore */
  }
  const res = await fetch(path, Object.assign({}, opts, { headers }));
  return res;
}

// opts can include:
// - silentError: boolean -> suppress generic error toast messages
// - suppressAuth: boolean -> suppress 401/403 session-expired handling (useful for login endpoints)
export async function fetchJson(path, opts = {}) {
  const res = await apiFetch(
    path,
    Object.assign({}, opts, {
      headers: Object.assign({ "Content-Type": "application/json" }, opts.headers || {}),
    }),
  );
  if (!res.ok) {
    const silent = !!opts.silentError;
    const suppressAuth = !!opts.suppressAuth;
    // Handle unauthorized: clear session and redirect to admin login (unless silent)
    if ((res.status === 401 || res.status === 403) && !silent && !suppressAuth) {
      try {
        const { useAuthStore } = await import("@/stores/authStore");
        const auth = useAuthStore();
        auth.logout();
      } catch { /* ignore */ }
      try {
        const { default: router } = await import("@/router");
        if (typeof window !== "undefined") {
          const isAdminPath = window.location && String(window.location.pathname || "").startsWith("/admin");
          if (isAdminPath) router.push({ name: "admin" });
        }
      } catch { /* ignore */ }
      try {
        const { useUiStore } = await import("@/stores/uiStore");
        const ui = useUiStore();
        ui.toastError("Oturum süreniz doldu. Lütfen tekrar giriş yapın.");
      } catch { /* ignore */ }
    }
    let msg = "İstek başarısız: " + res.status;
    try {
      const text = await res.text();
      if (text) {
        try {
          const j = JSON.parse(text);
          msg = j.error || j.message || text;
        } catch {
          msg = text;
        }
      }
    } catch { /* ignore */ }
    if (!silent) {
      try {
        const { useUiStore } = await import("@/stores/uiStore");
        const ui = useUiStore();
        ui.toastError(msg);
      } catch { /* ignore */ }
    }
    throw new Error(msg);
  }
  // Gracefully handle empty or non-JSON successful responses (e.g., 200 with no body, 204 No Content)
  try {
    const text = await res.text();
    if (!text || !text.trim()) return {};
    try {
      return JSON.parse(text);
    } catch {
      // Not JSON, but success: return empty object to avoid runtime errors
      return {};
    }
  } catch {
    return {};
  }
}
