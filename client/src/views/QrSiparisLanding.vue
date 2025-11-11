<template>
  <div class="mx-auto max-w-4xl px-6 py-12">
    <header class="mb-8">
      <h1 class="mb-3 text-3xl font-bold text-gray-900 md:text-4xl">QR Sipariş Sistemi</h1>
      <p class="text-gray-600">
        QrMatik ile <strong>QR sipariş</strong>: Müşteriler masadan kalkmadan sipariş verir, durumu
        canlı takip eder. Mutfak ve bar panoları ile hatasız akış.
      </p>
    </header>

    <section class="mb-10 grid gap-4">
      <h2 class="text-2xl font-semibold">Öne çıkanlar</h2>
      <ul class="grid gap-2 text-gray-700">
        <li>• Masa kodu ile siparişler doğru masaya yönlenir</li>
        <li>• Durumlar: Yeni → Hazırlanıyor → Hazır → Servis</li>
        <li>• Bar ve mutfak ekranları ayrı; tek tıkla güncelleme</li>
        <li>• Raporlar ve oturum bazlı analiz</li>
      </ul>
    </section>

    <section class="mb-10 grid gap-3">
      <h2 class="text-2xl font-semibold">3 adımda kurulum</h2>
      <ol class="list-decimal space-y-1 pl-5 text-gray-700">
        <li>İşletme hesabınızı oluşturun.</li>
        <li>Menünüzü ve fiyatlarınızı ekleyin.</li>
        <li>Masalara QR yerleştirin ve siparişi başlatın.</li>
      </ol>
    </section>

    <div class="fixed inset-x-0 bottom-0 z-50 border-t bg-white/90 backdrop-blur md:hidden">
      <div class="mx-auto flex max-w-4xl items-center gap-2 px-4 py-3">
        <a href="/#demo" class="bg-brand-gradient inline-flex flex-1 items-center justify-center rounded-md px-4 py-2 text-sm text-white shadow hover:opacity-95">Demo talep et</a>
        <router-link to="/" class="rounded-md border bg-white px-4 py-2 text-gray-800 hover:bg-gray-50">Anasayfa</router-link>
      </div>
    </div>
    <div class="h-16 md:hidden"></div>
    <RelatedLinks
      :items="[
        { to: '/dijital-menu', label: 'Dijital menü rehberi', desc: 'Akıllı QR menü detayları' },
        { to: '/restoran-bar-menu', label: 'Bar & mutfak yönetimi', desc: 'Hazırlanıyor → Servis' },
        { to: '/#pricing', label: 'Planlar ve fiyatlar', desc: 'Fiyatlandırma karşılaştırması' }
      ]"
    />
  </div>
</template>

<script setup>
  import { useHead } from "@unhead/vue";
  import RelatedLinks from "@/components/RelatedLinks.vue";

  const title = "QR Sipariş Sistemi | QrMatik";
  const description =
    "QrMatik ile QR sipariş: Mobil sipariş verme, canlı takip, mutfak & bar panoları ve raporlama ile hızlı servis.";
  const baseUrl = typeof window !== "undefined" ? window.location.origin : "https://qrmatik.cloud";
  const canonical = baseUrl + "/qr-siparis";

  useHead({
    title,
    meta: [
      { name: "description", content: description },
      { name: "keywords", content: "qr sipariş, qr restoran sipariş, dijital sipariş, mobil sipariş" },
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
          "@type": "HowTo",
          name: "QR sipariş sistemi nasıl kurulur?",
          step: [
            { "@type": "HowToStep", text: "İşletme hesabı oluşturun" },
            { "@type": "HowToStep", text: "Menü öğelerini ve fiyatları ekleyin" },
            { "@type": "HowToStep", text: "Masalara QR kod yerleştirin" },
            { "@type": "HowToStep", text: "Müşteriler sipariş vermeye başlasın" },
          ],
        }),
      },
    ],
  });
</script>

<style scoped></style>
