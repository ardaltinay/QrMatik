<template>
  <div>
    <div class="mb-4">
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div class="flex items-center gap-3">
          <h2 class="text-xl font-semibold">Kasa - Hesap İstekleri</h2>
          <!-- Badge: toplam bekleyen hesap isteği sayısı -->
          <span
            v-if="requests.length"
            class="inline-flex items-center rounded-full bg-red-600 px-2 py-0.5 text-xs font-medium text-white shadow"
          >
            {{ requests.length }} bekleyen
          </span>
        </div>
        <div class="flex w-full items-center gap-2 sm:w-auto">
          <!-- Ses toggle -->
          <button
            type="button"
            @click="toggleSound"
            class="rounded-md border px-3 py-1.5 text-sm shadow-sm transition hover:bg-gray-50"
            :class="soundEnabled ? 'border-green-300 text-green-700' : 'border-gray-300 text-gray-600'"
          >
            <span v-if="soundEnabled">🔔 Ses Açık</span>
            <span v-else>🔕 Ses Kapalı</span>
          </button>
          <button
            class="rounded-md border border-red-300 px-3 py-1.5 text-red-600 hover:bg-red-50"
            @click="onLogout"
          >
            Çıkış
          </button>
        </div>
      </div>
    </div>

    <div v-if="requests.length">
      <div class="grid gap-3">
        <div
          v-for="o in requests"
          :key="o.id"
          class="flex flex-col rounded-lg border bg-white p-3 shadow-sm sm:flex-row sm:items-center sm:justify-between"
        >
          <div>
            <div class="flex flex-wrap items-center gap-x-2 text-sm sm:text-base">
              <span class="font-medium">#{{ orderCodeFromId(o.id) }}</span>
              <span class="text-gray-400">•</span>
              <span class="text-gray-700">Masa {{ o.table }}</span>
              <span class="text-gray-400">•</span>
              <span class="text-gray-500">{{ formatDateTz(o.createdAt || o.createdTime) }}</span>
            </div>
            <div class="mt-1 text-sm text-gray-700">
              Toplam: <span class="font-medium">{{ formatMoney(o.total) }}</span>
            </div>
          </div>
          <div class="mt-3 flex items-center gap-2 sm:mt-0">
            <button class="btn btn-secondary" @click="markPaid(o.id)">Ödeme Alındı</button>
            <button class="btn" @click="openDetail(o)">Detay</button>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <p class="text-gray-500">Bekleyen hesap isteği yok.</p>
    </div>

  <!-- onlyPayment: kasa ekranında sadece ödeme tamamlandı durumuna izin ver -->
  <OrderDetailDrawer :order="selected" :onlyPayment="true" @close="selected = null" @updated="refresh" />
  </div>
</template>

<script>
  import { computed, ref, onMounted, onBeforeUnmount } from "vue";
  import { useOrderStore } from "@/stores/orderStore";
  import { useAuthStore } from "@/stores/authStore";
  import { useRouter } from "vue-router";
  import { formatMoney, formatDateTz, orderCodeFromId } from "@/utils/format";
  import OrderDetailDrawer from "@/components/OrderDetailDrawer.vue";
  import { useUiStore } from "@/stores/uiStore";

  export default {
    name: "CashierView",
    components: { OrderDetailDrawer },
    setup() {
    const store = useOrderStore();
      const auth = useAuthStore();
      const router = useRouter();
  const ui = useUiStore();
      const orders = computed(() => store.orders);
      const selected = ref(null);

      const requests = computed(() => {
        return (orders.value || []).filter((o) => o.status === "bill_requested");
      });

      // Önceden bilinen bill_requested sipariş id'leri
      const knownRequestIds = ref(new Set());

      function detectNewRequests(current) {
        const currentIds = new Set(current.map((o) => o.id));
        const newly = [];
        current.forEach((o) => {
          if (!knownRequestIds.value.has(o.id)) newly.push(o);
        });
        // Bilinenleri güncelle
        knownRequestIds.value = currentIds;
        return newly;
      }

      // Ses ayarı (persist localStorage)
      const SOUND_KEY = "qrmatik_sound_enabled";
      const soundEnabled = ref(true);

      function loadSoundPref() {
        try {
          const raw = localStorage.getItem(SOUND_KEY);
          if (raw === "0") soundEnabled.value = false;
          else if (raw === "1") soundEnabled.value = true;
        } catch {
          /* ignore */
        }
      }

      function saveSoundPref() {
        try {
          localStorage.setItem(SOUND_KEY, soundEnabled.value ? "1" : "0");
        } catch {
          /* ignore */
        }
      }

      function toggleSound() {
        soundEnabled.value = !soundEnabled.value;
        saveSoundPref();
        ui.toast(soundEnabled.value ? "Ses açıldı" : "Ses kapatıldı", "info", 1800);
      }

      // Basit bip; soundEnabled false ise çalma
      function playBeep() {
        if (!soundEnabled.value) return;
        try {
          const ctx = new (window.AudioContext || window.webkitAudioContext)();
          const osc = ctx.createOscillator();
          const gain = ctx.createGain();
          osc.type = "sine";
          osc.frequency.value = 880; // A5
          gain.gain.value = 0.15;
          osc.connect(gain).connect(ctx.destination);
          osc.start();
          setTimeout(() => {
            osc.stop();
            ctx.close();
          }, 180);
        } catch {
          /* ignore */
        }
      }

      async function refresh() {
        try {
          await store.loadOrders();
          const list = requests.value;
          const newly = detectNewRequests(list);
          if (newly.length) {
            // Çoklu yeni istek varsa özet mesaj
            if (newly.length === 1) {
              const o = newly[0];
              ui.toastSuccess(`Yeni hesap isteği • Masa ${o.table}`);
            } else {
              ui.toastSuccess(`${newly.length} yeni hesap isteği`);
            }
            playBeep();
          }
        } catch (e) {
          /* ignore */
        }
      }

      async function markPaid(id) {
        try {
          await store.updateOrderStatus(id, "payment_completed");
          await refresh();
        } catch (e) {
          /* ignore */
        }
      }

      function openDetail(o) {
        selected.value = o;
      }

      function onLogout() {
        try {
          auth.logout();
        } finally {
          router.push({ name: "admin" });
        }
      }

      let intervalId = null;
      onMounted(async () => {
        loadSoundPref();
        await refresh();
        intervalId = setInterval(refresh, 8000);
      });
      onBeforeUnmount(() => {
        if (intervalId) clearInterval(intervalId);
      });

      return {
        requests,
        markPaid,
        formatMoney,
        formatDateTz,
        orderCodeFromId,
        onLogout,
        selected,
        openDetail,
        refresh,
        // expose maybe for debugging
        knownRequestIds,
        soundEnabled,
        toggleSound,
      };
    },
  };
</script>
