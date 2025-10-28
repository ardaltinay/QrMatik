<template>
  <!-- Desktop: floating side panel, Mobile: bottom sheet -->
  <div>
    <!-- desktop -->
    <div class="hidden md:block fixed right-6 bottom-6 w-80">
      <div class="bg-white border rounded-lg shadow-lg p-4 transform transition hover:shadow-2xl">
        <h3 class="font-semibold mb-2">Sepet</h3>
        <ul class="space-y-2 max-h-56 overflow-auto">
          <li v-for="it in store.cart" :key="it.itemId" class="flex justify-between items-center">
            <div>
              <div class="font-medium">{{ menuName(it.itemId) }}</div>
              <div class="text-sm text-gray-500">x{{ it.qty }}</div>
            </div>
            <button @click="store.removeFromCart(it.itemId)" class="text-red-500">✕</button>
          </li>
        </ul>
        <div class="mt-4">
          <button
            v-if="store.cart.length"
            @click="submitOrder"
            class="w-full px-4 py-2 bg-indigo-600 text-white rounded-md"
          >
            Sipariş Ver
          </button>
          <div v-else class="text-sm text-gray-500">Sepetin boş</div>
        </div>
      </div>
    </div>

    <!-- mobile bottom sheet (controlled by parent via v-model:mobileOpen) -->
    <div class="md:hidden fixed left-0 right-0 bottom-0 z-50">
      <transition name="slide-up" appear>
        <div
          v-if="mobileOpen"
          class="bg-white border-t rounded-t-xl shadow-xl p-4 motion-safe:animate-slideUp"
        >
          <div class="flex items-center justify-between">
            <div class="font-semibold">Sepet ({{ store.cart.length }})</div>
            <div class="flex items-center gap-2">
              <button @click="clearCart" class="text-sm text-red-500">Tümünü Temizle</button>
              <button @click="closeMobile" class="text-sm text-gray-500">Kapat</button>
            </div>
          </div>
          <ul class="mt-3 space-y-2 max-h-56 overflow-auto">
            <li v-for="it in store.cart" :key="it.itemId" class="flex justify-between items-center">
              <div>
                <div class="font-medium">{{ menuName(it.itemId) }}</div>
                <div class="text-sm text-gray-500">x{{ it.qty }}</div>
              </div>
              <button @click="store.removeFromCart(it.itemId)" class="text-red-500">✕</button>
            </li>
          </ul>
          <div class="mt-3">
            <button
              @click="submitOrder"
              class="w-full px-4 py-2 bg-indigo-600 text-white rounded-md"
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

      function submitOrder() {
        const tableCode = (() => {
          try {
            return localStorage.getItem("qm_table_code");
          } catch {
            return null;
          }
        })();
        const order = store.createOrder(tableCode || null);
        if (!order) return;
        // close sheet immediately
        emit("update:mobileOpen", false);
        // Navigate to order-status once server confirms and returns real orderId
        let navigated = false;
        const onCreated = (ev) => {
          try {
            const realId = ev && ev.detail && ev.detail.orderId;
            if (realId && !navigated) {
              navigated = true;
              window.removeEventListener("qm:orderSession", onCreated);
              router.push({ name: "order-status", params: { id: String(realId) } });
            }
          } catch {
            /* ignore */
          }
        };
        try {
          window.addEventListener("qm:orderSession", onCreated);
        } catch {
          /* ignore */
        }
        // Fallback: if not received within 2s, go to session list
        setTimeout(() => {
          if (!navigated) {
            try {
              window.removeEventListener("qm:orderSession", onCreated);
            } catch {
              /* ignore */
            }
            const sess = (() => {
              try {
                return localStorage.getItem("qm_order_session");
              } catch {
                return null;
              }
            })();
            if (sess) router.push({ name: "my-orders", params: { sessionId: sess } });
          }
        }, 2000);
      }
      function clearCart() {
        store.clearCart();
      }

      function closeMobile() {
        emit("update:mobileOpen", false);
      }

      return { store, menuName, submitOrder, closeMobile, clearCart };
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
