<template>
  <div>
    <h2 class="mb-4 text-lg font-semibold">Masalar</h2>

    <div
      v-if="plan === 'FREE' && tables.length >= nearLimitThreshold"
      class="mb-4 rounded-md border border-amber-300 bg-amber-50 p-3 text-amber-800"
    >
      Ücretsiz planda masa limiti {{ tableLimit }}. Şu an {{ tables.length }}/{{ tableLimit }}. Daha
      fazla masa eklemek için
      <router-link to="/admin/upgrade" class="font-medium underline"
        >planınızı yükseltin</router-link
      >.
    </div>

    <div class="mb-6 rounded border bg-white p-4">
      <h3 class="mb-3 font-medium">Yeni Masa Ekle</h3>
      <div class="grid grid-cols-1 gap-3 md:grid-cols-4">
        <input v-model="form.code" placeholder="Kod (örn: A1)" class="rounded border p-2" />
        <input
          v-model="form.description"
          placeholder="Açıklama (opsiyonel)"
          class="rounded border p-2"
        />
        <BaseSelect v-model="form.status" :options="statusOptions" />
        <button
          @click="createTable"
          class="w-full rounded bg-brand-500 px-3 py-2 text-white md:w-auto"
        >
          Ekle
        </button>
      </div>
    </div>

    <div class="overflow-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="border-b text-left">
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
              <input v-if="editId === t.id" v-model="edit.code" class="w-32 rounded border p-1" />
              <span v-else class="font-medium">{{ t.code }}</span>
            </td>
            <td class="p-2">
              <input
                v-if="editId === t.id"
                v-model="edit.description"
                class="w-64 rounded border p-1"
              />
              <span v-else>{{ t.description }}</span>
            </td>
            <td class="p-2">
              <BaseSelect v-if="editId === t.id" v-model="edit.status" :options="statusOptions" />
              <span v-else>{{ statusLabel(t.status) }}</span>
            </td>
            <td class="p-2">
              <img :src="qrSrc(t.code)" :alt="'QR ' + t.code" class="h-16 w-16 object-contain" />
            </td>
            <td class="p-2 text-right">
              <div class="flex flex-col items-stretch gap-2 sm:flex-row sm:justify-end">
                <template v-if="editId === t.id">
                  <button
                    @click="saveEdit(t)"
                    class="w-full rounded bg-green-600 px-3 py-1.5 text-white sm:w-auto"
                  >
                    Kaydet
                  </button>
                  <button @click="cancelEdit" class="w-full rounded border px-3 py-1.5 sm:w-auto">
                    Vazgeç
                  </button>
                </template>
                <template v-else>
                  <button @click="startEdit(t)" class="w-full rounded border px-3 py-1.5 sm:w-auto">
                    Düzenle
                  </button>
                  <button
                    @click="removeTable(t)"
                    class="w-full rounded bg-red-600 px-3 py-1.5 text-white sm:w-auto"
                  >
                    Sil
                  </button>
                </template>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="mt-6">
      <router-link to="/admin/qr" class="inline-block rounded border px-3 py-2"
        >Toplu QR PDF</router-link
      >
    </div>
  </div>
</template>

<script>
  import { ref, onMounted, computed } from "vue";
  import { fetchJson, apiFetch } from "@/utils/api";
  import BaseSelect from "@/components/BaseSelect.vue";

  export default {
    name: "AdminTablesView",
    components: { BaseSelect },
    setup() {
      const tables = ref([]);
      const plan = ref(null);
      const tableLimit = computed(() => {
        const p = String(plan.value || "FREE").toUpperCase();
        if (p === "PRO") return 999999;
        if (p === "STANDARD") return 50;
        return 10;
      });
      const nearLimitThreshold = computed(() => Math.max(0, tableLimit.value - 2));
      const form = ref({ code: "", description: "", status: "AVAILABLE" });
      const editId = ref(null);
      const edit = ref({ code: "", description: "", status: "AVAILABLE" });
      const statusOptions = [
        { value: "AVAILABLE", label: "Uygun" },
        { value: "UNAVAILABLE", label: "Kullanım Dışı" },
      ];

      function statusLabel(s) {
        const v = String(s || "").toUpperCase();
        if (v === "UNAVAILABLE") return "Kullanım Dışı";
        if (v === "BUSY") return "Dolu";
        return "Uygun";
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
        try {
          const cfg = await fetchJson("/api/tenant/config", { silentError: true });
          plan.value = cfg && cfg.plan ? String(cfg.plan) : null;
        } catch {
          plan.value = null;
        }
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
        try {
          await fetchJson("/api/tables", { method: "POST", body: JSON.stringify(payload) });
        } catch (e) {
          // Toast already shown in fetchJson; avoid unhandled promise error in console
          return;
        }
        form.value = { code: "", description: "", status: "AVAILABLE" };
        await load();
      }

      function startEdit(t) {
        editId.value = t.id;
        edit.value = {
          code: t.code,
          description: t.description || "",
          status: t.status || "AVAILABLE",
        };
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

      return {
        tables,
        plan,
        form,
        editId,
        edit,
        load,
        createTable,
        startEdit,
        cancelEdit,
        saveEdit,
        removeTable,
        qrSrc,
        statusLabel,
        statusOptions,
        tableLimit,
        nearLimitThreshold,
      };
    },
  };
</script>

<style scoped>
  /* minimal styles inherited from Tailwind */
</style>
