<template>
  <section class="py-12">
    <div class="container mx-auto">
      <h2 class="mb-6 text-2xl font-bold text-gray-900 md:text-3xl">Fiyatlandırma</h2>
      <div class="grid gap-6 md:grid-cols-3">
        <div
          v-for="p in plans"
          :key="p.name"
          class="flex flex-col rounded-xl border bg-white p-5 shadow-sm"
        >
          <div class="mb-1 text-lg font-semibold">{{ p.name }}</div>
          <div class="mb-2 text-3xl font-bold text-gray-900">{{ p.price }}</div>
          <ul class="mb-4 list-disc pl-5 text-sm leading-6 text-gray-600">
            <li v-for="f in p.features" :key="f">{{ f }}</li>
          </ul>
          <!-- intentionally no CTA button; section kept simple as requested -->
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
          {
            name: "Başlangıç",
            price: "Ücretsiz",
            features: ["Sınırlı menü", "Temel sipariş", "QR üretimi"],
          },
          {
            name: "Standart",
            price: "₺",
            features: ["Sınırsız menü", "Sipariş takibi", "Mutfak-Bar panoları"],
          },
          { name: "Pro", price: "₺₺", features: ["Online ödeme", "Raporlar", "Öncelikli destek"] },
        ],
      };
    },
  };
</script>
