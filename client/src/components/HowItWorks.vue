<template>
  <section class="py-12">
    <div class="container mx-auto">
      <div class="mb-1 text-xs font-semibold uppercase tracking-wide text-indigo-600">Adımlar</div>
      <h2 class="mb-6 text-2xl font-bold text-gray-900 md:text-3xl">Nasıl Çalışır?</h2>
      <ol class="grid gap-6 md:grid-cols-3">
        <li class="rounded-xl border bg-white p-5 shadow-sm">
          <div class="mb-1 text-sm font-semibold text-indigo-600">Adım 1</div>
          <div class="mb-2 text-lg font-semibold">QR Kodunu Tara</div>
          <p class="text-sm text-gray-600">
            Menüye hızlı erişim için masadaki QR kodunu tarayın veya linki açın.
          </p>
        </li>
        <li class="rounded-xl border bg-white p-5 shadow-sm">
          <div class="mb-1 text-sm font-semibold text-indigo-600">Adım 2</div>
          <div class="mb-2 text-lg font-semibold">Sipariş Ver</div>
          <p class="text-sm text-gray-600">
            Ürünleri sepetinize ekleyin, isteğe bağlı not bırakın ve gönderin.
          </p>
        </li>
        <li class="rounded-xl border bg-white p-5 shadow-sm">
          <div class="mb-1 text-sm font-semibold text-indigo-600">Adım 3</div>
          <div class="mb-2 text-lg font-semibold">Takip & Ödeme</div>
          <p class="text-sm text-gray-600">
            Hazırlık durumunu takip edin, iptal/tekrar verin ve ödemeyi tamamlayın.
          </p>
        </li>
      </ol>
    </div>
  </section>
</template>

<script>
  export default { name: "HowItWorks" };
</script>
