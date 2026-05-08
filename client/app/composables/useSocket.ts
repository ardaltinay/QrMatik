import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs'

// Singleton state to avoid multiple connections across components
const client = ref<Client | null>(null)
const connected = ref(false)
const connecting = ref(false)

// Keep track of active subscriptions to avoid duplicates
const activeSubscriptions = new Set<string>()

/**
 * Composable for STOMP over WebSocket connection.
 * Production-ready with singleton connection management and duplicate subscription prevention.
 */
export function useSocket() {
  function connect(onConnect?: () => void) {
    if (import.meta.server) return

    if (client.value?.active || connecting.value) {
      if (connected.value && onConnect) onConnect()
      return
    }

    connecting.value = true

    let url = ''
    if (import.meta.client) {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
      const host = window.location.host

      // Development: Connect directly to 8080 if using localhost subdomains to avoid proxy issues
      if (import.meta.dev && host.includes('localhost')) {
        const hostname = window.location.hostname
        url = `${protocol}//${hostname}:8080/ws/raw`
      } else {
        // Production: Use proxied path which should be handled by the production server (Nginx/Traefik etc.)
        url = `${protocol}//${host}/api/ws/raw`
      }
    }

    const stompClient = new Client({
      brokerURL: url,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        connected.value = true
        connecting.value = false
        activeSubscriptions.clear()
        if (onConnect) onConnect()
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame.headers['message'])
        connecting.value = false
      },
      onWebSocketClose: () => {
        connected.value = false
        connecting.value = false
      },
      onDisconnect: () => {
        connected.value = false
        connecting.value = false
      },
      debug: (str) => {
        // Only log in development and filter out heartbeats
        if (import.meta.dev && !str.includes('PONG') && !str.includes('PING')) {
          //console.log('STOMP:', str)
        }
      }
    })

    try {
      stompClient.activate()
      client.value = stompClient
    } catch (err) {
      console.error('STOMP Activation failed', err)
      connecting.value = false
    }
  }

  function disconnect() {
    // Keep connection alive for global use
  }

  function subscribe(topic: string, callback: (payload: any) => void) {
    if (import.meta.server) return () => { }

    let subscription: StompSubscription | null = null

    const doSub = () => {
      if (client.value?.connected && !activeSubscriptions.has(topic)) {
        subscription = client.value.subscribe(topic, (message: IMessage) => {
          try {
            callback(JSON.parse(message.body))
          } catch (e) {
            callback(message.body)
          }
        })
        activeSubscriptions.add(topic)
        return true
      }
      return false
    }

    if (!doSub()) {
      const unwatch = watch(connected, (isConn) => {
        if (isConn) {
          if (doSub()) unwatch()
        }
      })
    }

    return () => {
      if (subscription) {
        subscription.unsubscribe()
        activeSubscriptions.delete(topic)
      }
    }
  }

  return {
    connect,
    disconnect,
    subscribe,
    connected,
    connecting
  }
}
