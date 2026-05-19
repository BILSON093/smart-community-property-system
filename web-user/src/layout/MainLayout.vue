<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <h1>智慧社区</h1>
        </div>
        <nav class="nav">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/home/news" class="nav-item">通知</router-link>
          <router-link to="/home/activity" class="nav-item">活动</router-link>
          <router-link to="/home/forum" class="nav-item">论坛</router-link>
          <router-link to="/home/pay" class="nav-item">缴费</router-link>
          <router-link to="/home/repair" class="nav-item">报修</router-link>
          <router-link to="/home/chat" class="nav-item">智能助手</router-link>
          <router-link to="/home/feedback" class="nav-item">反馈</router-link>
          <router-link to="/home/profile" class="nav-item">我的</router-link>
        </nav>
        <div class="user-info" v-if="userInfo">
          <div class="avatar" v-if="userInfo.avatar">
            <img :src="userInfo.avatar" alt="头像" />
          </div>
          <div class="avatar" v-else>
            <span>{{ (userInfo.realName || userInfo.username || '?').charAt(0) }}</span>
          </div>
          <span class="username">{{ userInfo.realName || userInfo.username || '用户' }}</span>
          <div class="notification-bell" @click="goToNotification">
            <span class="bell-icon">🔔</span>
            <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
          </div>
          <div class="reminder-btn" v-if="hasUnpaidFees" @click="goToPay">
            <span class="reminder-dot"></span>
            <span class="reminder-text">缴费提醒</span>
          </div>
          <button @click="handleLogout" class="logout-btn">退出</button>
        </div>
        <div class="user-info" v-else>
          <router-link to="/login" class="login-btn">登录</router-link>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="content">
      <router-view />
    </div>

    <!-- 底部 -->
    <div class="footer">
      <p>© 2026 智慧社区物业管理系统</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const userInfo = ref(null)
const hasUnpaidFees = ref(false)
const unreadCount = ref(0)

onMounted(() => {
  const token = localStorage.getItem('token')
  const userData = localStorage.getItem('userInfo')
  if (userData) {
    userInfo.value = JSON.parse(userData)
    if (token) {
      loadUnpaidFees()
      loadUnreadCount()
    }
  }
})

const loadUnpaidFees = async () => {
  try {
    const res = await request.get('/fee/my', { params: { page: 1, size: 10 } })
    if (res.data && res.data.records) {
      const unpaidFees = res.data.records.filter(fee => fee.status === 0)
      hasUnpaidFees.value = unpaidFees.length > 0
    }
  } catch (error) {
    console.error('加载未缴费信息失败:', error)
  }
}

const goToPay = () => {
  router.push('/pay')
}

const goToNotification = () => {
  router.push('/home/notification')
}

const loadUnreadCount = async () => {
  try {
    const res = await request.get('/notification/unread-count')
    if (res.code === 200) {
      unreadCount.value = res.data.count || 0
    }
  } catch (e) {
    // ignore
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  userInfo.value = null
  hasUnpaidFees.value = false
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-page);
}

.header {
  background: linear-gradient(135deg, #1E293B 0%, #0F172A 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  flex-shrink: 0;
}

.logo h1 {
  color: white;
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #fff 0%, #94A3B8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav {
  display: flex;
  gap: 4px;
  flex: 1;
  margin-left: 40px;
}

.nav-item {
  color: #94A3B8;
  text-decoration: none;
  padding: 8px 14px;
  border-radius: var(--radius-sm);
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
  position: relative;
  display: inline-block;
}

.nav-item:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.08);
}

.nav-item.router-link-exact-active {
  color: white;
  background-color: rgba(79, 110, 247, 0.2);
}

.nav-item.router-link-exact-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: var(--primary-gradient);
  border-radius: 1px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.username {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.3);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar span {
  color: white;
  font-size: 16px;
  font-weight: 600;
}

.reminder-btn {
  position: relative;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 20px;
  background-color: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s ease;
}

.reminder-btn:hover {
  background-color: rgba(239, 68, 68, 0.25);
}

.reminder-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: var(--danger);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.reminder-text {
  color: var(--danger);
  font-size: 12px;
  font-weight: 500;
}

.login-btn {
  background: var(--primary-gradient);
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s ease;
  display: inline-block;
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.3);
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(79, 110, 247, 0.4);
}

.logout-btn {
  background-color: transparent;
  color: #94A3B8;
  border: 1px solid rgba(148, 163, 184, 0.3);
  padding: 6px 16px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
  background-color: rgba(255, 255, 255, 0.08);
}

.notification-bell {
  position: relative;
  cursor: pointer;
  padding: 6px 8px;
  border-radius: var(--radius-sm);
  transition: background-color 0.2s;
}

.notification-bell:hover {
  background-color: rgba(255, 255, 255, 0.08);
}

.bell-icon {
  font-size: 18px;
}

.unread-badge {
  position: absolute;
  top: -2px;
  right: 0;
  background: linear-gradient(135deg, #EF4444 0%, #F87171 100%);
  color: white;
  font-size: 10px;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  border-radius: 8px;
  padding: 0 4px;
  box-shadow: 0 2px 6px rgba(239, 68, 68, 0.4);
}

.content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: var(--space-lg);
  background: var(--bg-card);
  box-shadow: var(--shadow-sm);
  margin-top: var(--space-md);
  margin-bottom: var(--space-md);
  border-radius: var(--radius-lg);
}

.footer {
  text-align: center;
  padding: 20px;
  color: var(--text-muted);
  font-size: 13px;
  background: var(--bg-card);
  border-top: 1px solid #F1F5F9;
}
</style>
