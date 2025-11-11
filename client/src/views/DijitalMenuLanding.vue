<template>
  <div class="mx-auto max-w-4xl px-6 py-12">
    <header class="mb-8">
      <h1 class="mb-3 text-3xl font-bold text-gray-900 md:text-4xl">Dijital Menü (Akıllı QR Menü)</h1>
      <p class="text-gray-600">
        QrMatik ile <strong>dijital menü</strong> ve <strong>akıllı QR menü</strong> altyapısını birkaç adımda kurun.
        Restoran, kafe ve barlar için hızlı kurulum; canlı sipariş takibi; mutfak & bar panoları.
      </p>
    </header>

    <section class="mb-10 grid gap-4">
      <h2 class="text-2xl font-semibold">Neden dijital menü?</h2>
      <ul class="grid gap-2 text-gray-700">
        <li>• Müşteriler menüyü telefondan, her zaman güncel görür</li>
        <li>• Fiyat ve içerik değişiklikleri anında yayında</li>
        <li>• Stok kontrolü ile tükenen ürünler otomatik gizlenir</li>
        <li>• QR sipariş ile bekleme süresi azalır, servis hızlanır</li>
      </ul>
    </section>

    <section class="mb-10 grid gap-3">
      <h2 class="text-2xl font-semibold">Kurulum çok kolay</h2>
      <ol class="list-decimal space-y-1 pl-5 text-gray-700">
        <li>Kayıt olun ve işletme bilgilerinizi ekleyin.</li>
        <li>Menü öğelerinizi ve fiyatlarınızı girin.</li>
        <li>Masalara karekod (QR) yerleştirin.</li>
        <li>Hazır! Dijital menünüz ve QR sipariş akışınız çalışıyor.</li>
      </ol>
    </section>

    <section class="py-2">
      <details class="group rounded-xl border bg-white shadow-sm">
        <summary class="flex cursor-pointer select-none items-center justify-between px-4 py-3">
          <span class="font-semibold text-gray-900">Sık sorulanlar</span>
          <span class="text-gray-400 transition-transform group-open:rotate-180">▾</span>
        </summary>
        <div class="px-4 pb-4 text-sm leading-6 text-gray-700">
          <ul class="list-disc space-y-1 pl-5">
            <li><strong>Akıllı QR menü</strong> nedir? — Güncel fiyat ve içerikleri otomatik gösteren, stokla entegre dijital menüdür.</li>
            <li>QR menü çok dilli mi? — Evet, içerikleri istediğiniz dilde sunabilirsiniz.</li>
            <li>Menüye görsel ekleyebilir miyim? — Evet, ürün fotoğrafları desteklenir.</li>
          </ul>
        </div>
      </details>
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
        { to: '/qr-siparis', label: 'QR sipariş akışı', desc: 'Adım adım sipariş durumları' },
        { to: '/restoran-bar-menu', label: 'Bar ve mutfak panoları', desc: 'Operasyon yönetimi' },
        { to: '/#pricing', label: 'Planlar ve fiyatlar', desc: 'STANDARD ve PRO planlar' }
      ]"
    />
  </div>
</template>

<script setup>
  import { useHead } from "@unhead/vue";
  import RelatedLinks from "@/components/RelatedLinks.vue";

  const title = "Dijital Menü (Akıllı QR Menü) | QrMatik";
  const description =
    "QrMatik ile dijital menü ve akıllı QR menü: her zaman güncel menü, stok kontrolü, QR sipariş ve mutfak/bar panoları.";
  const baseUrl = typeof window !== "undefined" ? window.location.origin : "https://qrmatik.cloud";
  const canonical = baseUrl + "/dijital-menu";

  useHead({
    title,
    meta: [
      { name: "description", content: description },
      { name: "keywords", content: "dijital menü, akıllı qr menü, qr menü, karekod menü, restoran menü" },
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
          "@type": "FAQPage",
          mainEntity: [
            {
              "@type": "Question",
              name: "Dijital menü nedir?",
              acceptedAnswer: {
                "@type": "Answer",
                text:
                  "Dijital menü, telefonla görüntülenen çevrim içi menüdür. QrMatik ile menünüz her zaman güncel ve stokla entegredir.",
              },
            },
            {
              "@type": "Question",
              name: "Akıllı QR menü nasıl çalışır?",
              acceptedAnswer: {
                "@type": "Answer",
                text:
                  "Masaya yerleştirilen QR kodu okutulur; müşteri menüyü görür, sipariş verir ve durumu canlı takip eder.",
              },
            },
          ],
        }),
      },
    ],
  });
</script>

<style scoped></style>
