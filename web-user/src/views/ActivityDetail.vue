<template>
  <div class="activity-detail">
    <div class="header">
      <button @click="$router.back()" class="btn-back">
        <van-icon name="arrow-left" style="font-size: 18px;" />
        返回
      </button>
      <h1 class="page-title">活动详情</h1>
    </div>
    
    <div class="content">
      <h2 class="title">{{ activity.title }}</h2>
      
      <div class="cover-image">
        <img :src="activity.coverImage" @click="previewCoverImage" />
      </div>
      
      <div class="info">
        <div class="info-item">
          <van-icon name="location-o" />
          <span>{{ activity.location }}</span>
        </div>
        <div class="info-item">
          <van-icon name="clock-o" />
          <span>{{ formattedStartTime }}</span>
        </div>
        <div class="info-item" v-if="activity.endTime">
          <van-icon name="time" />
          <span>结束时间: {{ formattedEndTime }}</span>
        </div>
      </div>
      
      <div class="description">
        <h2>活动介绍</h2>
        <p>{{ activity.description }}</p>
      </div>
      
      <div class="signup-section" v-if="token">
        <van-button
          v-if="!signupStatus.signedUp"
          type="primary"
          block
          @click="handleSignup"
          :loading="signupLoading"
        >
          立即报名 ({{ signupStatus.count || 0 }} 人已报名)
        </van-button>
        <van-button
          v-else
          type="default"
          block
          @click="handleCancelSignup"
          :loading="signupLoading"
        >
          取消报名 ({{ signupStatus.count || 0 }} 人已报名)
        </van-button>
      </div>

      <div class="images" v-if="activityImages.length > 0">
        <h2>活动图片</h2>
        <div class="image-grid">
          <div v-for="(img, index) in activityImages" :key="index" class="image-item">
            <img :src="img" @click="previewImage(index)" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import request from '@/utils/request'

const route = useRoute()
const activity = ref({})
const activityImages = ref([])
const token = localStorage.getItem('token')
const signupStatus = ref({ signedUp: false, count: 0 })
const signupLoading = ref(false)

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formattedStartTime = computed(() => {
  return formatDate(activity.value.startTime)
})

const formattedEndTime = computed(() => {
  return formatDate(activity.value.endTime)
})

onMounted(async () => {
  const id = route.params.id
  if (!id) {
    showToast('活动ID不存在')
    return
  }
  
  try {
    const res = await request.get(`/activity/${id}`)
    console.log('活动详情响应:', res)
    activity.value = res.data

    // 处理活动图片
    if (activity.value.images) {
      try {
        activityImages.value = JSON.parse(activity.value.images)
      } catch (e) {
        console.error('解析图片失败', e)
      }
    }

    // 加载报名状态
    if (token) {
      loadSignupStatus()
    }
  } catch (error) {
    console.error('加载活动详情失败:', error)
    showToast('加载活动详情失败')
  }
})

const loadSignupStatus = async () => {
  try {
    const res = await request.get(`/activity/${route.params.id}/signup-status`)
    if (res.code === 200) {
      signupStatus.value = res.data
    }
  } catch (e) {
    // ignore
  }
}

const handleSignup = async () => {
  signupLoading.value = true
  try {
    const res = await request.post(`/activity/${route.params.id}/signup`)
    if (res.code === 200) {
      showToast('报名成功')
      loadSignupStatus()
    } else {
      showToast(res.message || '报名失败')
    }
  } catch (e) {
    showToast('报名失败')
  } finally {
    signupLoading.value = false
  }
}

const handleCancelSignup = async () => {
  signupLoading.value = true
  try {
    const res = await request.delete(`/activity/${route.params.id}/signup`)
    if (res.code === 200) {
      showToast('取消报名成功')
      loadSignupStatus()
    } else {
      showToast(res.message || '取消失败')
    }
  } catch (e) {
    showToast('取消失败')
  } finally {
    signupLoading.value = false
  }
}

const previewImage = (index) => {
  if (activityImages.value.length === 0) return
  
  showImagePreview({
    images: activityImages.value,
    startPosition: index,
    closeable: true
  })
}

const previewCoverImage = () => {
  if (!activity.value.coverImage) return
  
  const allImages = [activity.value.coverImage, ...activityImages.value]
  showImagePreview({
    images: allImages,
    startPosition: 0,
    closeable: true
  })
}
</script>

<style scoped>
.activity-detail {
  min-height: 100vh;
  background: var(--bg-page);
}

.header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #F1F5F9;
}

.page-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
  margin-left: 16px;
}

.content {
  padding: 20px;
  background: white;
}

.title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: var(--text-primary);
}

.cover-image {
  width: 100%;
  margin-bottom: 20px;
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.cover-image img {
  width: 100%;
  height: auto;
  object-fit: contain;
  cursor: pointer;
}

.info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: var(--text-secondary);
  font-size: 14px;
}

.description {
  margin-bottom: 20px;
}

.description h2,
.images h2 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: var(--text-primary);
}

.description p {
  line-height: 1.6;
  color: var(--text-secondary);
  font-size: 14px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.image-item {
  border-radius: var(--radius-sm);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.image-item img {
  width: 100%;
  height: auto;
  object-fit: contain;
  cursor: pointer;
}

.btn-back {
  background: var(--primary-bg);
  color: var(--primary);
  border: 1px solid rgba(79, 110, 247, 0.2);
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-back:hover {
  background: rgba(79, 110, 247, 0.12);
  transform: translateY(-2px);
}

.signup-section {
  margin-bottom: 20px;
  padding: 16px;
  background: var(--primary-bg);
  border-radius: var(--radius-sm);
}
</style>