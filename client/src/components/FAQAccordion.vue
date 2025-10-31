<template>
  <section class="py-12">
    <div class="container mx-auto">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 mb-6">Sıkça Sorulanlar</h2>
      <div class="divide-y rounded-xl border bg-white shadow-sm">
        <details v-for="qa in faqs" :key="qa.q" class="group">
          <summary class="flex items-center justify-between px-4 py-3 cursor-pointer select-none">
            <span class="font-medium text-gray-900">{{ qa.q }}</span>
            <span class="text-gray-400 group-open:rotate-180 transition-transform">▾</span>
          </summary>
          <div class="px-4 pb-4 text-sm text-gray-600 leading-6">{{ qa.a }}</div>
        </details>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: "FAQAccordion",
  data() {
    return {
      faqs: [
        {
          q: "QrMatik ile nasıl başlarım?",
          a: "Her masa için QR kodu üretin, menünüzü yükleyin ve yayına alın. Müşterileriniz QR'ı okutarak mobil menüden sipariş verir.",
        },
        {
          q: "Ödemeler nasıl alınır?",
          a: "Sistem üzerinden online ödeme modülleri entegre edilebilir; alternatif olarak masa hesabı kapama da desteklenir.",
        },
        {
          q: "Mutfak ve bar için ayrı panolar var mı?",
          a: "Evet, sipariş akışını hızlandırmak için mutfak ve bar özel ekranları bulunur.",
        },
      ],
    };
  },
};
</script>
