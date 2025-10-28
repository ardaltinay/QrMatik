import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/menu',
    name: 'menu',
    component: () => import(/* webpackChunkName: "menu" */ '../views/MenuView.vue')
  },
  {
    path: '/order/:id',
    name: 'order-status',
    component: () => import(/* webpackChunkName: "order-status" */ '../views/OrderStatusView.vue')
  },
  {
    path: '/my-orders/:sessionId?',
    name: 'my-orders',
    component: () => import(/* webpackChunkName: "my-orders" */ '../views/SessionOrdersView.vue')
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import(/* webpackChunkName: "admin" */ '../views/AdminDashboard.vue'),
    children: [
      {
        path: 'orders',
        component: () => import(/* webpackChunkName: "orders" */ '../views/AdminOrdersView.vue'),
        meta: { requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'users',
        component: () => import(/* webpackChunkName: "users" */ '../views/UsersView.vue'),
        meta: { requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'menu-management',
        component: () => import(/* webpackChunkName: "menu-mgmt" */ '../views/MenuManagementView.vue'),
        meta: { requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'reports',
        component: () => import(/* webpackChunkName: "reports" */ '../views/ReportsView.vue'),
        meta: { requiresAuth: true, requiresRole: 'admin' }
      }
      ,
      {
        path: 'qr',
        component: () => import(/* webpackChunkName: "admin-qr" */ '../views/AdminQrView.vue'),
        meta: { requiresAuth: true, requiresRole: 'admin' }
      }
    ]
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: function () {
      return import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL || '/'),
  routes
})

// navigation guard using auth store (lazy import to avoid circular deps)
router.beforeEach(async (to, from, next) => {
  const { useAuthStore } = await import('@/stores/authStore')
  const auth = useAuthStore()

  // capture table code from query if present and persist for order creation
  try {
    const t = (to.query && (to.query.table || to.query.t))
    if (t) localStorage.setItem('qm_table_code', String(t))
  } catch { /* ignore */ }

  // requiresAuth guard
  if (to.matched.some(r => r.meta && r.meta.requiresAuth)) {
    if (!auth.user) return next({ name: 'admin' })
  }

  // requiresRole guard (meta.requiresRole = 'kitchen'|'bar'|'admin')
  const roleReq = to.matched.find(r => r.meta && r.meta.requiresRole)
  if (roleReq) {
    const required = roleReq.meta.requiresRole
    if (!auth.user || auth.user.role !== required) {
      return next({ name: 'admin' })
    }
  }

  next()
})

export default router
