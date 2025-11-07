<template>
  <section class="py-12">
    <div class="container mx-auto px-6">
      <div class="mb-6 flex items-end justify-between">
        <div>
          <h2 class="text-2xl font-bold text-gray-900 md:text-3xl">Ekran Yapıları</h2>
          <p class="mt-1 text-gray-600">Basit, hızlı ve kullanışlı ekran yapıları</p>
        </div>
        <div class="flex gap-2">
          <button
            class="rounded-md border px-3 py-1.5 text-sm"
            :class="
              mode === 'web' ? 'border-brand-500 bg-brand-500 text-white' : 'hover:bg-gray-100'
            "
            @click="setMode('web')"
          >
            Web
          </button>
          <button
            class="rounded-md border px-3 py-1.5 text-sm"
            :class="
              mode === 'mobil' ? 'border-brand-500 bg-brand-500 text-white' : 'hover:bg-gray-100'
            "
            @click="setMode('mobil')"
          >
            Mobil
          </button>
        </div>
      </div>

      <!-- Mobile: horizontal snap carousel -->
      <div class="no-scrollbar -mx-6 snap-x snap-mandatory overflow-x-auto px-6 md:hidden">
        <div class="flex gap-4 pr-6">
          <figure
            v-for="(s, i) in shots"
            :key="s.alt"
            class="min-w-[78%] shrink-0 cursor-pointer snap-center overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm transition-shadow hover:shadow-md"
            @click="open(i)"
          >
            <div class="relative w-full">
              <!-- Mobile: contain + letterbox so görsel kırpılmadan görünür -->
              <div class="flex h-56 items-center justify-center bg-gray-50">
                <img
                  :src="s.src"
                  :alt="s.alt"
                  class="max-h-full w-auto object-contain"
                  loading="lazy"
                />
              </div>
              <span
                class="absolute left-2 top-2 rounded bg-black/55 px-2 py-0.5 text-xs font-medium text-white"
              >
                {{ modeLabel }}
              </span>
            </div>
            <figcaption class="h-10 px-3 py-2 text-sm text-gray-700">
              <span class="block truncate">{{ s.alt }}</span>
            </figcaption>
          </figure>
        </div>
      </div>

      <!-- Desktop: 4-column grid with hover -->
      <div class="hidden gap-6 md:grid md:grid-cols-3 lg:grid-cols-4">
        <figure
          v-for="(s, i) in shots"
          :key="s.alt"
          class="group cursor-pointer overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm transition-shadow hover:shadow-md"
          @click="open(i)"
        >
          <div class="relative">
            <img
              :src="s.src"
              :alt="s.alt"
              :class="[
                mode === 'mobil'
                  ? 'h-56 w-full bg-gray-50 object-contain'
                  : 'h-40 w-full object-cover',
                'transition-transform duration-200 group-hover:scale-[1.02]',
              ]"
              loading="lazy"
            />
            <span
              class="absolute left-2 top-2 rounded bg-black/55 px-2 py-0.5 text-xs font-medium text-white"
            >
              {{ modeLabel }}
            </span>
          </div>
          <figcaption class="h-10 px-3 py-2 text-sm text-gray-700">
            <span class="block truncate">{{ s.alt }}</span>
          </figcaption>
        </figure>
      </div>

      <!-- Lightbox Modal -->
      <div v-if="lightbox.open" class="fixed inset-0 z-[60]" @keydown.esc="close" tabindex="0">
        <div class="absolute inset-0 bg-black/70" @click="close" aria-hidden="true"></div>
        <div class="absolute inset-0 flex items-center justify-center px-4">
          <div class="relative w-full max-w-5xl">
            <div class="overflow-hidden rounded-xl bg-white shadow-xl">
              <div class="flex items-center justify-between border-b px-4 py-2">
                <div class="text-sm text-gray-600">{{ current.alt }}</div>
                <button
                  class="px-2 py-1 text-gray-600 hover:text-gray-800"
                  @click="close"
                  aria-label="Kapat"
                >
                  ✕
                </button>
              </div>
              <div class="flex items-center justify-center bg-gray-50">
                <img
                  :src="current.src"
                  :alt="current.alt"
                  class="max-h-[70vh] w-auto object-contain"
                />
              </div>
              <div
                class="flex items-center justify-between border-t px-4 py-2 text-sm text-gray-600"
              >
                <div>{{ lightbox.index + 1 }} / {{ shots.length }}</div>
                <div class="flex gap-2">
                  <button
                    class="rounded border px-3 py-1 hover:bg-gray-50"
                    @click="prev"
                    aria-label="Önceki"
                  >
                    Önceki
                  </button>
                  <button
                    class="rounded bg-brand-500 px-3 py-1 text-white"
                    @click="next"
                    aria-label="Sonraki"
                  >
                    Sonraki
                  </button>
                </div>
              </div>
            </div>
            <!-- Nav arrows -->
            <button
              class="absolute left-0 top-1/2 -translate-y-1/2 p-3 text-white hover:text-brand-500"
              @click="prev"
              aria-label="Önceki"
            >
              ‹
            </button>
            <button
              class="absolute right-0 top-1/2 -translate-y-1/2 p-3 text-white hover:text-brand-500"
              @click="next"
              aria-label="Sonraki"
            >
              ›
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
  // Real screenshots (web + mobil)
  import menuw from "@/assets/screens/musteri_menusu_web.png";
  import menum from "@/assets/screens/musteri_menusu_mobil.png";
  import takipw from "@/assets/screens/siparis_takibi_web.png";
  import takipm from "@/assets/screens/siparis_takibi_mobil.png";
  import siplistw from "@/assets/screens/siparislerim_web.png";
  import siplistm from "@/assets/screens/siparislerim_mobil.png";
  import adminw from "@/assets/screens/admin_panel_web.png";
  import adminm from "@/assets/screens/admin_paneli_mobil.png";
  import kbw from "@/assets/screens/bar_mutfak_paneli_web.png";
  import kbm from "@/assets/screens/bar_mutfak_paneli_mobil.png";

  export default {
    name: "ScreenshotsGrid",
    data() {
      return {
        mode: "web",
        webShots: [
          { src: menuw, alt: "Müşteri Menüsü (Web)" },
          { src: siplistw, alt: "Siparişlerim (Web)" },
          { src: takipw, alt: "Sipariş Takibi (Web)" },
          { src: adminw, alt: "Admin Paneli (Web)" },
          { src: kbw, alt: "Mutfak/Bar Paneli (Web)" },
        ],
        mobileShots: [
          { src: menum, alt: "Müşteri Menüsü (Mobil)" },
          { src: siplistm, alt: "Siparişlerim (Mobil)" },
          { src: takipm, alt: "Sipariş Takibi (Mobil)" },
          { src: adminm, alt: "Admin Paneli (Mobil)" },
          { src: kbm, alt: "Mutfak/Bar Paneli (Mobil)" },
        ],
        lightbox: {
          open: false,
          index: 0,
        },
      };
    },
    computed: {
      shots() {
        return this.mode === "web" ? this.webShots : this.mobileShots;
      },
      modeLabel() {
        return this.mode === "web" ? "Web" : "Mobil";
      },
      current() {
        const i = Math.min(Math.max(this.lightbox.index, 0), this.shots.length - 1);
        return this.shots[i] || { src: "", alt: "" };
      },
    },
    methods: {
      setMode(m) {
        if (m === this.mode) return;
        this.mode = m;
        // Reset index for new set
        this.lightbox.index = 0;
        // Close lightbox if open to avoid index mismatch
        if (this.lightbox.open) this.lightbox.open = false;
      },
      open(i = 0) {
        this.lightbox.index = i;
        this.lightbox.open = true;
        this.$nextTick(() => {
          const root = this.$el;
          if (root && typeof root.querySelector === "function") {
            const el = root.querySelector('[tabindex="0"]');
            if (el && typeof el.focus === "function") el.focus();
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
