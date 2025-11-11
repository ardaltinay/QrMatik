<template>
  <div class="mx-auto max-w-4xl px-6 py-12">
    <header class="mb-8">
      <h1 class="mb-3 text-3xl font-bold text-gray-900 md:text-4xl">
        Restoran ve Bar Menü Yönetimi
      </h1>
      <p class="text-gray-600">
        QrMatik ile <strong>restoran bar menüsü</strong>nü dijitalleştirin. QR menü, hızlı fiyat
        güncelleme, stok kontrolü ve bar/mutfak panoları ile akıcı bir sipariş akışı sağlayın.
      </p>
    </header>

    <section class="mb-10 grid gap-4">
      <h2 class="text-2xl font-semibold">Size ne kazandırır?</h2>
      <ul class="grid gap-2 text-gray-700">
        <li>• Bar ve mutfak için ayrı ekranlar; tek tıkla durum güncelleme</li>
        <li>• Kategori ve ürün bazlı detaylı menü yönetimi</li>
        <li>• Stok durumuna göre ürünlerin otomatik gizlenmesi</li>
        <li>• Raporlar ile satış performansını ölçme</li>
      </ul>
    </section>

    <section class="mb-10 grid gap-3">
      <h2 class="text-2xl font-semibold">Başlamak çok kolay</h2>
      <ol class="list-decimal space-y-1 pl-5 text-gray-700">
        <li>İşletmenizi oluşturun ve alan adınızı alın.</li>
        <li>Menü öğelerini ve kategorileri ekleyin.</li>
        <li>Masalara karekod yerleştirin ve siparişleri toplamaya başlayın.</li>
      </ol>
    </section>

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
    <RelatedLinks
      :items="[
        { to: '/dijital-menu', label: 'Dijital menü', desc: 'Stok kontrollü akıllı menü' },
        { to: '/qr-siparis', label: 'QR sipariş', desc: 'Sipariş ve durum takibi' },
        { to: '/#pricing', label: 'Planlar', desc: 'STANDARD ve PRO farkları' },
      ]"
    />
  </div>
</template>

<script setup>
  import { useHead } from "@unhead/vue";
  import RelatedLinks from "@/components/RelatedLinks.vue";

  const title = "Restoran Bar Menü ve Dijital Menü Yönetimi | QrMatik";
  const description =
    "Restoran ve barlar için dijital menü yönetimi: QR menü, stok kontrolü, bar/mutfak panoları ve hızlı fiyat güncelleme.";
  const baseUrl = typeof window !== "undefined" ? window.location.origin : "https://qrmatik.cloud";
  const canonical = baseUrl + "/restoran-bar-menu";

  useHead({
    title,
    meta: [
      { name: "description", content: description },
      { name: "keywords", content: "restoran bar menü, bar menüsü, dijital menü, karekod menü" },
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:type", content: "website" },
      { property: "og:image", content: baseUrl + "/og-image.svg" },
      { name: "twitter:image", content: baseUrl + "/og-image.svg" },
      { property: "og:url", content: canonical },
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
          "@type": "BreadcrumbList",
          itemListElement: [
            {
              "@type": "ListItem",
              position: 1,
              name: "Anasayfa",
              item: baseUrl + "/",
            },
            {
              "@type": "ListItem",
              position: 2,
              name: "Restoran Bar Menü",
              item: canonical,
            },
          ],
        }),
      },
    ],
  });
</script>

<style scoped></style>
