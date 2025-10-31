<template>
  <div>
    <h2 class="text-lg font-semibold mb-4">Masalar</h2>

    <div class="bg-white rounded border p-4 mb-6">
      <h3 class="font-medium mb-3">Yeni Masa Ekle</h3>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
        <input v-model="form.code" placeholder="Kod (örn: A1)" class="border rounded p-2" />
        <input v-model="form.description" placeholder="Açıklama (opsiyonel)" class="border rounded p-2" />
        <select v-model="form.status" class="border rounded p-2">
          <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
        </select>
        <button @click="createTable" class="px-3 py-2 bg-brand-500 text-white rounded">Ekle</button>
      </div>
    </div>

    <div class="overflow-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="text-left border-b">
            <th class="p-2">Kod</th>
            <th class="p-2">Açıklama</th>
            <th class="p-2">Durum</th>
            <th class="p-2">QR</th>
            <th class="p-2 text-right">İşlemler</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in tables" :key="t.id" class="border-b">
            <td class="p-2">
              <input v-if="editId===t.id" v-model="edit.code" class="border rounded p-1 w-32" />
              <span v-else class="font-medium">{{ t.code }}</span>
            </td>
            <td class="p-2">
              <input v-if="editId===t.id" v-model="edit.description" class="border rounded p-1 w-64" />
              <span v-else>{{ t.description }}</span>
            </td>
            <td class="p-2">
              <select v-if="editId===t.id" v-model="edit.status" class="border rounded p-1">
                <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
              </select>
              <span v-else>{{ statusLabel(t.status) }}</span>
            </td>
            <td class="p-2">
              <img :src="qrSrc(t.code)" :alt="'QR '+t.code" class="w-16 h-16 object-contain" />
            </td>
            <td class="p-2 text-right">
              <template v-if="editId===t.id">
                <button @click="saveEdit(t)" class="px-2 py-1 bg-green-600 text-white rounded mr-2">Kaydet</button>
                <button @click="cancelEdit" class="px-2 py-1 border rounded">Vazgeç</button>
              </template>
              <template v-else>
                <button @click="startEdit(t)" class="px-2 py-1 border rounded mr-2">Düzenle</button>
                <button @click="removeTable(t)" class="px-2 py-1 bg-red-600 text-white rounded">Sil</button>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="mt-6">
      <router-link to="/admin/qr" class="inline-block px-3 py-2 border rounded">Toplu QR PDF</router-link>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { fetchJson, apiFetch } from "@/utils/api";

export default {
  name: "AdminTablesView",
  setup() {
    const tables = ref([]);
    const form = ref({ code: "", description: "", status: "AVAILABLE" });
    const editId = ref(null);
    const edit = ref({ code: "", description: "", status: "AVAILABLE" });
    const statusOptions = [
      { value: "AVAILABLE", label: "Uygun" },
      { value: "UNAVAILABLE", label: "Kullanım Dışı" },
    ];

    function statusLabel(s) {
      return String(s) === "UNAVAILABLE" ? "Kullanım Dışı" : "Uygun";
    }

    function buildTableUrl(code) {
      try {
        const u = new URL(window.location.href);
        // we assume admin is on tenant subdomain or /r/:tenant; origin works
        return `${u.origin}/menu?table=${encodeURIComponent(code)}`;
      } catch {
        return `/menu?table=${encodeURIComponent(code)}`;
      }
    }

    function qrSrc(code) {
      const url = buildTableUrl(code);
      return `/api/qr/image?text=${encodeURIComponent(url)}&size=160`;
    }

    async function load() {
      tables.value = await fetchJson("/api/tables", { silentError: true });
      if (!Array.isArray(tables.value)) tables.value = [];
    }

    function normalizeStatus(s) {
      return s === "UNAVAILABLE" ? "UNAVAILABLE" : "AVAILABLE";
    }

    async function createTable() {
      const payload = {
        code: form.value.code?.trim(),
        description: form.value.description?.trim() || "",
        status: normalizeStatus(form.value.status),
      };
      if (!payload.code) return;
      await fetchJson("/api/tables", { method: "POST", body: JSON.stringify(payload) });
      form.value = { code: "", description: "", status: "AVAILABLE" };
      await load();
    }

    function startEdit(t) {
      editId.value = t.id;
      edit.value = { code: t.code, description: t.description || "", status: t.status || "AVAILABLE" };
    }

    function cancelEdit() {
      editId.value = null;
      edit.value = { code: "", description: "", status: "AVAILABLE" };
    }

    async function saveEdit(t) {
      const payload = {
        code: edit.value.code?.trim(),
        description: edit.value.description?.trim() || "",
        status: normalizeStatus(edit.value.status),
      };
      await fetchJson(`/api/tables/${t.id}`, { method: "PUT", body: JSON.stringify(payload) });
      cancelEdit();
      await load();
    }

    async function removeTable(t) {
      if (!confirm(`${t.code} masasını silmek istiyor musunuz?`)) return;
      const res = await apiFetch(`/api/tables/${t.id}`, { method: "DELETE" });
      if (res.ok) await load();
    }

    onMounted(load);

    return { tables, form, editId, edit, load, createTable, startEdit, cancelEdit, saveEdit, removeTable, qrSrc, statusLabel, statusOptions };
  },
};
</script>

<style scoped>
/* minimal styles inherited from Tailwind */
</style>
