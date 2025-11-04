<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-50 p-6">
    <div class="w-full max-w-md text-center">
      <h1 class="mb-3 text-2xl font-semibold text-gray-800">Restoran bulunamadı</h1>
      <p class="mb-6 text-gray-600">
        Girdiğiniz bağlantı geçersiz veya restoran pasif. Lütfen QR kodunu kontrol edin ya da doğru
        alt alan adını kullanın.
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
    name: "TenantNotFound",
  };
</script>

<style scoped></style>
