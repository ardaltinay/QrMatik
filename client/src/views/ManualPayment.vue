<template>
  <div class="max-w-xl">
    <h2 class="mb-2 text-xl font-semibold">Ödeme Talebi</h2>
    <p class="mb-4 text-sm text-gray-700">
      Online ödeme şu an aktif değil. Ödeme yapmak için lütfen aşağıdaki e‑posta adresine sipariş ve
      iletişim bilgilerinizi gönderin.
    </p>
    <div class="mb-4">
      <div class="rounded border bg-white p-4">
        <p class="font-medium">İletişim e‑posta adresi</p>
        <p class="break-words text-sm text-gray-700">{{ email }}</p>
      </div>
    </div>
    <p class="mb-4 text-sm text-gray-600">
      Mailinize banka havalesi bilgileri veya ödeme adımlarını geri bildireceğiz. Siparişiniz
      onaylandıktan sonra hesap planınız manuel olarak güncellenecektir.
    </p>
    <div class="flex gap-2">
      <router-link to="/admin" class="btn btn-secondary">Geri</router-link>
      <a :href="mailtoLink" class="btn btn-primary">E‑posta Gönder</a>
    </div>
  </div>
</template>

<script>
  import { computed } from "vue";
  import { useRoute } from "vue-router";

  export default {
    name: "ManualPayment",
    setup() {
      const route = useRoute();
      const email = computed(() => String(route.query.email || "support@qrmatik.cloud"));
      const subject = computed(() => encodeURIComponent("QrMatik Ödeme Talebi"));
      const body = computed(() =>
        encodeURIComponent(
          "Lütfen ödeme talebim ile ilgili bilgi veriniz.\n\nTenant kodu:\nİsim:\nTelefon:\nSipariş detayları:\n",
        ),
      );
      const mailtoLink = computed(
        () => `mailto:${email.value}?subject=${subject.value}&body=${body.value}`,
      );
      return { email, mailtoLink };
    },
  };
</script>
