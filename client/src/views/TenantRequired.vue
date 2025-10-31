<template>
  <div class="min-h-screen flex items-center justify-center p-6 bg-gray-50">
    <div class="max-w-md w-full text-center">
      <h1 class="text-2xl font-semibold text-gray-800 mb-3">Tenant gerekli</h1>
      <p class="text-gray-600 mb-6">
        Bu panele erişmek için lütfen restoranınızın alt alan adını kullanın.
        Örnek: <code class="px-1 py-0.5 bg-gray-100 rounded">restoran-adiniz.sizin-domaininiz.com</code>
      </p>
      <div class="space-x-3">
        <a href="/" class="inline-block px-4 py-2 rounded-md bg-gray-800 text-white hover:bg-gray-700">Ana sayfaya dön</a>
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
code { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }
</style>
