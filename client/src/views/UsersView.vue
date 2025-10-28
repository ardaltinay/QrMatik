<template>
  <div>
    <h2 class="text-xl font-semibold mb-4">Kullanıcı Yönetimi</h2>
    <div class="mb-4 grid grid-cols-1 sm:grid-cols-4 gap-2 items-start">
      <input
        v-model="username"
        placeholder="Kullanıcı adı"
        class="w-full p-2 rounded-lg border shadow-sm focus:ring-2 focus:ring-brand-200"
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
        class="w-full p-2 rounded-lg border shadow-sm focus:ring-2 focus:ring-brand-200"
      />
      <button @click="addUser" class="px-3 py-2 bg-brand-500 text-white rounded-lg shadow">
        Ekle
      </button>
    </div>

    <div class="grid gap-3">
      <div
        v-for="u in store.users"
        :key="u.id"
        class="p-3 bg-white border rounded-lg flex flex-col sm:flex-row sm:justify-between gap-3 items-start sm:items-center shadow-sm"
      >
        <div>
          <div class="font-medium">{{ u.username }}</div>
          <div class="text-sm text-gray-500">{{ u.role }}</div>
        </div>
        <div class="flex gap-2">
          <button @click="editUser(u)" class="px-3 py-1 bg-white border rounded-lg">Düzenle</button>
          <button
            @click="store.deleteUser(u.id)"
            class="px-3 py-1 bg-red-50 text-red-600 rounded-lg"
          >
            Sil
          </button>
        </div>
      </div>
    </div>

    <div v-if="editing" class="fixed inset-0 bg-black/40 flex items-center justify-center p-4">
      <div class="bg-white p-6 rounded-lg shadow w-full max-w-md">
        <h3 class="font-semibold mb-2">Kullanıcı Düzenle</h3>
        <input
          v-model="editing.username"
          class="w-full p-2 rounded-lg border mb-2 shadow-sm focus:ring-2 focus:ring-brand-200"
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
          class="w-full p-2 rounded-lg border my-2 shadow-sm focus:ring-2 focus:ring-brand-200"
        />
        <div class="flex justify-end gap-2">
          <button @click="editing = null" class="px-3 py-1 border rounded-lg">İptal</button>
          <button @click="saveEdit" class="px-3 py-1 bg-brand-500 text-white rounded-lg">
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
