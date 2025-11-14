<template>
  <div>
    <div class="mb-6">
      <div class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <h1 class="text-2xl font-semibold">Admin Paneli</h1>
        <!-- Merhaba + Çıkış: mobilde de aynı satırda -->
        <div v-if="isAdmin" class="flex flex-row flex-wrap items-center gap-2">
          <template v-if="auth.user">
            <span class="text-sm text-gray-600">Merhaba, {{ roleLabel(auth.user.role) }}</span>
            <button @click="onLogout" class="rounded border px-3 py-1 text-sm hover:bg-gray-50">
              Çıkış
            </button>
          </template>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 gap-4 lg:grid-cols-4">
      <aside
        v-if="isAdmin"
        class="sticky top-6 z-10 max-h-[calc(100vh-2rem)] self-start overflow-auto rounded-lg bg-white p-4 shadow-lg lg:top-20 lg:col-span-1 lg:max-h-[calc(100vh-5rem)]"
      >
        <nav class="flex flex-col gap-3">
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/orders') +
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
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
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
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
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
            "
            @click="navigateOrLogin('/admin/menu-management', 'admin')"
          >
            Menü Yönetimi
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/stock') +
              ' block w-full shadow-sm transition-shadow hover:shadow-lg '
            "
            @click="navigateOrLogin('/admin/stock', 'admin')"
          >
            Stok Kontrolü
            <span
              v-if="!isProPlan"
              class="ml-2 inline-block rounded bg-amber-100 px-2 py-0.5 text-xs font-medium text-amber-800"
            >
              Pro
            </span>
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/tables') +
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
            "
            @click="navigateOrLogin('/admin/tables', 'admin')"
          >
            Masalar
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/reports') +
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
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
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
            "
            @click="navigateOrLogin('/admin/qr', 'admin')"
          >
            QR Kodları
          </button>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/branding') +
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
            "
            @click="navigateOrLogin('/admin/branding', 'admin')"
          >
            Marka
          </button>
          <div class="my-3 border-t"></div>
          <button
            v-if="isAdmin"
            type="button"
            :class="
              navItemClass('/admin/upgrade') +
              ' block w-full shadow-sm transition-shadow hover:shadow-lg'
            "
            @click="navigateOrLogin('/admin/upgrade', 'admin')"
          >
            Planı Yükselt
          </button>
        </nav>
      </aside>

      <main
        :class="
          (isAdmin ? 'lg:col-span-3' : 'lg:col-span-4') +
          ' relative z-20 rounded-lg bg-white p-4 shadow-md'
        "
      >
        <template v-if="auth.user">
          <router-view />
        </template>
        <template v-else>
          <div class="flex min-h-[40vh] items-center justify-center">
            <div class="w-full max-w-sm rounded-xl border bg-white p-6 shadow">
              <h3 class="mb-4 font-semibold">Admin Girişi</h3>
              <input
                v-model="username"
                placeholder="Kullanıcı adı"
                class="mb-3 w-full rounded border p-2"
              />
              <input
                v-model="password"
                placeholder="Parola"
                type="password"
                class="mb-4 w-full rounded border p-2"
              />
              <div class="flex justify-end gap-2">
                <button @click="login" class="rounded bg-brand-500 px-3 py-2 text-white">
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
      const isPaidPlan = computed(() => {
        try {
          if (typeof window === "undefined") return false;
          const raw = localStorage.getItem("qm_tenant_cfg");
          if (!raw) return false;
          const cfg = JSON.parse(raw);
          const plan = String(cfg?.plan || "").toUpperCase();
          return plan && plan !== "FREE";
        } catch {
          return false;
        }
      });
      const isProPlan = computed(() => {
        try {
          if (typeof window === "undefined") return false;
          const raw = localStorage.getItem("qm_tenant_cfg");
          if (!raw) return false;
          const cfg = JSON.parse(raw);
          const plan = String(cfg?.plan || "").toUpperCase();
          return plan === "PRO";
        } catch {
          return false;
        }
      });

      function roleLabel(role) {
        const r = String(role || "").toLowerCase();
        if (r === "admin") return "admin";
        if (r === "kitchen") return "mutfak";
        if (r === "bar") return "bar";
        if (r === "cashier") return "kasiyer";
        if (r === "superadmin") return "süper admin";
        return r || "kullanıcı";
      }

      async function login() {
        try {
          await auth.login(username.value, password.value);
          showLogin.value = false;
        } catch (e) {
          // Hata tostu fetchJson içinde zaten gösteriliyor; burada tekrar göstermiyoruz.
          console.debug("Login failed", e);
        }
      }
      async function onLogout() {
        try {
          // Clear admin-related state
          const { useOrderStore } = await import("@/stores/orderStore");
          const store = useOrderStore();
          store.resetAfterLogout();
        } catch {
          /* ignore */
        }
        try {
          auth.logout();
        } finally {
          // Force unmount of child routes and show login panel immediately
          router.push({ name: "admin" });
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
          // default redirect by role when at /admin root
          if (route.path === "/admin") {
            const role = u.role;
            if (role === "superadmin") router.push({ name: "super-tenants" });
            else if (role === "admin") router.push("/admin/orders");
            else if (role === "kitchen") router.push({ name: "kitchen" });
            else if (role === "bar") router.push({ name: "bar" });
            else if (role === "cashier") router.push({ name: "cashier" });
            else router.push("/admin/orders");
            showLogin.value = false;
          }
        },
      );

      // if already logged in and at /admin, redirect immediately
      onMounted(() => {
        if (auth.user && route.path === "/admin") {
          const role = auth.user.role;
          if (role === "superadmin") router.push({ name: "super-tenants" });
          else if (role === "admin") router.push("/admin/orders");
          else if (role === "kitchen") router.push({ name: "kitchen" });
          else if (role === "bar") router.push({ name: "bar" });
          else if (role === "cashier") router.push({ name: "cashier" });
          else router.push("/admin/orders");
        }
        // Show upgrade result toast once if present
        try {
          const q = route.query || {};
          if (q.upgrade) {
            // Eğer bu sayfa bir popup/new tab olarak açıldıysa, açanı bilgilendirip kendimizi kapatmayı deneyelim
            try {
              const status = q.upgrade === "success" ? "success" : "fail";
              if (window.opener && !window.opener.closed) {
                // Mesaj gönder (opener tarafında BillingCheckout dinliyor)
                window.opener.postMessage({ type: "qm_upgrade", status }, "*");
                try {
                  // Ek bir emniyet: opener göremediyse localStorage ile de işaret bırak
                  window.localStorage.setItem("qm_upgrade_result", status);
                } catch (e) {
                  // ignore storage errors
                }
                // Kendimizi kapatmayı deneyelim (yalnızca script ile açıldıysa kapanır)
                window.close();
              }
            } catch (e) {
              // ignore postMessage/close errors
            }
            import("@/stores/uiStore").then(({ useUiStore }) => {
              const ui = useUiStore();
              if (q.upgrade === "success") ui.toastSuccess("Plan yükseltme başarılı.");
              else ui.toastError("Ödeme tamamlanamadı.");
              // Remove the query param to avoid re-show
              router.replace({ query: Object.assign({}, q, { upgrade: undefined }) });
            });
          }
        } catch {
          /* ignore */
        }
      });

      function navItemClass(path) {
        const base = "cursor-pointer text-left px-3 py-2 rounded-lg";
        const active = route.path === path || route.path.startsWith(path + "/");
        return base + (active ? " bg-brand-500 text-white" : " hover:bg-gray-100");
      }

      return {
        auth,
        showLogin,
        username,
        password,
        login,
        navigateOrLogin,
        navItemClass,
        isAdmin,
        isPaidPlan,
        isProPlan,
        onLogout,
        roleLabel,
      };
    },
  };
</script>
