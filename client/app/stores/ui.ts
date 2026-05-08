import { defineStore } from 'pinia'

interface ToastItem {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
}

export const useUiStore = defineStore('ui', () => {
  const toasts = ref<ToastItem[]>([])
  let nextId = 0

  const confirmData = ref<{
    isOpen: boolean
    title: string
    message: string
    onConfirm: () => void
    onCancel?: () => void
    confirmText?: string
    cancelText?: string
    isDanger?: boolean
  } | null>(null)

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

  function confirm(data: {
    title: string
    message: string
    onConfirm: () => void
    onCancel?: () => void
    confirmText?: string
    cancelText?: string
    isDanger?: boolean
  }) {
    confirmData.value = { ...data, isOpen: true }
  }

  function closeConfirm() {
    if (confirmData.value) {
      confirmData.value.isOpen = false
    }
  }

  function success(message: string) { toast(message, 'success') }
  function error(message: string) { toast(message, 'error') }
  function info(message: string) { toast(message, 'info') }

  return {
    toasts,
    confirmData,
    toast,
    removeToast,
    confirm,
    closeConfirm,
    success,
    error,
    info,
  }
})
