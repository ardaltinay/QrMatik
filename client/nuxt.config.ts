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
    strategy: 'prefix',
    lazy: true,
    langDir: '../app/locales',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'fm_i18n',
      alwaysRedirect: false,
      fallbackLocale: 'tr',
      redirectOn: 'root',
      cookieDomain: process.env.NODE_ENV === 'production' ? '.feasymenu.com' : undefined,
      cookieSecure: process.env.NODE_ENV === 'production',
      cookieCrossOrigin: true,
      cookieSameSite: 'Lax',
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
      titleTemplate: '%s | feasymenu',
      meta: [
        { name: 'theme-color', content: '#0f172a' },
        { property: 'og:image', content: 'https://feasymenu.com/og-image.png' },
        { property: 'og:url', content: 'https://feasymenu.com' },
        { property: 'og:type', content: 'website' },
        { property: 'og:site_name', content: 'feasymenu' },
        { name: 'twitter:card', content: 'summary_large_image' },
        { name: 'twitter:image', content: 'https://feasymenu.com/og-image.png' },
      ],
      link: [
        { rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' },
        { rel: 'icon', type: 'image/png', sizes: '32x32', href: '/favicon-32x32.png' },
        { rel: 'icon', type: 'image/png', sizes: '16x16', href: '/favicon-16x16.png' },
        { rel: 'apple-touch-icon', sizes: '180x180', href: '/apple-touch-icon.png' },
        { rel: 'manifest', href: '/site.webmanifest' },
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
