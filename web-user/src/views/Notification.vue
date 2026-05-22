<template>
  <div class="notification-page">
    <div class="page-header">
      <h2>我的通知</h2>
      <button v-if="notifications.length > 0" class="btn-mark-all" @click="markAllRead">
        <van-icon name="passed" /> 全部标为已读
      </button>
    </div>

    <div class="notification-list" v-if="notifications.length > 0">
      <div
        v-for="item in notifications"
        :key="item.id"
        :class="['notification-item', { unread: item.isRead === 0 }]"
        @click="readNotification(item)"
      >
        <div class="item-dot" v-if="item.isRead === 0"></div>
        <div class="item-body">
          <div class="item-header">
            <span class="item-title">{{ item.title }}</span>
            <span class="item-time" v-if="item.createTime">{{ item.createTime }}</span>
          </div>
          <div class="item-content">{{ item.content }}</div>
        </div>
      </div>
    </div>
    <van-empty v-else description="暂无通知" />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { showToast } from 'vant'
import request from '@/utils/request'
import { connectNotifications } from '@/utils/realtime'

const notifications = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
let notificationSocket = null

onMounted(() => {
  loadNotifications()
  notificationSocket = connectNotifications(handleRealtimeNotification)
})

onBeforeUnmount(() => {
  if (notificationSocket) {
    notificationSocket.close()
    notificationSocket = null
  }
})

const handleRealtimeNotification = (message) => {
  const payload = message.payload || {}
  if (message.event === 'notification.created') {
    const exists = notifications.value.some(item => item.id === payload.id)
    if (!exists) {
      notifications.value.unshift(payload)
      showToast(payload.title || '收到新通知')
    }
  } else if (message.event === 'notification.read') {
    const item = notifications.value.find(item => item.id === payload.id)
    if (item) item.isRead = 1
  } else if (message.event === 'notification.read_all') {
    notifications.value.forEach(item => { item.isRead = 1 })
  }
}

const loadNotifications = async () => {
  if (loading.value || finished.value) return
  loading.value = true
  try {
    const res = await request.get('/notification/my', {
      params: { page: page.value, size: 20 }
    })
    if (res.code === 200) {
      const records = res.data.records || []
      if (page.value === 1) {
        notifications.value = records
      } else {
        notifications.value.push(...records)
      }
      if (records.length < 20) {
        finished.value = true
      }
      page.value++
    }
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
  }
}

const readNotification = async (item) => {
  if (item.isRead === 0) {
    try {
      await request.post(`/notification/read/${item.id}`)
      item.isRead = 1
    } catch (e) {
      // ignore
    }
  }
}

const markAllRead = async () => {
  try {
    await request.post('/notification/read-all')
    notifications.value.forEach(n => { n.isRead = 1 })
    showToast('全部已读')
  } catch (e) {
    showToast('操作失败')
  }
}
</script>

<style scoped>
.notification-page {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
}

.btn-mark-all {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  background: transparent;
  border: 1px solid var(--primary);
  color: var(--primary);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-mark-all:hover {
  background: var(--primary);
  color: #fff;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 18px 20px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.notification-item:hover {
  box-shadow: var(--shadow-hover);
}

.notification-item.unread {
  background: #FAFBFF;
  border-left: 3px solid var(--primary);
}

.item-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
  flex-shrink: 0;
  margin-top: 6px;
}

.item-body {
  flex: 1;
  min-width: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  gap: 12px;
}

.item-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-time {
  font-size: 12px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.item-content {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
