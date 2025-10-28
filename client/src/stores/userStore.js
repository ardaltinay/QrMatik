import { defineStore } from "pinia";
import { ref, watch } from "vue";
import { fetchJson } from "@/utils/api";
import { useUiStore } from "@/stores/uiStore";

const STORAGE_KEY = "qm_users";

export const useUserStore = defineStore("user", () => {
  const raw = localStorage.getItem(STORAGE_KEY);
  const users = ref(raw ? JSON.parse(raw) : []);

  // server provides IDs; local nextId no longer needed

  function persist() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(users.value));
    } catch (err) {
      // ignore storage errors in environments where localStorage is unavailable
      void console.debug("Could not persist users", err);
    }
  }

  // persist on changes
  watch(users, persist, { deep: true });

  // load users from server with fallback to local defaults
  async function loadUsers() {
    try {
      const { fetchJson } = await import("@/utils/api");
      users.value = await fetchJson("/api/users");
      // normalize ids to numbers if needed
      users.value = users.value.map((u, idx) => ({
        id: u.id || idx + 1,
        username: u.username,
        role: u.role,
      }));
    } catch (err) {
      console.debug("Failed to fetch users, using fallback", err);
      if (!users.value || users.value.length === 0) {
        users.value = [
          { id: 1, username: "admin", role: "admin" },
          { id: 2, username: "kitchen", role: "kitchen" },
          { id: 3, username: "bar", role: "bar" },
        ];
      }
    }
  }

  // try load on init
  loadUsers().catch(() => {});

  async function createUser(username, role = "staff", password) {
    const ui = useUiStore();
    const payload = { username, role, password };
    const created = await fetchJson("/api/users", {
      method: "POST",
      body: JSON.stringify(payload),
      headers: { "Content-Type": "application/json" },
    });
    const u = { id: created.id, username: created.username, role: created.role };
    users.value.push(u);
    ui.toastSuccess("Kullanıcı eklendi");
    return u;
  }

  async function updateUser(id, data) {
    const ui = useUiStore();
    const payload = { username: data.username, role: data.role };
    if (data.password) payload.password = data.password;
    const updated = await fetchJson(`/api/users/${encodeURIComponent(id)}`, {
      method: "PUT",
      body: JSON.stringify(payload),
      headers: { "Content-Type": "application/json" },
    });
    const u = users.value.find((x) => String(x.id) === String(id));
    if (u) {
      u.username = updated.username;
      u.role = updated.role;
    }
    ui.toastSuccess("Kullanıcı güncellendi");
  }

  async function deleteUser(id) {
    const ui = useUiStore();
    await fetchJson(`/api/users/${encodeURIComponent(id)}`, { method: "DELETE" });
    users.value = users.value.filter((x) => String(x.id) !== String(id));
    ui.toastSuccess("Kullanıcı silindi");
  }

  // ensure initial persist for newly created default set
  persist();

  return { users, createUser, updateUser, deleteUser };
});
