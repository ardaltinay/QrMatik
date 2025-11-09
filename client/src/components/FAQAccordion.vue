<template>
  <section class="py-12">
    <div class="container mx-auto">
      <h2 class="mb-6 text-2xl font-bold text-gray-900 md:text-3xl">Sıkça Sorulanlar</h2>
      <div class="divide-y rounded-xl border bg-white shadow-sm">
        <details v-for="qa in faqs" :key="qa.q" class="group">
          <summary class="flex cursor-pointer select-none items-center justify-between px-4 py-3">
            <span class="font-medium text-gray-900">{{ qa.q }}</span>
            <span class="text-gray-400 transition-transform group-open:rotate-180">▾</span>
          </summary>
          <div class="px-4 pb-4 text-sm leading-6 text-gray-600">{{ qa.a }}</div>
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
            a: "Tek tuşla masaya hesabı isteyin; Alternatif olarak sistem üzerinden online ödeme modülleri entegre edilebilir.",
          },
          {
            q: "Mutfak ve bar için ayrı panolar var mı?",
            a: "Evet, sipariş akışını hızlandırmak için mutfak, bar ve kasa için özel ekranlar bulunur.",
          },
          {
            q: "Dijital menü ve QR sipariş bize ne kazandırır?",
            a: "Kâğıt menü maliyetini ve servis bekleme süresini azaltır. Müşteri, dijital menüden kendi telefonundan sipariş verir; mutfak ve bar anında bilgilendirilir. Daha hızlı servis ve daha fazla masa dönüşü demektir.",
          },
          {
            q: "Akıllı QR menü ile masaya özel yönlendirme mümkün mü?",
            a: "Evet. Masaya özel QR (restoran karekod menü) ile doğru masaya sipariş ve oturum takibi sağlanır. Bu sayede yanlış masa karışıklıkları ve manuel notlar ortadan kalkar.",
          },
        ],
      };
    },
  };
</script>
