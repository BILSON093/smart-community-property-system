<template>
  <div class="home">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h2>欢迎来到智慧社区</h2>
        <p>为您提供便捷的物业服务</p>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="carousel-section" v-if="carouselList.length > 0">
      <h3 class="section-title">
        <van-icon name="photo-o" />
        社区风采
      </h3>
      <div class="carousel-container">
        <van-swipe 
          ref="swipeRef"
          :autoplay="3000" 
          :show-indicators="true" 
          :touchable="true"
          :mousewheel="true"
          style="height: 300px; border-radius: 8px; position: relative;"
        >
          <van-swipe-item v-for="(item, index) in carouselList" :key="item.id">
            <div style="width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; cursor: pointer;" @click="previewImage(item.imageUrl, index)">
              <img :src="item.imageUrl" :alt="'轮播图' + item.id" style="width: 100%; height: 100%; object-fit: cover;" />
            </div>
          </van-swipe-item>
        </van-swipe>
        <div class="arrow-left" @click="prevSlide">
          <van-icon name="arrow-left" size="24" />
        </div>
        <div class="arrow-right" @click="nextSlide">
          <van-icon name="arrow" size="24" />
        </div>
      </div>
    </div>

    <!-- 功能入口 -->
    <div class="grid-section">
      <h3 class="section-title">
        <van-icon name="apps-o" />
        快捷服务
      </h3>
      <div class="grid-container">
        <div class="grid-item" @click="goTo('/pay')">
          <van-icon name="gold-coin-o" size="48" color="#409EFF" />
          <span class="item-title">物业缴费</span>
          <span class="item-desc">水费、电费、物业费</span>
        </div>
        <div class="grid-item" @click="goTo('/repair')">
          <van-icon name="service-o" size="48" color="#67C23A" />
          <span class="item-title">在线报修</span>
          <span class="item-desc">快速提交报修申请</span>
        </div>
        <div class="grid-item" @click="goTo('/chat')">
          <van-icon name="chat-o" size="48" color="#F56C6C" />
          <span class="item-title">AI客服</span>
          <span class="item-desc">智能问答服务</span>
        </div>
        <div class="grid-item" @click="goTo('/forum')">
          <van-icon name="comment" size="48" color="#F56C6C" />
          <span class="item-title">社区论坛</span>
          <span class="item-desc">邻里交流互动</span>
        </div>
        <div class="grid-item" @click="goTo('/news')">
          <van-icon name="volume-o" size="48" color="#E6A23C" />
          <span class="item-title">通知</span>
          <span class="item-desc">最新社区通知</span>
        </div>
        <div class="grid-item" @click="goTo('/activity')">
          <van-icon name="star" size="48" color="#409EFF" />
          <span class="item-title">活动</span>
          <span class="item-desc">社区活动详情</span>
        </div>
        <div class="grid-item" @click="goTo('/feedback')">
          <van-icon name="edit" size="48" color="#67C23A" />
          <span class="item-title">反馈</span>
          <span class="item-desc">意见与建议</span>
        </div>
        <div class="grid-item" @click="goTo('/profile')">
          <van-icon name="user-o" size="48" color="#409EFF" />
          <span class="item-title">个人中心</span>
          <span class="item-desc">个人信息管理</span>
        </div>
      </div>
    </div>

    <!-- 通知公告 -->
    <div class="notice-section" v-if="noticeList.length > 0">
      <h3 class="section-title">
        <van-icon name="volume-o" />
        通知公告
      </h3>
      <div class="notice-list">
        <div v-for="notice in noticeList" :key="notice.id" class="notice-item" @click="viewNotice(notice)">
          <div class="notice-content">
            <span class="notice-tag" :class="'tag-type-' + notice.type">
              {{ notice.type === 1 ? '紧急' : '普通' }}
            </span>
            <span class="notice-title">{{ notice.title }}</span>
          </div>
          <span class="notice-time">{{ formatDate(notice.publishTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 社区活动 -->
    <div class="activity-section" v-if="activityList.length > 0">
      <h3 class="section-title">
        <van-icon name="star" />
        社区活动
      </h3>
      <div class="activity-list">
        <div v-for="item in activityList" :key="item.id" class="activity-item" @click="showActivityDetail(item)">
          <img :src="item.coverImage" :alt="item.title" class="activity-cover" />
          <div class="activity-info">
            <h4 class="activity-title">{{ item.title }}</h4>
            <p class="activity-meta">
              <van-icon name="location-o" size="14" />
              {{ item.location }}
            </p>
            <p class="activity-meta">
              <van-icon name="clock-o" size="14" />
              {{ item.startTime }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog, showImagePreview } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const swipeRef = ref(null)
const carouselList = ref([])
const noticeList = ref([])
const activityList = ref([])

onMounted(async () => {
  loadCarousel()
  loadNotices()
  loadActivities()
})

const prevSlide = () => {
  swipeRef.value?.prev()
}

const nextSlide = () => {
  swipeRef.value?.next()
}

const previewImage = (imageUrl, index) => {
  showImagePreview({
    images: carouselList.value.map(item => item.imageUrl),
    startPosition: index,
    closeable: true
  })
}

const loadCarousel = async () => {
  try {
    const res = await request.get('/carousel/list')
    carouselList.value = res.data || []
    console.log('轮播图数据:', carouselList.value)
  } catch (error) {
    console.error('加载轮播图失败:', error)
  }
}

const loadNotices = async () => {
  try {
    const res = await request.get('/notice/list', { params: { page: 1, size: 5 } })
    noticeList.value = res.data || []
  } catch (error) {
    console.error('加载通知失败:', error)
  }
}

const loadActivities = async () => {
  try {
    const res = await request.get('/activity/list')
    activityList.value = res.data || []
  } catch (error) {
    console.error('加载活动失败:', error)
  }
}

const goTo = (path) => {
  router.push(path)
}

const viewNotice = (notice) => {
  showDialog({
    title: notice.title,
    message: notice.content,
    confirmButtonText: '确定'
  })
}

const showActivityDetail = (item) => {
  showDialog({
    title: item.title,
    message: item.description,
    confirmButtonText: '确定'
  })
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 2592000000) return Math.floor(diff / 86400000) + '天前'
  return dateStr.split(' ')[0]
}
</script>

<style scoped>
.home {
  padding-bottom: 40px;
}

.welcome-banner {
  background: linear-gradient(135deg, #304156 0%, #345473 100%);
  padding: 40px 20px;
  text-align: center;
  margin-bottom: 32px;
}

.banner-content h2 {
  color: white;
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.banner-content p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.carousel-section,
.grid-section,
.notice-section,
.activity-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.carousel-container {
  position: relative;
  margin-bottom: 20px;
}

.arrow-left,
.arrow-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s;
  opacity: 0;
}

.carousel-container:hover .arrow-left,
.carousel-container:hover .arrow-right {
  opacity: 1;
}

.arrow-left:hover,
.arrow-right:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: translateY(-50%) scale(1.1);
}

.arrow-left {
  left: 15px;
}

.arrow-right {
  right: 15px;
}

/* 轮播图指示器样式 */
.van-swipe__indicators {
  position: absolute;
  bottom: 15px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  z-index: 10;
}

.van-swipe__indicator {
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.5);
  margin: 0 4px;
  cursor: pointer;
}

.van-swipe__indicator--active {
  width: 16px;
  background: white;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.grid-item {
  padding: 32px 24px;
  background: linear-gradient(135deg, #f0f2f5 0%, #e4e7ed 100%);
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.grid-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.item-title {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 8px;
}

.item-desc {
  font-size: 14px;
  color: #606266;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.notice-item:hover {
  background: #e9ecef;
}

.notice-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.notice-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.tag-type-0 {
  background: #409eff;
  color: white;
}

.tag-type-1 {
  background: #f56c6c;
  color: white;
}

.notice-title {
  font-size: 15px;
  color: #303133;
}

.notice-time {
  font-size: 13px;
  color: #909399;
}

.activity-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.activity-item {
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.activity-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.activity-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.activity-info {
  padding: 16px;
}

.activity-title {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
}

.activity-meta {
  font-size: 14px;
  color: #606266;
  margin: 6px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
