<template>
  <div class="profile">
    <div class="page-header">
      <h2>个人中心</h2>
    </div>
    
    <!-- 关于我们弹窗 -->
    <div v-if="showAboutDialog" class="dialog-overlay" @click="showAboutDialog = false">
      <div class="dialog-content" @click.stop>
        <h3>关于我们</h3>
        <p>智慧社区物业管理系统</p>
        <p>版本：1.0.0</p>
        <p>致力于为业主提供便捷的社区服务</p>
        <button class="dialog-btn" @click="showAboutDialog = false">确定</button>
      </div>
    </div>
    
    <!-- 系统设置弹窗 -->
    <div v-if="showSettingsDialog" class="dialog-overlay" @click="showSettingsDialog = false">
      <div class="dialog-content" @click.stop>
        <h3>系统设置</h3>
        <div class="setting-item">
          <span>清除缓存</span>
          <button class="setting-btn" @click="clearCache">清除</button>
        </div>
        <button class="dialog-btn" @click="showSettingsDialog = false">确定</button>
      </div>
    </div>
    
    <div class="user-card" @click="goTo('/edit-profile')">
      <div class="user-avatar">
        <img :src="userInfo.avatar || defaultAvatar" alt="用户头像" />
        <div class="edit-icon">📝</div>
      </div>
      <div class="user-info">
        <h3>{{ userInfo.realName || userInfo.username }}</h3>
        <p class="user-role">业主</p>
      </div>
      <div class="user-arrow">→</div>
    </div>

    <div class="profile-sections">
      <div class="section">
        <h4 class="section-title">我的服务</h4>
        <div class="section-content">
          <div class="menu-item" @click="goTo('/repair')">
            <div class="menu-icon">🛠️</div>
            <div class="menu-text">我的报修</div>
            <div class="menu-arrow">→</div>
          </div>
          <div class="menu-item" @click="goTo('/pay')">
            <div class="menu-icon">💰</div>
            <div class="menu-text">我的缴费</div>
            <div class="menu-arrow">→</div>
          </div>
          <div class="menu-item" @click="goTo('/chat')">
            <div class="menu-icon">💬</div>
            <div class="menu-text">联系客服</div>
            <div class="menu-arrow">→</div>
          </div>
        </div>
      </div>

      <div class="section">
        <h4 class="section-title">系统设置</h4>
        <div class="section-content">
          <div class="menu-item" @click="showAbout">
            <div class="menu-icon">ℹ️</div>
            <div class="menu-text">关于我们</div>
            <div class="menu-arrow">→</div>
          </div>
          <div class="menu-item" @click="showSettings">
            <div class="menu-icon">⚙️</div>
            <div class="menu-text">系统设置</div>
            <div class="menu-arrow">→</div>
          </div>
        </div>
      </div>
    </div>

    <div class="action-buttons">
      <button class="logout-btn" @click="handleLogout">
        退出登录
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const userInfo = ref({})
const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20portrait%20professional%20clean%20design&image_size=square'
const showAboutDialog = ref(false)
const showSettingsDialog = ref(false)

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  }
})

const goTo = (path) => {
  router.push(path)
}

const handleLogout = () => {
  showConfirmDialog({
    title: '提示',
    message: '确认退出登录吗？'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    showToast('已退出登录')
    router.replace('/login')
  }).catch(() => {})
}

const showAbout = () => {
  showAboutDialog.value = true
}

const showSettings = () => {
  showSettingsDialog.value = true
}

const clearCache = () => {
  localStorage.clear()
  showToast('缓存已清除')
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
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

.user-card {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #304156 0%, #409EFF 100%);
  color: white;
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid rgba(255, 255, 255, 0.3);
  margin-right: 30px;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.user-role {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.user-arrow {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.8);
  margin-left: auto;
  transition: all 0.3s;
  font-weight: 300;
}

.user-card:hover .user-arrow {
  transform: translateX(4px);
  color: white;
}

.profile-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.section {
  background: #f9fafc;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.section-title {
  background: #304156;
  color: white;
  padding: 16px 24px;
  margin: 0;
  font-size: 14px;
  font-weight: 600;
}

.section-content {
  padding: 8px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid #f0f2f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background-color: rgba(64, 158, 255, 0.05);
  padding-left: 30px;
}

.menu-icon {
  font-size: 18px;
  margin-right: 16px;
  width: 24px;
  text-align: center;
}

.menu-text {
  flex: 1;
  font-size: 14px;
  color: #304156;
}

.menu-arrow {
  font-size: 18px;
  color: rgba(48, 65, 86, 0.3);
  transition: all 0.3s;
  font-weight: 300;
}

.menu-item:hover .menu-arrow {
  transform: translateX(4px);
  color: #409EFF;
}

.action-buttons {
  text-align: center;
  margin-top: 40px;
}

.logout-btn {
  background-color: #f56c6c;
  color: white;
  border: none;
  padding: 12px 48px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background-color: #f78989;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
}

.logout-btn:active {
  transform: translateY(1px);
}

@media (max-width: 768px) {
  .profile-sections {
    grid-template-columns: 1fr;
  }
  
  .user-card {
    flex-direction: row;
    text-align: left;
    padding: 20px;
  }
  
  .user-avatar {
    margin-right: 15px;
    margin-bottom: 0;
    width: 80px;
    height: 80px;
  }
}

/* 弹窗样式 */
.dialog-overlay {
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

.dialog-content {
  background: white;
  border-radius: 12px;
  padding: 32px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.dialog-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #304156;
  margin: 0 0 20px 0;
  text-align: center;
}

.dialog-content p {
  font-size: 14px;
  color: #606266;
  margin: 10px 0;
  line-height: 1.6;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
}

.setting-btn {
  background: #f0f9ff;
  color: #409eff;
  border: 1px solid #409eff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.setting-btn:hover {
  background: #e6f7ff;
}

.dialog-btn {
  width: 100%;
  margin-top: 24px;
  background: #409eff;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.dialog-btn:hover {
  background: #66b1ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
</style>
