const getWsBaseUrl = () => {
  if (import.meta.env.VITE_WS_BASE_URL) {
    return import.meta.env.VITE_WS_BASE_URL
  }
  if (window.location.protocol === 'https:') {
    return `wss://${window.location.host}/api`
  }
  return `ws://${window.location.host}/api`
}

export const connectRealtime = (path, onMessage) => {
  const token = localStorage.getItem('token')
  if (!token) {
    return null
  }
  const url = `${getWsBaseUrl()}${path}?token=${encodeURIComponent(token)}`
  const socket = new WebSocket(url)
  socket.onopen = () => {
    console.log('实时连接已建立', path)
  }
  socket.onerror = error => {
    console.error('实时连接失败', path, error)
  }
  socket.onclose = event => {
    console.log('实时连接已关闭', path, event.code, event.reason)
  }
  socket.onmessage = event => {
    try {
      onMessage && onMessage(JSON.parse(event.data))
    } catch (error) {
      console.error('解析实时消息失败', error)
    }
  }
  return socket
}

export const connectNotifications = onMessage => connectRealtime('/ws/notifications', onMessage)

export const connectChat = onMessage => connectRealtime('/ws/chat', onMessage)
