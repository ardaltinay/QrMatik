<template>
  <article class="prose prose-indigo mx-auto max-w-3xl px-6 py-12">
    <header>
      <h1>Dijital Menü Avantajları: Restoranlarda Verimlilik ve Deneyim</h1>
      <p class="text-sm text-gray-500">Yayın tarihi: {{ formatted }}</p>
    </header>

    <p>
      <strong>Dijital menü</strong>, fiyat ve içerik güncellemelerini anında yayına almanızı sağlar.
      <strong>Akıllı QR menü</strong> ile müşteriler telefondan menüyü görüntüler, sipariş verir ve
      durumu takip eder.
    </p>

    <h2>Başlıca faydalar</h2>
    <ul>
      <li>Her zaman güncel içerik ve fiyatlar</li>
      <li>Stokla entegre görünürlük; tükenen ürünler otomatik gizlenir</li>
      <li>Bekleme süresinde azalma ve daha hızlı servis</li>
      <li>Hata oranında düşüş; bar ve mutfak panolarıyla net akış</li>
    </ul>

    <h2>Operasyonel etkiler</h2>
    <p>
      Bar ve mutfak ekranları ile <em>Yeni → Hazırlanıyor → Hazır → Servis</em> durum akışı
      şeffaflaşır. Raporlar, oturum bazlı sipariş metriklerini görünür kılar.
    </p>

    <h2>Uygulama adımları</h2>
    <ol>
      <li>İşletme hesabı oluşturun</li>
      <li>Menü ve fiyatları ekleyin</li>
      <li>Masalara QR yerleştirerek dijital menüyü yayına alın</li>
    </ol>

    <p class="mt-6">
      İlgili rehberler: <router-link to="/qr-siparis">QR sipariş akışı</router-link>,
      <router-link to="/restoran-bar-menu">Bar & Mutfak Panoları</router-link>.
    </p>
  </article>
</template>

<script setup>
  import { useHead } from "@unhead/vue";
  const title = "Dijital Menü Avantajları | QrMatik";
  const description =
    "Dijital menü (akıllı QR menü) ile güncel içerik, stok entegrasyonu ve hızlı servis: restoranlar için avantajlar.";
  const url =
    (typeof window !== "undefined" ? window.location.origin : "https://qrmatik.cloud") +
    "/blog/dijital-menu-avantajlari";
  const datePublished = "2025-11-10";

  useHead({
    title,
    meta: [
      { name: "description", content: description },
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:type", content: "article" },
      { property: "og:url", content: url },
      { name: "twitter:card", content: "summary_large_image" },
    ],
    link: [{ rel: "canonical", href: url }],
    script: [
      {
        type: "application/ld+json",
        children: JSON.stringify({
          "@context": "https://schema.org",
          "@type": "Article",
          headline: "Dijital Menü Avantajları: Restoranlarda Verimlilik ve Deneyim",
          datePublished: datePublished,
          author: { "@type": "Organization", name: "QrMatik" },
          mainEntityOfPage: url,
        }),
      },
    ],
  });

  const formatted = new Date(datePublished).toLocaleDateString("tr-TR");
</script>
