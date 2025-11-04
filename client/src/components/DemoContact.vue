<template>
  <section id="demo" class="py-12">
    <div class="container mx-auto">
      <div class="rounded-2xl border bg-white p-6 shadow-sm md:p-8">
        <h2 class="mb-2 text-2xl font-bold text-gray-900 md:text-3xl">Demo Talep Edin</h2>
        <p class="mb-4 text-gray-600">
          QrMatik'i işletmenize nasıl uyarlayabileceğimizi birlikte keşfedelim.
        </p>
        <div class="flex flex-col gap-3 sm:flex-row">
          <a
            :href="mailtoDemo"
            class="rounded-md bg-indigo-600 px-5 py-3 text-center text-white hover:bg-indigo-700"
            >Demo Talebi</a
          >
          <a
            :href="mailtoSupport"
            class="rounded-md bg-gray-100 px-5 py-3 text-center text-gray-800 hover:bg-gray-200"
            >İletişime Geçin</a
          >
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
