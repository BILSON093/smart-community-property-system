<template>
  <div class="news">
    <h2 class="page-title">通知中心</h2>
    
    <div class="notice-list">
      <div 
        v-for="item in noticeList" 
        :key="item.id" 
        class="notice-item"
        @click="showDetail(item)"
      >
        <div class="notice-header">
          <h3 class="notice-title">
            {{ item.title }}
            <span v-if="item.type === 1" class="urgent-tag">紧急</span>
          </h3>
          <span class="notice-time">{{ item.publishTime }}</span>
        </div>
        <div class="notice-content">
            {{ item.content ? item.content.substring(0, 300) + '...' : '无内容' }}
          </div>
      </div>
      <div v-if="noticeList.length === 0" class="empty-state">
        暂无通知公告
      </div>
    </div>
    
    <!-- 通知详情弹窗 -->
    <div v-if="selectedNotice" class="modal-overlay" @click="selectedNotice = null">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedNotice.title }}</h3>
          <span class="close-btn" @click="selectedNotice = null">×</span>
        </div>
        <div class="modal-body">
          <div class="notice-meta">
            <span class="publish-time">发布时间：{{ selectedNotice.publishTime }}</span>
            <span v-if="selectedNotice.type === 1" class="urgent-tag">紧急</span>
          </div>
          <div class="notice-detail-content">
            {{ selectedNotice.content || '无内容' }}
          </div>
          <!-- 显示图片 -->
          <div v-if="selectedNotice.image" class="notice-image" style="margin-top: 20px;">
            <div v-for="(img, index) in selectedNotice.image.split(',').filter(i => i.trim())" :key="index" class="image-item" style="margin-bottom: 10px;">
              <img :src="img" :alt="`通知图片${index+1}`" style="max-width: 100%; max-height: 500px; height: auto; display: block; margin: 0 auto; cursor: pointer;" @click="previewNoticeImage(img, index)">
            </div>
          </div>
          <!-- 显示附件 -->
          <div v-if="selectedNotice.attachment" class="notice-attachment" style="margin-top: 20px;">
            <a :href="selectedNotice.attachment" target="_blank" class="attachment-link">
              📎 下载附件
            </a>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn" @click="selectedNotice = null">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'
import { showImagePreview } from 'vant'

const router = useRouter()

const noticeList = ref([])
const selectedNotice = ref(null)

onMounted(async () => {
  const res = await request.get('/notice/list', { params: { page: 1, size: 100 } })
  noticeList.value = res.data.records || []
})

const showDetail = (item) => {
  // 如果是缴费提醒通知，直接跳转到缴费界面
  if (item.title === '缴费提醒') {
    router.push('/pay')
  } else {
    selectedNotice.value = item
    // 标记为已读
    markNoticeAsRead(item.id)
  }
}

const markNoticeAsRead = async (noticeId) => {
  try {
    await request.post(`/notice/${noticeId}/read`)
  } catch (e) {
    // ignore
  }
}

const previewNoticeImage = (imageUrl, index) => {
  if (!selectedNotice.value?.image) return
  
  const images = selectedNotice.value.image.split(',').filter(i => i.trim())
  showImagePreview({
    images: images,
    startPosition: index,
    closeable: true
  })
}
</script>

<style scoped>
.news {
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 30px;
  color: #304156;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
}

.notice-item:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.notice-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.urgent-tag {
  background: #F56C6C;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: normal;
}

.notice-time {
  font-size: 14px;
  color: #909399;
}

.notice-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #909399;
  font-size: 16px;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.close-btn {
  font-size: 24px;
  color: #909399;
  cursor: pointer;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #606266;
}

.modal-body {
  padding: 24px;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.publish-time {
  font-size: 14px;
  color: #909399;
}

.notice-detail-content {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #e4e7ed;
  text-align: right;
}

.btn {
  padding: 10px 24px;
  background: #409EFF;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn:hover {
  background: #66b1ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .news {
    padding: 10px;
  }
  
  .page-title {
    font-size: 20px;
    margin-bottom: 20px;
  }
  
  .tab-item {
    padding: 10px 16px;
    font-size: 14px;
    margin-right: 12px;
  }
  
  .notice-item {
    padding: 16px;
  }
  
  .notice-title {
    font-size: 16px;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px;
  }
  
  .modal-header h3 {
    font-size: 18px;
  }
  
  .notice-detail-content {
    font-size: 14px;
  }
}
</style>
