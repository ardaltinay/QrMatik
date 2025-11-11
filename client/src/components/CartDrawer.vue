<template>
  <!-- Desktop: floating side panel, Mobile: bottom sheet -->
  <div>
    <!-- desktop -->
    <div class="fixed bottom-6 right-6 hidden w-80 md:block">
      <div class="transform rounded-lg border bg-white p-4 shadow-lg transition hover:shadow-2xl">
        <h3 class="mb-2 font-semibold">Sepet</h3>
        <ul class="max-h-56 space-y-2 overflow-auto">
          <li v-for="it in store.cart" :key="keyFor(it)" class="flex items-center justify-between">
            <div>
              <div class="font-medium">{{ menuName(it.itemId) }}</div>
              <div v-if="it.note" class="mt-0.5 text-xs text-gray-600">Not: {{ it.note }}</div>
              <div class="text-sm text-gray-500">x{{ it.qty }}</div>
            </div>
            <button @click="store.removeFromCart(it.itemId, it.note || '')" class="text-red-500">
              ✕
            </button>
          </li>
        </ul>
        <div class="mt-4">
          <button
            v-if="store.cart.length"
            @click="submitOrder"
            class="w-full rounded-md bg-indigo-600 px-4 py-2 text-white"
          >
            Sipariş Ver
          </button>
          <div v-else class="text-sm text-gray-500">Sepetin boş</div>
        </div>
      </div>
    </div>

    <!-- mobile bottom sheet (controlled by parent via v-model:mobileOpen) -->
    <div class="z-60 fixed bottom-0 left-0 right-0 md:hidden">
      <transition name="slide-up" appear>
        <div
          v-if="mobileOpen"
          class="rounded-t-xl border-t bg-white p-4 shadow-xl motion-safe:animate-slideUp"
        >
          <div class="flex items-center justify-between">
            <div class="font-semibold">Sepet ({{ store.cart.length }})</div>
            <div class="flex items-center gap-2">
              <button @click="clearCart" class="text-sm text-red-500">Tümünü Temizle</button>
              <button @click="closeMobile" class="text-sm text-gray-500">Kapat</button>
            </div>
          </div>
          <ul class="mt-3 max-h-56 space-y-2 overflow-auto">
            <li
              v-for="it in store.cart"
              :key="keyFor(it)"
              class="flex items-center justify-between"
            >
              <div>
                <div class="font-medium">{{ menuName(it.itemId) }}</div>
                <div v-if="it.note" class="mt-0.5 text-xs text-gray-600">Not: {{ it.note }}</div>
                <div class="text-sm text-gray-500">x{{ it.qty }}</div>
              </div>
              <button @click="store.removeFromCart(it.itemId, it.note || '')" class="text-red-500">
                ✕
              </button>
            </li>
          </ul>
          <div class="mt-3">
            <button
              @click="submitOrder"
              class="w-full rounded-md bg-indigo-600 px-4 py-2 text-white"
            >
              Sipariş Ver
            </button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
  import { useOrderStore } from "@/stores/orderStore";
  import { useRouter } from "vue-router";

  export default {
    name: "CartDrawer",
    props: {
      // v-model:mobileOpen
      mobileOpen: { type: Boolean, default: false },
    },
    emits: ["update:mobileOpen"],
    setup(props, { emit }) {
      const store = useOrderStore();
      const router = useRouter();

      function menuName(id) {
        const m = store.menu.find((x) => x.id === id);
        return m ? m.name : "Bilinmiyor";
      }

      function keyFor(it) {
        return String(it.itemId) + "::" + String(it.note || "");
      }

      function submitOrder() {
        const tableCode = (() => {
          try {
            return localStorage.getItem("qm_table_code");
          } catch {
            return null;
          }
        })();
        // tableCode bulunamazsa 'guest' ile devam et (sunucuya masa kodu gönderilmez)
        const order = store.createOrder(tableCode || "guest");
        if (!order) return;
        // sheet'i hemen kapat ve "Siparişlerim" sayfasına yönlendir
        emit("update:mobileOpen", false);
        try {
          const sess = localStorage.getItem("qm_order_session");
          if (sess) router.push({ name: "my-orders", params: { sessionId: sess } });
          else router.push({ name: "my-orders" });
        } catch {
          router.push({ name: "my-orders" });
        }
      }
      function clearCart() {
        store.clearCart();
      }

      function closeMobile() {
        emit("update:mobileOpen", false);
      }

      return { store, menuName, submitOrder, closeMobile, clearCart, keyFor };
    },
  };
</script>

<style scoped>
  .slide-up-enter-from {
    transform: translateY(100%);
    opacity: 0;
  }
  .slide-up-enter-active {
    transition:
      transform 260ms cubic-bezier(0.2, 0.8, 0.2, 1),
      opacity 200ms;
  }
  .slide-up-enter-to {
    transform: translateY(0);
    opacity: 1;
  }
  .slide-up-leave-from {
    transform: translateY(0);
    opacity: 1;
  }
  .slide-up-leave-active {
    transition:
      transform 220ms cubic-bezier(0.2, 0.8, 0.2, 1),
      opacity 160ms;
  }
  .slide-up-leave-to {
    transform: translateY(100%);
    opacity: 0;
  }
</style>
