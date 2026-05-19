<template>
  <div class="notification-page">
    <div class="page-header">
      <h2>我的通知</h2>
      <van-button v-if="notifications.length > 0" size="small" type="primary" plain @click="markAllRead">
        全部已读
      </van-button>
    </div>

    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="loadNotifications"
    >
      <van-cell-group v-if="notifications.length > 0">
        <van-cell
          v-for="item in notifications"
          :key="item.id"
          :title="item.title"
          :label="item.content"
          :value="item.isRead === 0 ? '未读' : ''"
          @click="readNotification(item)"
        >
          <template #right-icon>
            <van-tag v-if="item.isRead === 0" type="danger" size="small">未读</van-tag>
            <van-tag v-else type="success" size="small">已读</van-tag>
          </template>
        </van-cell>
      </van-cell-group>
      <van-empty v-else description="暂无通知" />
    </van-list>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import request from '@/utils/request'

const notifications = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)

const loadNotifications = async () => {
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
  padding: 16px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
</style>
