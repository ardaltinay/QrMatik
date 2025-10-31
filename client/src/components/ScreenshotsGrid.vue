<template>
  <section class="py-12">
    <div class="container mx-auto px-6">
      <div class="flex items-end justify-between mb-6">
        <div>
          <h2 class="text-2xl md:text-3xl font-bold text-gray-900">Ekran Yapıları</h2>
          <p class="text-gray-600 mt-1">Basit, hızlı ve kullanışlı ekran yapıları</p>
        </div>
      </div>

      <!-- Mobile: horizontal snap carousel -->
      <div class="md:hidden -mx-6 px-6 overflow-x-auto snap-x snap-mandatory no-scrollbar">
        <div class="flex gap-4 pr-6">
          <figure
            v-for="(s, i) in shots"
            :key="s.alt"
            class="min-w-[80%] snap-center rounded-xl border bg-white shadow-sm overflow-hidden cursor-pointer"
            @click="open(i)"
          >
            <img :src="s.src" :alt="s.alt" class="w-full h-48 object-cover" loading="lazy" />
            <figcaption class="px-3 py-2 text-sm text-gray-600">{{ s.alt }}</figcaption>
          </figure>
        </div>
      </div>

      <!-- Desktop: 4-column grid with hover -->
      <div class="hidden md:grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <figure
          v-for="(s, i) in shots"
          :key="s.alt"
          class="group rounded-xl border bg-white shadow-sm overflow-hidden cursor-pointer"
          @click="open(i)"
        >
          <div class="relative">
            <img :src="s.src" :alt="s.alt" class="w-full h-40 object-cover transition-transform duration-200 group-hover:scale-[1.02]" loading="lazy" />
            <div class="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity bg-gradient-to-t from-black/20 to-transparent"></div>
          </div>
          <figcaption class="px-3 py-2 text-sm text-gray-600">{{ s.alt }}</figcaption>
        </figure>
      </div>

      <!-- Lightbox Modal -->
      <div v-if="lightbox.open" class="fixed inset-0 z-[60]" @keydown.esc="close" tabindex="0">
        <div class="absolute inset-0 bg-black/70" @click="close" aria-hidden="true"></div>
        <div class="absolute inset-0 flex items-center justify-center px-4">
          <div class="relative w-full max-w-5xl">
            <div class="bg-white rounded-xl shadow-xl overflow-hidden">
              <div class="flex items-center justify-between px-4 py-2 border-b">
                <div class="text-sm text-gray-600">{{ current.alt }}</div>
                <button class="px-2 py-1 text-gray-600 hover:text-gray-800" @click="close" aria-label="Kapat">✕</button>
              </div>
              <div class="bg-gray-50 flex items-center justify-center">
                <img :src="current.src" :alt="current.alt" class="max-h-[70vh] w-auto object-contain" />
              </div>
              <div class="flex items-center justify-between px-4 py-2 border-t text-sm text-gray-600">
                <div>{{ lightbox.index + 1 }} / {{ shots.length }}</div>
                <div class="flex gap-2">
                  <button class="px-3 py-1 rounded border hover:bg-gray-50" @click="prev" aria-label="Önceki">Önceki</button>
                  <button class="px-3 py-1 rounded bg-brand-500 text-white" @click="next" aria-label="Sonraki">Sonraki</button>
                </div>
              </div>
            </div>
            <!-- Nav arrows -->
            <button class="absolute left-0 top-1/2 -translate-y-1/2 p-3 text-white hover:text-brand-500" @click="prev" aria-label="Önceki">
              ‹
            </button>
            <button class="absolute right-0 top-1/2 -translate-y-1/2 p-3 text-white hover:text-brand-500" @click="next" aria-label="Sonraki">
              ›
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import menu from "@/assets/screens/menu.svg";
import orders from "@/assets/screens/orders.svg";
import admin from "@/assets/screens/admin.svg";
import kitchen from "@/assets/screens/kitchen.svg";

export default {
  name: "ScreenshotsGrid",
  data() {
    return {
      shots: [
        { src: menu, alt: "Müşteri Menüsü" },
        { src: orders, alt: "Sipariş Takibi" },
        { src: admin, alt: "Admin Paneli" },
        { src: kitchen, alt: "Mutfak/Bar Ekranı" },
      ],
      lightbox: {
        open: false,
        index: 0,
      },
    };
  },
  computed: {
    current() {
      const i = Math.min(Math.max(this.lightbox.index, 0), this.shots.length - 1);
      return this.shots[i] || { src: '', alt: '' };
    },
  },
  methods: {
    open(i = 0) {
      this.lightbox.index = i;
      this.lightbox.open = true;
      this.$nextTick(() => {
        const root = this.$el;
        if (root && typeof root.querySelector === 'function') {
          const el = root.querySelector('[tabindex="0"]');
          if (el && typeof el.focus === 'function') el.focus();
        }
      });
    },
    close() {
      this.lightbox.open = false;
    },
    prev() {
      if (!this.shots.length) return;
      this.lightbox.index = (this.lightbox.index - 1 + this.shots.length) % this.shots.length;
    },
    next() {
      if (!this.shots.length) return;
      this.lightbox.index = (this.lightbox.index + 1) % this.shots.length;
    },
  },
};
</script>
