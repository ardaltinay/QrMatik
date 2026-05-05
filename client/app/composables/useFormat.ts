/**
 * Formatting utilities composable.
 * Provides money, date, label formatting helpers.
 */
export function useFormat() {
  function formatMoney(value: number | string, currency = 'TRY'): string {
    try {
      const num = typeof value === 'number' ? value : Number(value || 0)
      return new Intl.NumberFormat('tr-TR', { style: 'currency', currency }).format(num)
    } catch {
      const num = typeof value === 'number' ? value : Number(value || 0)
      return num.toFixed(2) + ' ₺'
    }
  }

  function formatDateTz(input: any, locale?: string, timeZone?: string): string {
    try {
      if (!locale) {
        try { locale = localStorage.getItem('qm_locale') || 'tr-TR' } catch { locale = 'tr-TR' }
      }
      if (!timeZone) {
        try { timeZone = localStorage.getItem('qm_tz') || 'Europe/Istanbul' } catch { timeZone = 'Europe/Istanbul' }
      }

      const d = parseBestDate(input)
      if (!d) return String(input || '-')

      return new Intl.DateTimeFormat(locale, {
        timeZone,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
      }).format(d)
    } catch {
      return String(input || '-')
    }
  }

  function statusLabel(s: string): string {
    const { t } = useI18n()
    const key = String(s || '').toLowerCase()
    const statusMap: Record<string, string> = {
      new: t('order.statuses.new'),
      preparing: t('order.statuses.preparing'),
      ready: t('order.statuses.ready'),
      served: t('order.statuses.served'),
      bill_requested: t('order.statuses.bill_requested'),
      payment_completed: t('order.statuses.payment_completed'),
      canceled: t('order.statuses.canceled'),
      expired: t('order.statuses.expired'),
    }
    return statusMap[key] || s || '-'
  }

  function categoryLabel(cat: string): string {
    const { t } = useI18n()
    const key = String(cat || '').toLowerCase()
    const map: Record<string, string> = {
      food: t('menu.categories.food'),
      drink: t('menu.categories.drink'),
      drinks: t('menu.categories.drinks'),
    }
    return map[key] || cat
  }

  function subLabel(s: string): string {
    const { t } = useI18n()
    const key = String(s || '').toLowerCase()
    try {
      const translated = t(`menu.subcategories.${key}`)
      // If key doesn't exist, vue-i18n returns the key path
      return translated.startsWith('menu.subcategories.') ? s : translated
    } catch {
      return s
    }
  }

  function orderCodeFromId(id: any): string {
    const s = id != null ? String(id) : ''
    if (!s) return '-'
    if (/^\d+$/.test(s)) return s
    return s.replace(/-/g, '').slice(-6).toUpperCase()
  }

  return {
    formatMoney,
    formatDateTz,
    statusLabel,
    categoryLabel,
    subLabel,
    orderCodeFromId,
  }
}

// ── Internal helpers ──────────────────────────────────────
function parseBestDate(input: any): Date | null {
  try {
    if (!input) return null
    if (input instanceof Date) return isNaN(input.getTime()) ? null : input
    if (typeof input === 'number') {
      const d = new Date(input)
      return isNaN(d.getTime()) ? null : d
    }
    if (typeof input === 'object') {
      const y = input.year || input.y
      const mo = (input.monthValue || input.month || 1) - 1
      const day = input.dayOfMonth || input.day || 1
      const h = input.hour || 0
      const mi = input.minute || 0
      const se = input.second || 0
      if (y) {
        const dt = new Date(y, mo, day, h, mi, se)
        return isNaN(dt.getTime()) ? null : dt
      }
    }
    if (typeof input === 'string') {
      let d = new Date(input)
      if (!isNaN(d.getTime())) return d
      if (input.includes(' ')) {
        d = new Date(input.replace(' ', 'T'))
        if (!isNaN(d.getTime())) return d
      }
    }
  } catch { /* ignore */ }
  return null
}
