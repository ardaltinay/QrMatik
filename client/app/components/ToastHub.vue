<template>
  <Teleport to="body">
    <TransitionGroup
      tag="div"
      name="toast"
      class="fixed bottom-6 right-6 z-[100] flex flex-col-reverse gap-3 pointer-events-none"
    >
      <div
        v-for="t in uiStore.toasts"
        :key="t.id"
        class="pointer-events-auto glass-strong rounded-xl px-5 py-3  max-w-sm flex items-center gap-3 cursor-pointer"
        :class="{
          'border-green-500/30': t.type === 'success',
          'border-red-500/30': t.type === 'error',
          'border-brand-500/30': t.type === 'info',
        }"
        @click="uiStore.removeToast(t.id)"
      >
        <div class="text-lg" aria-hidden="true">
          {{ t.type === 'success' ? '✓' : t.type === 'error' ? '✕' : 'ℹ' }}
        </div>
        <p class="text-sm leading-snug"
          :class="{
            'text-green-500': t.type === 'success',
            'text-red-500': t.type === 'error',
            'text-yellow-500': t.type === 'info',
          }"
        >{{ t.message }}</p>
      </div>
    </TransitionGroup>
  </Teleport>
</template>

<script setup lang="ts">
const uiStore = useUiStore()

// Expose globally via provide
provide('toast', { add: uiStore.toast, remove: uiStore.removeToast })

// Also listen to custom events for non-component usage
if (import.meta.client) {
  window.addEventListener('qm:toast', ((e: CustomEvent) => {
    const { message, type, duration } = e.detail || {}
    uiStore.toast(message || '', type || 'info', duration)
  }) as EventListener)
}
</script>

<style scoped>
.toast-enter-active { transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-leave-active { transition: all 0.25s ease-in; }
.toast-enter-from { opacity: 0; transform: translateY(16px) scale(0.95); }
.toast-leave-to { opacity: 0; transform: translateX(100px); }
.toast-move { transition: transform 0.3s ease; }
</style>
