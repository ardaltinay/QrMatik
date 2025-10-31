<template>
  <div
    class="bg-white border rounded-lg p-0 shadow-sm overflow-hidden transform transition hover:-translate-y-1 hover:scale-[1.01] hover:shadow-lg"
  >
    <img v-if="item.image" :src="item.image" alt="" class="w-full h-40 object-cover" />
    <div class="p-4 flex flex-col gap-2">
      <div>
        <div class="text-sm text-gray-500">{{ catLabel }} · {{ subLabel }}</div>
        <div class="mt-1 text-base font-semibold text-gray-900 truncate">{{ item.name }}</div>
      </div>
      <div class="flex items-center justify-between">
        <div class="text-indigo-600 font-semibold">{{ item.price }}₺</div>
        <button
          @click="$emit('add', item)"
          class="px-3 py-1 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
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
    computed: {
      catLabel() {
        return categoryLabel(this.item.category);
      },
      subLabel() {
        const s = this.item.sub || this.item.subcategory;
        return subLabelUtil(s);
      },
    },
  };
</script>
