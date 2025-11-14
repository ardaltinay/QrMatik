import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/menu",
    name: "menu",
    component: () => import(/* webpackChunkName: "menu" */ "../views/MenuView.vue"),
  },
  {
    path: "/order/:id",
    name: "order-status",
    component: () => import(/* webpackChunkName: "order-status" */ "../views/OrderStatusView.vue"),
  },
  {
    path: "/my-orders/:sessionId?",
    name: "my-orders",
    component: () => import(/* webpackChunkName: "my-orders" */ "../views/SessionOrdersView.vue"),
  },
  {
    path: "/admin",
    name: "admin",
    component: () => import(/* webpackChunkName: "admin" */ "../views/AdminDashboard.vue"),
    children: [
      {
        path: "kitchen",
        name: "kitchen",
        component: () => import(/* webpackChunkName: "kitchen" */ "../views/KitchenView.vue"),
        meta: { requiresAuth: true, requiresRole: "kitchen" },
      },
      {
        path: "bar",
        name: "bar",
        component: () => import(/* webpackChunkName: "bar" */ "../views/BarView.vue"),
        meta: { requiresAuth: true, requiresRole: "bar" },
      },
      {
        path: "cashier",
        name: "cashier",
        component: () => import(/* webpackChunkName: "cashier" */ "../views/CashierView.vue"),
        meta: { requiresAuth: true, requiresRole: "cashier" },
      },
      {
        path: "orders",
        component: () => import(/* webpackChunkName: "orders" */ "../views/AdminOrdersView.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "users",
        component: () => import(/* webpackChunkName: "users" */ "../views/UsersView.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "menu-management",
        component: () =>
          import(/* webpackChunkName: "menu-mgmt" */ "../views/MenuManagementView.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "stock",
        component: () => import(/* webpackChunkName: "stock" */ "../views/StockControl.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "tables",
        component: () => import(/* webpackChunkName: "tables" */ "../views/AdminTablesView.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "reports",
        component: () => import(/* webpackChunkName: "reports" */ "../views/ReportsView.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "qr",
        component: () => import(/* webpackChunkName: "admin-qr" */ "../views/AdminQrView.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "branding",
        component: () => import(/* webpackChunkName: "branding" */ "../views/BrandingView.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
      {
        path: "upgrade",
        component: () => import(/* webpackChunkName: "upgrade" */ "../views/UpgradePlan.vue"),
        meta: { requiresAuth: true, requiresRole: "admin" },
      },
    ],
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: function () {
      return import(/* webpackChunkName: "about" */ "../views/AboutView.vue");
    },
  },
  {
    path: "/qr-menu",
    name: "qr-menu",
    component: () => import(/* webpackChunkName: "qr-menu" */ "../views/QrMenuLanding.vue"),
  },
  {
    path: "/dijital-menu",
    name: "dijital-menu",
    component: () =>
      import(/* webpackChunkName: "dijital-menu" */ "../views/DijitalMenuLanding.vue"),
  },
  {
    path: "/qr-siparis",
    name: "qr-siparis",
    component: () => import(/* webpackChunkName: "qr-siparis" */ "../views/QrSiparisLanding.vue"),
  },
  {
    path: "/restoran-bar-menu",
    name: "restoran-bar-menu",
    component: () =>
      import(/* webpackChunkName: "restoran-bar-menu" */ "../views/RestoranBarMenuLanding.vue"),
  },
  {
    path: "/signup/tenant",
    name: "tenant-signup",
    component: () => import(/* webpackChunkName: "tenant-signup" */ "../views/TenantSignup.vue"),
  },
  {
    path: "/blog",
    name: "blog",
    component: () => import(/* webpackChunkName: "blog" */ "../views/BlogList.vue"),
  },
  {
    path: "/blog/qr-restoran-nedir",
    name: "blog-qr-restoran-nedir",
    component: () =>
      import(/* webpackChunkName: "blog-post" */ "../views/blog/QrRestoranNedir.vue"),
  },
  {
    path: "/blog/dijital-menu-avantajlari",
    name: "blog-dijital-menu-avantajlari",
    component: () =>
      import(/* webpackChunkName: "blog-post" */ "../views/blog/DijitalMenuAvantajlari.vue"),
  },
  {
    path: "/super/tenants",
    name: "super-tenants",
    component: () =>
      import(/* webpackChunkName: "super-tenants" */ "../views/TenantsAdminView.vue"),
    meta: { requiresAuth: true, requiresRole: "superadmin" },
  },
  {
    path: "/tenant-not-found",
    name: "tenant-not-found",
    component: () =>
      import(/* webpackChunkName: "tenant-not-found" */ "../views/TenantNotFound.vue"),
  },
  {
    path: "/tenant-required",
    name: "tenant-required",
    component: () =>
      import(/* webpackChunkName: "tenant-required" */ "../views/TenantRequired.vue"),
  },
  {
    path: "/billing/checkout",
    name: "billing-checkout",
    // Integration removed: redirect checkout route to manual payment view
    redirect: "/billing/manual",
  },
  {
    path: "/billing/manual",
    name: "billing-manual",
    component: () => import(/* webpackChunkName: "billing-manual" */ "../views/ManualPayment.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL || "/"),
  routes,
});

// navigation guard using auth store (lazy import to avoid circular deps)
router.beforeEach(async (to, from, next) => {
  // If we're running in a prerender/SSR environment, skip navigation guards that rely on
  // browser globals (window/localStorage/sessionStorage) to avoid runtime errors.
  try {
    if (typeof window === "undefined") return next();
  } catch {
    // If typeof throws for any reason, be conservative and skip guards
    return next();
  }

  // Tenant validation: ensure tenant exists for customer-facing routes on every navigation
  try {
    // Skip tenant check for super admin routes
    const path = to.path || "";
    if (!path.startsWith("/super") && !path.startsWith("/tenant-not-found")) {
      const host = typeof window !== "undefined" ? window.location.hostname : "";
      const query = to.query || {};
      const qTenant =
        query.tenant || query.t || query.code
          ? String(query.tenant || query.t || query.code)
          : null;
      let detected = qTenant;
      if (!detected) {
        // path style /r/:tenant
        const parts = path.split("/");
        const idx = parts.indexOf("r");
        if (idx >= 0 && parts.length > idx + 1) detected = parts[idx + 1];
      }
      if (!detected && host) {
        if (host.endsWith(".localhost")) {
          const dot = host.indexOf(".");
          if (dot > 0) detected = host.slice(0, dot);
        } else {
          const hostParts = host.split(".");
          if (hostParts.length > 2) detected = hostParts[0];
        }
      }

      if (detected) {
        // If not yet verified this session, verify now by calling tenant/config
        const verifiedKey = "qm_tenant_verified_" + detected;
        let verifiedVal = null;
        try {
          verifiedVal = sessionStorage.getItem(verifiedKey);
        } catch {
          /* ignore */
        }
        if (verifiedVal === "notfound") {
          return next({ name: "tenant-not-found" });
        }
        if (verifiedVal !== "ok") {
          try {
            const { fetchJson } = await import("@/utils/api");
            const cfg = await fetchJson("/api/tenant/config?code=" + encodeURIComponent(detected), {
              silentError: true,
            });
            // persist tenant and mark verified
            try {
              localStorage.setItem("qm_tenant", detected);
              if (cfg && typeof cfg === "object") {
                // also persist full config for plan gating
                localStorage.setItem("qm_tenant_cfg", JSON.stringify(cfg));
              }
              if (cfg && cfg.locale) localStorage.setItem("qm_locale", String(cfg.locale));
              if (cfg && cfg.timeZone) localStorage.setItem("qm_tz", String(cfg.timeZone));
              sessionStorage.setItem(verifiedKey, "ok");
            } catch {
              /* ignore */
            }
          } catch {
            try {
              localStorage.removeItem("qm_tenant");
              localStorage.removeItem("qm_tenant_cfg");
              sessionStorage.setItem(verifiedKey, "notfound");
            } catch {
              /* ignore */
            }
            return next({ name: "tenant-not-found" });
          }
        } else {
          // keep localStorage in sync
          try {
            localStorage.setItem("qm_tenant", detected);
          } catch {
            /* ignore */
          }
        }
      }
    }
  } catch {
    /* ignore */
  }

  const { useAuthStore } = await import("@/stores/authStore");
  const auth = useAuthStore();

  // Early gating: kitchen/bar/cashier rotalarına FREE planda giriş tamamen engellenir
  try {
    const routeName = String(to.name || "").toLowerCase();
    const isKitchenBarCashier =
      routeName === "kitchen" || routeName === "bar" || routeName === "cashier";
    if (isKitchenBarCashier) {
      const raw = localStorage.getItem("qm_tenant_cfg");
      const cfg = raw ? JSON.parse(raw) : null;
      const plan = String(cfg?.plan || "").toUpperCase();
      const isPaid = plan && plan !== "FREE";
      if (!isPaid) {
        const { useUiStore } = await import("@/stores/uiStore");
        useUiStore().toast("Mutfak ve Bar panoları Standart/Pro planlarda mevcuttur.", "info");

        // Auth durumuna ve mevcut ekrana göre davranış:
        const currentFromName = String(from?.name || "");
        const userRole = auth.user && auth.user.role ? String(auth.user.role).toLowerCase() : null;

        if (!auth.user) {
          // Giriş yapılmamışken kitchen/bar hedeflenirse: login ekranında kal.
          if (currentFromName === "admin") return next(false); // mevcut login ekranından ayrılma
          return next({ name: "admin" }); // değilse admin giriş ekranına götür
        }

        // Giriş yapılmışsa: admin kullanıcılarını güvenli sayfaya yönlendir, bar/kitchen kullanıcılarını çıkışa zorla.
        if (userRole === "admin") {
          return next("/admin/orders");
        }
        if (userRole === "kitchen" || userRole === "bar" || userRole === "cashier") {
          try {
            auth.logout();
          } catch {
            /* ignore */
          }
          return next({ name: "admin" }); // login ekranı
        }

        // Diğer roller için de admin girişine gönder
        return next({ name: "admin" });
      }
    }
  } catch {
    /* ignore */
  }

  // capture table code from query if present and persist for order creation
  try {
    const t = to.query && (to.query.table || to.query.t);
    if (t) {
      localStorage.setItem("qm_table_code", String(t));
      try {
        const tenant = localStorage.getItem("qm_tenant");
        if (tenant) localStorage.setItem("qm_table_tenant", tenant);
      } catch {
        /* ignore */
      }
    }
  } catch {
    /* ignore */
  }

  // requiresAuth guard
  if (to.matched.some((r) => r.meta && r.meta.requiresAuth)) {
    if (!auth.user) return next({ name: "admin" });
  }

  // requiresRole guard (meta.requiresRole = 'kitchen'|'bar'|'cashier'|'admin'|'superadmin')
  const roleReq = to.matched.find((r) => r.meta && r.meta.requiresRole);
  if (roleReq) {
    const required = roleReq.meta.requiresRole;
    const actual = auth.user && auth.user.role ? String(auth.user.role).toLowerCase() : null;
    // For tenant-bound panels (admin/bar/kitchen), enforce tenant-only access via subdomain or /r/:tenant
    if (["admin", "bar", "kitchen", "cashier"].includes(String(required).toLowerCase())) {
      // recompute detected tenant for this navigation
      let detected = null;
      try {
        const host = typeof window !== "undefined" ? window.location.hostname : "";
        const query = to.query || {};
        const qTenant =
          query.tenant || query.t || query.code
            ? String(query.tenant || query.t || query.code)
            : null;
        detected = qTenant;
        if (!detected) {
          const path = to.path || "";
          const parts = path.split("/");
          const idx = parts.indexOf("r");
          if (idx >= 0 && parts.length > idx + 1) detected = parts[idx + 1];
        }
        if (!detected && host) {
          if (host.endsWith(".localhost")) {
            const dot = host.indexOf(".");
            if (dot > 0) detected = host.slice(0, dot);
          } else {
            const hostParts = host.split(".");
            if (hostParts.length > 2) detected = hostParts[0];
          }
        }
      } catch {
        /* ignore */
      }
      if (!detected) {
        return next({ name: "tenant-required" });
      }
    }
    if (!actual || actual !== String(required).toLowerCase()) {
      // Redirect to appropriate landing by actual role
      if (actual === "superadmin") return next({ name: "super-tenants" });
      if (actual === "bar") return next({ name: "bar" });
      if (actual === "kitchen") return next({ name: "kitchen" });
      if (actual === "cashier") return next({ name: "cashier" });
      if (actual === "admin") return next("/admin/orders");
      return next({ name: "admin" });
    }

    // Plan gating for tenant-bound roles and premium admin views
    // kitchen/bar için plan kısıtı yukarıda (early gating) ele alınıyor
    // Not: Raporlar ana sayfasını (Admin > Raporlar) FREE planda da erişilebilir bırakıyoruz
    // ve içeride kart bazında kilit overlay'i gösteriyoruz. Alt rapor sayfaları eklenirse
    // burada ayrıca engellenebilir.
  }

  next();
});

// Post-navigation: set robots meta for marketing vs tenant/admin pages
router.afterEach((to) => {
  // Only manipulate the DOM when running in a browser environment
  try {
    if (typeof document === "undefined") return;
  } catch {
    return;
  }
  try {
    const marketingPaths = new Set([
      "/",
      "/about",
      "/qr-menu",
      "/dijital-menu",
      "/qr-siparis",
      "/restoran-bar-menu",
      "/signup/tenant",
    ]);
    const isMarketing = marketingPaths.has(to.path);
    const robots = isMarketing ? "index,follow" : "noindex, nofollow";
    let el = document.querySelector('meta[name="robots"]');
    if (!el) {
      el = document.createElement("meta");
      el.setAttribute("name", "robots");
      document.head.appendChild(el);
    }
    el.setAttribute("content", robots);
  } catch {
    /* ignore */
  }
});

export default router;
