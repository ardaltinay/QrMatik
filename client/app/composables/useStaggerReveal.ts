import type { Ref } from 'vue'

export function useStaggerReveal(rootRef: Ref<HTMLElement | null>, itemSelector = '.reveal-item') {
  let observer: IntersectionObserver | null = null

  onMounted(() => {
    if (!import.meta.client) return
    const root = rootRef.value
    if (!root) return

    const items = Array.from(root.querySelectorAll<HTMLElement>(itemSelector))
    if (!items.length) return

    items.forEach((item, index) => {
      const delay = Math.min(index * 70, 420)
      item.style.transitionDelay = `${delay}ms`
    })

    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return
          ;(entry.target as HTMLElement).classList.add('is-visible')
          observer?.unobserve(entry.target)
        })
      },
      {
        threshold: 0.12,
        rootMargin: '0px 0px -8% 0px',
      }
    )

    items.forEach((item) => observer?.observe(item))
  })

  onUnmounted(() => {
    observer?.disconnect()
    observer = null
  })
}
