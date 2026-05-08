<template>
  <div class="py-10 text-center">
    <!-- This page is just a router entry. The layout will handle the redirect. -->
    <div v-if="authStore.user" class="animate-pulse flex flex-col items-center">
      <div class="w-10 h-10 border-4 border-brand-200 border-t-brand-500 rounded-full animate-spin mb-4"></div>
      <p class="text-slate-500 font-medium">{{ $t('admin.common.redirecting') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

definePageMeta({
  layout: 'admin'
})

const authStore = useAuthStore()
const router = useRouter()

function redirectByRole() {
  if (!authStore.user) return

  const role = String(authStore.user.role || '').toLowerCase().trim().replace(/ı/g, 'i')
  
  if (role === 'superadmin') {
    window.location.href = '/super/tenants'
  } else if (role === 'admin') {
    router.push('/admin/orders')
  } else if (role.includes('kitchen')) {
    router.push('/admin/kitchen')
  } else if (role.includes('bar')) {
    router.push('/admin/bar')
  } else if (role.includes('cashier')) {
    router.push('/admin/cashier')
  } else if (role.includes('saloon')) {
    router.push('/admin/saloon')
  } else {
    router.push('/admin/orders')
  }
}

// Ensure redirect happens even if layout misses it
onMounted(() => {
  if (authStore.user) redirectByRole()
})

watch(() => authStore.user, (user) => {
  if (user) redirectByRole()
})

useSeoMeta({
  title: 'Admin Paneli'
})
</script>
