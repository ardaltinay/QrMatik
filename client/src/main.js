import { createApp } from "vue";
import { createHead } from "@unhead/vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import "./index.css";
import { fetchJson } from "@/utils/api";
import { useUiStore } from "@/stores/uiStore";

const app = createApp(App);
const head = createHead();
const pinia = createPinia();

// tenant detection: path /r/:tenant or subdomain or query param
function detectTenant() {
  try {
    const params = new URLSearchParams(window.location.search);
    const q = params.get("tenant") || params.get("t") || params.get("code");
    if (q) return q;
    const path = window.location.pathname || "";
    const parts = path.split("/");
    const idx = parts.indexOf("r");
    if (idx >= 0 && parts.length > idx + 1) return parts[idx + 1];
    // subdomain
    const host = window.location.hostname;
    // Special-case localhost subdomains like foo.localhost
    if (host && host.endsWith(".localhost")) {
      const dot = host.indexOf(".");
      if (dot > 0) return host.slice(0, dot);
    }
    const hostParts = host.split(".");
    if (hostParts.length > 2) return hostParts[0];
  } catch {
    /* ignore */
  }
  return null;
}

// table detection: read from query param and persist for order creation
function detectTableCode() {
  try {
    const params = new URLSearchParams(window.location.search);
    const t = params.get("table") || params.get("t");
    if (t) return t;
  } catch {
    /* ignore */
  }
  return null;
}

async function loadTenantConfig() {
  const tenant = detectTenant();
  if (!tenant) return { found: true }; // no implicit fallback
  // fetch tenant config; only persist tenant if config resolves
  // Persist candidate tenant
  try { localStorage.setItem("qm_tenant", tenant); } catch { /* ignore */ }
  // fetch tenant config using JSON helper (handles errors/toasts)
  try {
    const cfg = await fetchJson("/api/tenant/config?code=" + encodeURIComponent(tenant), { silentError: true });
    if (cfg && typeof cfg === "object") {
      if (cfg.primaryColor)
        document.documentElement.style.setProperty("--brand-primary", cfg.primaryColor);
      if (cfg.accentColor)
        document.documentElement.style.setProperty("--brand-accent", cfg.accentColor);
      try { localStorage.setItem("qm_tenant_cfg", JSON.stringify(cfg)); } catch { /* ignore */ }
      try { sessionStorage.setItem("qm_tenant_verified_" + tenant, "ok"); } catch { /* ignore */ }
      return { found: true };
    }
  } catch {
    // 404 veya diğer hatalarda tenant bilgisini temizle ve kullanıcıyı bilgilendir
    try { localStorage.removeItem("qm_tenant"); localStorage.removeItem("qm_tenant_cfg"); } catch { /* ignore */ }
    const ui = useUiStore();
    ui.toastError("Restoran bulunamadı veya pasif.");
    try { sessionStorage.setItem("qm_tenant_verified_" + tenant, "notfound"); } catch { /* ignore */ }
    return { found: false };
  }
  return { found: true };
}

app.use(pinia);
app.use(router);
app.use(head);

// capture table code early so CartDrawer can include it in first order
try {
  const table = detectTableCode();
  if (table) {
    localStorage.setItem("qm_table_code", table);
    try {
      const tenant = localStorage.getItem("qm_tenant");
      if (tenant) localStorage.setItem("qm_table_tenant", tenant);
    } catch { /* ignore */ }
  } else {
    // Eğer daha önce ayarlanmamışsa varsayılan 'guest' kullan (sunucuya gönderilmeyecek)
    const existing = localStorage.getItem("qm_table_code");
    if (!existing) localStorage.setItem("qm_table_code", "guest");
  }
} catch {
  /* ignore */
}

// check session expiration on app start
try {
  // Bridge: if a session exists in localStorage but cookie is missing (e.g., from before cookie rollout), set cookie for cross-subdomain reuse
  (function bridgeSessionCookie() {
    try {
      const sid = localStorage.getItem("qm_order_session");
      if (!sid) return;
      // check if cookie already present
      const hasCookie = (() => {
        const n = encodeURIComponent("qm_order_session") + "=";
        const parts = (document.cookie || "").split("; ");
        return parts.some((p) => p.startsWith(n));
      })();
      if (hasCookie) return;
      const base = (() => {
        try {
          const h = window.location.hostname || "";
          if (!h) return null;
          if (h === "localhost" || h.endsWith(".localhost")) return "localhost";
          const parts = h.split(".");
          if (parts.length >= 2) return parts.slice(-2).join(".");
          return h;
        } catch {
          return null;
        }
      })();
      let cookie = encodeURIComponent("qm_order_session") + "=" + encodeURIComponent(sid) + "; Path=/";
      // 1 gün
      cookie += "; Max-Age=" + String(60 * 60 * 24);
      if (base && base.includes(".")) cookie += "; Domain=." + base;
      document.cookie = cookie;
    } catch { /* ignore */ }
  })();

  const exp = localStorage.getItem("qm_order_session_expires");
  if (exp) {
    const t = new Date(exp).getTime();
    if (!isNaN(t) && Date.now() > t) {
      localStorage.removeItem("qm_order_session");
      localStorage.removeItem("qm_order_session_expires");
      localStorage.removeItem("qm_order_session_tenant");
      try {
        // also clear cross-subdomain cookie if exists
        const base = (() => {
          try {
            const h = window.location.hostname || "";
            const parts = h.split(".");
            if (parts.length >= 3) return parts.slice(-2).join(".");
            return h;
          } catch { return null; }
        })();
        let cookie = encodeURIComponent("qm_order_session") + "=; Max-Age=0; Path=/";
        if (base && base.includes(".")) cookie += "; Domain=." + base;
        document.cookie = cookie;
      } catch { /* ignore */ }
      const ui = useUiStore();
      ui.toast("Oturum süresi doldu", "info");
    }
  }
} catch {
  /* ignore */
}

// load tenant config then mount app; unknown tenant -> not-found route
loadTenantConfig().then((res) => {
  app.mount("#app");
  if (res && res.found === false) {
    try { router.replace({ name: "tenant-not-found" }); } catch { /* ignore */ }
  }
});
