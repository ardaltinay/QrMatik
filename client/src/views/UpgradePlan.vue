<template>
  <div class="max-w-xl">
    <h2 class="mb-2 text-xl font-semibold">Planı Yükselt</h2>
    <p v-if="current.plan" class="mb-4 text-sm text-gray-700">
      Mevcut planınız: <strong>{{ planLabel(current.plan) }}</strong>
      <template v-if="current.billing">
        — <span class="font-medium">{{ billingLabel() }}</span>
      </template>
      <template v-if="current.paidUntil">
        <span class="text-gray-500"> ({{ current.paidUntil }})</span>
      </template>
    </p>
    <p v-else class="mb-4 text-sm text-gray-600">Hesabınız şu anda ücretsiz planda.</p>
  <p class="mb-6 text-sm text-gray-600">Aşağıdan planı seçip yıllık ödeme ile devam edin.</p>

    <div class="mb-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
      <button
        :class="sel.plan==='STANDARD' ? selClass(true) : selClass(false)"
        @click="sel.plan='STANDARD'"
      >
        Standart
      </button>
      <button
        :class="sel.plan==='PRO' ? selClass(true) : selClass(false)"
        @click="sel.plan='PRO'"
      >
        Pro
      </button>
    </div>

    <div class="mb-6">
      <div class="inline-flex items-center gap-2 rounded border px-3 py-2 text-sm text-gray-700 bg-gray-50">
        <span>Faturalama:</span>
        <strong>Yıllık</strong>
      </div>
    </div>

    <p class="mb-6 text-xs text-gray-600">
      Tüm planlar yıllık faturalandırılır; aylık abonelik seçeneği bulunmamaktadır. Yükseltmeler anında
      uygulanır, plan düşürme talepleri mevcut dönem sonunda geçerlidir.
    </p>

    <div class="flex items-center justify-center md:justify-start gap-3">
      <button @click="goCheckout" :disabled="loading" class="btn btn-primary gap-2 disabled:opacity-50 order-1">
        <span v-if="!loading">Ödemeye Git</span>
        <span v-else>Hazırlanıyor…</span>
        <svg v-if="!loading" xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 opacity-90" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd" />
        </svg>
      </button>
      <router-link class="btn btn-secondary order-2" to="/admin">İptal</router-link>
    </div>
  </div>
</template>

<script>
import { reactive, ref, onMounted } from 'vue';
import { fetchJson } from '@/utils/api';
import { useRouter, useRoute } from 'vue-router';
import { useUiStore } from '@/stores/uiStore';

export default {
  name: 'UpgradePlan',
  setup() {
  const router = useRouter();
  const route = useRoute();
  const sel = reactive({ plan: 'STANDARD', billing: 'YEARLY' });
  const current = reactive({ plan: null, billing: null, paidUntil: null });
  const ui = useUiStore();
    const loading = ref(false);

    function selClass(active) {
      return (
        'rounded px-3 py-2 text-center border transition ' +
        (active ? 'border-brand-500 bg-brand-50 text-brand-700' : 'border-gray-300 hover:bg-gray-50')
      );
    }

    function planLabel(p) {
      const u = String(p || '').toUpperCase();
      if (u === 'PRO') return 'Pro';
      if (u === 'STANDARD') return 'Standart';
      return 'Ücretsiz';
    }
    function billingLabel() {
      return 'Yıllık';
    }

    async function loadCurrent() {
      try {
        const cfg = await fetchJson('/api/tenant/config', { silentError: true });
        const plan = cfg && cfg.plan ? String(cfg.plan).toUpperCase() : 'FREE';
        const billing = cfg && cfg.billingPeriod ? String(cfg.billingPeriod).toUpperCase() : null;
        const untilRaw = cfg && cfg.planPaidUntil ? String(cfg.planPaidUntil) : null;
        current.plan = plan;
        current.billing = billing;
        current.paidUntil = untilRaw;
        // Eğer FREE değilse, varsayılan seçimleri mevcut plana göre ayarla
        if (plan === 'STANDARD' || plan === 'PRO') {
          sel.plan = plan;
          sel.billing = 'YEARLY';
        }
      } catch {
        /* ignore */
      }
      // Eğer checkout iptal ile dönüldüyse kullanıcıyı bilgilendir
      try {
        const q = route.query || {};
        if (q.checkout === 'cancel') {
          ui.toast('Ödeme iptal edildi.', 'info');
          router.replace({ query: Object.assign({}, q, { checkout: undefined }) });
        }
      } catch { /* ignore */ }
    }

    function rank(p) {
      const u = String(p || '').toUpperCase();
      if (u === 'PRO') return 2;
      if (u === 'STANDARD') return 1;
      return 0; // FREE or unknown
    }

    function isDowngrade(targetPlan) {
      const curPlan = current.plan || 'FREE';
      const planDown = rank(curPlan) > rank(targetPlan);
      return planDown;
    }

    async function goCheckout() {
      try {
        loading.value = true;
        // Aynı plan için ödeme başlatmayı engelle (erken geri bildirim)
        if ((current.plan || 'FREE') === sel.plan) {
          ui.toast('Zaten bu üyeliğe sahipsiniz.', 'info');
          return;
        }
        if (isDowngrade(sel.plan)) {
          await fetchJson('/api/billing/schedule-downgrade', {
            method: 'POST',
            body: JSON.stringify({ plan: sel.plan, billingPeriod: sel.billing }),
          });
          ui.toastSuccess('Plan değişikliği dönem sonunda uygulanacak.');
          router.push('/admin');
        } else {
          const res = await fetchJson('/api/billing/checkout/init', {
            method: 'POST',
            body: JSON.stringify({ plan: sel.plan, billingPeriod: sel.billing }),
          });
          try {
            sessionStorage.setItem('qm_checkout_content', res.checkoutFormContent || '');
          } catch (err) {
            /* ignore */
          }
          router.push('/billing/checkout');
        }
      } catch (e) {
        // toast already shown by fetchJson
        console.debug('init fail', e);
      } finally {
        loading.value = false;
      }
    }
    onMounted(loadCurrent);

  return { sel, current, loading, selClass, goCheckout, planLabel, billingLabel };
  },
};
</script>
