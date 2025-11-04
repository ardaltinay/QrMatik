<template>
  <div class="max-w-3xl mx-auto py-10">
    <h1 class="text-3xl font-bold mb-4">Hakkında</h1>
    <p class="text-gray-700 leading-7">
      QrMatik; restoran, kafe ve benzeri işletmeler için QR tabanlı dijital menü, mobil sipariş,
      mutfak/bar yönetimi ve ödeme süreçlerini bir araya getiren modern bir platformdur.
    </p>
    <div class="grid md:grid-cols-2 gap-6 mt-8">
      <div class="p-5 rounded-xl border bg-white shadow-sm">
        <div class="font-semibold mb-2">Vizyon</div>
        <p class="text-sm text-gray-600">Hızlı ve temassız restoran deneyimini herkes için erişilebilir kılmak.</p>
      </div>
      <div class="p-5 rounded-xl border bg-white shadow-sm">
        <div class="font-semibold mb-2">Öne Çıkanlar</div>
        <ul class="list-disc pl-5 text-sm text-gray-600 space-y-1">
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
    { name: "description", content: "QrMatik: QR tabanlı dijital menü, mobil sipariş ve işletme yönetimi platformu." },
    { property: "og:title", content: "Hakkında — QrMatik" },
    { property: "og:description", content: "QR ile menü, sipariş ve işletme yönetimi. QrMatik hakkında daha fazla bilgi." },
    { property: "og:type", content: "website" },
  ],
});
</script>
