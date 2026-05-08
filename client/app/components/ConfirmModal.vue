<script setup lang="ts">
const uiStore = useUiStore()
const { t } = useI18n()

function handleCancel() {
  if (uiStore.confirmData?.onCancel) {
    uiStore.confirmData.onCancel()
  }
  uiStore.closeConfirm()
}

function handleConfirm() {
  if (uiStore.confirmData?.onConfirm) {
    uiStore.confirmData.onConfirm()
  }
  uiStore.closeConfirm()
}
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="uiStore.confirmData?.isOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-md" @click="handleCancel"></div>

        <!-- Modal -->
        <div class="bg-white/90 backdrop-blur-xl rounded-3xl w-full max-w-sm relative z-10 overflow-hidden shadow-2xl border border-white/20 animate-slide-up">
          <div class="p-8 text-center">
            <!-- Icon -->
            <div 
              class="w-16 h-16 mx-auto mb-6 rounded-2xl flex items-center justify-center"
              :class="uiStore.confirmData.isDanger ? 'bg-rose-50 text-rose-500' : 'bg-brand-50 text-brand-500'"
            >
              <svg v-if="uiStore.confirmData.isDanger" class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <svg v-else class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>

            <h3 class="text-xl font-black text-slate-900 mb-2 leading-tight">
              {{ uiStore.confirmData.title }}
            </h3>
            <p class="text-slate-500 font-medium leading-relaxed mb-8">
              {{ uiStore.confirmData.message }}
            </p>

            <div class="grid grid-cols-2 gap-3">
              <button 
                @click="handleCancel" 
                class="px-6 py-3.5 text-sm font-black text-slate-500 bg-slate-100 hover:bg-slate-200 rounded-2xl transition-all active:scale-95"
              >
                {{ uiStore.confirmData.cancelText || t('admin.common.cancel') }}
              </button>
              <button 
                @click="handleConfirm" 
                class="px-6 py-3.5 text-sm font-black text-white shadow-lg shadow-brand-500/20 transition-all active:scale-95 rounded-2xl"
                :class="uiStore.confirmData.isDanger ? 'bg-rose-500 hover:bg-rose-600 shadow-rose-500/20' : 'bg-brand-500 hover:bg-brand-600'"
              >
                {{ uiStore.confirmData.confirmText || t('admin.common.confirm') || 'OK' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

@keyframes slide-up {
  from {
    transform: translateY(20px) scale(0.95);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.animate-slide-up {
  animation: slide-up 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
</style>
