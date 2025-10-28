import { defineStore } from "pinia";
import { ref } from "vue";
import { fetchJson } from "@/utils/api";

const SESSION_KEY = "qm_session";

export const useAuthStore = defineStore("auth", () => {
  const raw = localStorage.getItem(SESSION_KEY);
  const user = ref(raw ? JSON.parse(raw) : null);
  const token = ref(user.value && user.value.token ? user.value.token : null);

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
    });
    token.value = data.token;
    user.value = { username: data.user?.username || username, role: data.user?.role || "staff" };
    persist();
  }

  function logout() {
    user.value = null;
    token.value = null;
    try {
      localStorage.removeItem(SESSION_KEY);
    } catch (err) {
      void console.debug("Could not remove session", err);
    }
  }

  function isAdmin() {
    return user.value && user.value.role === "admin";
  }
  function hasRole(r) {
    return user.value && user.value.role === r;
  }

  return { user, token, login, logout, isAdmin, hasRole };
});
