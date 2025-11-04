<template>
  <section class="py-12">
    <div class="container mx-auto">
      <div class="text-xs font-semibold tracking-wide text-indigo-600 uppercase mb-1">Adımlar</div>
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 mb-6">Nasıl Çalışır?</h2>
      <ol class="grid gap-6 md:grid-cols-3">
        <li class="p-5 rounded-xl border bg-white shadow-sm">
          <div class="text-sm text-indigo-600 font-semibold mb-1">Adım 1</div>
          <div class="text-lg font-semibold mb-2">QR Kodunu Tara</div>
          <p class="text-gray-600 text-sm">Menüye hızlı erişim için masadaki QR kodunu tarayın veya linki açın.</p>
        </li>
        <li class="p-5 rounded-xl border bg-white shadow-sm">
          <div class="text-sm text-indigo-600 font-semibold mb-1">Adım 2</div>
          <div class="text-lg font-semibold mb-2">Sipariş Ver</div>
          <p class="text-gray-600 text-sm">Ürünleri sepetinize ekleyin, isteğe bağlı not bırakın ve gönderin.</p>
        </li>
        <li class="p-5 rounded-xl border bg-white shadow-sm">
          <div class="text-sm text-indigo-600 font-semibold mb-1">Adım 3</div>
          <div class="text-lg font-semibold mb-2">Takip & Ödeme</div>
          <p class="text-gray-600 text-sm">Hazırlık durumunu takip edin, iptal/tekrar verin ve ödemeyi tamamlayın.</p>
        </li>
      </ol>
    </div>
  </section>
</template>

<script>
export default { name: "HowItWorks" };
</script>
