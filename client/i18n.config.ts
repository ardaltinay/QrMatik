import tr from './app/locales/tr'
import en from './app/locales/en'

export default defineI18nConfig(() => ({
  legacy: false,
  fallbackLocale: 'tr',
  messages: { tr, en },
}))
