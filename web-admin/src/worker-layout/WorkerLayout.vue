<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="logo">
        <div class="logo-icon">维</div>
        <span>维修员端</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="transparent"
        text-color="#94A3B8"
        active-text-color="#4F6EF7"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/worker-home">
          <el-icon><House /></el-icon>
          <span>工单大厅</span>
        </el-menu-item>
        <el-menu-item index="/worker-home/completed">
          <el-icon><Check /></el-icon>
          <span>已完成订单</span>
        </el-menu-item>
        <el-menu-item index="/worker-home/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-left"></div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-avatar :size="34" :src="userInfo.avatar || ''" class="header-avatar">
                {{ (userInfo.realName || '维修员').charAt(0) }}
              </el-avatar>
              <span class="header-username">{{ userInfo.realName || '维修员' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Check, User, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userInfo = computed(() => JSON.parse(localStorage.getItem('userInfo') || '{}'))

const activeMenu = computed(() => {
  return route.path
})

const handleMenuSelect = (key) => {
  router.push(key)
}

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('退出成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: var(--sidebar-bg);
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  color: #FFFFFF;
  background: linear-gradient(135deg, rgba(79, 110, 247, 0.3) 0%, rgba(79, 110, 247, 0.1) 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: var(--primary-gradient);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: white;
}

.sidebar-menu {
  border-right: none !important;
  padding: 8px;
}

.sidebar-menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  margin: 2px 0;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.sidebar-menu .el-menu-item:hover {
  background-color: var(--sidebar-hover) !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: var(--sidebar-active-bg) !important;
  color: var(--primary) !important;
  font-weight: 600;
  position: relative;
}

.sidebar-menu .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--primary-gradient);
  border-radius: 0 2px 2px 0;
}

.sidebar-menu .el-menu-item .el-icon {
  font-size: 18px;
  margin-right: 8px;
}

.el-header {
  background-color: var(--bg-card);
  border-bottom: 1px solid #F1F5F9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px !important;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-primary);
  transition: color 0.2s;
}

.el-dropdown-link:hover {
  color: var(--primary);
}

.header-avatar {
  background: var(--primary-gradient) !important;
  color: white !important;
  font-weight: 600;
}

.header-username {
  font-weight: 500;
}

.el-main {
  background-color: var(--bg-page);
  padding: var(--space-lg);
  overflow-y: auto;
}
</style>
