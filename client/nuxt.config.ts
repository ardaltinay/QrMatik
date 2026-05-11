export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },


  modules: [
    '@pinia/nuxt',
    '@nuxt/fonts',
    '@vueuse/nuxt',
    '@nuxtjs/i18n',
    '@nuxtjs/sitemap',
    '@nuxtjs/robots',
    '@nuxt/image',
  ],

  site: {
    url: 'https://feasymenu.com',
    name: 'feasymenu',
  },

  sitemap: {
    sources: [
      '/api/public/blog' // Backend'den tüm blog yazılarını otomatik çekip haritaya ekler
    ],
    defaults: {
      changefreq: 'daily',
      priority: 0.7,
      lastmod: new Date().toISOString(),
    }
  },

  robots: {
    allow: '/',
    sitemap: 'https://feasymenu.com/sitemap.xml'
  },

  i18n: {
    baseUrl: 'https://feasymenu.com',
    locales: [
      { code: 'tr', iso: 'tr-TR', name: 'Türkçe', file: 'tr.ts' },
      { code: 'en', iso: 'en-US', name: 'English', file: 'en.ts' },
    ],
    defaultLocale: 'tr',
    strategy: 'prefix_except_default',
    lazy: true,
    langDir: '../app/locales',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'fm_i18n',
      alwaysRedirect: true,
      fallbackLocale: 'tr',
      redirectOn: 'root',
      cookieDomain: process.env.NODE_ENV === 'production' ? '.feasymenu.com' : undefined,
      cookieCrossOrigin: true,
    },
  },

  css: ['./app/assets/css/main.css'],

  postcss: {
    plugins: {
      '@tailwindcss/postcss': {},
    },
  },

  fonts: {
    families: [
      {
        name: 'Plus Jakarta Sans',
        provider: 'google',
        weights: [300, 400, 500, 600, 700, 800],
      },
      {
        name: 'Instrument Serif',
        provider: 'google',
        weights: [400],
      },
    ],
  },

  app: {
    head: {
      htmlAttrs: {},
      charset: 'utf-8',
      viewport: 'width=device-width, initial-scale=1',
      title: 'Hızlı ve Kolay QR Menü & Restoran Yönetim Sistemi',
      titleTemplate: '%s | feasymenu',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: 'feasymenu ile restoran ve barınızı dijitalleştirin. Hızlı ve kolay (fast & easy) QR menü deneyimi, anlık sipariş takibi ve kurumsal yönetim paneli.' },
        { name: 'theme-color', content: '#0f172a' },
        { property: 'og:title', content: 'feasymenu — Fast and Easy QR Menü & Restoran Yönetimi' },
        { property: 'og:description', content: 'Hızlı ve kolay (fast & easy) modern QR menü çözümleriyle işletmenize değer katın.' },
        { property: 'og:image', content: 'https://feasymenu.com/og-image.jpg' },
        { property: 'og:url', content: 'https://feasymenu.com' },
        { property: 'og:type', content: 'website' },
        { name: 'twitter:card', content: 'summary_large_image' },
      ],
      link: [
        { rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' },
      ],
    },
  },

  routeRules: {
    '/': { ssr: true },
    '/about': { ssr: true },
    '/qr-menu': { ssr: true },
    '/dijital-menu': { ssr: true },
    '/qr-siparis': { ssr: true },
    '/restoran-bar-menu': { ssr: true },
    '/blog/**': { ssr: true },
    '/admin/**': { ssr: true },
    '/super/**': { ssr: true },
    '/menu': { ssr: true },
    '/order/**': { ssr: true },
  },

  nitro: {
    devProxy: {
      '/api': {
        target: 'http://127.0.0.1:8080/api',
        changeOrigin: false,
      },
      '/api/ws': {
        target: 'http://127.0.0.1:8080/ws',
        ws: true,
        changeOrigin: false,
      },
      '/files': {
        target: 'http://127.0.0.1:8080/files',
        changeOrigin: false,
      },
    },
  },
})
