import { defineStore } from "pinia";
import { ref } from "vue";
import { fetchJson } from "@/utils/api";

const SESSION_KEY = "qm_session";

export const useAuthStore = defineStore("auth", () => {
  const raw = localStorage.getItem(SESSION_KEY);
  const parsed = raw ? JSON.parse(raw) : null;
  if (parsed && parsed.role) parsed.role = String(parsed.role).toLowerCase();
  const user = ref(parsed);
  const token = ref(user.value && user.value.token ? user.value.token : null);
  let expiryTimer = null;

  function clearExpiryTimer() {
    try {
      if (expiryTimer) clearTimeout(expiryTimer);
    } catch {
      /* ignore */
    }
    expiryTimer = null;
  }

  function decodeJwtExp(tkn) {
    try {
      const parts = String(tkn || "").split(".");
      if (parts.length < 2) return null;
      const b64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
      const json = atob(b64);
      const obj = JSON.parse(json);
      if (obj && typeof obj.exp === "number") return obj.exp; // seconds since epoch
    } catch {
      /* ignore */
    }
    return null;
  }

  async function scheduleAutoLogout() {
    clearExpiryTimer();
    if (!token.value) return;
    const exp = decodeJwtExp(token.value);
    if (!exp) return;
    const ms = exp * 1000 - Date.now() - 3000; // 3s leeway
    if (ms <= 0) {
      try {
        logout();
        const { default: router } = await import("@/router");
        router.replace({ name: "admin" });
        const { useUiStore } = await import("@/stores/uiStore");
        useUiStore().toastError("Oturum süreniz doldu. Lütfen tekrar giriş yapın.");
      } catch {
        /* ignore */
      }
      return;
    }
    expiryTimer = setTimeout(async () => {
      try {
        logout();
        const { default: router } = await import("@/router");
        router.replace({ name: "admin" });
        const { useUiStore } = await import("@/stores/uiStore");
        useUiStore().toastError("Oturum süreniz doldu. Lütfen tekrar giriş yapın.");
      } catch {
        /* ignore */
      }
    }, ms);
  }

  function persist() {
    const payload = user.value
      ? { username: user.value.username, role: user.value.role, token: token.value }
      : null;
    try {
      localStorage.setItem(SESSION_KEY, JSON.stringify(payload));
    } catch (err) {
      void console.debug("Could not persist session", err);
    }
  }

  async function login(username, _password) {
    const data = await fetchJson("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password: _password }),
      suppressAuth: true,
    });
    const role = String(data.user?.role || "staff").toLowerCase();
    // For non-superadmin roles, require a detected tenant in URL/context
    const hasTenant = (() => {
      try {
        // prefer URL-derived tenant over localStorage
        const params = new URLSearchParams(window.location.search);
        const q = params.get("tenant") || params.get("t") || params.get("code");
        if (q) return true;
        const path = window.location.pathname || "";
        const parts = path.split("/");
        const idx = parts.indexOf("r");
        if (idx >= 0 && parts.length > idx + 1) return true;
        const host = window.location.hostname || "";
        if (host.endsWith(".localhost")) {
          const dot = host.indexOf(".");
          if (dot > 0) return true;
        } else {
          const hostParts = host.split(".");
          if (hostParts.length > 2) return true;
        }
      } catch {
        /* ignore */
      }
      return false;
    })();
    if (role !== "superadmin" && !hasTenant) {
      try {
        const { useUiStore } = await import("@/stores/uiStore");
        const ui = useUiStore();
        ui.toastError("Tenant gerekli: Lütfen restoran alt alan adı üzerinden giriş yapın.");
      } catch {
        /* ignore */
      }
      throw new Error("tenant-required");
    }
    token.value = data.token;
    user.value = { username: data.user?.username || username, role };
    persist();
    scheduleAutoLogout();
  }

  function logout() {
    user.value = null;
    token.value = null;
    clearExpiryTimer();
    try {
      localStorage.removeItem(SESSION_KEY);
    } catch (err) {
      void console.debug("Could not remove session", err);
    }
  }

  function isAdmin() {
    return user.value && String(user.value.role).toLowerCase() === "admin";
  }
  function isSuperAdmin() {
    return user.value && String(user.value.role).toLowerCase() === "superadmin";
  }
  function hasRole(r) {
    return user.value && String(user.value.role).toLowerCase() === String(r).toLowerCase();
  }

  // Initialize expiry watcher on store load if a token is present
  if (token.value) {
    // schedule but don't block
    scheduleAutoLogout();
    // double-check on tab focus (if time changed or sleep resume)
    try {
      window.addEventListener("visibilitychange", () => {
        if (document.visibilityState === "visible") scheduleAutoLogout();
      });
    } catch {
      /* ignore */
    }
  }

  return { user, token, login, logout, isAdmin, isSuperAdmin, hasRole };
});
