<template>
  <div class="mx-auto max-w-3xl py-10">
    <h1 class="mb-4 text-3xl font-bold">Hakkında</h1>
    <p class="leading-7 text-gray-700">
      QrMatik; restoran, kafe ve benzeri işletmeler için QR tabanlı dijital menü, mobil sipariş,
      mutfak/bar yönetimi ve ödeme süreçlerini bir araya getiren modern bir platformdur.
    </p>
    <div class="mt-8 grid gap-6 md:grid-cols-2">
      <div class="rounded-xl border bg-white p-5 shadow-sm">
        <div class="mb-2 font-semibold">Vizyon</div>
        <p class="text-sm text-gray-600">
          Hızlı ve temassız restoran deneyimini herkes için erişilebilir kılmak.
        </p>
      </div>
      <div class="rounded-xl border bg-white p-5 shadow-sm">
        <div class="mb-2 font-semibold">Öne Çıkanlar</div>
        <ul class="list-disc space-y-1 pl-5 text-sm text-gray-600">
          <li>QR ile hızlı başlangıç</li>
          <li>Menü yönetimi ve raporlar</li>
          <li>Mutfak ve bar ekranları</li>
          <li>Sipariş takibi, iptal ve ödeme</li>
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
