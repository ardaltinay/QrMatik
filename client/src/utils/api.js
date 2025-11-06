export async function apiFetch(path, opts = {}) {
  const headers = Object.assign({}, opts.headers || {});
  const isAuthEndpoint = typeof path === "string" && path.startsWith("/api/auth/");
  try {
    const tenant = localStorage.getItem("qm_tenant");
    // Do NOT send tenant header for auth endpoints to allow superadmin login from apex
    if (tenant && !isAuthEndpoint && !opts.skipTenant) headers["X-Tenant"] = tenant;
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
    // Note: don't auto-logout on 403; keep session and show error instead
    if (res.status === 401 && !silent && !suppressAuth) {
      try {
        const { useAuthStore } = await import("@/stores/authStore");
        const auth = useAuthStore();
        auth.logout();
      } catch {
        /* ignore */
      }
      try {
        const { default: router } = await import("@/router");
        if (typeof window !== "undefined") {
          const isAdminPath =
            window.location && String(window.location.pathname || "").startsWith("/admin");
          if (isAdminPath) router.push({ name: "admin" });
        }
      } catch {
        /* ignore */
      }
      try {
        const { useUiStore } = await import("@/stores/uiStore");
        const ui = useUiStore();
        ui.toastError("Oturum süreniz doldu. Lütfen tekrar giriş yapın.");
      } catch {
        /* ignore */
      }
    }
    // Prefer backend-provided message when available; fallback sensibly
    let msg = "";
    let text = "";
    try {
      text = await res.text();
      if (text) {
        try {
          const j = JSON.parse(text);
          // Prefer detailed message over generic error label
          let candidate = "";
          if (j) {
            if (typeof j.message === "string" && j.message.trim()) candidate = j.message;
            else if (typeof j.detail === "string" && j.detail.trim()) candidate = j.detail;
            else if (typeof j.error_description === "string" && j.error_description.trim())
              candidate = j.error_description;
            else if (typeof j.errorDescription === "string" && j.errorDescription.trim())
              candidate = j.errorDescription;
            else if (
              Array.isArray(j.errors) &&
              j.errors.length &&
              typeof j.errors[0]?.message === "string" &&
              j.errors[0].message.trim()
            )
              candidate = j.errors[0].message;
            else if (typeof j.title === "string" && j.title.trim()) candidate = j.title;
            else if (typeof j.error === "string" && j.error.trim()) candidate = j.error;
            else if (typeof j === "string" && j.trim()) candidate = j;
          }
          msg = typeof candidate === "string" ? candidate : "";
        } catch {
          // Not JSON -> use raw text
          msg = text;
        }
      }
    } catch {
      /* ignore */
    }
    // Upgrade hint for plan limits — keep backend msg if present, otherwise default
    if (res.status === 402) {
      const base = msg && msg.trim() ? msg : "Plan limiti aşıldı";
      msg = base + " — planınızı Yükselt sayfasından artırabilirsiniz (Admin > Planı Yükselt).";
    }
    // Final fallback if still empty
    if (!msg || !msg.trim()) {
      msg = "İstek başarısız: " + res.status;
      if (res.status === 402) {
        msg += " — planınızı Yükselt sayfasından artırabilirsiniz (Admin > Planı Yükselt).";
      }
    }
    if (!silent) {
      try {
        const { useUiStore } = await import("@/stores/uiStore");
        const ui = useUiStore();
        ui.toastError(msg);
      } catch {
        /* ignore */
      }
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
