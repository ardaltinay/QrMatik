<template>
  <footer class="bg-white pt-24 pb-12">
    <div class="container-custom">
      <div class="grid gap-12 md:grid-cols-4 pb-16">
        <!-- Brand -->
        <div class="md:col-span-1">
          <NuxtLink :to="localePath('/')" class="mb-6 block">
            <Logo size="sm" />
          </NuxtLink>
          <p class="text-sm text-slate-400 leading-relaxed font-medium max-w-[200px]">{{ $t('footer.tagline') }}</p>
        </div>

        <!-- Links -->
        <div>
          <h4 class="text-sm font-bold text-slate-900 uppercase tracking-widest mb-6">{{ $t('footer.product') }}</h4>
          <ul class="space-y-4">
            <li><NuxtLink :to="localePath('/qr-menu')" class="text-sm text-slate-400 hover:text-brand-400 transition-colors font-medium">{{ $t('nav.qrMenu') }}</NuxtLink></li>
            <li><NuxtLink :to="localePath('/') + '#pricing'" class="text-sm text-slate-400 hover:text-brand-400 transition-colors font-medium">{{ $t('footer.pricing') }}</NuxtLink></li>
            <li><NuxtLink :to="localePath('/signup/tenant')" class="text-sm text-slate-400 hover:text-brand-400 transition-colors font-medium">{{ $t('footer.signUp') }}</NuxtLink></li>
          </ul>
        </div>

        <div>
          <h4 class="text-sm font-bold text-slate-900 uppercase tracking-widest mb-6">{{ $t('footer.resources') }}</h4>
          <ul class="space-y-4">
            <li><NuxtLink :to="localePath('/blog')" class="text-sm text-slate-400 hover:text-brand-400 transition-colors font-medium">{{ $t('common.blog') }}</NuxtLink></li>
            <li><NuxtLink :to="localePath('/about')" class="text-sm text-slate-400 hover:text-brand-400 transition-colors font-medium">{{ $t('common.about') }}</NuxtLink></li>
          </ul>
        </div>

        <div>
          <h4 class="text-sm font-bold text-slate-900 uppercase tracking-widest mb-6">{{ $t('footer.contact') }}</h4>
          <a href="mailto:support@feasymenu.com" class="text-sm text-slate-400 hover:text-brand-400 transition-colors font-medium block">
            support@feasymenu.com
          </a>
        </div>
      </div>

      <div class="pt-8 border-t border-slate-50 flex flex-col sm:flex-row items-center justify-between gap-4">
        <p class="text-[10px] font-bold text-slate-300 uppercase tracking-widest">
          &copy; {{ new Date().getFullYear() }} feasymenu. All rights reserved.
        </p>
        <div class="flex items-center gap-8">
          <NuxtLink :to="localePath('/about')" class="text-[10px] font-bold text-slate-300 uppercase tracking-widest hover:text-slate-900 transition-colors">Privacy</NuxtLink>
          <NuxtLink :to="localePath('/about')" class="text-[10px] font-bold text-slate-300 uppercase tracking-widest hover:text-slate-900 transition-colors">Terms</NuxtLink>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
</script>
