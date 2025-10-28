import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import "./index.css";
import { apiFetch } from "@/utils/api";
import { useUiStore } from "@/stores/uiStore";

const app = createApp(App);
const pinia = createPinia();

// tenant detection: path /r/:tenant or subdomain or query param
function detectTenant() {
  try {
    const params = new URLSearchParams(window.location.search);
    const q = params.get("tenant") || params.get("t");
    if (q) return q;
    const path = window.location.pathname || "";
    const parts = path.split("/");
    const idx = parts.indexOf("r");
    if (idx >= 0 && parts.length > idx + 1) return parts[idx + 1];
    // subdomain
    const host = window.location.hostname;
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
  const tenant =
    detectTenant() ||
    (() => {
      try {
        return localStorage.getItem("qm_tenant");
      } catch {
        return null;
      }
    })();
  if (tenant) {
    try {
      localStorage.setItem("qm_tenant", tenant);
    } catch {
      /* ignore */
    }
    // fetch tenant config
    try {
      const res = await apiFetch("/api/tenant/config?code=" + encodeURIComponent(tenant));
      if (res.ok) {
        const cfg = await res.json();
        if (cfg.primaryColor)
          document.documentElement.style.setProperty("--brand-primary", cfg.primaryColor);
        if (cfg.accentColor)
          document.documentElement.style.setProperty("--brand-accent", cfg.accentColor);
        // set logo if exists (App.vue uses logo src directly)
        try {
          localStorage.setItem("qm_tenant_cfg", JSON.stringify(cfg));
        } catch {
          /* ignore */
        }
      }
    } catch {
      /* ignore */
    }
  }
}

app.use(pinia);
app.use(router);

// capture table code early so CartDrawer can include it in first order
try {
  const table = detectTableCode();
  if (table) localStorage.setItem("qm_table_code", table);
} catch {
  /* ignore */
}

// check session expiration on app start
try {
  const exp = localStorage.getItem("qm_order_session_expires");
  if (exp) {
    const t = new Date(exp).getTime();
    if (!isNaN(t) && Date.now() > t) {
      localStorage.removeItem("qm_order_session");
      localStorage.removeItem("qm_order_session_expires");
      const ui = useUiStore();
      ui.toast("Oturum süresi doldu", "info");
    }
  }
} catch {
  /* ignore */
}

// load tenant config then mount app
loadTenantConfig().finally(() => app.mount("#app"));
