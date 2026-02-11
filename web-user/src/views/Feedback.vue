<template>
  <div class="feedback">
    <div class="page-header">
      <h2>留言反馈</h2>
    </div>
    
    <div class="feedback-form">
      <h3>请填写您的反馈</h3>
      <p class="form-description">我们重视您的每一条建议和意见，您的反馈将帮助我们不断改进物业服务质量。</p>
      
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="content">反馈内容</label>
          <textarea 
            id="content" 
            v-model="formData.content" 
            placeholder="请详细描述您的建议或投诉..."
            rows="8"
            required
          ></textarea>
        </div>
        
        <div class="form-actions">
          <button type="submit" class="submit-btn">提交反馈</button>
        </div>
      </form>
    </div>

    <div class="feedback-history">
      <h3>我的反馈记录</h3>
      <div v-if="feedbackList.length === 0" class="empty-tip">暂无反馈记录</div>
      <div v-for="item in feedbackList" :key="item.id" class="feedback-item">
        <div class="feedback-content">
          <div class="feedback-text">{{ item.content }}</div>
          <div class="feedback-time">{{ item.createTime }}</div>
        </div>
        <div v-if="item.reply" class="feedback-reply">
          <div class="reply-label">管理员回复：</div>
          <div class="reply-text">{{ item.reply }}</div>
          <div class="reply-time">{{ item.updateTime }}</div>
        </div>
        <div v-else class="feedback-status">
          <van-tag type="warning">待处理</van-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const formData = ref({
  content: ''
})
const feedbackList = ref([])

onMounted(() => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  console.log('页面加载 - Token:', token)
  console.log('页面加载 - 用户信息:', userInfo)
  
  if (!token || !userInfo.userId) {
    console.log('用户未登录，跳转到登录页')
    showToast('请先登录')
    router.push('/login')
    return
  }
  
  loadMyFeedbacks()
})

const loadMyFeedbacks = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    console.log('用户信息:', userInfo)
    if (!userInfo.userId) {
      console.log('用户未登录')
      return
    }
    const token = localStorage.getItem('token')
    console.log('Token:', token)
    const res = await request.get('/feedback/my', { params: { userId: userInfo.userId } })
    console.log('反馈列表响应:', res)
    feedbackList.value = res.data || []
  } catch (error) {
    console.error('加载反馈记录失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    if (error.response?.status === 401) {
      showToast('登录已过期，请重新登录')
      router.push('/login')
    }
  }
}

const handleSubmit = async () => {
  if (!formData.value.content.trim()) {
    showToast('请填写反馈内容')
    return
  }

  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.userId || !userInfo.realName) {
    showToast('请先登录')
    router.push('/login')
    return
  }

  const submitData = {
    ...formData.value,
    userId: userInfo.userId.toString(),
    userName: userInfo.realName
  }

  try {
    const res = await request.post('/feedback/add', submitData)
    showToast('反馈提交成功，我们将尽快处理')
    formData.value.content = ''
    loadMyFeedbacks()
  } catch (error) {
    console.error('提交反馈失败:', error)
    showToast('提交失败，请稍后重试')
  }
}
</script>

<style scoped>
.feedback {
  min-height: 100vh;
  padding-bottom: 20px;
}

.page-header {
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #304156;
  margin: 0;
}

.feedback-form {
  background: #f9fafc;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  max-width: 800px;
  margin: 0 auto;
}

.feedback-form h3 {
  font-size: 18px;
  font-weight: 600;
  color: #304156;
  margin: 0 0 16px 0;
}

.form-description {
  font-size: 14px;
  color: #606266;
  margin: 0 0 24px 0;
  line-height: 1.6;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #304156;
  margin-bottom: 8px;
}

.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  transition: all 0.3s;
}

.form-group textarea:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.form-actions {
  text-align: center;
  margin-top: 32px;
}

.submit-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
  border: none;
  padding: 12px 48px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
  transform: translateY(-2px);
}

.submit-btn:active {
  transform: translateY(0);
}

.feedback-history {
  max-width: 800px;
  margin: 40px auto 0;
}

.feedback-history h3 {
  font-size: 18px;
  font-weight: 600;
  color: #304156;
  margin-bottom: 20px;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

.feedback-item {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.feedback-content {
  margin-bottom: 12px;
}

.feedback-text {
  font-size: 15px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 8px;
}

.feedback-time {
  font-size: 12px;
  color: #909399;
}

.feedback-reply {
  background: #f0f9ff;
  border-left: 3px solid #409eff;
  padding: 12px 16px;
  border-radius: 4px;
  margin-top: 12px;
}

.reply-label {
  font-size: 13px;
  color: #409eff;
  font-weight: 500;
  margin-bottom: 6px;
}

.reply-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 6px;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.feedback-status {
  margin-top: 12px;
}

@media (max-width: 768px) {
  .feedback-form {
    padding: 24px;
  }
  
  .form-group textarea {
    min-height: 120px;
  }
  
  .feedback-history {
    margin: 30px auto 0;
  }
}
</style>