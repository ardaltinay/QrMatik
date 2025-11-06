<template>
  <div class="flex min-h-screen flex-col">
    <header class="relative bg-white shadow">
      <div class="h-0.5 w-full bg-brand-gradient"></div>
      <div class="container mx-auto flex items-center justify-between px-6 py-4">
        <router-link to="/" class="flex items-center gap-3">
          <img src="@/assets/logo.svg" alt="QrMatik" class="h-10 w-10" />
          <div class="text-xl font-bold text-brand-700">QrMatik</div>
        </router-link>
        <nav class="hidden items-center gap-4 md:flex">
          <router-link to="/" class="text-gray-700 hover:text-indigo-600">Anasayfa</router-link>
          <router-link v-if="hasTenant" to="/menu" class="text-gray-700 hover:text-indigo-600"
            >Menü</router-link
          >
          <router-link v-if="!hasTenant" to="/about" class="text-gray-700 hover:text-indigo-600"
            >Hakkında</router-link
          >
          <router-link
            v-if="isAdmin && hasTenant"
            to="/admin"
            class="text-gray-700 hover:text-indigo-600"
            >Admin</router-link
          >
          <router-link
            v-else-if="isSuperAdmin"
            to="/super/tenants"
            class="text-gray-700 hover:text-indigo-600"
            >Admin</router-link
          >
          <a v-if="hasTenant" :href="apexHome" class="ml-2 text-gray-700 hover:text-indigo-600"
            >QrMatik Anasayfa</a
          >
        </nav>
        <div class="md:hidden">
          <button ref="menuButton" @click="open = !open" class="rounded bg-gray-100 p-2">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16"
              />
            </svg>
          </button>
        </div>
      </div>
      <!-- mobile menu (modern sheet) -->
      <transition name="slide-down">
        <div
          v-if="open"
          ref="mobileMenu"
          class="absolute left-0 right-0 top-full z-40 flex justify-center md:hidden"
        >
          <div
            class="mt-2 w-[calc(100%-2rem)] overflow-hidden rounded-b-xl bg-white shadow-xl motion-safe:animate-slideDown"
          >
            <div class="flex items-center justify-between border-b p-3">
              <div class="flex items-center gap-3">
                <img src="@/assets/logo.svg" alt="QrMatik" class="h-8 w-8" />
                <div class="text-base font-semibold">QrMatik</div>
              </div>
              <button @click="open = false" class="rounded bg-gray-100 p-2">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fill-rule="evenodd"
                    d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 011.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                    clip-rule="evenodd"
                  />
                </svg>
              </button>
            </div>
            <div class="p-3">
              <nav class="flex flex-col gap-2">
                <router-link
                  @click="open = false"
                  to="/"
                  class="rounded-md px-3 py-3 text-base font-medium transition hover:bg-gray-50"
                  >Anasayfa</router-link
                >
                <router-link
                  @click="open = false"
                  v-if="hasTenant"
                  to="/menu"
                  class="rounded-md px-3 py-3 text-base font-medium transition hover:bg-gray-50"
                  >Menü</router-link
                >
                <router-link
                  v-if="!hasTenant"
                  @click="open = false"
                  to="/about"
                  class="rounded-md px-3 py-3 text-base font-medium transition hover:bg-gray-50"
                  >Hakkında</router-link
                >
                <router-link
                  v-if="isAdmin && hasTenant"
                  @click="open = false"
                  to="/admin"
                  class="rounded-md px-3 py-3 text-base font-medium transition hover:bg-gray-50"
                  >Admin</router-link
                >
                <router-link
                  v-else-if="isSuperAdmin"
                  @click="open = false"
                  to="/super/tenants"
                  class="rounded-md px-3 py-3 text-base font-medium transition hover:bg-gray-50"
                  >Admin</router-link
                >
                <a
                  v-if="hasTenant"
                  :href="apexHome"
                  @click="open = false"
                  class="rounded-md px-3 py-3 text-base font-medium transition hover:bg-gray-50"
                  >QrMatik Anasayfa</a
                >
              </nav>
            </div>
          </div>
        </div>
      </transition>
    </header>

    <main class="container mx-auto flex-1 px-6 py-8">
      <router-view />
    </main>
    <SiteFooter />
    <ToastHub />
  </div>
</template>

