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
            <span>{{ userInfo.realName.charAt(0) }}</span>
          </div>
          <span class="username">{{ userInfo.realName }}</span>
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

onMounted(() => {
  const token = localStorage.getItem('token')
  const userData = localStorage.getItem('userInfo')
  if (userData) {
    userInfo.value = JSON.parse(userData)
    if (token) {
      loadUnpaidFees()
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
  background-color: #f0f2f5;
}

.header {
  background-color: #304156;
  box-shadow: 0 2px 8px rgba(0, 0, 21, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
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
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  letter-spacing: 1px;
}

.nav {
  display: flex;
  gap: 16px;
  flex: 1;
  margin-left: 60px;
}

.nav-item {
  color: #bfcbd9;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  font-size: 14px;
  font-weight: 500;
  position: relative;
  display: inline-block;
  overflow: hidden;
}

.nav-item:hover {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
  transform: translateY(-1px);
}

.nav-item:active {
  transform: translateY(0);
  transition: all 0.1s;
}

.nav-item.router-link-exact-active {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
  border-bottom: 2px solid #409EFF;
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
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
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
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(255, 102, 102, 0.1);
  border: 1px solid rgba(255, 102, 102, 0.3);
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
}

.reminder-btn:hover {
  background-color: rgba(255, 102, 102, 0.2);
}

.reminder-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #F56C6C;
}

.reminder-text {
  color: #F56C6C;
  font-size: 12px;
  font-weight: 500;
}

.login-btn {
  background-color: #409EFF;
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  display: inline-block;
}

.login-btn:hover {
  background-color: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.login-btn:active {
  transform: translateY(0);
  transition: all 0.1s;
}

.logout-btn {
  background-color: transparent;
  color: #bfcbd9;
  border: 1px solid #409EFF;
  padding: 6px 16px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.logout-btn:hover {
  color: #409EFF;
  border-color: #66b1ff;
  background-color: rgba(64, 158, 255, 0.1);
  transform: translateY(-1px);
}

.logout-btn:active {
  transform: translateY(0);
  transition: all 0.1s;
}

.content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
  background: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-top: 16px;
  margin-bottom: 16px;
  border-radius: 8px;
}

.footer {
  text-align: center;
  padding: 24px;
  color: #909399;
  font-size: 14px;
  background: white;
  border-top: 1px solid #e4e7ed;
}
</style>
