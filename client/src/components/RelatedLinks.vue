<template>
  <section class="mt-10">
    <h2 class="mb-3 text-xl font-semibold text-gray-900">İlgili içerikler</h2>
    <ul class="grid gap-2 sm:grid-cols-2">
      <li v-for="it in items" :key="it.to">
        <router-link
          :to="it.to"
          class="block rounded-lg border bg-white px-4 py-3 text-indigo-700 hover:bg-indigo-50"
        >
          <span class="font-medium">{{ it.label }}</span>
          <span v-if="it.desc" class="block text-sm text-gray-600">{{ it.desc }}</span>
        </router-link>
      </li>
    </ul>
  </section>
</template>

<script>
  export default {
    name: "RelatedLinks",
    props: {
      items: { type: Array, required: true },
    },
  };
</script>
