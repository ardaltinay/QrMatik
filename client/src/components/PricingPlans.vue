<template>
  <section id="pricing" class="py-12">
    <div class="container mx-auto">
      <h2 class="mb-6 text-2xl font-bold text-gray-900 md:text-3xl">Fiyatlandırma</h2>
      <!-- Yalnızca yıllık faturalama -->
      <div
        class="mb-6 inline-flex items-center gap-2 rounded-lg border bg-white px-3 py-2 text-sm shadow-sm"
      >
        <span>Faturalama:</span>
        <strong>Yıllık</strong>
      </div>

      <div v-if="loading" class="mb-6 text-sm text-gray-600">Fiyatlandırma yükleniyor…</div>
      <div v-else-if="!displayPlans.length" class="mb-6 text-sm text-gray-600">
        Şu anda fiyatlandırma bilgisi bulunamadı.
      </div>
      <div v-else class="grid gap-6 md:grid-cols-3">
        <div
          v-for="p in displayPlans"
          :key="p.name"
          class="flex flex-col rounded-xl border bg-white p-5 shadow-sm"
        >
          <div class="mb-1 text-lg font-semibold">{{ p.name }}</div>
          <div class="mb-2 text-3xl font-bold text-gray-900">{{ p.price }}</div>
          <ul class="mb-4 list-disc pl-5 text-sm leading-6 text-gray-600">
            <li v-for="f in p.features" :key="f">{{ f }}</li>
          </ul>
        </div>
      </div>
      <p v-if="note" class="mt-4 text-sm text-gray-600">{{ note }}</p>
    </div>
  </section>
</template>

<script>
  export default {
    name: "PricingPlans",
    data() {
      return {
        billing: "yearly", // yalnızca 'yearly'
        pricing: null, // server response: [{name, monthly, yearly, features}]
        note: "Tüm planlar yıllık faturalandırılır; aylık abonelik seçeneği bulunmamaktadır.", // default note; can be overridden by server
        loading: true,
        error: false,
      };
    },
    async mounted() {
      try {
        const res = await fetch("/api/public/pricing");
        if (res.ok) {
          const data = await res.json();
          if (data && Array.isArray(data.tiers)) {
            this.pricing = data.tiers.map((t) => ({
              name: t.name,
              monthly: Number(t.monthly || 0),
              yearly: Number(t.yearly || 0),
              features: Array.isArray(t.features) ? t.features : [],
            }));
            this.note = data.note || this.note;
          } else {
            this.error = true;
          }
        } else {
          this.error = true;
        }
      } catch {
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
    computed: {
      displayPlans() {
        const source = this.pricing && this.pricing.length ? this.pricing : [];
        return source.map((p) => ({
          name: p.name,
          price: this.formatPrice(p),
          subtext: this.computeSubtext(p),
          features: p.features,
        }));
      },
    },
    methods: {
      formatPrice(plan) {
        const y = Number(plan.yearly || 0);
        if (!y || y <= 0) return "0₺/yıl";
        return `${this.formatTRY(y)}/yıl`;
      },
      computeSubtext() {
        return "";
      },
      formatTRY(n) {
        try {
          return new Intl.NumberFormat("tr-TR").format(n) + "₺";
        } catch {
          return n + "₺";
        }
      },
    },
  };
</script>
