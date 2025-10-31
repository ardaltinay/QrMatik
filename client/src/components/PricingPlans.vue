<template>
  <section class="py-12">
    <div class="container mx-auto">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 mb-6">Fiyatlandırma</h2>
      <div class="grid gap-6 md:grid-cols-3">
        <div v-for="p in plans" :key="p.name" class="p-5 rounded-xl border bg-white shadow-sm flex flex-col">
          <div class="text-lg font-semibold mb-1">{{ p.name }}</div>
          <div class="text-3xl font-bold text-gray-900 mb-2">{{ p.price }}</div>
          <ul class="text-sm text-gray-600 leading-6 mb-4 list-disc pl-5">
            <li v-for="f in p.features" :key="f">{{ f }}</li>
          </ul>
          <a :href="mailto" class="mt-auto inline-flex justify-center px-4 py-2 rounded-md bg-indigo-600 text-white hover:bg-indigo-700">İletişime Geçin</a>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: "PricingPlans",
  data() {
    return {
  mailto: "mailto:ardaltinay48@gmail.com?subject=QrMatik%20Fiyatlandırma",
      plans: [
        { name: "Başlangıç", price: "Ücretsiz", features: ["Sınırlı menü", "Temel sipariş", "QR üretimi"] },
        { name: "Standart", price: "₺", features: ["Sınırsız menü", "Sipariş takibi", "Mutfak-Bar panoları"] },
        { name: "Pro", price: "₺₺", features: ["Online ödeme", "Raporlar", "Öncelikli destek"] },
      ],
    };
  },
};
</script>
