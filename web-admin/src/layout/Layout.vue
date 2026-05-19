<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="logo">
        <div class="logo-icon">智</div>
        <span>智慧社区</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="transparent"
        text-color="#94A3B8"
        active-text-color="#4F6EF7"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/admin">
          <el-icon><UserFilled /></el-icon>
          <span>管理员管理</span>
        </el-menu-item>
        <el-menu-item index="/owner">
          <el-icon><User /></el-icon>
          <span>业主管理</span>
        </el-menu-item>
        <el-menu-item index="/worker">
          <el-icon><Tools /></el-icon>
          <span>维修员管理</span>
        </el-menu-item>
        <el-menu-item index="/repair">
          <el-icon><DocumentChecked /></el-icon>
          <span>报修管理</span>
        </el-menu-item>
        <el-menu-item index="/fee">
          <el-icon><Money /></el-icon>
          <span>缴费管理</span>
        </el-menu-item>
        <el-menu-item index="/notice">
          <el-icon><Bell /></el-icon>
          <span>通知公告</span>
        </el-menu-item>
        <el-menu-item index="/activity">
          <el-icon><Calendar /></el-icon>
          <span>社区活动</span>
        </el-menu-item>
        <el-menu-item index="/carousel">
          <el-icon><Picture /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
        <el-menu-item index="/forum">
          <el-icon><ChatDotRound /></el-icon>
          <span>论坛管理</span>
        </el-menu-item>
        <el-menu-item index="/forum-category">
          <el-icon><FolderOpened /></el-icon>
          <span>论坛分类管理</span>
        </el-menu-item>
        <el-menu-item index="/chat">
          <el-icon><Service /></el-icon>
          <span>在线客服</span>
        </el-menu-item>
        <el-menu-item index="/feedback">
          <el-icon><Message /></el-icon>
          <span>留言反馈</span>
        </el-menu-item>
        <el-menu-item index="/notification">
          <el-icon><Bell /></el-icon>
          <span>通知管理</span>
        </el-menu-item>
        <el-menu-item index="/ai-config">
          <el-icon><Setting /></el-icon>
          <span>AI模型配置</span>
        </el-menu-item>
        <el-menu-item index="/agent-chat">
          <el-icon><Promotion /></el-icon>
          <span>AI智能助手</span>
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
                {{ (userInfo.realName || userInfo.username || '管理员').charAt(0) }}
              </el-avatar>
              <span class="header-username">{{ userInfo.realName || userInfo.username || '管理员' }}</span>
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
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const userInfo = computed(() => JSON.parse(localStorage.getItem('userInfo') || '{}'))
const activeMenu = computed(() => route.path)

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
