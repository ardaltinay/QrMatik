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
    path: "/signup/tenant",
    name: "tenant-signup",
    component: () => import(/* webpackChunkName: "tenant-signup" */ "../views/TenantSignup.vue"),
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
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL || "/"),
  routes,
});

// navigation guard using auth store (lazy import to avoid circular deps)
router.beforeEach(async (to, from, next) => {
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
            await fetchJson("/api/tenant/config?code=" + encodeURIComponent(detected), {
              silentError: true,
            });
            // persist tenant and mark verified
            try {
              localStorage.setItem("qm_tenant", detected);
              sessionStorage.setItem(verifiedKey, "ok");
            } catch {
              /* ignore */
            }
          } catch {
            try {
              localStorage.removeItem("qm_tenant");
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

  // requiresRole guard (meta.requiresRole = 'kitchen'|'bar'|'admin'|'superadmin')
  const roleReq = to.matched.find((r) => r.meta && r.meta.requiresRole);
  if (roleReq) {
    const required = roleReq.meta.requiresRole;
    const actual = auth.user && auth.user.role ? String(auth.user.role).toLowerCase() : null;
    // For tenant-bound panels (admin/bar/kitchen), enforce tenant-only access via subdomain or /r/:tenant
    if (["admin", "bar", "kitchen"].includes(String(required).toLowerCase())) {
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
      if (actual === "admin") return next("/admin/orders");
      return next({ name: "admin" });
    }
  }

  next();
});

export default router;
