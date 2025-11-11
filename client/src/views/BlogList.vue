<template>
  <div class="mx-auto max-w-4xl px-6 py-12">
    <header class="mb-8">
      <h1 class="mb-3 text-3xl font-bold text-gray-900 md:text-4xl">Blog</h1>
      <p class="text-gray-600">QR restoran, dijital menü ve mobil sipariş dünyasından rehberler.</p>
    </header>
    <div v-if="!posts.length" class="text-sm text-gray-500">İçerik yükleniyor…</div>
    <ul v-else class="space-y-4">
      <li v-for="p in posts" :key="p.slug" class="rounded-lg border bg-white p-4 shadow-sm">
        <router-link :to="'/blog/' + p.slug" class="text-lg font-semibold text-indigo-700 hover:underline">{{ p.title }}</router-link>
        <div class="mt-1 text-xs text-gray-500">{{ formatDate(p.date) }}</div>
        <p class="mt-2 text-sm text-gray-600">{{ p.excerpt }}</p>
      </li>
    </ul>
  </div>
</template>
<script setup>
import { useHead } from '@unhead/vue';
import { ref } from 'vue';

useHead({
  title: 'Blog | QrMatik',
  meta: [
    { name: 'description', content: 'QR restoran ve dijital menü rehberleri: qr sipariş sistemi, akıllı qr menü, stok kontrollü mobil menü.' },
    { property: 'og:title', content: 'Blog | QrMatik' },
    { property: 'og:type', content: 'website' },
  ],
  link: [{ rel: 'canonical', href: (typeof window!== 'undefined' ? window.location.origin : 'https://qrmatik.cloud') + '/blog' }]
});

const posts = ref([
  {
    slug: 'qr-restoran-nedir',
    title: 'QR Restoran Nedir? Dijitalleşmenin Temelleri',
    date: '2025-11-10',
    excerpt:
      'QR restoran modeli: dijital menü, mobil sipariş ve operasyonel hızın birleşimi. Avantajlar ve uygulama adımları.',
  },
  {
    slug: 'dijital-menu-avantajlari',
    title: 'Dijital Menü Avantajları: Restoranlarda Verimlilik ve Deneyim',
    date: '2025-11-10',
    excerpt:
      'Dijital menü (akıllı QR menü) ile güncel içerik, stok entegrasyonu ve hızlı servis: restoranlar için başlıca faydalar.',
  },
]);

function formatDate(d){
  try { return new Date(d).toLocaleDateString('tr-TR'); } catch { return d; }
}
</script>
