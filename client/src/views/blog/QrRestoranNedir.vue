<template>
  <article class="prose prose-indigo mx-auto max-w-3xl px-6 py-12">
    <header>
      <h1>QR Restoran Nedir? Dijitalleşmenin Temelleri</h1>
      <p class="text-sm text-gray-500">Yayın tarihi: {{ formatted }}</p>
    </header>

    <p>
      QR restoran, menü ve sipariş süreçlerinin karekod (QR) ile mobil cihazlar üzerinden yönetildiği
      işletme modelidir. <strong>Dijital menü</strong> ve <strong>QR sipariş</strong> ile servis hızlanır,
      hatalar azalır ve operasyon görünürlük kazanır.
    </p>

    <h2>Dijital Menü Neden Önemli?</h2>
    <ul>
      <li>Her zaman güncel fiyat ve içerik</li>
      <li>Stok kontrolü ile tükenen ürünlerin otomatik gizlenmesi</li>
      <li>Çok dilli içerik imkanı</li>
    </ul>

    <h2>QR Sipariş Akışı</h2>
    <p>Yeni → Hazırlanıyor → Hazır → Servis adımlarında net takip ve bildirimler.</p>

    <h2>Nasıl Başlanır?</h2>
    <ol>
      <li>İşletme hesabı oluşturun</li>
      <li>Menü öğelerini ve fiyatları ekleyin</li>
      <li>Masalara QR kod yerleştirin</li>
    </ol>

    <p class="mt-6">Devamını keşfedin: <router-link to="/dijital-menu">Akıllı QR menü</router-link> ve
      <router-link to="/qr-siparis">QR sipariş</router-link>.</p>
  </article>
</template>

<script setup>
import { useHead } from '@unhead/vue';
const title = 'QR Restoran Nedir? Dijitalleşmenin Temelleri | QrMatik';
const description = 'QR restoran modeli, dijital menü ve QR sipariş ile operasyonları hızlandırır. Avantajlar ve uygulama adımlarını keşfedin.';
const url = (typeof window!== 'undefined' ? window.location.origin : 'https://qrmatik.cloud') + '/blog/qr-restoran-nedir';
const datePublished = '2025-11-10';

useHead({
  title,
  meta: [
    { name: 'description', content: description },
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: 'article' },
    { property: 'og:url', content: url },
    { property: 'article:published_time', content: datePublished },
    { name: 'twitter:card', content: 'summary_large_image' }
  ],
  link: [{ rel: 'canonical', href: url }],
  script: [
    {
      type: 'application/ld+json',
      children: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'Article',
        headline: 'QR Restoran Nedir? Dijitalleşmenin Temelleri',
        datePublished: datePublished,
        author: { '@type': 'Organization', name: 'QrMatik' },
        mainEntityOfPage: url
      })
    }
  ]
});

const formatted = new Date(datePublished).toLocaleDateString('tr-TR');
</script>
