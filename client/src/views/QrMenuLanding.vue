<template>
  <div class="max-w-4xl mx-auto px-6 py-12">
    <header class="mb-8">
      <h1 class="text-3xl md:text-4xl font-bold text-gray-900 mb-3">
        Dijital QR Menü ve QR Sipariş Sistemi
      </h1>
      <p class="text-gray-600">
        QrMatik ile işletmenize <strong>dijital menü</strong>, <strong>QR sipariş</strong> ve
        <strong>akıllı QR menü</strong> altyapısını kolayca ekleyin. Restoranlar, kafeler ve barlar için hızlı
        kurulum, canlı takip, mutfak/bar panoları ve raporlama.
      </p>
    </header>



    <section class="grid gap-4 mb-10">
      <h2 class="text-2xl font-semibold">Öne çıkan özellikler</h2>
      <ul class="grid gap-2 text-gray-700">
        <li>• QR ile hızlı erişim; <em>Yeni → Hazırlanıyor → Hazır → Servis</em> akışında takip</li>
        <li>• Mutfak ve bar için ayrı panolar, tek tıkla durum güncelleme</li>
        <li>• Tablo/masa bazlı sipariş oturumları ve raporlar</li>
        <li>• Çok kiracılı yapı; her işletme kendi alt alan adında çalışır</li>
      </ul>
    </section>

    <section class="grid gap-3 mb-10">
      <h2 class="text-2xl font-semibold">Hızlı başlangıç</h2>
      <ol class="list-decimal pl-5 text-gray-700 space-y-1">
        <li>Kayıt olun ve işletme bilgilerinizi ekleyin.</li>
        <li>Menü kalemlerinizi ve fiyatlarınızı girin.</li>
        <li>Masa/alan kodları için karekod (QR) üretin ve yerleştirin.</li>
        <li>Müşterileriniz QR'ı okutup <strong>dijital menü</strong>den <strong>QR sipariş</strong> verir.</li>
      </ol>
    </section>

    <!-- Aranan terimler ve kullanım alanları: en altta akordeon -->
    <section class="py-2">
      <details class="group bg-white border rounded-xl shadow-sm">
        <summary class="cursor-pointer select-none px-4 py-3 flex items-center justify-between">
          <span class="font-semibold text-gray-900">Aranan terimler ve kullanım alanları</span>
          <span class="text-gray-400 group-open:rotate-180 transition-transform">▾</span>
        </summary>
        <div class="px-4 pb-4 text-sm text-gray-700 leading-6">
          <ul class="list-disc pl-5 space-y-1">
            <li><strong>qr restoran / restoran qr / restorant qr:</strong> Masaya özel karekod ile mobil sipariş.</li>
            <li><strong>dijital menü / dijital qr menü / qr digital menü:</strong> Menünüzü telefondan, anında güncel gösterin.</li>
            <li><strong>qr sipariş:</strong> Müşteriler masadan kalkmadan sipariş verir; durumunu canlı takip eder.</li>
            <li><strong>karekod restoran / restoran karekod menü:</strong> Tek QR ile menü ve sipariş akışı.</li>
            <li><strong>akıllı qr menü:</strong> Stok/menü değişiklikleri anında yansır; mutfak ve bar ekranlarına düşer.</li>
          </ul>
        </div>
      </details>
    </section>
    <!-- Sticky CTA (mobile): demo anchor on home -->
    <div class="fixed inset-x-0 bottom-0 z-50 md:hidden bg-white/90 backdrop-blur border-t">
      <div class="max-w-4xl mx-auto px-4 py-3 flex items-center gap-2">
  <a href="/#demo" class="flex-1 inline-flex items-center justify-center px-4 py-2 text-sm bg-brand-gradient text-white rounded-md shadow hover:opacity-95">Demo talep et</a>
        <router-link to="/" class="px-4 py-2 rounded-md border bg-white text-gray-800 hover:bg-gray-50">Anasayfa</router-link>
      </div>
    </div>
    <div class="h-16 md:hidden"></div>
  </div>

</template>

<script setup>
import { useHead } from '@unhead/vue'

useHead({
  title: 'Dijital QR Menü ve QR Sipariş | QrMatik',
  meta: [
    { name: 'description', content: 'QrMatik ile dijital menü, QR sipariş ve akıllı QR menü: restoran, kafe ve barlar için karekod menü ve mobil sipariş altyapısı.' },
    { property: 'og:title', content: 'Dijital QR Menü ve QR Sipariş | QrMatik' },
    { property: 'og:description', content: 'Restoranlar için dijital menü, QR sipariş ve akıllı QR menü. Karekod restoran menüsü ve canlı sipariş takibi.' },
    { property: 'og:type', content: 'website' },
    { name: 'twitter:card', content: 'summary_large_image' }
  ],
  script: [
    {
      type: 'application/ld+json',
      children: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'FAQPage',
        'mainEntity': [
          {
            '@type': 'Question',
            'name': 'Dijital QR menü nedir? Nasıl çalışır?',
            'acceptedAnswer': {
              '@type': 'Answer',
              'text': 'Dijital QR menü, masaya yerleştirilen karekodun telefonla okutulmasıyla açılan çevrim içi menüdür. QrMatik ile müşteriler menüden ürün seçip QR sipariş verir ve sipariş durumunu canlı takip eder.'
            }
          },
          {
            '@type': 'Question',
            'name': 'QR restoran (restorant) çözümü hangi işletmeler için uygun?',
            'acceptedAnswer': {
              '@type': 'Answer',
              'text': 'Restoran, kafe ve barlar için uygundur. Karekod restoran menüsüyle sipariş hızlanır; mutfak/bar ekranlarıyla hazırlık ve servis akışı düzenlenir.'
            }
          }
        ]
      })
    }
  ]
})
</script>

<style scoped>
</style>
