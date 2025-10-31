<template>
  <section class="py-12">
    <div class="container mx-auto text-center">
      <div class="rounded-2xl bg-gradient-to-r from-indigo-600 to-purple-600 text-white p-8 md:p-12">
        <h2 class="text-2xl md:text-3xl font-bold mb-3">Hemen Başlayın</h2>
        <p class="text-indigo-100 mb-6">Özellikleri keşfedin ve işletmeniz için QrMatik'i nasıl kullanabileceğinizi görün.</p>
        <div class="flex flex-col sm:flex-row gap-3 justify-center">
          <router-link to="/about" class="px-5 py-3 rounded-md bg-white text-indigo-700 font-semibold hover:bg-indigo-50">Hakkında</router-link>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
export default { name: "CTASection" };
</script>
