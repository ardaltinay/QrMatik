<template>
  <section id="contact" class="py-12">
    <div class="container mx-auto">
      <div class="rounded-2xl border bg-white p-6 shadow-sm md:p-8">
        <h2 class="mb-2 text-2xl font-bold text-gray-900 md:text-3xl">Bizimle İletişime Geçin</h2>
        <p class="mb-4 text-gray-600">Sorularınız ve iş birliği için bize ulaşın.</p>
        <div class="flex">
          <a
            :href="mailto"
            class="rounded-md bg-indigo-600 px-5 py-3 text-white hover:bg-indigo-700"
            >İletişime Geçin</a
          >
        </div>
      </div>
    </div>
  </section>
</template>

<script>
  export default {
    name: "ContactUs",
    data() {
      return {
        mailto: "mailto:qrmatik.cloud@gmail.com?subject=QrMatik%20İletişim",
      };
    },
  };
</script>
