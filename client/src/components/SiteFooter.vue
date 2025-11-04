<template>
  <footer class="mt-12 border-t bg-white">
    <div class="container mx-auto px-4 sm:px-6 py-4 text-xs sm:text-sm text-gray-600 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
      <div class="flex items-center justify-center sm:justify-start gap-2">
        <img src="@/assets/logo.svg" alt="QrMatik" class="w-5 h-5" />
        <div>© {{ year }} QrMatik</div>
      </div>
      <nav class="flex flex-wrap items-center justify-center sm:justify-end gap-x-4 gap-y-2 sm:gap-y-0 sm:border-0 border-t w-full sm:w-auto pt-2 sm:pt-0 mt-2 sm:mt-0">
        <router-link to="/about" class="px-2 py-2 sm:py-1 rounded hover:text-indigo-600">Hakkında</router-link>
        <router-link to="/qr-menu" class="px-2 py-2 sm:py-1 rounded hover:text-indigo-600">Dijital QR Menü</router-link>
        <a href="#" @click.prevent class="px-2 py-2 sm:py-1 rounded hover:text-indigo-600">Gizlilik</a>
        <a href="#" @click.prevent class="px-2 py-2 sm:py-1 rounded hover:text-indigo-600">Koşullar</a>
      </nav>
    </div>
  </footer>

</template>

<script>
export default {
  name: "SiteFooter",
  data() {
    return { year: new Date().getFullYear() };
  },
};
</script>
