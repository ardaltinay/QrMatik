<template>
  <div>
    <h2 class="mb-4 text-xl font-semibold">Kullanıcı Yönetimi</h2>
    <div class="mb-4 grid grid-cols-1 items-start gap-2 sm:grid-cols-4">
      <input
        v-model="username"
        placeholder="Kullanıcı adı"
        class="focus:ring-brand-200 w-full rounded-lg border p-2 shadow-sm focus:ring-2"
      />
      <div class="w-full sm:w-auto">
        <BaseSelect
          v-model="role"
          :options="[
            { value: 'admin', label: 'Admin' },
            { value: 'staff', label: 'Staff' },
            { value: 'kitchen', label: 'Mutfak' },
            { value: 'bar', label: 'Bar' },
          ]"
        />
      </div>
      <input
        v-model="password"
        type="password"
        placeholder="Parola"
        class="focus:ring-brand-200 w-full rounded-lg border p-2 shadow-sm focus:ring-2"
      />
      <button @click="addUser" class="rounded-lg bg-brand-500 px-3 py-2 text-white shadow">
        Ekle
      </button>
    </div>

    <div class="grid gap-3">
      <div
        v-for="u in store.users"
        :key="u.id"
        class="flex flex-col items-start gap-3 rounded-lg border bg-white p-3 shadow-sm sm:flex-row sm:items-center sm:justify-between"
      >
        <div>
          <div class="font-medium">{{ u.username }}</div>
          <div class="text-sm text-gray-500">{{ u.role }}</div>
        </div>
        <div class="flex gap-2">
          <button @click="editUser(u)" class="rounded-lg border bg-white px-3 py-1">Düzenle</button>
          <button
            @click="store.deleteUser(u.id)"
            class="rounded-lg bg-red-50 px-3 py-1 text-red-600"
          >
            Sil
          </button>
        </div>
      </div>
    </div>

    <div v-if="editing" class="fixed inset-0 flex items-center justify-center bg-black/40 p-4">
      <div class="w-full max-w-md rounded-lg bg-white p-6 shadow">
        <h3 class="mb-2 font-semibold">Kullanıcı Düzenle</h3>
        <input
          v-model="editing.username"
          class="focus:ring-brand-200 mb-2 w-full rounded-lg border p-2 shadow-sm focus:ring-2"
        />
        <div>
          <BaseSelect
            v-model="editing.role"
            :options="[
              { value: 'admin', label: 'Admin' },
              { value: 'staff', label: 'Staff' },
              { value: 'kitchen', label: 'Mutfak' },
              { value: 'bar', label: 'Bar' },
            ]"
          />
        </div>
        <input
          v-model="editing.password"
          type="password"
          placeholder="Yeni parola (isteğe bağlı)"
          class="focus:ring-brand-200 my-2 w-full rounded-lg border p-2 shadow-sm focus:ring-2"
        />
        <div class="flex justify-end gap-2">
          <button @click="editing = null" class="rounded-lg border px-3 py-1">İptal</button>
          <button @click="saveEdit" class="rounded-lg bg-brand-500 px-3 py-1 text-white">
            Kaydet
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { ref } from "vue";
  import { useUserStore } from "@/stores/userStore";
  import BaseSelect from "@/components/BaseSelect.vue";

  export default {
    components: { BaseSelect },
    setup() {
      const store = useUserStore();
      const username = ref("");
      const role = ref("staff");
      const password = ref("");
      const editing = ref(null);

      function addUser() {
        if (!username.value || !password.value) return;
        store.createUser(username.value, role.value, password.value);
        username.value = "";
        password.value = "";
      }

      function editUser(u) {
        editing.value = { ...u };
      }
      function saveEdit() {
        store.updateUser(editing.value.id, editing.value);
        editing.value = null;
      }

      return { store, username, role, password, addUser, editing, editUser, saveEdit };
    },
  };
</script>
