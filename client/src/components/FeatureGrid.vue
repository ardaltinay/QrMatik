<template>
  <section class="py-12">
    <div class="container mx-auto">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 mb-6">Neler Yapabilirsiniz?</h2>
      <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div v-for="f in features" :key="f.title" class="p-5 rounded-xl border bg-white shadow-sm">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 rounded-lg bg-indigo-50 text-indigo-600 flex items-center justify-center">
              <span v-html="f.iconSvg" aria-hidden="true"></span>
            </div>
            <div class="text-lg font-semibold">{{ f.title }}</div>
          </div>
          <p class="text-gray-600 text-sm leading-6">{{ f.desc }}</p>
        </div>
      </div>
    </div>
  </section>
  </template>

<script setup>
const makeIcon = (paths) =>
  `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="w-6 h-6">${paths}</svg>`;

const features = [
  {
    title: "Menüden Sipariş",
    desc: "QR ile dijital menüye erişin ve masadan kalkmadan mobil sipariş verin.",
    iconSvg: makeIcon('<path d="M5 12h14"/><path d="M7 4h10"/><path d="M9 8v8"/><path d="M15 8v6"/>'),
  },
  {
    title: "Sipariş Takibi & İptal",
    desc: "Hazırlanıyor → Hazır → Servis akışını anlık takip edin, gerekirse iptal edin.",
    iconSvg: makeIcon('<path d="M9 12l2 2 4-4"/><circle cx="12" cy="12" r="9"/>'),
  },
  {
    title: "Online Ödeme",
    desc: "Ödemeyi doğrudan sistem üzerinden güvenle tamamlayın.",
    iconSvg: makeIcon('<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M3 10h18"/>'),
  },
  {
    title: "Admin Paneli",
    desc: "Menü yönetimi, fiyat güncelleme ve raporlara tek yerden erişin.",
    iconSvg: makeIcon('<path d="M12 6v6l4 2"/><circle cx="12" cy="12" r="9"/>'),
  },
  {
    title: "Mutfak & Bar Ekranları",
    desc: "Hazırlama akışını panolardan yönetin; durumları tek dokunuşla güncelleyin.",
    iconSvg: makeIcon('<path d="M4 3h16"/><path d="M9 7v13"/><path d="M15 7v13"/><path d="M4 7h16"/>'),
  },
  {
    title: "QR ile Hızlı Başlangıç",
    desc: "Her masa için QR kodları oluşturup anında yayına alın.",
    iconSvg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6"><path d="M3 3h8v8H3V3zm2 2v4h4V5H5zM13 3h8v8h-8V3zm2 2v4h4V5h-4zM3 13h8v8H3v-8zm2 2v4h4v-4H5zM15 13h2v2h-2v-2zm4 0h2v2h-2v-2zm-4 4h2v2h-2v-2zm4 0h2v4h-4v-2h2v-2z"/></svg>`,
  },
];
</script>
