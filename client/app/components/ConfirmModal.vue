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
    <Transition name="modal-overlay">
      <div v-if="uiStore.confirmData?.isOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
        <!-- High-end Backdrop -->
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-md transition-all duration-500" @click="handleCancel"></div>

        <!-- Sleek Modal Box -->
        <div class="relative w-full max-w-sm bg-white/95 backdrop-blur-2xl rounded-[2.5rem] shadow-[0_40px_80px_-15px_rgba(0,0,0,0.3)] border border-white/20 overflow-hidden animate-modal-in">
          <!-- Top Accent Line (Optional) -->
          <div 
            class="h-2 w-full"
            :class="uiStore.confirmData.isDanger ? 'bg-rose-500' : 'bg-brand-500'"
          ></div>

          <div class="p-10 text-center">
            <!-- Modern Icon Sphere -->
            <div 
              class="w-20 h-20 mx-auto mb-8 rounded-[2rem] flex items-center justify-center shadow-inner relative group"
              :class="uiStore.confirmData.isDanger ? 'bg-rose-50 text-rose-500' : 'bg-brand-50 text-brand-500'"
            >
              <div class="absolute inset-0 rounded-[2rem] animate-pulse opacity-20" :class="uiStore.confirmData.isDanger ? 'bg-rose-400' : 'bg-brand-400'"></div>
              <svg v-if="uiStore.confirmData.isDanger" class="w-10 h-10 relative z-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <svg v-else class="w-10 h-10 relative z-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>

            <h3 class="text-2xl font-black text-slate-900 mb-3 tracking-tight leading-tight">
              {{ uiStore.confirmData.title }}
            </h3>
            <p class="text-slate-500 font-medium leading-relaxed mb-10 px-2">
              {{ uiStore.confirmData.message }}
            </p>

            <div class="flex flex-col gap-3">
              <button 
                @click="handleConfirm" 
                class="w-full py-4.5 px-6 text-sm font-black text-white shadow-[0_20px_40px_-10px] transition-all active:scale-95 rounded-2xl tracking-widest uppercase"
                :class="uiStore.confirmData.isDanger 
                  ? 'bg-rose-500 hover:bg-rose-600 shadow-rose-500/40' 
                  : 'bg-brand-600 hover:bg-brand-700 shadow-brand-500/40'"
              >
                {{ uiStore.confirmData.confirmText || t('admin.common.confirm') || 'OK' }}
              </button>
              <button 
                @click="handleCancel" 
                class="w-full py-4 text-sm font-black text-slate-400 hover:text-slate-600 hover:bg-slate-50 rounded-2xl transition-all uppercase tracking-widest"
              >
                {{ uiStore.confirmData.cancelText || t('admin.common.cancel') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-overlay-enter-active, .modal-overlay-leave-active {
  transition: opacity 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}
.modal-overlay-enter-from, .modal-overlay-leave-to {
  opacity: 0;
}

@keyframes modal-in {
  from {
    transform: translateY(30px) scale(0.9) rotateX(-10deg);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1) rotateX(0);
    opacity: 1;
  }
}

.animate-modal-in {
  animation: modal-in 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  perspective: 1000px;
}

.py-4\.5 {
  padding-top: 1.125rem;
  padding-bottom: 1.125rem;
}
</style>
