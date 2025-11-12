<template>
  <div class="mx-auto max-w-4xl px-6 py-12">
    <header class="mb-8">
      <h1 class="mb-3 text-3xl font-bold text-gray-900 md:text-4xl">
        QR Restoran Dijital Menü ve Mobil Sipariş Sistemi
      </h1>
      <p class="text-gray-600">
        QrMatik ile işletmenize <strong>dijital menü</strong>, <strong>QR sipariş</strong> ve
        <strong>akıllı QR menü</strong> altyapısını kolayca ekleyin. Restoranlar, kafeler ve benzeri
        işletmeler için hızlı kurulum, canlı takip, mutfak/bar panoları ve raporlama.
      </p>
    </header>

    <section class="mb-10 grid gap-4">
      <h2 class="text-2xl font-semibold">Öne çıkan özellikler</h2>
      <ul class="grid gap-2 text-gray-700">
        <li>• QR ile hızlı erişim; <em>Yeni → Hazırlanıyor → Hazır → Servis</em> akışında takip</li>
        <li>• Mutfak ve bar için ayrı panolar, tek tıkla durum güncelleme</li>
        <li>• Tablo/masa bazlı sipariş oturumları ve raporlar</li>
        <li>• Çoklu işletme desteği; her işletme kendi alt alan adında çalışır</li>
      </ul>
    </section>

    <section class="mb-10 grid gap-3">
      <h2 class="text-2xl font-semibold">Hızlı başlangıç</h2>
      <ol class="list-decimal space-y-1 pl-5 text-gray-700">
        <li>Kayıt olun ve işletme bilgilerinizi ekleyin.</li>
        <li>
          Menü kalemlerinizi ve fiyatlarınızı girin.(Veya bizimle iletişime geçin destek olalım.)
        </li>
        <li>Masa/alan kodları için karekod (QR) üretin ve yerleştirin.</li>
        <li>
          Hepsi bu kadar! Müşterileriniz QR'ı okutup <strong>dijital qr menü</strong>den
          <strong>sipariş vermeye ve sipariş takibi yapmaya</strong> başlasın.
        </li>
      </ol>
    </section>

    <!-- Aranan terimler ve kullanım alanları: en altta akordeon -->
    <section class="py-2">
      <details class="group rounded-xl border bg-white shadow-sm">
        <summary class="flex cursor-pointer select-none items-center justify-between px-4 py-3">
          <span class="font-semibold text-gray-900">Aranan terimler ve kullanım alanları</span>
          <span class="text-gray-400 transition-transform group-open:rotate-180">▾</span>
        </summary>
        <div class="px-4 pb-4 text-sm leading-6 text-gray-700">
          <ul class="list-disc space-y-1 pl-5">
            <li>
              <strong>qr restoran / restoran qr / restorant qr:</strong> Masaya özel karekod ile
              mobil sipariş.
            </li>
            <li>
              <strong>dijital menü / dijital qr menü / qr digital menü:</strong> Menünüzü
              telefondan, anında güncel gösterin.
            </li>
            <li>
              <strong>qr sipariş:</strong> Müşteriler masadan kalkmadan sipariş verir; durumunu
              canlı takip eder.
            </li>
            <li>
              <strong>karekod restoran / restoran karekod menü:</strong> Tek QR ile menü ve sipariş
              akışı.
            </li>
            <li>
              <strong>akıllı qr menü:</strong> Stok/menü değişiklikleri anında yansır; mutfak ve bar
              ekranlarına düşer.
            </li>
          </ul>
        </div>
      </details>
    </section>
    <!-- Sticky CTA (mobile): demo anchor on home -->
    <div class="fixed inset-x-0 bottom-0 z-50 border-t bg-white/90 backdrop-blur md:hidden">
      <div class="mx-auto flex max-w-4xl items-center gap-2 px-4 py-3">
        <a
          href="/#demo"
          class="bg-brand-gradient inline-flex flex-1 items-center justify-center rounded-md px-4 py-2 text-sm text-white shadow hover:opacity-95"
          >Demo talep et</a
        >
        <router-link
          to="/"
          class="rounded-md border bg-white px-4 py-2 text-gray-800 hover:bg-gray-50"
          >Anasayfa</router-link
        >
      </div>
    </div>
    <div class="h-16 md:hidden"></div>
  </div>
</template>

<script setup>
  import { useHead } from "@unhead/vue";

  useHead({
    title: "QR Restoran Dijital Menü ve Mobil Sipariş | QrMatik",
    meta: [
      {
        name: "description",
        content:
          "QrMatik ile QR restoran çözümü: dijital QR menü, mobil sipariş ve akıllı QR menü. Restoran, kafe ve barlar için karekod menü altyapısı.",
      },
      { property: "og:title", content: "QR Restoran Dijital Menü ve Mobil Sipariş | QrMatik" },
      {
        property: "og:description",
        content:
          "Restoranlar için QR restoran, dijital QR menü ve mobil sipariş. Karekod restoran menüsü ve canlı sipariş takibi.",
      },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
    script: [
      {
        type: "application/ld+json",
        children: JSON.stringify({
          "@context": "https://schema.org",
          "@type": "FAQPage",
          mainEntity: [
            {
              "@type": "Question",
              name: "Dijital QR menü nedir? Nasıl çalışır?",
              acceptedAnswer: {
                "@type": "Answer",
                text: "Dijital QR menü, masaya yerleştirilen karekodun telefonla okutulmasıyla açılan çevrim içi menüdür. QrMatik ile müşteriler menüden ürün seçip QR sipariş verir ve sipariş durumunu canlı takip eder.",
              },
            },
            {
              "@type": "Question",
              name: "QR restoran (restorant) çözümü hangi işletmeler için uygun?",
              acceptedAnswer: {
                "@type": "Answer",
                text: "Restoran, kafe ve barlar için uygundur. Karekod restoran menüsüyle sipariş hızlanır; mutfak/bar ekranlarıyla hazırlık ve servis akışı düzenlenir.",
              },
            },
          ],
        }),
      },
    ],
  });
</script>

<style scoped></style>
