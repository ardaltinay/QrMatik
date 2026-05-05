import { defineStore } from 'pinia'

interface ToastItem {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
}

export const useUiStore = defineStore('ui', () => {
  const toasts = ref<ToastItem[]>([])
  let nextId = 0

  function toast(message: string, type: ToastItem['type'] = 'info', duration = 4000) {
    const id = nextId++
    toasts.value.push({ id, message, type })
    if (import.meta.client) {
      setTimeout(() => removeToast(id), duration)
    }
  }

  function removeToast(id: number) {
    toasts.value = toasts.value.filter(t => t.id !== id)
  }

  function success(message: string) { toast(message, 'success') }
  function error(message: string) { toast(message, 'error') }
  function info(message: string) { toast(message, 'info') }

  return {
    toasts,
    toast,
    removeToast,
    success,
    error,
    info,
  }
})
