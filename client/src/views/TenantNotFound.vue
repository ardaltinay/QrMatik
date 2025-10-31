<template>
  <div class="min-h-screen flex items-center justify-center p-6 bg-gray-50">
    <div class="max-w-md w-full text-center">
      <h1 class="text-2xl font-semibold text-gray-800 mb-3">Restoran bulunamadı</h1>
      <p class="text-gray-600 mb-6">
        Girdiğiniz bağlantı geçersiz veya restoran pasif. Lütfen QR kodunu kontrol edin ya da doğru alt alan adını kullanın.
      </p>
      <div class="space-x-3">
        <a href="/" class="inline-block px-4 py-2 rounded-md bg-gray-800 text-white hover:bg-gray-700">Ana sayfaya dön</a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "TenantNotFound",
};
</script>

<style scoped>
</style>
