import { defineNuxtPlugin } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  const { locale } = nuxtApp.$i18n

  const upper = (str: string) => {
    if (!str) return ''
    // Use the current locale for case transformation
    return str.toLocaleUpperCase(locale.value)
  }

  const lower = (str: string) => {
    if (!str) return ''
    return str.toLocaleLowerCase(locale.value)
  }

  // Inject into the app context
  return {
    provide: {
      upper,
      lower
    }
  }
})
