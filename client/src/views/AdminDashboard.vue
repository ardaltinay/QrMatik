<template>
  <div>
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-2xl font-semibold">Admin Paneli</h1>
      <div>
        <template v-if="auth.user">
          <span class="text-sm text-gray-600 mr-4">Merhaba, {{ auth.user.username }}</span>
          <button @click="auth.logout" class="px-3 py-1 border rounded">Çıkış</button>
        </template>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-4">
      <aside
        v-if="isAdmin"
        class="lg:col-span-1 p-4 bg-white rounded-lg shadow-lg sticky top-6 lg:top-20 self-start max-h-[calc(100vh-2rem)] lg:max-h-[calc(100vh-5rem)] overflow-auto z-10"
      >
        <nav class="flex flex-col gap-3">
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/orders') +
              ' shadow-sm hover:shadow-lg transition-shadow block w-full'
            "
            @click="navigateOrLogin('/admin/orders', 'admin')"
          >
            Siparişler
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/users') +
              ' shadow-sm hover:shadow-lg transition-shadow block w-full'
            "
            @click="navigateOrLogin('/admin/users', 'admin')"
          >
            Kullanıcılar
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/menu-management') +
              ' shadow-sm hover:shadow-lg transition-shadow block w-full'
            "
            @click="navigateOrLogin('/admin/menu-management', 'admin')"
          >
            Menü Yönetimi
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/reports') +
              ' shadow-sm hover:shadow-lg transition-shadow block w-full'
            "
            @click="navigateOrLogin('/admin/reports', 'admin')"
          >
            Raporlar
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/qr') +
              ' shadow-sm hover:shadow-lg transition-shadow block w-full'
            "
            @click="navigateOrLogin('/admin/qr', 'admin')"
          >
            QR Kodları
          </button>
        </nav>
      </aside>

      <main
        :class="
          (isAdmin ? 'lg:col-span-3' : 'lg:col-span-4') +
          ' p-4 bg-white rounded-lg shadow-md relative z-20'
        "
      >
        <template v-if="isAdmin">
          <router-view />
        </template>
        <template v-else>
          <div class="min-h-[40vh] flex items-center justify-center">
            <div class="w-full max-w-sm bg-white border rounded-xl shadow p-6">
              <h3 class="font-semibold mb-4">Admin Girişi</h3>
              <input
                v-model="username"
                placeholder="Kullanıcı adı"
                class="w-full mb-3 p-2 border rounded"
              />
              <input
                v-model="password"
                placeholder="Parola"
                type="password"
                class="w-full mb-4 p-2 border rounded"
              />
              <div class="flex justify-end gap-2">
                <button @click="login" class="px-3 py-2 bg-brand-500 text-white rounded">
                  Giriş Yap
                </button>
              </div>
            </div>
          </div>
        </template>
      </main>
    </div>

    <OrderDetailDrawer
      :order="selected"
      @close="selected = null"
      @updated="onUpdated"
      @print="onPrint"
    />

    <!-- Modal kaldırıldı; giriş paneli ana içerikte gösteriliyor -->
  </div>
</template>

<script>
  import { ref, computed, onMounted } from "vue";
  import { useAuthStore } from "@/stores/authStore";
  import { useRouter } from "vue-router";
  import { watch } from "vue";
  import { useRoute } from "vue-router";

  export default {
    name: "AdminDashboard",
    components: {},
    setup() {
      const auth = useAuthStore();
      const showLogin = ref(false);
      const username = ref("");
      const password = ref("");
      const isAdmin = computed(() => auth.user && auth.user.role === "admin");

      async function login() {
        try {
          await auth.login(username.value, password.value);
          showLogin.value = false;
        } catch (e) {
          // Hata tostu fetchJson içinde zaten gösteriliyor; burada tekrar göstermiyoruz.
          console.debug("Login failed", e);
        }
      }

      const router = useRouter();
      const route = useRoute();

      const postLoginPath = ref(null);

      function navigateOrLogin(path, roleRequired) {
        console.log("navigateOrLogin called", path, roleRequired, "auth.user=", auth.user);
        if (!auth.user) {
          postLoginPath.value = path;
          showLogin.value = true;
          return;
        }
        if (roleRequired && auth.user.role !== roleRequired) {
          // unauthorized for this link
          postLoginPath.value = path;
          showLogin.value = true;
          return;
        }
        router.push(path);
      }

      // when auth.user becomes available after login, navigate
      watch(
        () => auth.user,
        (u) => {
          if (!u) return;
          if (postLoginPath.value) {
            router.push(postLoginPath.value);
            postLoginPath.value = null;
            showLogin.value = false;
            return;
          }
          // default: if at /admin root, go to orders
          if (route.path === "/admin") {
            router.push("/admin/orders");
            showLogin.value = false;
          }
        },
      );

      // if already logged in and at /admin, redirect immediately
      onMounted(() => {
        if (auth.user && route.path === "/admin") {
          router.push("/admin/orders");
        }
      });

      function navItemClass(path) {
        const base = "cursor-pointer text-left px-3 py-2 rounded-lg";
        const active = route.path === path || route.path.startsWith(path + "/");
        return base + (active ? " bg-brand-500 text-white" : " hover:bg-gray-100");
      }

      return { auth, showLogin, username, password, login, navigateOrLogin, navItemClass, isAdmin };
    },
  };
</script>
