<template>
  <el-container class="worker-layout">
    <el-aside width="200px" class="aside">
      <div class="logo">维修员端</div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
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
        <div class="header-content">
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-avatar :size="32" :src="userInfo.avatar || ''" style="margin-right: 8px;">
                  {{ (userInfo.realName || '维修员').charAt(0) }}
                </el-avatar>
                {{ userInfo.realName || '维修员' }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
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
.worker-layout {
  height: 100vh;
}

.aside {
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.aside .logo {
  font-size: 18px;
  font-weight: bold;
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-header {
  background-color: #ffffff;
  color: #333;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}

.el-menu-vertical-demo {
  background-color: transparent;
  border-right: none;
}

.el-menu-vertical-demo .el-menu-item {
  color: rgba(255, 255, 255, 0.8);
  margin: 10px 0;
  border-radius: 0 20px 20px 0;
}

.el-menu-vertical-demo .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.el-menu-vertical-demo .el-menu-item.is-active {
  background-color: #409EFF;
  color: white;
}
</style>
