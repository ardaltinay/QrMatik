<template>
  <div
    class="transform overflow-hidden rounded-lg border bg-white p-0 shadow-sm transition hover:-translate-y-1 hover:scale-[1.01] hover:shadow-lg"
  >
    <img v-if="item.image" :src="item.image" alt="" class="h-40 w-full object-cover" />
    <div class="flex flex-col gap-2 p-4">
      <div>
        <div class="text-sm text-gray-500">{{ catLabel }} · {{ subLabel }}</div>
        <div class="mt-1 truncate text-base font-semibold text-gray-900">{{ item.name }}</div>
      </div>
      <div class="flex items-center justify-between gap-2">
        <div class="font-semibold text-indigo-600">{{ item.price }}₺</div>
        <button
          @click="$emit('add', item)"
          class="rounded-md bg-indigo-600 px-3 py-1 text-white hover:bg-indigo-700"
        >
          Sepete Ekle
        </button>
      </div>
    </div>
  </div>
</template>

<script>
  import { categoryLabel, subLabel as subLabelUtil } from "@/utils/format";
  export default {
    name: "MenuItemCard",
    props: { item: { type: Object, required: true } },
    emits: ["add"],
    computed: {
      catLabel() {
        return categoryLabel(this.item.category);
      },
      subLabel() {
        const s = this.item.sub || this.item.subcategory;
        return subLabelUtil(s);
      },
    },
    methods: {},
  };
</script>
