<template>
  <section class="py-12">
    <div class="container mx-auto">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 mb-6">Ekran Görüntüleri</h2>
      <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <figure v-for="s in shots" :key="s.alt" class="rounded-xl border bg-white shadow-sm overflow-hidden">
          <img :src="s.src" :alt="s.alt" class="w-full h-40 object-cover" />
          <figcaption class="px-3 py-2 text-sm text-gray-600">{{ s.alt }}</figcaption>
        </figure>
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
    };
  },
};
</script>
