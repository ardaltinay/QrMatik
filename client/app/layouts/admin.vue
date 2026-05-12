<template>
  <div class="min-h-screen bg-slate-50 font-sans text-slate-800">
    <!-- Unauthenticated State (Login Form) -->
    <div v-if="!authStore.user" class="flex min-h-screen flex-col items-center justify-center p-4">
      <div class="w-full max-w-md bg-white rounded-3xl p-8 border border-slate-100">
        <div class="text-center mb-8">
          <Logo size="lg" animate shadow />
          <h2 class="text-2xl font-bold text-slate-900">{{ $t('admin.login.title') }}</h2>
          <p class="text-sm text-slate-500 mt-2">{{ $t('admin.login.subtitle') }}</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1.5">{{ $t('auth.username') }}</label>
            <input 
              v-model="loginForm.username" 
              type="text" 
              required
              class="w-full px-4 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none"
              :placeholder="$t('auth.usernamePlaceholder')"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1.5">{{ $t('auth.password') }}</label>
            <input 
              v-model="loginForm.password" 
              type="password" 
              required
              class="w-full px-4 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all outline-none"
              placeholder="••••••••"
            />
          </div>
          <button 
            type="submit" 
            :disabled="authStore.loading"
            class="w-full py-3.5 px-4 bg-brand-600 hover:bg-brand-700 text-white font-semibold rounded-xl shadow-lg transition-all disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            <svg v-if="authStore.loading" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span v-else>{{ $t('auth.loginButton') }}</span>
          </button>
        </form>
      </div>
    </div>

    <!-- Authenticated Layout -->
    <div v-else class="flex h-screen overflow-hidden relative">
      <!-- Persistent Notification Overlays -->
      <div v-if="notifStore.notifications.length > 0" class="fixed bottom-6 right-6 z-[60] flex flex-col gap-3 w-full max-w-xs sm:max-w-sm pointer-events-none">
        <TransitionGroup name="list">
          <div 
            v-for="notif in notifStore.notifications" 
            :key="notif.id"
            class="pointer-events-auto bg-white rounded-2xl shadow-2xl border-2 border-amber-500 overflow-hidden animate-in slide-in-from-right duration-300"
          >
            <div class="p-4 flex items-start gap-4">
              <div class="w-12 h-12 rounded-full bg-amber-100 flex items-center justify-center shrink-0 animate-bounce">
                <svg class="w-6 h-6 text-amber-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between gap-2 mb-1">
                  <h4 class="font-black text-slate-900 text-sm uppercase tracking-tight">{{ notif.title }}</h4>
                  <span class="text-[10px] font-bold text-slate-400">{{ formatTime(notif.timestamp) }}</span>
                </div>
                <p class="text-xs font-bold text-slate-600 bg-slate-50 p-2 rounded-lg border border-slate-100">
                  {{ notif.message }}
                </p>
                <div class="mt-3 flex justify-end">
                  <button 
                    @click="notifStore.remove(notif.id)"
                    class="px-4 py-2 bg-slate-900 text-white text-[10px] font-black uppercase tracking-widest rounded-xl hover:bg-slate-800 transition-all active:scale-95"
                  >
                    {{ $t('common.confirm') || 'TAMAM' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </TransitionGroup>
      </div>
      
      <!-- Desktop Sidebar -->
      <aside 
        class="hidden lg:flex flex-col bg-white border-r border-slate-200 shrink-0 transition-all duration-300 ease-in-out relative"
        :class="isSidebarCollapsed ? 'w-20' : 'w-64'"
      >
        <!-- Brand & Toggle -->
        <div class="h-16 flex items-center border-b border-slate-100 shrink-0 overflow-hidden" :class="isSidebarCollapsed ? 'justify-center px-0' : 'justify-between px-6'">
          <button @click="redirectByRole" class="text-left focus:outline-none transition-all duration-300" :class="isSidebarCollapsed ? 'scale-75' : ''">
            <Logo :size="isSidebarCollapsed ? 'xs' : 'sm'" :hideText="isSidebarCollapsed" shadow />
          </button>
          
          <button 
            @click="toggleSidebar" 
            class="w-8 h-8 flex items-center justify-center rounded-lg bg-slate-50 text-slate-400 hover:bg-slate-100 hover:text-slate-900 transition-all shadow-sm border border-slate-200"
            :class="isSidebarCollapsed ? 'absolute -right-4 top-20 z-10' : ''"
          >
            <svg 
              class="w-4 h-4 transition-transform duration-300" 
              :class="isSidebarCollapsed ? 'rotate-180' : ''"
              fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
        </div>

        <!-- Navigation -->
        <div class="flex-1 overflow-y-auto py-6 px-4 space-y-1 scrollbar-thin overflow-x-hidden">
          <p 
            v-if="!isSidebarCollapsed"
            class="px-3 text-xs font-bold text-slate-400 uppercase tracking-wider mb-2 animate-in fade-in duration-500"
          >
            {{ $t('admin.sidebar.title') }}
          </p>
          
          <template v-for="item in navItems" :key="item.path">
            <NuxtLink 
              v-if="authStore.hasRole(...item.roles)"
              :to="item.path"
              class="flex items-center gap-3 py-2.5 rounded-xl font-medium transition-all duration-200 group relative"
              :class="[
                isActive(item.path) ? 'bg-brand-50 text-brand-600 shadow-sm' : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900',
                isSidebarCollapsed ? 'px-0 justify-center' : 'px-3'
              ]"
              :title="isSidebarCollapsed ? item.name : ''"
            >
              <div class="w-5 h-5 flex items-center justify-center shrink-0 transition-transform group-hover:scale-110">
                <span v-html="item.icon"></span>
              </div>
              <span v-if="!isSidebarCollapsed" class="truncate animate-in slide-in-from-left-2 duration-300">{{ item.name }}</span>
            </NuxtLink>
          </template>

          <div class="my-6 border-t border-slate-100"></div>

          <NuxtLink 
            to="/admin/upgrade"
            class="flex items-center gap-3 py-2.5 rounded-xl font-medium text-slate-600 hover:bg-amber-50 hover:text-amber-600 transition-all group"
            :class="isSidebarCollapsed ? 'px-0 justify-center' : 'px-3'"
            v-show="isAdmin"
            :title="isSidebarCollapsed ? $t('admin.sidebar.upgrade') : ''"
          >
            <div class="w-5 h-5 flex items-center justify-center shrink-0 text-amber-500 transition-transform group-hover:scale-110">
              <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            <span v-if="!isSidebarCollapsed" class="truncate animate-in slide-in-from-left-2 duration-300">{{ $t('admin.sidebar.upgrade') }}</span>
          </NuxtLink>
        </div>

        <!-- User Profile Footer -->
        <div class="p-4 border-t border-slate-200 shrink-0 bg-slate-50/50 transition-all duration-300">
          <div class="flex items-center gap-3 mb-4 transition-all" :class="isSidebarCollapsed ? 'px-0 justify-center' : 'px-2'">
            <div class="w-10 h-10 rounded-full bg-white border border-slate-200 shadow-sm flex items-center justify-center text-slate-600 font-bold shrink-0">
              {{ $upper(authStore.user.username.charAt(0)) }}
            </div>
            <div v-if="!isSidebarCollapsed" class="min-w-0 flex-1 animate-in fade-in duration-500">
              <p class="text-sm font-bold text-slate-900 truncate">{{ authStore.user.username }}</p>
              <p class="text-xs text-slate-500 truncate capitalize">{{ $upper(roleLabel(authStore.user.role)) }}</p>
            </div>
          </div>
          
          <div class="flex gap-2 transition-all" :class="isSidebarCollapsed ? 'flex-col items-center' : ''">
            <button 
              @click="toggleLanguage" 
              class="flex items-center justify-center h-10 transition-all font-medium text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 shadow-sm"
              :class="isSidebarCollapsed ? 'w-10' : 'px-3 py-2 text-sm'"
              :title="isSidebarCollapsed ? (locale === 'tr' ? 'English' : 'Türkçe') : ''"
            >
              <span class="uppercase font-bold">{{ locale === 'tr' ? 'EN' : 'TR' }}</span>
            </button>
            <button 
              @click="handleLogout" 
              class="flex items-center justify-center h-10 transition-all font-medium text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 hover:text-rose-600 shadow-sm"
              :class="isSidebarCollapsed ? 'w-10' : 'flex-1 gap-2 px-4 py-2 text-sm'"
              :title="isSidebarCollapsed ? $t('auth.logoutButton') : ''"
            >
              <svg class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
              <span v-if="!isSidebarCollapsed" class="truncate">{{ $t('auth.logoutButton') }}</span>
            </button>
          </div>
        </div>
      </aside>

      <!-- Main Content -->
      <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
        
        <!-- Mobile Header -->
        <header class="lg:hidden h-16 bg-white border-b border-slate-200 flex items-center justify-between px-4 shrink-0">
          <button @click="redirectByRole" class="text-left focus:outline-none">
            <Logo size="sm" />
          </button>
          <button @click="isMobileMenuOpen = true" class="w-10 h-10 flex items-center justify-center text-slate-600 bg-slate-50 rounded-xl">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" /></svg>
          </button>
        </header>

        <!-- Main Scroll Area -->
        <main class="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 scrollbar-thin">
          <div class="max-w-[1600px] mx-auto transition-all duration-300">
            <slot />
          </div>
        </main>
      </div>

      <!-- Mobile Navigation Drawer -->
      <Transition
        enter-active-class="transition-opacity duration-300"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-active-class="transition-opacity duration-300"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div v-if="isMobileMenuOpen" class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-40 lg:hidden" @click="isMobileMenuOpen = false"></div>
      </Transition>

      <Transition
        enter-active-class="transition-transform duration-300 ease-out"
        enter-from-class="-translate-x-full"
        enter-to-class="translate-x-0"
        leave-active-class="transition-transform duration-300 ease-in"
        leave-from-class="translate-x-0"
        leave-to-class="-translate-x-full"
      >
        <aside v-if="isMobileMenuOpen" class="fixed inset-y-0 left-0 w-64 bg-white  z-50 flex flex-col lg:hidden">
          <div class="h-16 flex items-center justify-between px-6 border-b border-slate-100 shrink-0">
            <Logo size="sm" />
            <button @click="isMobileMenuOpen = false" class="text-slate-400 hover:text-slate-600 p-1">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </div>
          <div class="flex-1 overflow-y-auto py-4 px-4 space-y-1">
            <NuxtLink 
              v-for="item in navItems" 
              :key="item.path"
              :to="item.path"
              @click="isMobileMenuOpen = false"
              class="flex items-center gap-3 px-3 py-3 rounded-xl font-medium"
              :class="isActive(item.path) ? 'bg-brand-50 text-brand-600' : 'text-slate-600'"
              v-show="authStore.hasRole(...item.roles)"
            >
              <div class="w-5 h-5 flex items-center justify-center shrink-0">
                <span v-html="item.icon"></span>
              </div>
              {{ item.name }}
            </NuxtLink>
          </div>
          <div class="p-4 border-t border-slate-200 flex gap-2">
            <button @click="toggleLanguage" class="flex items-center justify-center px-3 py-2 text-sm font-medium text-slate-600 bg-slate-50 border border-slate-200 rounded-lg shadow-sm">
              <span class="uppercase font-bold">{{ locale === 'tr' ? 'EN' : 'TR' }}</span>
            </button>
            <button @click="handleLogout" class="flex-1 flex items-center justify-center gap-2 px-4 py-2 text-sm font-medium text-rose-600 bg-rose-50 border border-rose-100 rounded-lg">
              {{ $t('auth.logoutButton') }}
            </button>
          </div>
        </aside>
      </Transition>

    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'
import { useOrderStore } from '~/stores/order'
import { useNotificationStore } from '~/stores/notification'
import { useSocket } from '~/composables/useSocket'

const authStore = useAuthStore()
const orderStore = useOrderStore()
const notifStore = useNotificationStore()
const uiStore = useUiStore()
const route = useRoute()
const router = useRouter()

const isMobileMenuOpen = ref(false)
const isSidebarCollapsed = ref(false)

// Load collapsed state from localStorage on mount
onMounted(() => {
  const saved = localStorage.getItem('admin_sidebar_collapsed')
  if (saved !== null) isSidebarCollapsed.value = saved === 'true'
})

function toggleSidebar() {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
  localStorage.setItem('admin_sidebar_collapsed', isSidebarCollapsed.value.toString())
}
const { connect, subscribe } = useSocket()
let unsubNotif: (() => void) | null = null

const loginForm = ref({
  username: '',
  password: ''
})

async function handleLogin() {
  // Validation
  if (!loginForm.value.username || loginForm.value.username.length < 3) {
    uiStore.error(t('auth.usernameInvalid'))
    return
  }

  try {
    await authStore.login(loginForm.value.username, loginForm.value.password)
    // Role based redirect logic happens in watch or directly here
    redirectByRole()
  } catch (e: any) {
    const errorMessage = e?.message || e?.toString() || t('auth.loginFailed');
    const translated = t(errorMessage);
    useUiStore().error(translated.includes('error.') ? errorMessage : translated);
  }
}

async function handleLogout() {
  orderStore.orders = []
  await authStore.logout()
  isMobileMenuOpen.value = false
  router.push('/admin')
}

function redirectByRole() {
  if (!authStore.user) return
  const role = String(authStore.user.role || '').toLowerCase().trim().replace(/ı/g, 'i')
  if (role === 'superadmin') {
    window.location.href = '/super/tenants'
  }
  else if (role === 'admin') router.push('/admin/orders')
  else if (role === 'kitchen') router.push('/admin/kitchen')
  else if (role === 'bar') router.push('/admin/bar')
  else if (role === 'cashier') router.push('/admin/cashier')
  else if (role === 'saloon') router.push('/admin/saloon')
  else router.push('/admin/orders')
}

// Watch route changes to enforce role-based access (Fallback for middleware)
watch(() => route.path, (newPath) => {
  if (!authStore.user) return
  
  const role = String(authStore.user.role || '').toLowerCase().trim().replace(/ı/g, 'i')

  // Kitchen user should only see kitchen
  if ((role === 'kitchen' || role.includes('kitchen')) && !newPath.includes('/admin/kitchen')) {
    router.replace('/admin/kitchen')
  }
  
  // Bar user should only see bar
  if (role.includes('bar') && !newPath.includes('/admin/bar')) {
    router.replace('/admin/bar')
  }

  // Saloon user should only see saloon
  if (role.includes('saloon') && !newPath.includes('/admin/saloon')) {
    router.replace('/admin/saloon')
  }

  // Redirect from root /admin to appropriate dashboard
  if ((newPath === '/admin' || newPath === '/admin/') && authStore.user) {
    redirectByRole()
  }
}, { immediate: true })

onMounted(async () => {
  if (authStore.user && (route.path === '/admin' || route.path === '/admin/')) {
    redirectByRole()
  }

  if (authStore.user?.tenantCode) {
    try {
      await connect()
      unsubNotif = subscribe(`/topic/notifications/${authStore.user.tenantCode}`, (notif: any) => {
        if (notif.type === 'WAITER_CALL') {
          notifStore.add({
            type: 'WAITER_CALL',
            title: t('menu.callWaiter'),
            message: `${t('order.table')}: ${notif.tableCode}`,
            tableCode: notif.tableCode,
            timestamp: notif.timestamp || new Date().toISOString()
          })
          playNotificationSound()
        }
      })
    } catch (e) {
      console.error('Failed to connect to notification socket', e)
    }
  }
})

onBeforeUnmount(() => {
  if (unsubNotif) unsubNotif()
})

const { t, locale, setLocale } = useI18n()

function toggleLanguage() {
  const newLocale = locale.value === 'tr' ? 'en' : 'tr'
  setLocale(newLocale)
  const i18nCookie = useCookie('fm_i18n', { path: '/', maxAge: 60 * 60 * 24 * 365 })
  i18nCookie.value = newLocale
}

function roleLabel(role: string) {
  const r = String(role || '').toLowerCase().trim().replace(/ı/g, 'i')
  if (r === 'admin') return t('admin.roles.admin')
  if (r === 'kitchen') return t('admin.roles.kitchen')
  if (r === 'bar') return t('admin.roles.bar')
  if (r === 'cashier') return t('admin.roles.cashier')
  if (r === 'saloon') return t('admin.roles.saloon')
  if (r === 'superadmin') return t('admin.roles.superadmin')
  return r
}

const isAdmin = computed(() => authStore.hasRole('admin'))

function isActive(path: string) {
  return route.path === path || route.path.startsWith(path + '/')
}

function formatTime(iso: string) {
  try {
    return new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  } catch {
    return ''
  }
}

function playNotificationSound() {
  try {
    const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3')
    audio.volume = 0.5
    audio.play()
  } catch (e) {
    console.warn('Sound play failed', e)
  }
}

const navItems = computed(() => [
  { 
    name: t('admin.nav.orders'), 
    path: '/admin/orders', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" /></svg>'
  },
  { 
    name: t('admin.nav.menu'), 
    path: '/admin/menu-management', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>'
  },
  { 
    name: t('admin.nav.stock'), 
    path: '/admin/stock', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>'
  },
  { 
    name: t('admin.nav.tables'), 
    path: '/admin/tables', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" /></svg>'
  },
  { 
    name: t('admin.nav.users'), 
    path: '/admin/users', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>'
  },
  { 
    name: t('admin.nav.qr'), 
    path: '/admin/qr', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" /></svg>'
  },
  { 
    name: t('admin.nav.reports'), 
    path: '/admin/reports', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" /></svg>'
  },
  { 
    name: t('admin.nav.branding'), 
    path: '/admin/branding', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" /></svg>'
  },
  { 
    name: t('admin.nav.loyalty') || 'Sadakat Programı', 
    path: '/admin/loyalty', 
    roles: ['admin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>'
  },
  { 
    name: t('admin.nav.kitchen'), 
    path: '/admin/kitchen', 
    roles: ['kitchen'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 14v6m-3-3h6M6 10h2a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v2a2 2 0 002 2zm10 0h2a2 2 0 002-2V6a2 2 0 00-2-2h-2a2 2 0 00-2 2v2a2 2 0 002 2zM6 20h2a2 2 0 002-2v-2a2 2 0 00-2-2H6a2 2 0 00-2 2v2a2 2 0 002 2z" /></svg>'
  },
  { 
    name: t('admin.nav.bar'), 
    path: '/admin/bar', 
    roles: ['bar'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>'
  },
  { 
    name: t('admin.nav.cashier'), 
    path: '/admin/cashier', 
    roles: ['cashier'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" /></svg>'
  },
  { 
    name: t('admin.nav.saloon'), 
    path: '/admin/saloon', 
    roles: ['saloon'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>'
  },
  { 
    name: t('admin.nav.tenants') || 'Tenants', 
    path: '/super/tenants', 
    roles: ['superadmin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" /></svg>'
  },
  { 
    name: t('admin.nav.subscriptions') || 'Subscriptions', 
    path: '/super/subscriptions', 
    roles: ['superadmin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" /></svg>'
  },
  { 
    name: t('admin.nav.blog') || 'Blog', 
    path: '/super/blog', 
    roles: ['superadmin'],
    icon: '<svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10l4 4v10a2 2 0 01-2 2z" /><path stroke-linecap="round" stroke-linejoin="round" d="M14 2v4a2 2 0 002 2h4" /><path stroke-linecap="round" stroke-linejoin="round" d="M7 10h10M7 14h10M7 18h5" /></svg>'
  }
])
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar {
  width: 6px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}
</style>

<style>
.list-enter-active,
.list-leave-active {
  transition: all 0.4s ease;
}
.list-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.list-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
