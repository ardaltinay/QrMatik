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

export async function fetchJson(path, opts = {}) {
  const res = await apiFetch(
    path,
    Object.assign({}, opts, {
      headers: Object.assign({ "Content-Type": "application/json" }, opts.headers || {}),
    }),
  );
  if (!res.ok) {
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
    } catch {
      /* ignore */
    }
    try {
      const { useUiStore } = await import("@/stores/uiStore");
      const ui = useUiStore();
      ui.toastError(msg);
    } catch {
      /* ignore */
    }
    throw new Error(msg);
  }
  return res.json();
}
