<template>
  <div class="fixed top-4 right-4 z-[100] space-y-2">
    <div
      v-for="t in toasts"
      :key="t.id"
      :class="[
        'min-w-[240px] max-w-sm px-4 py-3 rounded shadow-lg text-white',
        t.type === 'error' ? 'bg-red-600' : t.type === 'success' ? 'bg-green-600' : 'bg-gray-800',
      ]"
    >
      <div class="flex items-start gap-3">
        <span class="mt-[2px]">{{
          t.type === "error" ? "Hata" : t.type === "success" ? "Başarılı" : "Bilgi"
        }}</span>
        <div class="flex-1">{{ t.text }}</div>
        <button @click="remove(t.id)" class="opacity-80 hover:opacity-100">✕</button>
      </div>
    </div>
  </div>
</template>

<script>
  import { storeToRefs } from "pinia";
  import { useUiStore } from "@/stores/uiStore";

  export default {
    name: "ToastHub",
    setup() {
      const ui = useUiStore();
      const { toasts } = storeToRefs(ui);
      function remove(id) {
        ui.removeToast(id);
      }
      return { toasts, remove };
    },
  };
</script>
