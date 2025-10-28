<template>
  <div class="p-3 bg-white border rounded-lg flex flex-col sm:flex-row sm:items-center sm:justify-between hover:shadow-lg transition">
    <div>
      <div class="font-medium">#{{ order.id }} — {{ order.table }}</div>
      <div class="text-sm text-gray-500">{{ order.createdAt }}</div>
      <div class="text-sm text-gray-700 mt-1">Toplam: <span class="font-medium">{{ formatMoney(order.total) }}</span></div>
    </div>
    <div class="mt-3 sm:mt-0 flex items-center gap-2">
      <div class="px-3 py-1 rounded-full text-sm font-medium" :class="statusColor">{{ statusLabel }}</div>
      <button @click="$emit('open', order)" class="px-3 py-1 bg-white border rounded-lg shadow-sm hover:bg-gray-50">Detay</button>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { formatMoney } from '@/utils/format'
import { statusLabel as statusLabelUtil } from '@/utils/format'

export default {
  name: 'AdminOrderRow',
  props: { order: { type: Object, required: true } },
  setup(props) {
    const statusColor = computed(() => {
      const s = props.order.status
      if (s === 'new') return 'bg-yellow-100 text-yellow-800'
      if (s === 'preparing') return 'bg-indigo-100 text-indigo-800'
      if (s === 'ready') return 'bg-green-100 text-green-800'
      if (s === 'payment_completed') return 'bg-blue-100 text-blue-800'
      return 'bg-gray-100 text-gray-700'
    })

    const statusLabel = computed(() => statusLabelUtil(props.order.status))

    return { statusColor, statusLabel, formatMoney }
  }
}
</script>
