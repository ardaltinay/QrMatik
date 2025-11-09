<template>
  <div class="mx-auto max-w-3xl py-10">
    <h1 class="mb-4 text-3xl font-bold">Hakkında</h1>
    <p class="leading-7 text-gray-700">
      QrMatik; restoran, kafe ve benzeri işletmeler için QR tabanlı dijital menü, mobil sipariş,
      mutfak/bar yönetimi ve ödeme süreçlerini bir araya getiren modern bir platformdur.
    </p>
    <div class="mt-8 grid gap-6 md:grid-cols-2">
      <div class="rounded-xl border bg-white p-5 shadow-sm">
        <div class="mb-2 font-semibold">Öne Çıkanlar</div>
        <ul class="list-disc space-y-1 pl-5 text-sm text-gray-600">
          <li>QR ile hızlı başlangıç</li>
          <li>Menü yönetimi ve raporlar</li>
          <li>Gelişmiş stok yönetimi</li>
          <li>Mutfak ve bar ekranları</li>
          <li>Sipariş takibi, iptal ve ödeme</li>
        </ul>
      </div>

      <!-- Hikayemiz -->
      <div class="rounded-xl border bg-white p-5 shadow-sm">
        <div class="mb-2 font-semibold">Hikayemiz</div>
        <p class="text-sm leading-6 text-gray-600">
          QrMatik, restoranlarda beklemeyi azaltmak ve hataları en aza indirmek amacıyla doğdu.
          Bugün onlarca işletmenin her gün binlerce siparişi hızlı ve hatasız yönetmesine yardımcı
          oluyoruz.
        </p>
      </div>

      <!-- Güvenlik & Gizlilik -->
      <div class="rounded-xl border bg-white p-5 shadow-sm">
        <div class="mb-2 font-semibold">Güvenlik & Gizlilik</div>
        <ul class="list-disc space-y-1 pl-5 text-sm leading-6 text-gray-600">
          <li>Tüm trafik HTTPS ile şifrelenir</li>
          <li>Rol bazlı erişim ve yetkilendirme</li>
          <li>Günlük otomatik yedekleme rutini</li>
          <li>KVKK'ya uyumlu veri işleme</li>
        </ul>
      </div>

      <!-- Destek & SLA -->
      <div class="rounded-xl border bg-white p-5 shadow-sm">
        <div class="mb-2 font-semibold">Destek & SLA</div>
        <ul class="list-disc space-y-1 pl-5 text-sm leading-6 text-gray-600">
          <li>Hafta içi 09:00–18:00 destek</li>
          <li>Ortalama ilk yanıt: 30 dakikanın altında</li>
          <li>
            E-posta:
            <a class="text-brand-600 hover:underline" href="mailto:qrmatik.cloud@gmail.com"
              >qrmatik.cloud@gmail.com</a
            >
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { useHead } from "@unhead/vue";

  useHead({
    title: "Hakkında — QrMatik",
    meta: [
      {
        name: "description",
        content: "QrMatik: QR tabanlı dijital menü, mobil sipariş ve işletme yönetimi platformu.",
      },
      { property: "og:title", content: "Hakkında — QrMatik" },
      {
        property: "og:description",
        content: "QR ile menü, sipariş ve işletme yönetimi. QrMatik hakkında daha fazla bilgi.",
      },
      { property: "og:type", content: "website" },
    ],
  });
</script>
