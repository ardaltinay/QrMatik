<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-50 p-6">
    <div class="w-full max-w-md text-center">
      <h1 class="mb-3 text-2xl font-semibold text-gray-800">Tenant gerekli</h1>
      <p class="mb-6 text-gray-600">
        Bu panele erişmek için lütfen restoranınızın alt alan adını kullanın. Örnek:
        <code class="rounded bg-gray-100 px-1 py-0.5">restoran-adiniz.sizin-domaininiz.com</code>
      </p>
      <div class="space-x-3">
        <a
          href="/"
          class="inline-block rounded-md bg-gray-800 px-4 py-2 text-white hover:bg-gray-700"
          >Ana sayfaya dön</a
        >
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "TenantRequired",
  };
</script>

<style scoped>
  code {
    font-family:
      ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New",
      monospace;
  }
</style>
