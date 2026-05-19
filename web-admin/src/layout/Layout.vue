<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">智慧社区</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
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
              <el-avatar :size="32" :src="userInfo.avatar || ''" style="margin-right: 8px;">
                {{ (userInfo.realName || userInfo.username || '管理员').charAt(0) }}
              </el-avatar>
              {{ userInfo.realName || userInfo.username || '管理员' }}
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
  background-color: #304156;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: white;
  background-color: #263445;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
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
  font-size: 14px;
  color: #333;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
