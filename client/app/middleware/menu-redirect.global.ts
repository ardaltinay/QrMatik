export default defineNuxtRouteMiddleware((to) => {
  // Eğer kullanıcı dil öneki olmadan /menu yoluna gelirse (QR'dan veya manuel)
  if (to.path === '/menu') {
    const localePath = useLocalePath()

    // Query parametrelerini (table vb.) koruyarak doğru dile yönlendir
    return navigateTo({
      path: localePath('/menu'),
      query: to.query
    }, { replace: true })
  }
})
