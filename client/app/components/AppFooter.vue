<template>
  <footer class="border-t border-slate-100 bg-slate-50/50 mt-auto">
    <div class="mx-auto max-w-7xl px-6 py-16">
      <div class="grid gap-12 md:grid-cols-4">
        <!-- Brand -->
        <div class="md:col-span-1">
          <NuxtLink to="/" class="group mb-6 block">
            <Logo size="sm" />
          </NuxtLink>
          <p class="text-sm text-slate-500 leading-relaxed font-medium">{{ $t('footer.tagline') }}</p>
        </div>

        <!-- Product -->
        <div>
          <h4 class="text-sm font-bold text-slate-900 uppercase tracking-wider mb-6">{{ $t('footer.product') }}</h4>
          <ul class="space-y-4">
            <li><NuxtLink to="/qr-menu" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">{{ $t('nav.qrMenu') }}</NuxtLink></li>
            <li><NuxtLink to="/dijital-menu" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">{{ $t('menu.viewMenu') }}</NuxtLink></li>
            <li><NuxtLink to="/qr-siparis" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">{{ $t('landing.features.orderTrackingTitle') }}</NuxtLink></li>
            <li><NuxtLink to="/#pricing" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">{{ $t('footer.pricing') }}</NuxtLink></li>
          </ul>
        </div>

        <!-- Resources -->
        <div>
          <h4 class="text-sm font-bold text-slate-900 uppercase tracking-wider mb-6">{{ $t('footer.resources') }}</h4>
          <ul class="space-y-4">
            <li><NuxtLink to="/blog" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">{{ $t('common.blog') }}</NuxtLink></li>
            <li><NuxtLink to="/about" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">{{ $t('common.about') }}</NuxtLink></li>
            <li><NuxtLink to="/signup/tenant" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">{{ $t('footer.signUp') }}</NuxtLink></li>
          </ul>
        </div>

        <!-- Contact -->
        <div>
          <h4 class="text-sm font-bold text-slate-900 uppercase tracking-wider mb-6">{{ $t('footer.contact') }}</h4>
          <ul class="space-y-4">
            <li>
              <a href="mailto:hello@feasymenu.com" class="text-sm text-slate-500 hover:text-brand-600 transition-colors font-medium">
                hello@feasymenu.com
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- Bottom -->
      <div class="mt-12 pt-8 border-t border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-4">
        <p class="text-xs text-slate-400 font-medium">{{ $t('footer.copyright', { year: new Date().getFullYear() }) }}</p>
        <div class="flex items-center gap-6">
          <NuxtLink to="/about" class="text-xs text-slate-400 hover:text-slate-600 transition-colors font-medium">{{ $t('footer.privacy') }}</NuxtLink>
          <NuxtLink to="/about" class="text-xs text-slate-400 hover:text-slate-600 transition-colors font-medium">{{ $t('footer.terms') }}</NuxtLink>
        </div>
      </div>
    </div>
  </footer>
</template>
