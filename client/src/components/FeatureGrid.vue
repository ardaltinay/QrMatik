<template>
  <section class="py-12">
    <div class="container mx-auto">
      <div class="mb-1 text-xs font-semibold uppercase tracking-wide text-indigo-600">
        Öne Çıkanlar
      </div>
      <h2 class="mb-6 text-2xl font-bold text-gray-900 md:text-3xl">Neler Yapabilirsiniz?</h2>
      <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div
          v-for="f in features"
          :key="f.title"
          class="rounded-xl border bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
        >
          <div class="mb-3 flex items-center gap-3">
            <div
              class="flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-50 text-indigo-600"
            >
              <!-- Güvenlik: SVG dizgisi sadece iç kaynaklı; dış input yok. XSS riskini azaltmak için inline render yerine compute edilmiş vnode kullanabiliriz. -->
              <component :is="f.icon" aria-hidden="true" />
            </div>
            <div class="text-lg font-semibold">{{ f.title }}</div>
          </div>
          <p class="text-sm leading-6 text-gray-600">{{ f.desc }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
  import { h } from "vue";
  const makeIcon = (paths) => {
    return {
      render() {
        return h(
          'svg',
          {
            xmlns: 'http://www.w3.org/2000/svg',
            viewBox: '0 0 24 24',
            fill: 'none',
            stroke: 'currentColor',
            'stroke-width': '2',
            'stroke-linecap': 'round',
            'stroke-linejoin': 'round',
            class: 'w-6 h-6'
          },
          paths.map(p => h('path', p))
        );
      }
    };
  };

  const features = [
    {
      title: "Menüden Sipariş",
      desc: "QR ile dijital menüye erişin ve masadan kalkmadan mobil sipariş verin.",
      icon: makeIcon([
        { d: 'M5 12h14' },
        { d: 'M7 4h10' },
        { d: 'M9 8v8' },
        { d: 'M15 8v6' },
      ]),
    },
    {
      title: "Sipariş Takibi & İptal",
      desc: "Hazırlanıyor → Hazır → Servis akışını anlık takip edin, gerekirse iptal edin.",
      icon: makeIcon([
        { d: 'M9 12l2 2 4-4' },
        { tag: 'circle', cx: '12', cy: '12', r: '9' },
      ]),
    },
    {
      title: "Bildirim Sistemi",
      desc: "Sipariş durumu güncellemeleri ve müşteri bildirimlerini anında görün.",
      icon: makeIcon([
        { tag: 'rect', x: '3', y: '6', width: '18', height: '12', rx: '2' },
        { d: 'M3 10h18' },
      ]),
    },
    {
      title: "Admin Paneli",
      desc: "Menü yönetimi, fiyat güncelleme ve raporlara tek yerden erişin.",
      icon: makeIcon([
        { d: 'M12 6v6l4 2' },
        { tag: 'circle', cx: '12', cy: '12', r: '9' },
      ]),
    },
    {
      title: "Mutfak & Bar Ekranları",
      desc: "Hazırlama akışını panolardan yönetin; durumları tek dokunuşla güncelleyin.",
      icon: makeIcon([
        { d: 'M4 3h16' },
        { d: 'M9 7v13' },
        { d: 'M15 7v13' },
        { d: 'M4 7h16' },
      ]),
    },
    {
      title: "QR ile Hızlı Başlangıç",
      desc: "Her masa için QR kodları oluşturup anında yayına alın.",
      icon: {
        render() {
          return h(
            'svg',
            { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'currentColor', class: 'w-6 h-6' },
            [
              h('path', {
                d: 'M3 3h8v8H3V3zm2 2v4h4V5H5zM13 3h8v8h-8V3zm2 2v4h4V5h-4zM3 13h8v8H3v-8zm2 2v4h4v-4H5zM15 13h2v2h-2v-2zm4 0h2v2h-2v-2zm-4 4h2v2h-2v-2zm4 0h2v4h-4v-2h2v-2z'
              })
            ]
          );
        }
      },
    },
  ];
</script>
