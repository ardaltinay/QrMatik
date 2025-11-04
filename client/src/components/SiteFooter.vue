<template>
  <footer class="mt-12 border-t bg-white">
    <div
      class="container mx-auto flex flex-col gap-2 px-4 py-4 text-xs text-gray-600 sm:flex-row sm:items-center sm:justify-between sm:px-6 sm:text-sm"
    >
      <div class="flex items-center justify-center gap-2 sm:justify-start">
        <img src="@/assets/logo.svg" alt="QrMatik" class="h-5 w-5" />
        <div>© {{ year }} QrMatik</div>
      </div>
      <nav
        class="mt-2 flex w-full flex-wrap items-center justify-center gap-x-4 gap-y-2 border-t pt-2 sm:mt-0 sm:w-auto sm:justify-end sm:gap-y-0 sm:border-0 sm:pt-0"
      >
        <router-link to="/about" class="rounded px-2 py-2 hover:text-indigo-600 sm:py-1"
          >Hakkında</router-link
        >
        <router-link to="/qr-menu" class="rounded px-2 py-2 hover:text-indigo-600 sm:py-1"
          >Dijital QR Menü</router-link
        >
        <a href="#" @click.prevent class="rounded px-2 py-2 hover:text-indigo-600 sm:py-1"
          >Gizlilik</a
        >
        <a href="#" @click.prevent class="rounded px-2 py-2 hover:text-indigo-600 sm:py-1"
          >Koşullar</a
        >
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
