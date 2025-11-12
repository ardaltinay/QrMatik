<template>
  <div class="mx-auto max-w-4xl px-6 py-12">
    <header class="mb-8">
      <h1 class="mb-3 text-3xl font-bold text-gray-900 md:text-4xl">
        Mobil Sipariş Sistemi Nedir?
      </h1>
      <p class="text-lg text-gray-600">
        Mobil sipariş sistemi; müşterilerin masadan kalkmadan, telefonları üzerinden
        <strong>qr dijital menü</strong> aracılığıyla sipariş verebildiği ve sipariş durumunu anlık
        takip edebildiği modern restoran altyapısıdır.
      </p>
    </header>

    <article class="prose prose-indigo max-w-none">
      <h2>Temel Bileşenler</h2>
      <ul>
        <li>
          <strong>QR Dijital Menü:</strong> Anlık güncellenen fiyat ve içerik bilgisi; stoktaki
          ürünler otomatik gösterilir.
        </li>
        <li>
          <strong>Mobil Sipariş Akışı:</strong> Müşteri ürünü seçer; durumlar
          <em>Yeni → Hazırlanıyor → Hazır → Servis</em> şeklinde görüntülenir.
        </li>
        <li>
          <strong>Mutfak & Bar Panoları:</strong> Hazırlama ekiplerinin tek tıkla durum
          güncellemesi; gecikme azalır.
        </li>
        <li>
          <strong>Raporlama ve Oturumlar:</strong> Masa bazlı sipariş geçmişi; performans
          analizleri.
        </li>
      </ul>

      <h2>Neden Önemli?</h2>
      <p>
        Servis hızını artırır, hatayı azaltır ve müşteri deneyimini iyileştirir.
        <strong>QR restoran</strong> modeline geçen işletmeler, yoğun saatlerde bile daha az kağıt
        menü karmaşası ve daha düzenli bir mutfak akışı elde eder.
      </p>

      <h2>Kurulum Adımları</h2>
      <ol>
        <li>Kayıt olun ve işletme (tenant) bilgilerinizi girin.</li>
        <li>Menü kalemlerini fiyatlarıyla ekleyin; görseller ile zenginleştirin.</li>
        <li>Masalara karekod çıktılarını yerleştirin.</li>
        <li>Müşterileriniz mobil sipariş vermeye başlasın; panolar durumu yansıtsın.</li>
      </ol>

      <h2>SEO Açısından Anahtar Kelimeler</h2>
      <p>
        Bu yazı <strong>mobil sipariş</strong>, <strong>qr restoran</strong>,
        <strong>qr dijital menü</strong>, <strong>akıllı qr menü</strong> gibi terimler etrafında
        arama görünürlüğünü desteklemek için optimize edilmiştir.
      </p>

      <h2>Sık Sorulan Sorular</h2>
      <dl>
        <dt><strong>Mobil sipariş sistemi zor mu?</strong></dt>
        <dd>Hayır. Dakikalar içinde menünüzü ekleyip QR kodları yerleştirebilirsiniz.</dd>
        <dt><strong>QR dijital menü ile fiyat güncellemesi nasıl çalışır?</strong></dt>
        <dd>Fiyatı panelden güncellersiniz; müşteriler anında yeni fiyatı görür.</dd>
        <dt><strong>Müşteri sipariş durumunu nereden takip eder?</strong></dt>
        <dd>QR ile açılan oturum sayfasında her siparişin durumunu canlı görür.</dd>
      </dl>
    </article>

    <RelatedLinks
      class="mt-12"
      :items="[
        {
          to: '/qr-menu',
          label: 'QR Restoran Dijital Menü',
          desc: 'Menü ve mobil sipariş temelleri',
        },
        { to: '/qr-siparis', label: 'QR Sipariş Akışı', desc: 'Durum yönetimi adımları' },
        { to: '/dijital-menu', label: 'QR Dijital Menü Rehberi', desc: 'Akıllı menü avantajları' },
      ]"
    />
  </div>
</template>

<script setup>
  import { useHead } from "@unhead/vue";
  import RelatedLinks from "@/components/RelatedLinks.vue";

  const base = typeof window !== "undefined" ? window.location.origin : "https://qrmatik.cloud";
  const canonical = base + "/blog/mobil-siparis-sistemi-nedir";
  const title = "Mobil Sipariş Sistemi Nedir? | QrMatik";
  const description =
    "Mobil sipariş sistemi ile qr dijital menü üzerinden hızlı sipariş ve canlı takip. QR restoran altyapısının bileşenleri.";

  useHead({
    title,
    meta: [
      { name: "description", content: description },
      {
        name: "keywords",
        content: "mobil sipariş, qr restoran, qr dijital menü, akıllı qr menü, qr sipariş sistemi",
      },
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:type", content: "article" },
      { property: "og:url", content: canonical },
      { property: "og:image", content: base + "/og-image.svg" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
    link: [
      { rel: "canonical", href: canonical },
      { rel: "alternate", href: canonical, hreflang: "tr" },
    ],
    script: [
      {
        type: "application/ld+json",
        children: JSON.stringify({
          "@context": "https://schema.org",
          "@type": "Article",
          headline: title,
          description,
          datePublished: "2025-11-12",
          author: { "@type": "Organization", name: "QrMatik" },
        }),
      },
      {
        type: "application/ld+json",
        children: JSON.stringify({
          "@context": "https://schema.org",
          "@type": "FAQPage",
          mainEntity: [
            {
              "@type": "Question",
              name: "Mobil sipariş sistemi zor mu?",
              acceptedAnswer: {
                "@type": "Answer",
                text: "Hayır, birkaç adımda kurulur ve hemen kullanılabilir.",
              },
            },
            {
              "@type": "Question",
              name: "QR dijital menü ile fiyat güncellemesi nasıl çalışır?",
              acceptedAnswer: {
                "@type": "Answer",
                text: "Panelden güncellendiğinde müşteriler anında yeni fiyatı görür.",
              },
            },
            {
              "@type": "Question",
              name: "Müşteri sipariş durumunu nereden takip eder?",
              acceptedAnswer: {
                "@type": "Answer",
                text: "QR ile açılan oturum sayfasından canlı takip eder.",
              },
            },
          ],
        }),
      },
    ],
  });
</script>

<style scoped>
  .prose :where(h2, h3) {
    scroll-margin-top: 80px;
  }
</style>
