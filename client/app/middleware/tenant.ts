export default defineNuxtRouteMiddleware((to) => {
  // SSR ve CSR'da çalışabilir
  // Gerçek senaryoda API'ye istek atılıp (ya da store'dan) tenant ID/Slug geçerliliği kontrol edilir.
  
  const tenantId = to.query.t || to.params.id

  if (to.path === '/menu' || to.path.startsWith('/order')) {
    // Mock doğrulama: Eğer 'invalid' parametresi geldiyse bulunamadı sayfasına at
    if (tenantId === 'invalid') {
      return navigateTo('/tenant-not-found')
    }

    // Parametre yoksa da yönlendirilebilir (Şu anda UI testi olduğu için esnek bırakıyoruz)
    // if (!tenantId) {
    //   return navigateTo('/tenant-not-found')
    // }
  }
})
