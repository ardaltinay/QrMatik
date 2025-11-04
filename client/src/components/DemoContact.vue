<template>
  <section id="demo" class="py-12">
    <div class="container mx-auto">
      <div class="rounded-2xl border bg-white shadow-sm p-6 md:p-8">
        <h2 class="text-2xl md:text-3xl font-bold text-gray-900 mb-2">Demo Talep Edin</h2>
        <p class="text-gray-600 mb-4">QrMatik'i işletmenize nasıl uyarlayabileceğimizi birlikte keşfedelim.</p>
        <div class="flex flex-col sm:flex-row gap-3">
          <a :href="mailtoDemo" class="px-5 py-3 rounded-md bg-indigo-600 text-white text-center hover:bg-indigo-700">Demo Talebi</a>
          <a :href="mailtoSupport" class="px-5 py-3 rounded-md bg-gray-100 text-gray-800 text-center hover:bg-gray-200">İletişime Geçin</a>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: "DemoContact",
  data() {
    return {
      mailtoDemo: "mailto:ardaltinay48@gmail.com?subject=QrMatik%20Demo%20Talebi",
      mailtoSupport: "mailto:ardaltinay48@gmail.com?subject=QrMatik%20İletişim",
    };
  },
};
</script>
