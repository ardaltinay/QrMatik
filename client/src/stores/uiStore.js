import { defineStore } from 'pinia'

let nextId = 1

export const useUiStore = defineStore('ui', {
  state: () => ({
    toasts: [] // { id, type: 'info'|'success'|'error', text }
  }),
  actions: {
    toast(text, type = 'info', timeout = 3000) {
      const id = nextId++
      this.toasts.push({ id, type, text })
      if (timeout > 0) setTimeout(() => this.removeToast(id), timeout)
      return id
    },
    toastSuccess(text, timeout) { return this.toast(text, 'success', timeout) },
    toastError(text, timeout) { return this.toast(text, 'error', timeout) },
    removeToast(id) { this.toasts = this.toasts.filter(t => t.id !== id) }
  }
})
