import { defineStore } from 'pinia'

export interface FMNotification {
  id: string
  type: 'WAITER_CALL' | 'BILL_REQUEST' | 'OTHER'
  title: string
  message: string
  tableCode?: string
  timestamp: string
}

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<FMNotification[]>([])

  function add(notif: Omit<FMNotification, 'id'>) {
    const id = Math.random().toString(36).substring(2, 9)
    notifications.value.unshift({ ...notif, id })
  }

  function remove(id: string) {
    notifications.value = notifications.value.filter(n => n.id !== id)
  }

  function clearAll() {
    notifications.value = []
  }

  return {
    notifications,
    add,
    remove,
    clearAll
  }
})