<script setup>
  import { ref, onMounted, onBeforeUnmount, computed } from "vue";
  import { useHead } from "@unhead/vue";
  import { useAuthStore } from "@/stores/authStore";
  import { useRoute } from "vue-router";
  import ToastHub from "@/components/ToastHub.vue";
  import SiteFooter from "@/components/SiteFooter.vue";
  const open = ref(false);
  const mobileMenu = ref(null);
  const menuButton = ref(null);
  const auth = useAuthStore();

  const isAdmin = computed(() => auth.user && String(auth.user.role).toLowerCase() === "admin");
  const isSuperAdmin = computed(
    () => auth.user && String(auth.user.role).toLowerCase() === "superadmin",
  );
  const route = useRoute();
  const hasTenant = computed(() => {
    try {
      const params = new URLSearchParams(window.location.search);
      if (params.get("tenant") || params.get("t") || params.get("code")) return true;
      const path = window.location.pathname || "";
      const parts = path.split("/");
      const idx = parts.indexOf("r");
      if (idx >= 0 && parts.length > idx + 1) return true;
      const host = window.location.hostname || "";
      if (host.endsWith(".localhost")) {
        const dot = host.indexOf(".");
        if (dot > 0) return true;
      } else {
        const hostParts = host.split(".");
        if (hostParts.length > 2) return true;
      }
    } catch {
      /* ignore */
    }
    return false;
  });

  const apexHome = computed(() => {
    try {
      const loc = window.location;
      const hostname = loc.hostname || "";
      const host = loc.host || hostname;
      let apexHost = host;
      if (hostname.endsWith(".localhost")) {
        const dot = host.indexOf(".");
        if (dot > 0) apexHost = host.slice(dot + 1);
      } else {
        const parts = hostname.split(".");
        if (parts.length > 2) {
          const apexHostname = parts.slice(-2).join(".");
          const port = loc.port ? ":" + loc.port : "";
          apexHost = apexHostname + port;
        }
      }
      return (loc.protocol || "https:") + "//" + apexHost + "/";
    } catch {
      return "/";
    }
  });

  function onDocClick(e) {
    if (!open.value) return;
    const el = e.target;
    if (mobileMenu.value && mobileMenu.value.contains(el)) return;
    if (menuButton.value && menuButton.value.contains(el)) return;
    open.value = false;
  }

  function onFocusIn(e) {
    if (!open.value) return;
    const el = e.target;
    if (mobileMenu.value && mobileMenu.value.contains(el)) return;
    if (menuButton.value && menuButton.value.contains(el)) return;
    open.value = false;
  }

  function onKeyDown(e) {
    if (e.key === "Escape" && open.value) open.value = false;
  }

  onMounted(() => {
    document.addEventListener("click", onDocClick, true);
    document.addEventListener("focusin", onFocusIn, true);
    document.addEventListener("keydown", onKeyDown);
  });

  onBeforeUnmount(() => {
    document.removeEventListener("click", onDocClick, true);
    document.removeEventListener("focusin", onFocusIn, true);
    document.removeEventListener("keydown", onKeyDown);
  });

  const canonical = computed(() => {
    try {
      return window.location.origin + window.location.pathname + window.location.search;
    } catch {
      return "/";
    }
  });

  const routeNoindex = computed(() => {
    try {
      const p = route.path || "";
      return (
        p.startsWith("/admin") ||
        p.startsWith("/super") ||
        p.startsWith("/my-orders") ||
        p.startsWith("/order/")
      );
    } catch {
      return false;
    }
  });

  // Global head defaults + tenant/admin-specific robots
  useHead(() => ({
    title: hasTenant.value ? "Mobil Sipariş" : "QrMatik — Mobil Sipariş ve Yönetim",
    meta: [
      {
        name: "description",
        content: hasTenant.value
          ? "QR ile menü ve hızlı sipariş deneyimi"
          : "QR ile menüye hızlı eriş, sipariş ver; mutfak ve bar anında çalışsın.",
      },
      hasTenant.value || routeNoindex.value
        ? { name: "robots", content: "noindex,nofollow" }
        : { name: "robots", content: "index,follow" },
      {
        property: "og:title",
        content: hasTenant.value ? "Mobil Sipariş" : "QrMatik — Mobil Sipariş ve Yönetim",
      },
      {
        property: "og:description",
        content: hasTenant.value
          ? "QR ile menü ve hızlı sipariş deneyimi"
          : "QR ile menüye hızlı eriş, sipariş ver; mutfak ve bar anında çalışsın.",
      },
      { property: "og:type", content: "website" },
      { property: "og:url", content: canonical.value },
      { property: "og:site_name", content: "QrMatik" },
      { name: "twitter:card", content: "summary_large_image" },
      // Rich previews (apex only image)
      ...(hasTenant.value
        ? []
        : [
            { property: "og:image", content: "/og-image.svg" },
            { property: "og:image:width", content: "1200" },
            { property: "og:image:height", content: "630" },
            { name: "twitter:image", content: "/og-image.svg" },
          ]),
    ],
    link: [
      { rel: "canonical", href: canonical.value },
      // hreflang (tek dil tr); x-default da aynı sayfaya işaret etsin
      { rel: "alternate", hreflang: "tr", href: canonical.value },
      { rel: "alternate", hreflang: "x-default", href: canonical.value },
    ],
  }));
</script>

<style scoped>
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 180ms ease;
  }
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
  .fade-enter-to,
  .fade-leave-from {
    opacity: 1;
  }

  /* small tweak for sheet shadow */
  .rounded-xl {
    border-radius: 12px;
  }

  /* slide-down transition */
  .slide-down-enter-from {
    transform: translateY(-12px);
    opacity: 0;
  }
  .slide-down-enter-active {
    transition:
      transform 220ms cubic-bezier(0.2, 0.8, 0.2, 1),
      opacity 220ms;
  }
  .slide-down-enter-to {
    transform: translateY(0);
    opacity: 1;
  }
  .slide-down-leave-from {
    transform: translateY(0);
    opacity: 1;
  }
  .slide-down-leave-active {
    transition:
      transform 180ms cubic-bezier(0.2, 0.8, 0.2, 1),
      opacity 160ms;
  }
  .slide-down-leave-to {
    transform: translateY(-12px);
    opacity: 0;
  }
</style>
