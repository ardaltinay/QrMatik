
<template>
  <div>
  <header class="bg-white shadow relative">
      <div class="container mx-auto px-6 py-4 flex justify-between items-center">
        <router-link to="/" class="flex items-center gap-3">
          <img src="@/assets/logo.svg" alt="QrMatik" class="w-10 h-10" />
          <div class="text-xl font-bold text-brand-700">QrMatik</div>
        </router-link>
        <nav class="hidden md:flex gap-4 items-center">
          <router-link to="/" class="text-gray-700 hover:text-indigo-600">Anasayfa</router-link>
          <router-link to="/menu" class="text-gray-700 hover:text-indigo-600">Menü</router-link>
          <router-link v-if="isAdmin" to="/admin" class="text-gray-700 hover:text-indigo-600">Admin</router-link>
        </nav>
        <div class="md:hidden">
          <button ref="menuButton" @click="open = !open" class="p-2 rounded bg-gray-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
        </div>
      </div>
        <!-- mobile menu (modern sheet) -->
        <transition name="slide-down">
          <div v-if="open" ref="mobileMenu" class="md:hidden absolute left-0 right-0 top-full z-40 flex justify-center">
            <div class="mt-2 w-[calc(100%-2rem)] bg-white rounded-b-xl shadow-xl overflow-hidden motion-safe:animate-slideDown">
              <div class="p-3 border-b flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <img src="@/assets/logo.svg" alt="QrMatik" class="w-8 h-8" />
                  <div class="font-semibold text-base">QrMatik</div>
                </div>
                <button @click="open = false" class="p-2 rounded bg-gray-100">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 011.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
                  </svg>
                </button>
              </div>
              <div class="p-3">
                <nav class="flex flex-col gap-2">
                  <router-link @click="open = false" to="/" class="py-3 px-3 rounded-md hover:bg-gray-50 transition text-base font-medium">Anasayfa</router-link>
                  <router-link @click="open = false" to="/menu" class="py-3 px-3 rounded-md hover:bg-gray-50 transition text-base font-medium">Menü</router-link>
                  <router-link v-if="isAdmin" @click="open = false" to="/admin" class="py-3 px-3 rounded-md hover:bg-gray-50 transition text-base font-medium">Admin</router-link>
                </nav>
              </div>
            </div>
          </div>
        </transition>
      </header>

      <main class="container mx-auto px-6 py-8">
        <router-view/>
      </main>
      <ToastHub />
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import ToastHub from '@/components/ToastHub.vue'
const open = ref(false)
const mobileMenu = ref(null)
const menuButton = ref(null)
const auth = useAuthStore()

const isAdmin = computed(() => auth.user && auth.user.role === 'admin')

function onDocClick(e) {
  if (!open.value) return
  const el = e.target
  if (mobileMenu.value && mobileMenu.value.contains(el)) return
  if (menuButton.value && menuButton.value.contains(el)) return
  open.value = false
}

function onFocusIn(e) {
  if (!open.value) return
  const el = e.target
  if (mobileMenu.value && mobileMenu.value.contains(el)) return
  if (menuButton.value && menuButton.value.contains(el)) return
  open.value = false
}

function onKeyDown(e) {
  if (e.key === 'Escape' && open.value) open.value = false
}

onMounted(() => {
  document.addEventListener('click', onDocClick, true)
  document.addEventListener('focusin', onFocusIn, true)
  document.addEventListener('keydown', onKeyDown)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocClick, true)
  document.removeEventListener('focusin', onFocusIn, true)
  document.removeEventListener('keydown', onKeyDown)
})

</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 180ms ease; }
.fade-enter-from, .fade-leave-to { opacity: 0 }
.fade-enter-to, .fade-leave-from { opacity: 1 }

/* small tweak for sheet shadow */
.rounded-xl { border-radius: 12px }

/* slide-down transition */
.slide-down-enter-from { transform: translateY(-12px); opacity: 0 }
.slide-down-enter-active { transition: transform 220ms cubic-bezier(.2,.8,.2,1), opacity 220ms }
.slide-down-enter-to { transform: translateY(0); opacity: 1 }
.slide-down-leave-from { transform: translateY(0); opacity: 1 }
.slide-down-leave-active { transition: transform 180ms cubic-bezier(.2,.8,.2,1), opacity 160ms }
.slide-down-leave-to { transform: translateY(-12px); opacity: 0 }
</style>

